package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

public class InformerError {
	private int status;
	private String message;
 
	public InformerError() {
		super();
	}
 
	public InformerError(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
 
	public int getStatus() {
		return status;
	}
 
	public void setStatus(int status) {
		this.status = status;
	}
 
	public String getMessage() {
		return message;
	}
 
	public void setMessage(String message) {
		this.message = message;
	}
}
