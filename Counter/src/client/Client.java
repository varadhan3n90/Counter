/*
 * 
 */
package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * The Class Client.
 */
public class Client extends JFrame implements ActionListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -60997400387172441L;
	
	/** The my client. */
	public static Client myClient;
	
	/** The counter number. */
	private final int counterNumber;
	
	/** The PORT. */
	private final int PORT = 8100;
	
	/** The next client. */
	JButton nextClient;
	
	/**
	 * Instantiates a new client.
	 */
	public Client(){		
		nextClient = new JButton("Next");
		this.add(nextClient);		
		nextClient.addActionListener(this);
		counterNumber = Integer.parseInt(JOptionPane.showInputDialog("Please enter counter number.."));
		setTitle("Counter "+counterNumber);		
		this.setAlwaysOnTop(true);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Next")){
			try {
				Socket server = new Socket("localhost",PORT);
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.write(counterNumber);
				DataInputStream in = new DataInputStream(server.getInputStream());
				int result = in.read();				
				JOptionPane.showMessageDialog(null, "Next Token ."+result);
				in.close();
				out.close();
				server.close();
			} catch (UnknownHostException e1) {
				System.out.println("Unknown host");
				e1.printStackTrace();
			} catch (IOException e1) {				
				e1.printStackTrace();
			}
		}		
	}
	
	
}
