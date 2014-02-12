package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.MediaType;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.UserResource;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.User;

public class UsersAPILinkBuilder {


//	 public final static Link buildURIRootAPI(UriInfo uriInfo) { //getBase ->
//	 //http:blablabla/beeter-api/
//	 URI uriRoot =
//	 uriInfo.getBaseUriBuilder().path(LibrosRootAPIResource.class).build();
//	 Link link = new Link();
//	 link.setUri(uriRoot.toString());
//	 link.setRel("self bookmark"); //apunta a el mismo / pagina inicial
//	 link.setTitle("Libros API");
//	 link.setType(MediaType.LIBROS_API_LINK_COLLECTION); //devolver coleccion
//	 de enlaces
//	
//	 return link;
//	 }

	public static final Link buildURILibros(UriInfo uriInfo, String rel) {
		return buildURIUsers(uriInfo, null, null, rel);
	}

	public static final Link buildURIUsers(UriInfo uriInfo, String offset,
			String length, String rel) {
		URI uriStings;
		if (offset == null && length == null)
			uriStings = uriInfo.getBaseUriBuilder().path(UserResource.class)
					.build(); // devuelve http:blabla/users
		else {
			uriStings = uriInfo.getBaseUriBuilder().path(UserResource.class)
					.queryParam("o", offset).queryParam("l", length).build();
			// devuelve http:blabla/users/servicio?0=X&l=Y

		}

		Link self = new Link();
		self.setUri(uriStings.toString());
		self.setRel(rel);
		self.setTitle("Users collection");
		self.setType(MediaType.INFORMER_API_USER_COLLECTION);

		return self;
	}


	public static final Link buildTemplatedURIUsers(UriInfo uriInfo,
			String rel) {
		URI uriUsers;

		uriUsers = uriInfo.getBaseUriBuilder().path(UserResource.class)
				.queryParam("o", "{offset}").queryParam("l", "{length}")
				.build();

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriUsers));
		link.setRel(rel);

		link.setTitle("User collection resource");
		link.setType(MediaType.INFORMER_API_USER_COLLECTION);

		return link;
	}

	public final static Link buildURILibro(UriInfo uriInfo, User user) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(UserResource.class)
				.build();
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel("self");
		link.setTitle("User " + user.getUsername());
		link.setType(MediaType.INFORMER_API_USER_COLLECTION);

		return link;
	}

	public final static Link buildURIUserName(UriInfo uriInfo, String username,
			String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(UserResource.class)
				.path(UserResource.class, "getUser").build(username);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle("Usuario " + username);
		link.setType(MediaType.INFORMER_API_USER);

		return link;
	}
	
	public final static Link buildURIDeleteUser(UriInfo uriInfo, String username,
			String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(UserResource.class)
				.path(UserResource.class, "deleteUser").build(username);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle("Delete Usuario: " + username);
		link.setType(MediaType.INFORMER_API_USER);

		return link;
	}
	
	public final static Link buildURISolicitud(UriInfo uriInfo, String username,
			String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(UserResource.class)
				.path(UserResource.class, "SolicitudAmigo").build(username);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle(" Solicitud al Usuario " + username);
		link.setType(MediaType.INFORMER_API_USER);

		return link;
	}
	public final static Link buildURIEliminarAmigo(UriInfo uriInfo, String username,
			String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(UserResource.class)
				.path(UserResource.class, "Deletefriend").build(username);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle(" Eliminar Solicitud/Amigo: " + username);
		link.setType(MediaType.INFORMER_API_USER);

		return link;
	}
	public final static Link buildURIAceptarSolicitud(UriInfo uriInfo, String username,
			String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(UserResource.class)
				.path(UserResource.class, "AceptarSolicitud").build(username);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle(" Aceptar Solicitud de: " + username);
		link.setType(MediaType.INFORMER_API_USER);

		return link;
	}

}
