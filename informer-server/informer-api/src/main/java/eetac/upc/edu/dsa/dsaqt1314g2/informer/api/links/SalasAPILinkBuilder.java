package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.MediaType;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.SalasResource;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Sala;

public class SalasAPILinkBuilder {

	// public final static Link buildURIRootAPI(UriInfo uriInfo) { //getBase ->
	// http:blablabla/beeter-api/
	// URI uriRoot =
	// uriInfo.getBaseUriBuilder().path(InformerRootAPIResource.class).build();
	// Link link = new Link();
	// link.setUri(uriRoot.toString());
	// link.setRel("self bookmark"); //apunta a el mismo / pagina inicial
	// link.setTitle("Salas API");
	// link.setType(MediaType.INFORMER_API_SALA_COLLECTION); //devolver
	// coleccion de enlaces
	//
	// return link;
	// }

	public static final Link buildURISalasVision(UriInfo uriInfo,
			String offset, String length, int categoria, String rel) {
		URI uriSalas;
		if (offset == null && length == null)
			uriSalas = uriInfo.getBaseUriBuilder().path(SalasResource.class)
					.path(SalasResource.class, "getVisibilidad")
					.build(categoria);
		else {

			uriSalas = uriInfo.getBaseUriBuilder().path(SalasResource.class)
					.path(SalasResource.class, "getVisibilidad")
					.queryParam("o", offset).queryParam("l", length)
					.build(categoria);

		}

		Link self = new Link();
		self.setUri(uriSalas.toString());
		self.setRel(rel);
		switch (categoria) {
		case 0:
			self.setTitle("Salas Publicas");
			break;

		case 1:
			self.setTitle("Salas Privadas Visibles");
			break;

		case 2:
			self.setTitle("Salas Privadas Subscrito");
			break;

		case 3:
			self.setTitle("Todas tus Subscripciones a Salas");
			break;

		}
		self.setType(MediaType.INFORMER_API_SALA_COLLECTION);

		return self;
	}

	public static final Link buildTemplatedURISalasUnirse(UriInfo uriInfo,
			int salaid, boolean pass) {
		URI uriSalas;
		if (pass)
			uriSalas = uriInfo.getBaseUriBuilder().path(SalasResource.class)
					.path(SalasResource.class, "Unirse")
					.queryParam("pass", "{pass}").build(salaid);
		else
			uriSalas = uriInfo.getBaseUriBuilder().path(SalasResource.class)
					.path(SalasResource.class, "Unirse").build(salaid);

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriSalas));
		link.setRel("self");
		if (pass)
			link.setTitle("Unirse a una Sala Privada");
		else
			link.setTitle("Unirse a una Sala Publica");
		link.setType(MediaType.INFORMER_API_SALA_COLLECTION);

		return link;
	}

	public static final Link buildTemplatedURIAbandonarSala(UriInfo uriInfo,
			int salaid) {
		URI uriSalas;

		uriSalas = uriInfo.getBaseUriBuilder().path(SalasResource.class)
				.path(SalasResource.class, "AbandonarSala").build(salaid);

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriSalas));
		link.setRel("leave");
		link.setTitle("Abandonar una Sala ");
		link.setType(MediaType.INFORMER_API_SALA_COLLECTION);

		return link;
	}
	public static final Link buildTemplatedURIDenegarInvitacion(UriInfo uriInfo,
			int salaid) {
		URI uriSalas;

		uriSalas = uriInfo.getBaseUriBuilder().path(SalasResource.class)
				.path(SalasResource.class, "DenegarInvitacion").build(salaid);

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriSalas));
		link.setRel("declined");
		link.setTitle("Declinar una Invitacion ");
		link.setType(MediaType.INFORMER_API_SALA_COLLECTION);

		return link;
	}
	public static final Link buildTemplatedURIgetInvitaciones(UriInfo uriInfo) {
		URI uriSalas;

		uriSalas = uriInfo.getBaseUriBuilder().path(SalasResource.class)
				.path(SalasResource.class, "getInvitaciones").build();

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriSalas));
		link.setRel("invitations");
		link.setTitle("Obtener invitaciones Sala ");
		link.setType(MediaType.INFORMER_API_SALA_COLLECTION);

		return link;
	}
	
	

	public static final Link buildTemplatedURISalasInvitar(UriInfo uriInfo,
			int salaid) {
		URI uriSalas;

		uriSalas = uriInfo.getBaseUriBuilder().path(SalasResource.class)
				.path(SalasResource.class, "Invitar")
				.queryParam("username", "{username}").build(salaid);

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriSalas));
		link.setRel("self");
		link.setTitle("Invitar a la Sala");
		link.setType(MediaType.INFORMER_API_SALA_COLLECTION);

		return link;
	}

	public final static Link buildURISala(UriInfo uriInfo, String tipo) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(SalasResource.class)
				.build();
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel("Salas action");
		link.setTitle("Tipy " + tipo);
		link.setType(MediaType.INFORMER_API_SALA);

		return link;
	}

	public final static Link buildURISalaId(UriInfo uriInfo, Sala sala,
			String type, String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(SalasResource.class)
				.path(SalasResource.class, "getSala")
				.build(sala.getIdentificador());
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle(type + " Sala :" + sala.getIdentificador() + " "
				+ sala.getNombre_sala());
		link.setType(MediaType.INFORMER_API_SALA);

		return link;
	}

}