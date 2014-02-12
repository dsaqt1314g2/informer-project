package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.InformerError;

public class InternalServerException extends WebApplicationException {
	private static final long serialVersionUID = -1284310111307676552L;

	public InternalServerException(String message) {
		super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new InformerError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message))
				.type(MediaType.INFORMER_API_ERROR).build());
	}
}