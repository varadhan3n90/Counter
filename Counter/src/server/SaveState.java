/*
 * This class once invoked automatically saves the state of server
 * regularly in seconds specified.
 */
package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.*;


/**
 * The Class SaveState.
 */
public class SaveState implements Runnable{
	
	private boolean DEBUG = true;
	private final String packageName = "server";
	private final int timer = 5000;
	
	@Override
	public void run() {
		if(DEBUG)
			System.out.println("Save state called...");
		while(true){
	    	saveState();
	    	try {
				Thread.sleep(timer);
			} catch (InterruptedException e) {				
				Logger log = Logger.getLogger(packageName);
				log.log(Level.WARNING, e.getStackTrace().toString());
			}
		}
	}

	/**
	 * Save state.
	 */
	public void saveState() {
		try{
			FileOutputStream fos = new  FileOutputStream(new File(Server.myServer.getSettingsFile()));
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(Server.myServer);
		}catch(Exception ex) {
			 Logger log = Logger.getLogger(packageName);
			 log.log(Level.WARNING, ex.getStackTrace().toString());
			 if(DEBUG) ex.printStackTrace();
		}		
	}
	
}
