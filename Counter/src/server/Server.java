/*
 * This is the main program.
 * It provides token numbers to clients.
 * Initializes the display.
 * State is automatically saved every n seconds by SaveState class.
 * State will be restored accordingly.
 * 
 * Working:
 * myserver is a static object that can be initialized using serialization.
 * myserver.setVisible(true) will show the initial window with two buttons Display and settings.
 * When this is done and Display is clicked a new object for display is created which need not be serialized as display details are not stored (local object not object of class).
 * Also only when the display window is shown the token dispenser would work as only during that time serial values are read.
 * Only members of server class such as the finalTokenissued and tokens dispensed are maintained and serialized.
 * 
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

import printing.CitizenPOSPrinting;

import serialProgram.ButtonRead8051;
import utility.ConfigurationReader;
import display.*;


/**
 * The Class Server.
 */
public class Server extends JFrame implements Serializable, ActionListener, Runnable{
	
	private boolean usingEmbeddedDevices = true;
	
	private static final String packageName = "server";
	
	private static final boolean DEBUG = true;
	
	private static final boolean TESTING = true;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 12345L;
	
	private static final String settingsFile = "Resources/ServerSetting.obj";
	
	/** The Constant PORT. */
	private static int PORT = 8100;
		
	/** The my server. */
	public static Server myServer;
	
	/** The token number. */
	//private int tokenNumber = 0;
	
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
	
	private static int TEST_QUEUE_LIMIT = 0;
	
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
		usingEmbeddedDevices = ConfigurationReader.getServerInit("usingEmbeddedDevice").equalsIgnoreCase("false")? false: true;
		try{
			TEST_QUEUE_LIMIT = Integer.parseInt(ConfigurationReader.getServerInit("TestQueueLimit"));
		}catch(Exception e){
			TEST_QUEUE_LIMIT = 10;
		}
		try{
			PORT = Integer.parseInt(ConfigurationReader.getServerInit("server_port"));
		}catch(Exception e){
			PORT = 8100;
		}
	}
	
	
	private void createTestQueue(){
		if(TESTING){
			for(int i=0;i<TEST_QUEUE_LIMIT;i++){
				// If tokens are of type integer then -----> tokensDispensed.add(i+1);				
				Token t = new Token(i+1);
				tokensDispensed.add(t);
			}
		}
	}
	
	
	/**
	 * Gets the next token number.
	 *
	 * @return the next token number
	 */
	public Token getNextToken() throws NoTokensDispensedException{
		/*
		if(TESTING){
			return new Random().nextInt(100);
		}
		*/
		// Changing logic here
		/*
		tokenNumber = EMPTY_QUEUE;
		if(!tokensDispensed.isEmpty())
			 tokenNumber = tokensDispensed.remove();
			// TODO: return the token number to be dispensed			
		return tokenNumber;
		*/
		if(tokensDispensed.isEmpty())
			throw new NoTokensDispensedException();
		return tokensDispensed.remove();
	}
		
	/**
	 * Accept connections.
	 */
	public void acceptConnections(){
		try{
			
			int nextToken = EMPTY_QUEUE,counterNumber=0;
			if(DEBUG) System.out.println("Accept connections called.");
			// TODO: Call createTestQueue after implementation for testing
			if(TESTING) createTestQueue();
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
					try{
						nextToken = getNextToken().getTokenValue();
					}catch(NoTokensDispensedException ntde){
						nextToken = EMPTY_QUEUE;
					}
					// Problem is token number is assumed to be integer
					// TODO: In case token number is made to contain string also then send either 2 integers or send a string instead of token number
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
			if(DEBUG) e.printStackTrace();
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
			//JOptionPane.showMessageDialog(null, "Setting button pressed");
			this.setVisible(false);
			Settings adjustSettings = new Settings();
			adjustSettings.setVisible(true);
		}
		if(ae.getActionCommand().equals("Display Counter")){
			if(DEBUG) System.out.println("Display counter button pressed");			
			Thread t = new Thread(this);
			t.start();
			this.setVisible(false);
			DHCP d = DHCP.getInstance();
			d.start();
			Display	display = new Display();
			display.setVisible(true);
			// TODO: Serial communication function my be called here
			if(usingEmbeddedDevices){
				String defaultComPort = "COM1";
				if(!ConfigurationReader.getServerInit("COMPort").isEmpty())
					defaultComPort = ConfigurationReader.getServerInit("COMPort");
				SerialCommunication embeddedDevice = SerialCommunication.getInstance(tokensDispensed, defaultComPort);
				// if(embeddedDevice.getThreadGroup().activeCount()==0)
					embeddedDevice.start();
			}
		}
	}
}

class SerialCommunication extends Thread {
	Queue<Token> queue;
	String port;
	private int finalTokenNumberIssued = 0;
	static SerialCommunication s = new SerialCommunication();
	private boolean started = false;
	
	private SerialCommunication(){
		// Singleton object
	}
	
	public static SerialCommunication getInstance(Queue<Token> tokenQueue,String port){
		s.queue = tokenQueue;
		s.port = port;
		return s;
	}
	
	private String createTokenID(String value){
		// TODO: add logic to make the token and add it to queue based on
		// Temporary logic has been added for testing purpose.
		finalTokenNumberIssued ++;
		Token t = new Token(finalTokenNumberIssued);
		s.queue.add(t);
		return t.getTokenValue()+"";
	}
	
	
	public void run(){
		if(!started){
			started = true;
			ButtonRead8051 tokenDispenser = new ButtonRead8051(port);
			String requestTokenNumber = "Token Dispenser Not working";
			
			try {
				String value = tokenDispenser.readPort();
				requestTokenNumber = createTokenID(value);
				// TODO: Add logic for different queues on different values.
				CitizenPOSPrinting print = CitizenPOSPrinting.getInstance();
				print.printToken(requestTokenNumber);
				
			} catch (NoSuchPortException | PortInUseException
					| UnsupportedCommOperationException | IOException e) {				
				// TODO: Log values
				e.printStackTrace();
			}
		}
	}
}