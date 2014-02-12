package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.InformerError;

public class BadRequestException extends WebApplicationException {

	private static final long serialVersionUID = -3770862164392622526L;

	public BadRequestException(String message) {
		super(Response.status(Response.Status.BAD_REQUEST).entity(new InformerError(Response.Status.BAD_REQUEST.getStatusCode(), message))
				.type(MediaType.INFORMER_API_ERROR).build());
	}
}
