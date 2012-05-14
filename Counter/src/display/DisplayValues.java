package display;

import java.io.Serializable;

public class DisplayValues implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1329749430473478648L;
	public int counterNumber;
	public int tokenNumber;
	public DisplayValues(int counterNumber,int tokenNumber) {
		this.counterNumber = counterNumber;
		this.tokenNumber = tokenNumber;
	}
}
