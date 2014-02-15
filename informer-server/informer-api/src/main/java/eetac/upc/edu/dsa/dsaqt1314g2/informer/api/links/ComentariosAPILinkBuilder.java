package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.ComentarioResource;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.ComentariosRootAPIResource;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.MediaType;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Comentario;

public class ComentariosAPILinkBuilder {
	public final static Link buildURIRootAPI(UriInfo uriInfo) { // getBase ->
																// http:blablabla/beeter-api/
		URI uriRoot = uriInfo.getBaseUriBuilder().path(ComentariosRootAPIResource.class).build();
		Link link = new Link();
		link.setUri(uriRoot.toString());
		link.setRel("self bookmark"); // apunta a el mismo / pagina inicial
		link.setTitle("Libros API");
		link.setType(MediaType.INFORMER_API_LINK_COLLECTION); // devolver
																// coleccion de
																// enlaces

		return link;
	}

	public static final Link buildURIComentarios(UriInfo uriInfo, String rel) {
		return buildURIComentarios(uriInfo, 0, null, null, rel);
	}

	public static final Link buildURIComentarios(UriInfo uriInfo, int offset, String length, String postid, String rel) {
		//URI uriStings = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).queryParam("o", offset).queryParam("l", length).build(postid);
		URI uriStings = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).build(postid);
		Link self = new Link();
		self.setUri(uriStings.toString());
		self.setRel(rel);
		self.setTitle("Comentario collection");
		self.setType(MediaType.INFORMER_API_COMENTARIO_COLLECTION);

		return self;
	}

	public static final Link buildTemplatedURIComentarios(UriInfo uriInfo, String rel) {

		return buildTemplatedURIComentarios(uriInfo, rel, false);
	}

	public static final Link buildTemplatedURIComentarios(UriInfo uriInfo, String rel, boolean username) {
		URI uriStings;
		if (username)
			uriStings = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).queryParam("offset", "{offset}").queryParam("length", "{length}").queryParam("username", "{username}").build();
		else
			uriStings = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).queryParam("offset", "{offset}").queryParam("length", "{length}").build();

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriStings));
		link.setRel(rel);
		if (username)
			link.setTitle("Comentario collection resource filtered by {username}");
		else
			link.setTitle("Comentario collection resource");
		link.setType(MediaType.INFORMER_API_COMENTARIO_COLLECTION);

		return link;
	}

	public final static Link buildURIComentario(UriInfo uriInfo, Comentario comentario) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).build();
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel("self");
		link.setTitle("Comentario " + comentario.getIdentificador());
		link.setType(MediaType.INFORMER_API_COMENTARIO);

		return link;
	}

	public final static Link buildURIComentarioId(UriInfo uriInfo, String postid, int comentarioid, String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).path(ComentarioResource.class, "getComentario").build(postid, comentarioid);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle("Comentario " + comentarioid);
		link.setType(MediaType.INFORMER_API_COMENTARIO);

		return link;
	}

	public final static Link buildURIDenunciarComentarioId(UriInfo uriInfo, String postid, int comentarioid, String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).path(ComentarioResource.class, "denunciaComentario").build(postid, comentarioid);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle("Comentario " + comentarioid);
		return link;
	}

	public final static Link buildURIModificarComentarioId(UriInfo uriInfo, String postid, int comentarioid, String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).path(ComentarioResource.class, "getComentario").build(postid, comentarioid);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle("Comentario " + comentarioid);
		link.setType(MediaType.INFORMER_API_COMENTARIO);

		return link;
	}

	public final static Link buildURIDeleteComentarioId(UriInfo uriInfo, String postid, int comentarioid, String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(ComentarioResource.class).path(ComentarioResource.class, "getComentario").build(postid, comentarioid);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle("Comentario " + comentarioid);
		link.setType(MediaType.INFORMER_API_COMENTARIO);

		return link;
	}

}