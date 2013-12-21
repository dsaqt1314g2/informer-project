package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.InformerError;

public class ServiceUnavailableException extends WebApplicationException {
	private static final long serialVersionUID = -1284310111307676552L;

	public ServiceUnavailableException(String message) {
		super(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(new InformerError(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), message))
				.type(MediaType.INFORMER_API_ERROR).build());
	}
}
