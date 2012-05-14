package server;

import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class Display extends JFrame implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JTable displayTable;	
	JTableHeader tableHeader;
	private String[] title = {"Counter Number","Token Number"};
	private String[][] data = {{"1","2"},{"3","4"}};
	
	
	private void initialize(){
		displayTable = new JTable(data,title);		
	}
	
	Display(){
		this.setAlwaysOnTop(true);
		this.setSize(getMaximumSize());
		initialize();
		this.add(displayTable);
		this.setVisible(true);
	}
}
