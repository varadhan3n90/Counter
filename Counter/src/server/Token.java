package server;

import java.io.Serializable;

public class Token implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// TODO: Create fields for token
	private int number;
	public Token(int number){
		this.number = number;
	}
	public int getTokenValue(){
		return number;
	}
}
