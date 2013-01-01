/*
 * 
 */
package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * The Class Client.
 */
public class Client extends JFrame implements ActionListener,Runnable {
	
	private final static String packageName = "client";
	
	private final static boolean DEBUG = true;
	
	private boolean threadStarted = false;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -60997400387172441L;
	
	/** The my client. */
	public static Client myClient;
	
	/** The counter number. */
	private final int counterNumber;
	
	/** The PORT. */
	private final int PORT = 8100;
	
	private final static int POLL_TIME = 3000;
	
	private String serverIP="localhost";
	
	public static final int CHECK_REQUEST = -1;
	public static final int ACCEPT_REQUEST = -2;
	public static final int NON_EMPTY_QUEUE = -3;
	public static final int EMPTY_QUEUE = -4;
	public static final int OK_MESSAGE = -5;
	public static final int TEST_MESSAGE = -6;
	public static final int TEST_REPLY = -7;
	public static final int NEW_REQUEST = -8;
	
	/** The next client. */
	JButton nextClient;
	
	Thread t;
	/**
	 * Instantiates a new client.
	 */
	public Client(){
		t = new Thread(this);
		nextClient = new JButton("Next");
		nextClient.setBackground(Color.GREEN);
		this.add(nextClient);		
		nextClient.addActionListener(this);		
		counterNumber = Integer.parseInt(JOptionPane.showInputDialog("Please enter counter number.."));
		setTitle("Counter "+counterNumber);	
		this.setSize(200, 200);
		this.setAlwaysOnTop(true);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Next")){
			try {
				callServer();
			} catch (UnknownHostException e1) {
				if(DEBUG) System.out.println("Unknown host");
				Logger log = Logger.getLogger(packageName);
				log.log(Level.WARNING, e1.getStackTrace().toString());
			} catch (IOException e1) {				
				Logger log = Logger.getLogger(packageName);
				log.log(Level.WARNING, e1.getStackTrace().toString());
			}
		}		
	}

	public void waitForClients(){
		while(true)
		try{
			Socket server = new Socket(serverIP,PORT);
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			DataInputStream in = new DataInputStream(server.getInputStream());
			out.writeInt(CHECK_REQUEST);
			int result = in.readInt();
			if(result == NON_EMPTY_QUEUE){
				return;
			}
			Thread.sleep(POLL_TIME);
		}catch(Exception e){
			Logger log = Logger.getLogger(packageName);
			log.log(Level.WARNING, e.getStackTrace().toString());
		}
	}

	private void callServer() throws UnknownHostException, IOException {
		Socket server = new Socket(serverIP,PORT);
		DataOutputStream out = new DataOutputStream(server.getOutputStream());
		DataInputStream in = new DataInputStream(server.getInputStream());
		out.writeInt(NEW_REQUEST);
		int reply = in.readInt();
		if(DEBUG) System.out.println("Reply from server "+reply );
		if(reply!=OK_MESSAGE){
			JOptionPane.showMessageDialog(null, "Server response not proper", "Error", JOptionPane.ERROR_MESSAGE);			
		}else{
			out.writeInt(counterNumber);		
			reply = in.readInt();
			if(reply!=EMPTY_QUEUE){
				if(DEBUG) System.out.println("Queue not is empty now. "+reply);
				JOptionPane.showMessageDialog(null, "Next Token "+reply);
			}
			else{
				if(DEBUG) System.out.println("Queue is empty "+reply);
				nextClient.setBackground(Color.RED);
				if(t!=null&&!threadStarted)				
					t.start();
			}
		}
		in.close();
		out.close();
		server.close();
	}


	@Override
	public void run() {
		threadStarted = true;
		waitForClients();
		nextClient.setBackground(Color.GREEN);
		threadStarted = false;
		if(DEBUG) System.out.println("Queue is not empty");
	}	
	
}
