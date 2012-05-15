/*
 * 
 */
package display;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import server.Server;

/**
 * The Class Display.
 */
public class Display extends JFrame implements Serializable, Runnable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 12346L;
	
	/** The display table. */
	JTable displayTable;
	
	/** The table header. */
	JTableHeader tableHeader;
	
	/** The jp. */
	JScrollPane jp;
	
	/** The column names. */
	String columnNames[] = {"Token Number","Counter Number"};
	
	/** The data. */
	String[][] data= {{" "," "},{" "," "},{" "," "},{" "," "}};
	
	/** The content displayed. */
	private boolean contentDisplayed = false;

	/**
	 * Initialize.
	 */
	private void initialize() {
		System.out.println("Creating display tables.");		
		Font font = new Font("Times New Roman", Font.BOLD, 40);		
		displayTable = new JTable(data, columnNames);
		displayTable.setFont(font);
		displayTable.setRowHeight(100);
		jp = new JScrollPane(displayTable);		
		getContentPane().add(jp);
	}
	
	/**
	 * Checks if is content displayed.
	 *
	 * @return true, if is content displayed
	 */
	public boolean isContentDisplayed(){
		return contentDisplayed;
	}
	
	/**
	 * Sets the content displayed.
	 *
	 * @param contentDisplayed the new content displayed
	 */
	public synchronized void setContentDisplayed(boolean contentDisplayed){
		this.contentDisplayed = contentDisplayed;
	}
	
	/**
	 * Cycle data.
	 *
	 * @param d the d
	 */
	public void cycleData(DisplayValues d){
		System.out.println("Cycle data called.");
		for(int i=data.length-1;i>=1;i--){
			data[i][0] = data[i-1][0];
			data[i][1] = data[i-1][1];
		}
		data[0][1] = ""+d.counterNumber;
		data[0][0] = ""+d.tokenNumber;
		contentDisplayed = true;
		displayTable.updateUI();
	}
	
	/**
	 * Instantiates a new display.
	 */
	public Display() {
		super("Queue management system...");		
		this.setAlwaysOnTop(true);
		this.initialize();
		this.setSize(getMaximumSize());
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		WindowListener listner = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we){
				contentDisplayed = true;
				if(Server.myServer!=null){
					Server.myServer.setVisible(true);					
				}
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
	public void paint(Graphics g){
		try {
			Image img = ImageIO.read(new File("C:/Users/Elcot/Projects/Counter/Counter/Resources/Lighthouse.jpg"));
			g.drawImage(img, 0, 0,480,640, null);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	

	@Override
	public void run() {
		if(Thread.currentThread().getName().equals("DataDisplay")){
			while(true){
				if(Server.myServer!=null){
					DisplayValues d = Server.myServer.getNextInQueue();
					if(d!=null){
						jp.setVisible(true);
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
					Thread.sleep(10000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}else{
				System.out.println("Displaying ad..");							
				try {					
					jp.setVisible(false);
					repaint();
					Thread.sleep(10000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
		}
	}
	
	
}
