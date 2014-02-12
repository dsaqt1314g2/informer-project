package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.InformerError;

public class DislikeAlreadyFoundException extends WebApplicationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4110163799971920585L;
	private final static String MESSAGE = "Calificacion already found";

	public DislikeAlreadyFoundException() {
		super(Response.status(Response.Status.NOT_FOUND).entity(new InformerError(Response.Status.FOUND.getStatusCode(), MESSAGE))
				.type(MediaType.INFORMER_API_ERROR).build());
	}
}
