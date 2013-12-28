package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.ComentarioResource;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.ComentariosRootAPIResource;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.MediaType;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.MensajeResource;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Comentario;

public class MensajesAPILinkBuilder {
	public final static Link buildURIRootAPI(UriInfo uriInfo) { // getBase ->
																// http:blablabla/beeter-api/
		URI uriRoot = uriInfo.getBaseUriBuilder().path(ComentariosRootAPIResource.class).build();
		Link link = new Link();
		link.setUri(uriRoot.toString());
		link.setRel("self bookmark"); // apunta a el mismo / pagina inicial
		link.setTitle("Informer API");
		link.setType(MediaType.INFORMER_API_LINK_COLLECTION); // devolver
																// coleccion de
																// enlaces

		return link;
	}

	public static final Link buildURIComentarios(UriInfo uriInfo, String rel) {
		return buildURIMensajes(uriInfo, 0, null, 0, null, rel);
	}

	public static final Link buildURIMensajes(UriInfo uriInfo, int offset, String length, long ifecha, String salaid, String rel) {
		URI uriStings = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).queryParam("o", offset).queryParam("l", length).queryParam("f", ifecha).build(salaid);
		Link self = new Link();
		self.setUri(uriStings.toString());
		self.setRel(rel);
		self.setTitle("Mensaje collection");
		self.setType(MediaType.INFORMER_API_MENSAJE_COLLECTION);

		return self;
	}

	public static final Link buildTemplatedURIComentarios(UriInfo uriInfo, String rel) {

		return buildTemplatedURIComentarios(uriInfo, rel, false);
	}

	public static final Link buildTemplatedURIComentarios(UriInfo uriInfo, String rel, boolean username) {
		URI uriStings;
		uriStings = uriInfo.getBaseUriBuilder().path(MensajeResource.class).queryParam("o", "{offset}").queryParam("l", "{length}").queryParam("f", "{fecha}").build();

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriStings));
		link.setRel(rel);
		link.setTitle("Mensaje collection resource");
		link.setType(MediaType.INFORMER_API_MENSAJE_COLLECTION);

		return link;
	}

	public final static Link buildURIComentario(UriInfo uriInfo, Comentario comentario) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).build();
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel("self");
		link.setTitle("Mensaje " + comentario.getIdentificador());
		link.setType(MediaType.INFORMER_API_MENSAJE);

		return link;
	}

	public final static Link buildURIMensajeId(UriInfo uriInfo, String salaid, int mensajeid, String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(MensajeResource.class).path(MensajeResource.class, "getMensaje").build(salaid, mensajeid);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle("Mensaje " + mensajeid);
		link.setType(MediaType.INFORMER_API_MENSAJE);

		return link;
	}
}