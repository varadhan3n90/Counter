package server;

import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Server extends JFrame implements Serializable, ActionListener, WindowListener{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int PORT = 8101;
	private static final int OK = 0;
	
	
	static Server myServer;	
	private int tokenNumber = 0;
	
	Button setting;
	Button display;
		
	
	public void initialize(){
		
		System.out.println("Initialize called.");
		GridLayout gl = new GridLayout(1,2);
		this.setLayout(gl);		
		setting = new Button("Settings");
		display = new Button("Display Counter");
		this.add(setting);
		this.add(display);
		setting.addActionListener(this);
		display.addActionListener(this);
		
	}
	
	public int getTokenNumber(){
		tokenNumber++;
		return tokenNumber;
	}
		
	public void acceptConnections(ServerSocket server){
		try{			
			System.out.println("Accept connections called.");
			while(true){
				Socket client = server.accept();
				Scanner input = new Scanner(client.getInputStream());
				System.out.println("Counter: "+input.nextInt());			
				System.out.println("Token number"+getTokenNumber());
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				out.write(OK);
			}
		}catch(Exception e) { e.printStackTrace(); }
	}
	
	public void CreateServer(){
		try{
			boolean createNew = true;
			File f = new File("settings.obj");
			if(f.exists()){
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream in = new ObjectInputStream(fis);
				myServer = (Server)in.readObject();				
				createNew = false;
				myServer.setVisible(true);
			}else{
				f.createNewFile();				
				myServer = new Server();
				myServer.initialize();
				myServer.setVisible(true);
				myServer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			}		
			if(createNew){				
				myServer.setVisible(true);				
			}			
		}catch(Exception e){ System.out.println("Error occured"); e.printStackTrace(); }
	}
	
	public static void main(String[] args){
		new Server().CreateServer();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {		
		if(ae.getActionCommand().equals("Settings")){
			JOptionPane.showMessageDialog(null, "Setting button pressed");
		}
		if(ae.getActionCommand().equals("Display Counter")){
			//JOptionPane.showConfirmDialog(null, "Display counter called. Writing object.");			
			try {
				ServerSocket s = new ServerSocket(PORT);
				new Display();
				this.setVisible(false);
				myServer.acceptConnections(s);
			} catch (IOException e) { e.printStackTrace(); }			
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent we) {
		try{
			FileOutputStream fos = new  FileOutputStream(new File("Settings.obj"));
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(Server.myServer);
		}catch(Exception e) { e.printStackTrace(); }
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
