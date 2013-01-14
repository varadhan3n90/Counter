package server;

import java.io.Serializable;

public class NoTokensDispensedException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String getMessage(){
		return "No remaining tokens";
	}

}
