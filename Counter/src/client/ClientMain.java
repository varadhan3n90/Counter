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

/**
 * The Class ClientMain.
 */
public class ClientMain {
	
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
				Client.myClient.setVisible(true);
				System.out.println("Client initiated from existing settings.");
			}else{
				System.out.println("New client created..");
				f.createNewFile();
				Client.myClient = new Client();
				Client.myClient.setVisible(true);
			}
			WindowListener listener = new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e){
					System.out.println("Client window closing.. Writing to file..");
					try{
						FileOutputStream fos = new  FileOutputStream(new File(ClientMain.settingsFile));
						ObjectOutputStream out = new ObjectOutputStream(fos);
						out.writeObject(Client.myClient);
						System.out.println("Exiting program");
						System.exit(0);
					}catch(Exception ex){ ex.printStackTrace(); }
				}
			};
			Client.myClient.addWindowListener(listener);						
		}catch(Exception e){ e.printStackTrace(); }
	}
}
