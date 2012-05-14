package server;

import java.io.Serializable;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class Display extends JFrame implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JScrollPane jp;
	JTable displayTable;
	JTableHeader tableHeader;

	String[] columnNames = { "First Name", "Last Name", "Sport", "# of Years",
			"Vegetarian" };

	Object[][] data = {
			{ "Kathy", "Smith", "Snowboarding", new Integer(5),
					new Boolean(false) },
			{ "John", "Doe", "Rowing", new Integer(3), new Boolean(true) },
			{ "Sue", "Black", "Knitting", new Integer(2), new Boolean(false) },
			{ "Jane", "White", "Speed reading", new Integer(20),
					new Boolean(true) },
			{ "Joe", "Brown", "Pool", new Integer(10), new Boolean(false) } };

	private void initialize() {
		jp = new JScrollPane();
		displayTable = new JTable(data, columnNames);
		displayTable.setPreferredScrollableViewportSize(getMaximumSize());
		displayTable.setFillsViewportHeight(true);
		displayTable.setVisible(true);
		jp.add(displayTable);
		getContentPane().add(jp);
		pack();
	}

	Display() {
		//this.setAlwaysOnTop(true);
		this.setSize(getMaximumSize());
		initialize();
		this.setVisible(true);
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
