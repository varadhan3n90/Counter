package client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import utility.ConfigurationReader;


/**
 * The Class Client.
 */
public class Client extends JFrame implements ActionListener,Runnable {

	// private final static String packageName = "client";

	private final static boolean DEBUG = true;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -60997400387172441L;

	/** The my client. */
	public static Client myClient;

	/** The counter number. */
	private final int counterNumber;

	/** The PORT. */
	private final int PORT = Integer.parseInt(ConfigurationReader.getClientInit("server_port"));

	private String serverIP = ConfigurationReader.getClientInit("server_ip");

	private static final int CONTENT_FONT_SIZE = Integer.parseInt(ConfigurationReader.getClientInit("content_font_size"));

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
	JLabel token;
	JLabel counter;
	
	JTextField numberField;
	JButton callNow;
	/**
	 * Instantiates a new client.
	 */
	public Client(){
		
		FlowLayout layout = new FlowLayout();
		Dimension nextClient_d =new Dimension(100,40);
		Dimension callNow_d =new Dimension(100,40);
		this.setLayout(layout);
		Font font = new Font("Times New Roman", Font.BOLD,CONTENT_FONT_SIZE);	
		token = new JLabel("Token : " + 0 );
		nextClient = new JButton("Next");
		callNow =  new JButton("Call");
		numberField = new JTextField(4);
		nextClient.setPreferredSize( nextClient_d );
		callNow.setPreferredSize( callNow_d );
		counterNumber = Integer.parseInt(JOptionPane.showInputDialog("Please enter counter number.."));
		setTitle("Counter "+counterNumber);	
		nextClient.setFont(font);
		token.setFont(font);
		callNow.setFont(font);
		counter = new JLabel("Counter : " + counterNumber);
		counter.setFont(font);
		numberField.setFont(font);
		this.add(token);
		this.add(nextClient);		
		this.add(counter);
		nextClient.addActionListener(this);		
		this.setSize(450,150);
		this.setAlwaysOnTop(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Next")){
				callServer();			
		}		
	}

	private void callServer() {
		int retry = 0;
		boolean connected = false;
		while(!connected&&retry<2){
			try{
				Socket server = new Socket(serverIP,PORT);
				server.setSoTimeout(3000);
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
						token.setText("Token : " + reply);
					}
					else{
						if(DEBUG) System.out.println("Queue is empty "+reply);
						JOptionPane.showMessageDialog(null, "Queue is empty", "QMS", JOptionPane.OK_OPTION);
					}
				}
				in.close();
				out.close();
				server.close();
				connected = true;
			}catch(ConnectException e){
				DHCPDiscover d = new DHCPDiscover();
				serverIP = d.getServerIP();
				if(serverIP!=null)
					ConfigurationReader.setClientInit("server_ip", serverIP);
				retry ++;
			}
			catch(UnknownHostException e){
				DHCPDiscover d = new DHCPDiscover();
				serverIP = d.getServerIP();
				System.out.println("Changing to new server ip "+serverIP);
				if(serverIP!=null)
					ConfigurationReader.setClientInit("server_ip", serverIP);
				retry ++;
			} catch (IOException e) {				
				e.printStackTrace();
			} 
		}
		if(connected==false){
			JOptionPane.showMessageDialog(null, "Unable to reach server", "QMS Client Error", JOptionPane.ERROR_MESSAGE);
		}
	}


	@Override
	public void run() {
		new ReceiveBroadCast();		
	}

}