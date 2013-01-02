/*
 * This is the main program.
 * It provides token numbers to clients.
 * Initializes the display.
 * State is automatically saved every n seconds by SaveState class.
 * State will be restored accordingly.
 */
package server;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

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
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import printing.CitizenPOSPrinting;

import serialProgram.ButtonRead8051;
import display.*;


/**
 * The Class Server.
 */
public class Server extends JFrame implements Serializable, ActionListener, Runnable{
	
	private static final String packageName = "server";
	
	private static final boolean DEBUG = false;
	
	// private static final boolean TESTING = false;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 12345L;
	
	private static final String settingsFile = "Resources/ServerSetting.obj";
	
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
	
	/** The queue to hold token number and counter number. */
	Queue<DisplayValues> queue;
	
	/** The queue to hold tokens that have been dispensed but not attended*/
	Queue<Token> tokensDispensed;
	
	public static final int CHECK_REQUEST = -1;
	public static final int ACCEPT_REQUEST = -2;
	public static final int NON_EMPTY_QUEUE = -3;
	public static final int EMPTY_QUEUE = -4;
	public static final int OK_MESSAGE = -5;
	public static final int TEST_MESSAGE = -6;
	public static final int TEST_REPLY = -7;
	public static final int NEW_REQUEST = -8;
	
	// private static final int TEST_QUEUE_LIMIT = 10;
	
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
	 * 
	 * @return settings file
	 */
	public String getSettingsFile(){
		return settingsFile;
	}
	
	/**
	 * Initialize.
	 */
	public void initialize(){
		int HT = 500,WD = 500;
		if(DEBUG) System.out.println("Initialize called.");
		this.setTitle("Queue management system.");
		GridLayout gl = new GridLayout(1,2);
		this.setLayout(gl);
		this.setSize(HT, WD);
		setting = new JButton("Settings");
		displayScreen = new JButton("Display Counter");
		this.add(setting);
		this.add(displayScreen);
		setting.addActionListener(this);
		displayScreen.addActionListener(this);
		queue = new LinkedList<DisplayValues>();
		tokensDispensed = new LinkedList<Token>();
	}
	
	/*
	private void createTestQueue(){
		if(TESTING){
			for(int i=0;i<TEST_QUEUE_LIMIT;i++){
				// If tokens are of type integer then -----> tokensDispensed.add(i+1);
				// TODO: Add logic for creating test tokens
			}
		}
	}
	*/
	
	/**
	 * Gets the next token number.
	 *
	 * @return the next token number
	 */
	// TODO : change return type to token instead of token number
	public int getNextTokenNumber(){
		/*
		if(TESTING){
			return new Random().nextInt(100);
		}
		*/
		tokenNumber = EMPTY_QUEUE;
		if(!tokensDispensed.isEmpty())
			// tokenNumber = tokensDispensed.remove();
			// TODO: return the token number to be dispensed
			;
		return tokenNumber;
	}
		
	/**
	 * Accept connections.
	 */
	public void acceptConnections(){
		try{
			
			int nextToken = EMPTY_QUEUE,counterNumber=0;
			if(DEBUG) System.out.println("Accept connections called.");
			// TODO: Call createTestQueue after implementation for testing
			// if(TESTING) createTestQueue();
			ServerSocket s = new ServerSocket(PORT);
			while(true){
				
				if(DEBUG) System.out.println("Waiting for clients to connect..");
				Socket client = s.accept();
				if(DEBUG) System.out.println("Client connected..");
				
				DataInputStream in = new DataInputStream(client.getInputStream());
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				
				int clientValue = in.readInt();
				if(DEBUG) System.out.println("Client value "+clientValue);
				if(clientValue==TEST_MESSAGE){
					if(DEBUG) System.out.println("Test message received");
					out.writeInt(TEST_REPLY);
				}
				
				else if(clientValue==NEW_REQUEST){
					if(DEBUG) System.out.println("New request received.");
					out.writeInt(OK_MESSAGE);
					counterNumber = in.readInt();
					nextToken = getNextTokenNumber();
					out.writeInt(nextToken);
				}
				
				else if(clientValue==CHECK_REQUEST){
					if(tokensDispensed.isEmpty())
						out.writeInt(EMPTY_QUEUE);
					else
						out.writeInt(NON_EMPTY_QUEUE);
				}
				
				out.close();
				client.close();				
				if(clientValue==NEW_REQUEST&&(queue!=null&&nextToken!=EMPTY_QUEUE)){
					DisplayValues next = new DisplayValues(counterNumber,nextToken);
					queue.add(next);
				}
				if(DEBUG) System.out.println("Counter: "+counterNumber);
				if(DEBUG) System.out.println("Token number"+nextToken);
			}
		}catch(BindException be){
			return;
		}
		catch(Exception e) {		
			 Logger log = Logger.getLogger(packageName);
			 log.log(Level.WARNING, e.getStackTrace().toString());		
			if(DEBUG)	e.printStackTrace(); 
		}
	}
	
	/**
	 * New server.
	 */
	public void newServer(){
		File f = new File(getSettingsFile());	
		myServer = new Server();
		myServer.initialize();
		myServer.setVisible(true);
		myServer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		if(f.exists()){
			f.delete();
			try {
				f.createNewFile();
			} catch (IOException e1) {				
				Logger log = Logger.getLogger(packageName);
				log.log(Level.WARNING, e1.getStackTrace().toString());
				if(DEBUG) System.out.println("Unable to create file.");
			}
		}
	}
	
	/**
	 * Creates the server.
	 */
	public void CreateServer(){
		File f = new File(getSettingsFile());
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
				if(DEBUG) System.out.println("Main window closing");
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
		if(DEBUG) System.out.println("Run method called..");
		acceptConnections();
	}
	

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getActionCommand().equals("Settings")){
			JOptionPane.showMessageDialog(null, "Setting button pressed");
		}
		if(ae.getActionCommand().equals("Display Counter")){
			if(DEBUG) System.out.println("Display counter button pressed");			
			Thread t = new Thread(this);
			t.start();
			this.setVisible(false);
			Display	display = new Display();
			display.setVisible(true);			
		}
	}
	
	
	class SerialCommunication extends Thread {
		Queue<Integer> queue;
		String port;
		public SerialCommunication(Queue<Integer> tokenQueue,String port){
			queue = tokenQueue;
			this.port = port;
		}
		
		private String createTokenID(String value){
			// TODO: add logic to make the token and add it to queue based on 
			return null;
		}
		
		
		public void run(){
			ButtonRead8051 tokenDispenser = new ButtonRead8051(port);
			String requestTokenNumber = "Token Dispenser Not working";
			try {
				String value = tokenDispenser.readPort();
				requestTokenNumber = createTokenID(value);
				// TODO: Add logic for different queues on different values.
				
			} catch (NoSuchPortException | PortInUseException
					| UnsupportedCommOperationException | IOException e) {				
				// TODO: Log values
			}
			CitizenPOSPrinting print = CitizenPOSPrinting.getInstance();
			print.printToken(requestTokenNumber);
			
		}
	}
	
	
}
