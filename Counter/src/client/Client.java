package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Client extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -60997400387172441L;
	public static Client myClient;
	private final int counterNumber;
	private final int PORT = 8100;
	
	JButton nextClient;
	public Client(){		
		nextClient = new JButton("Next");
		this.add(nextClient);		
		nextClient.addActionListener(this);
		counterNumber = Integer.parseInt(JOptionPane.showInputDialog("Please enter counter number.."));
		setTitle("Counter "+counterNumber);
		WindowListener listener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				System.out.println("Client window closing.. Writing to file..");
				try{
					FileOutputStream fos = new  FileOutputStream(new File("Client.obj"));
					ObjectOutputStream out = new ObjectOutputStream(fos);
					out.writeObject(Client.myClient);
					System.exit(0);
				}catch(Exception ex){ ex.printStackTrace(); }
			}
		};
		this.addWindowListener(listener);
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
