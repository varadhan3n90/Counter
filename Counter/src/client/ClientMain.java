/*
 * 
 */
package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class ClientMain.
 */
public class ClientMain {
	
	private static final String packageName = "client";
	
	private static final boolean DEBUG = false;
	
	private static final String settingsFile = "Resources/ClientSetting.obj";
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	
	public static void main(String[] args){		
			File f = new File(ClientMain.settingsFile);
			try{
			if(f.exists()){
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream in = new ObjectInputStream(fis);
				Client.myClient = (Client)in.readObject();
				//Client.myClient.setVisible(true);
				if(DEBUG) System.out.println("Client initiated from existing settings.");
			}else{
				if(DEBUG) System.out.println("New client created..");
				f.createNewFile();
				Client.myClient = new Client();
				
			}
			Client.myClient.setVisible(true);
			Thread notificationThread = new Thread(Client.myClient);
			notificationThread.start();
			WindowListener listener = new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e){
					if(DEBUG) System.out.println("Client window closing.. Writing to file..");
					try{
						FileOutputStream fos = new  FileOutputStream(new File(ClientMain.settingsFile));
						ObjectOutputStream out = new ObjectOutputStream(fos);
						out.writeObject(Client.myClient);
						if(DEBUG) System.out.println("Exiting program");
						System.exit(0);
					}catch(Exception ex){ 
						Logger log = Logger.getLogger(packageName);
						log.log(Level.WARNING, ex.getStackTrace().toString());
					}
				}
			};
			
			Client.myClient.addWindowListener(listener);
			
		}catch(Exception e){ 
			Logger log = Logger.getLogger(packageName);
			log.log(Level.WARNING, e.getStackTrace().toString()); 
		}
	}
}
