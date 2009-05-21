package org.bonmassar.crappydb.server.exceptions;

public abstract class CrappyDBException extends Exception{
	
	private final static String DEFAULT_REASON="No details";
	private String reason;
	
	public CrappyDBException(String reason){
		this.reason = reason;
	}
	
	public CrappyDBException(){
		this.reason = DEFAULT_REASON;
	}
	
	public String toString(){
		return getClass().getSimpleName() +" ["+ getReason() +"]";
	}

	private String getReason() {
		if(null != reason && reason.length() > 0)
			return reason;
		
		return DEFAULT_REASON;
	}
}
