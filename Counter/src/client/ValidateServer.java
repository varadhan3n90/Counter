package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class ValidateServer {
	
	private static final String ipFile = "Resources/ServerIP";
	
	
	ValidateServer(){
		String ipAddress = getServerAddress();
		if(ipAddress!=null){
			
		}
	}
	
	private String getServerAddress(){
		String ipAddress = null;
		File f = new File(ipFile);
		if(f.exists()){
			try {
				Scanner input = new Scanner(f);
				if(input.hasNext())
					ipAddress = input.nextLine();
			} catch (FileNotFoundException e) {				
				e.printStackTrace();
			}
		}else{
			try {
				f.createNewFile();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		return ipAddress;
	}
}
