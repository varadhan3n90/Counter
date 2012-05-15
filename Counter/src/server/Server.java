/*
 * 
 */
package server;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import display.*;;


/**
 * The Class Server.
 */
public class Server extends JFrame implements Serializable, ActionListener, Runnable{	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 12345L;
	
	/** The Constant PORT. */
	private static final int PORT = 8100;
		
	/** The my server. */
	public static Server myServer;
	
	/** The token number. */
	private int tokenNumber = 0;
	
	/** The setting. */
	JButton setting;
	
	/** The display screen. */
	JButton displayScreen;
	
	/** The queue. */
	Queue<DisplayValues> queue;
	
	/**
	 * Gets the next in queue.
	 *
	 * @return the next in queue
	 */
	public synchronized DisplayValues getNextInQueue(){
		if(queue.isEmpty()){			
			return null;
		}
		else{			
			return queue.remove();
		}
	}
	
	/**
	 * Initialize.
	 */
	public void initialize(){		
		System.out.println("Initialize called.");
		this.setTitle("Queue management system.");
		GridLayout gl = new GridLayout(1,2);
		this.setLayout(gl);
		setting = new JButton("Settings");
		displayScreen = new JButton("Display Counter");
		this.add(setting);
		this.add(displayScreen);
		setting.addActionListener(this);
		displayScreen.addActionListener(this);
		queue = new LinkedList<DisplayValues>();
	}
	
	/**
	 * Gets the next token number.
	 *
	 * @return the next token number
	 */
	public int getNextTokenNumber(){
		tokenNumber++;
		return tokenNumber;
	}
		
	/**
	 * Accept connections.
	 */
	public void acceptConnections(){
		try{			
			System.out.println("Accept connections called.");
			ServerSocket s = new ServerSocket(PORT);
			while(true){
				System.out.println("Waiting for clients to connect..");
				Socket client = s.accept();
				System.out.println("Client connected..");
				DataInputStream in = new DataInputStream(client.getInputStream());
				int counterNumber = in.read();
				System.out.println("Counter: "+counterNumber);
				int nextToken = getNextTokenNumber();
				System.out.println("Token number"+nextToken);
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				out.write(nextToken);
				out.close();
				client.close();
				DisplayValues next = new DisplayValues(counterNumber,nextToken);
				if(queue!=null)
					queue.add(next);
			}
		}catch(Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * New server.
	 */
	public void newServer(){
		File f = new File("settings.obj");	
		myServer = new Server();
		myServer.initialize();
		myServer.setVisible(true);
		myServer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		if(f.exists()){
			f.delete();
			try {
				f.createNewFile();
			} catch (IOException e1) {
				System.out.println("Unable to create file.");
			}
		}
	}
	
	/**
	 * Creates the server.
	 */
	public void CreateServer(){
		File f = new File("settings.obj");
		try{
			boolean createNew = true;			
			if(f.exists()){
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream in = new ObjectInputStream(fis);
				myServer = (Server)in.readObject();
				createNew = false;
				myServer.setVisible(true);
			}else{
				f.createNewFile();
				newServer();
			}		
			if(createNew){				
				myServer.setVisible(true);				
			}			
		}catch(Exception e){			
			newServer();						
		}
		WindowListener listner = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we){
				System.out.println("Main window closing");
				new SaveState().saveState();
				System.exit(0);
			}			
		};
		myServer.addWindowListener(listner);
		myServer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Thread t = new Thread(new SaveState());
		t.start();
	}
	
	
	@Override
	public void run(){
		System.out.println("Run method called..");
		acceptConnections();
	}
	

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getActionCommand().equals("Settings")){
			JOptionPane.showMessageDialog(null, "Setting button pressed");
		}
		if(ae.getActionCommand().equals("Display Counter")){
			System.out.println("Display counter button pressed");				
			Thread t = new Thread(this);			
			t.start();
			this.setVisible(false);			
			Display	display = new Display();
			display.setVisible(true);			
		}
	}
	
}
