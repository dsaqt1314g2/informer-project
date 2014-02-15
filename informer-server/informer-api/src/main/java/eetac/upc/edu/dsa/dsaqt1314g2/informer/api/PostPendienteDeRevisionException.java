package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.InformerError;

public class PostPendienteDeRevisionException extends WebApplicationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4110163799971920585L;
	private final static String MESSAGE = "Post pendiente de revisión";
	public PostPendienteDeRevisionException() {
		super(Response
				.status(Response.Status.SEE_OTHER)
				.entity(new InformerError(Response.Status.SEE_OTHER
						.getStatusCode(), MESSAGE))
				.type(MediaType.INFORMER_API_ERROR).build());
	}
}
