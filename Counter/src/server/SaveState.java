package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class SaveState implements Runnable{	
	@Override
	public void run() {
		System.out.println("Save state called...");
		while(true){
	    	saveState();
	    	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
	}

	public void saveState() {
		try{
			FileOutputStream fos = new  FileOutputStream(new File("Settings.obj"));
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(Server.myServer);
		}catch(Exception ex) {
			ex.printStackTrace(); 
		}		
	}
	
}
