package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.InformerError;

public class DenunciaAlreadyFoundException extends WebApplicationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4110163799971920585L;
	private final static String MESSAGE = "Denuncia already found";
	public DenunciaAlreadyFoundException() {
		super(Response
				.status(Response.Status.FOUND)
				.entity(new InformerError(Response.Status.FOUND.getStatusCode(), MESSAGE))
				.type(MediaType.INFORMER_API_ERROR).build());
	}
}
