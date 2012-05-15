/*
 * 
 */
package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;



// TODO: Auto-generated Javadoc
/**
 * The Class ClientMain.
 */
public class ClientMain {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
			File f = new File("Client.obj");
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
		}catch(Exception e){ e.printStackTrace(); }
	}
}
