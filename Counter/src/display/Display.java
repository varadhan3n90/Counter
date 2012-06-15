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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import server.Server;

/**
 * The Class Display.
 */
public class Display extends JFrame implements Serializable, Runnable {
	
	private static final String packageName = "display";
	
	private static final boolean DEBUG = false;
	
	private static final int TIMER_VALUE = 10000;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 12346L;
	
	/** The display table. */
	JTable displayTable;
	
	/** The table header. */
	JTableHeader tableHeader;
	
	/** The jscroll pane. */
	JScrollPane jp;
	
	/** The column names. */
	String columnNames[] = {"Token Number","Counter Number"};
	
	/** The data. */
	String[][] data= {{" "," "},{" "," "},{" "," "}};
	
	private final String dingSound = "Resources/Ding.wav";
	
	/** The content displayed. */
	private boolean contentDisplayed = false;
	
	ArrayList<File> ads;

	private int adCounter = 0;
	
	private void initializeAds(){
		ads = new ArrayList<File>();
		File folder = new File("Resources");
		FilenameFilter ff = new FilenameFilter() {			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith("jpg");
			}
		};
		File f[] = folder.listFiles(ff);
		for(int i=0;i<f.length;i++){
			ads.add(f[i]);
		}
	}
	
	/**
	 * Initialize.
	 */
	private void initialize() {
		initializeAds();
		if(DEBUG) System.out.println("Creating display tables.");		
		Font font = new Font("Times New Roman", Font.BOLD, 140);		
		displayTable = new JTable(data, columnNames);
		displayTable.setFont(font);
		displayTable.setRowHeight(200);
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
		if(DEBUG) System.out.println("Cycle data called.");
		for(int i=data.length-1;i>=1;i--){
			data[i][0] = data[i-1][0];
			data[i][1] = data[i-1][1];
		}
		data[0][1] = ""+d.counterNumber;
		data[0][0] = ""+d.tokenNumber;
		contentDisplayed = true;
		displayTable.updateUI();
		playSound(dingSound);
	}
	
	public void paint(Graphics g){
		Image image = null;
		try {
			if(adCounter > ads.size()){
				adCounter = 0;
			}else{
				adCounter++;				
			}
			File f = ads.get(adCounter);
			image = ImageIO.read(f);
		} catch (IOException e) {		
			Logger log = Logger.getLogger(packageName);
			log.log(Level.WARNING, e.getStackTrace().toString());
		}
		
		g.drawImage(image, // draw it  
                this.getWidth()/2 - image.getWidth(this) / 2, // at the center  
                this.getHeight()/2 - image.getHeight(this) / 2, // of screen 
                null);
		
	}
	
	  public static synchronized void playSound(final String url) {
		    new Thread(new Runnable() {
		      public void run() {
		        try {
		        if(DEBUG) System.out.println("Going to play ding sound from "+url);
		        Clip clip = AudioSystem.getClip();		        
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(url));
		        clip.open(inputStream);
		        clip.start(); 
		        } catch (Exception e) {
		        	Logger log = Logger.getLogger(packageName);
					log.log(Level.WARNING, e.getStackTrace().toString());
		        }
		      }
		    }).start();
		  }

	
	/**
	 * Instantiates a new display.
	 */
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
	public void run() {
		if(Thread.currentThread().getName().equals("DataDisplay")){
			while(true){
				if(Server.myServer!=null){
					DisplayValues d = Server.myServer.getNextInQueue();
					if(d!=null){							
						jp.setVisible(true);
						jp.updateUI();
						cycleData(d);
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {					
					Logger log = Logger.getLogger(packageName);
					log.log(Level.WARNING, e.getStackTrace().toString());
				}
			}
		}
		if(Thread.currentThread().getName().equals("AdDisplay"))
		while(true){
			if(isContentDisplayed()){
				try {					
					setContentDisplayed(false);
					Thread.sleep(TIMER_VALUE);
				} catch (InterruptedException e) {					
					Logger log = Logger.getLogger(packageName);
					log.log(Level.WARNING, e.getStackTrace().toString());
				}
			}else{
				if(DEBUG) System.out.println("Displaying ad..");							
				try {					
					jp.setVisible(false);
					jp.updateUI();
					repaint();
					Thread.sleep(TIMER_VALUE);
				} catch (InterruptedException e) {					
					Logger log = Logger.getLogger(packageName);
					log.log(Level.WARNING, e.getStackTrace().toString());
				}
			}
		}
	}
	
	
}
