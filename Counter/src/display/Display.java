package display;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serializable;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import server.Server;

public class Display extends JFrame implements Serializable, Runnable {
	
	private static final long serialVersionUID = 12346L;
	JTable displayTable;
	JTableHeader tableHeader;
	JScrollPane jp;
	String columnNames[] = {"Counter Number","Token Number"};
	String[][] data= {{" "," "},{" "," "},{" "," "},{" "," "}};
	
	private boolean contentDisplayed = false;

	private void initialize() {
		System.out.println("Creating display tables.");		
		Font font = new Font("Times New Roman", Font.BOLD, 40);		
		displayTable = new JTable(data, columnNames);
		displayTable.setFont(font);
		displayTable.setRowHeight(100);
		jp = new JScrollPane(displayTable);		
		getContentPane().add(jp);
	}
	
	public boolean isContentDisplayed(){
		return contentDisplayed;
	}
	
	public synchronized void setContentDisplayed(boolean contentDisplayed){
		this.contentDisplayed = contentDisplayed;
	}
	
	public void cycleData(DisplayValues d){
		System.out.println("Cycle data called.");
		for(int i=data.length-1;i>=1;i--){
			data[i][0] = data[i-1][0];
			data[i][1] = data[i-1][1];
		}
		data[0][0] = ""+d.counterNumber;
		data[0][1] = ""+d.tokenNumber;
		contentDisplayed = true;
		displayTable.updateUI();
	}
	
	public Display() {
		super("Queue management system...");		
		//this.setAlwaysOnTop(true);
		this.initialize();
		this.setSize(getMaximumSize());
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		WindowListener listner = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we){
				contentDisplayed = true;
				if(Server.myServer!=null)
					Server.myServer.setVisible(true);
				dispose();
			}
			
		};
		this.addWindowListener(listner);
		Thread t1 = new Thread(this);
		t1.setName("DataDisplay");
		t1.start();
		Thread t2 = new Thread(this);
		t2.setName("AdDisplay");
		t2.start();
	}
	
	@Override
	public void run() {
		if(Thread.currentThread().getName().equals("DataDisplay")){
			while(true){
				if(Server.myServer!=null){
					DisplayValues d = Server.myServer.getNextInQueue();
					if(d!=null){
						cycleData(d);
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
		}
		if(Thread.currentThread().getName().equals("AdDisplay"))
		while(true){
			if(isContentDisplayed()){
				try {					
					setContentDisplayed(false);
					Thread.sleep(5000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}else{
				System.out.println("Displaying ad..");
				// TODO: Display ad.
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
		}
	}
	
	
}
