package server;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class Display extends JFrame implements Serializable {
	
	private static final long serialVersionUID = 12346L;

	JScrollPane jp;
	JTable displayTable;
	JTableHeader tableHeader;

	String columnNames[] = {"Counter Number","Token Number"};
	String[][] data= {{"1","2"},{"3","4"}};

	private void initialize() {
		System.out.println("Creating display tables.");
		displayTable = new JTable(data, columnNames);				
		jp = new JScrollPane(displayTable);
		getContentPane().add(jp);
	}
	
	Display() {
		super("Queue management system...");
		WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	System.out.println("Window closing event called...");
                //int confirm = JOptionPane.showOptionDialog(null,"Are You Sure to Close this Application?","Exit Confirmation", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE, null, null, null);
                //if (confirm == 0) {
	        	try{
					FileOutputStream fos = new  FileOutputStream(new File("Settings.obj"));
					ObjectOutputStream out = new ObjectOutputStream(fos);
					out.writeObject(Server.myServer);
				}catch(Exception ex) { ex.printStackTrace(); }
	        		System.exit(0);
                //}
            }
			};
		this.addWindowListener(exitListener);
		this.setAlwaysOnTop(true);
		this.initialize();
		this.setSize(getMaximumSize());
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	
}
