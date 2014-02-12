package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.InformerError;

public class SalaOwnerNotAreYouException extends WebApplicationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4110123799971920585L;
	private final static String MESSAGE = "Sala owner not are you";
	public SalaOwnerNotAreYouException() {
		super(Response
				.status(Response.Status.NOT_FOUND)
				.entity(new InformerError(Response.Status.NOT_FOUND
						.getStatusCode(), MESSAGE))
				.type(MediaType.INFORMER_API_ERROR).build());
	}
}
