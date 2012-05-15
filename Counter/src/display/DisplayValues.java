/*
 * 
 */
package display;

import java.io.Serializable;


/**
 * The Class DisplayValues.
 */
public class DisplayValues implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1329749430473478648L;
	
	/** The counter number. */
	public int counterNumber;
	
	/** The token number. */
	public int tokenNumber;
	
	/**
	 * Instantiates a new display values.
	 *
	 * @param counterNumber the counter number
	 * @param tokenNumber the token number
	 */
	public DisplayValues(int counterNumber,int tokenNumber) {
		this.counterNumber = counterNumber;
		this.tokenNumber = tokenNumber;
	}
}
