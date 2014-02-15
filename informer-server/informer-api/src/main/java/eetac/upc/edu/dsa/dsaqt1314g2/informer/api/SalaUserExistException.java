package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.InformerError;

public class SalaUserExistException extends WebApplicationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -411012399971920585L;
	private final static String MESSAGE = "User is yet in the sala or pass is incorrect";
	public SalaUserExistException() {
		super(Response
				.status(Response.Status.NOT_ACCEPTABLE)
				.entity(new InformerError(Response.Status.NOT_ACCEPTABLE
						.getStatusCode(), MESSAGE))
				.type(MediaType.INFORMER_API_ERROR).build());
	}
}
