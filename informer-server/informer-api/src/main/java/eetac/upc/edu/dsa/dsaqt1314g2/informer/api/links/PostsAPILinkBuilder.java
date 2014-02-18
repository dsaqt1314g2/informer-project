package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.InformerRootAPIResource;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.MediaType;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.PostResource;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Post;

public class PostsAPILinkBuilder {
	public final static Link buildURIRootAPI(UriInfo uriInfo) { // getBase ->
																// http:blablabla/beeter-api/
		URI uriRoot = uriInfo.getBaseUriBuilder().path(InformerRootAPIResource.class).build();
		Link link = new Link();
		link.setUri(uriRoot.toString());
		link.setRel("self bookmark"); // apunta a el mismo / pagina inicial
		link.setTitle("Libros API");
		link.setType(MediaType.INFORMER_API_LINK_COLLECTION); // devolver
																// coleccion de
																// enlaces

		return link;
	}

	public static final Link buildURIPosts(UriInfo uriInfo, String rel) {
		return buildURIPosts(uriInfo, null, null, null, rel);
	}

	public static final Link buildURIPosts(UriInfo uriInfo, String offset, String length, String username, String rel) {
		URI uriStings;
		if (offset == null && length == null)
			uriStings = uriInfo.getBaseUriBuilder().path(PostResource.class).build(); // devuelve
																						// http:blabla/stings
		else {
			// if (username == null)
			uriStings = uriInfo.getBaseUriBuilder().path(PostResource.class).queryParam("o", offset).queryParam("l", length).build();
			// else
			// uriStings =
			// uriInfo.getBaseUriBuilder().path(PostResource.class).queryParam("offset",
			// offset).queryParam("length", length)
			// .queryParam("username", username).build();
		}

		Link self = new Link();
		self.setUri(uriStings.toString());
		self.setRel(rel);
		self.setTitle("Post collection");
		self.setType(MediaType.INFORMER_API_POST_COLLECTION);

		return self;
	}

	public static final Link buildURIPostsDenunciados(UriInfo uriInfo, int offset, int length, String rel) {
		URI uriStings;
		// if (offset == null && length == null)
		// uriStings =
		// uriInfo.getBaseUriBuilder().path(PostResource.class).build(); //
		// devuelve
		// // http:blabla/stings
		// else {
		// if (username == null)
		uriStings = uriInfo.getBaseUriBuilder().path(PostResource.class).queryParam("o", offset).queryParam("l", length).build();
		// else
		// uriStings =
		// uriInfo.getBaseUriBuilder().path(PostResource.class).queryParam("offset",
		// offset).queryParam("length", length)
		// .queryParam("username", username).build();
		// }

		Link self = new Link();
		self.setUri(uriStings.toString());
		self.setRel(rel);
		self.setTitle("Post collection");
		self.setType(MediaType.INFORMER_API_POST_COLLECTION);

		return self;
	}

	public static final Link buildTemplatedURIPosts(UriInfo uriInfo, String rel) {
		return buildTemplatedURIPosts(uriInfo, rel, false);
	}

	public static final Link buildTemplatedURIPosts(UriInfo uriInfo, String rel, boolean username) {
		URI uriPosts;
		// if (username)
		// uriPosts =
		// uriInfo.getBaseUriBuilder().path(LibroResource.class).queryParam("offset",
		// "{offset}").queryParam("length", "{length}")
		// .queryParam("username", "{username}").build();
		// else
		uriPosts = uriInfo.getBaseUriBuilder().path(PostResource.class).queryParam("o", "{offset}").queryParam("l", "{length}").build();

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriPosts));
		link.setRel(rel);
		// if (username)
		// link.setTitle("Post collection resource filtered by {username}");
		// else
		link.setTitle("Post collection resource");
		link.setType(MediaType.INFORMER_API_POST);

		return link;
	}

	public final static Link buildURIPost(UriInfo uriInfo, Post post) {
		URI postURI = uriInfo.getBaseUriBuilder().path(PostResource.class).build();
		Link link = new Link();
		link.setUri(postURI.toString());
		link.setRel("self");
		link.setTitle("Post " + post.getIdentificador());
		link.setType(MediaType.INFORMER_API_POST);
		return link;
	}

	public final static Link buildURIPostId(UriInfo uriInfo, int postid, String rel) {
		URI postURI = uriInfo.getBaseUriBuilder().path(PostResource.class).path(PostResource.class, "getPost").build(postid);
		Link link = new Link();
		link.setUri(postURI.toString());
		link.setRel(rel);
		link.setTitle("Post " + postid);
		link.setType(MediaType.INFORMER_API_POST);
		return link;
	}

	public final static Link buildURILikePostId(UriInfo uriInfo, int postid, String rel) {
		URI postURI = uriInfo.getBaseUriBuilder().path(PostResource.class).path(PostResource.class, "likePost").build(postid);
		Link link = new Link();
		link.setUri(postURI.toString());
		link.setRel(rel);
		link.setTitle("Post " + postid);
		// link.setType(MediaType.INFORMER_API_POST);
		return link;
	}

	public final static Link buildURIDislikePostId(UriInfo uriInfo, int postid, String rel) {
		URI postURI = uriInfo.getBaseUriBuilder().path(PostResource.class).path(PostResource.class, "dislikePost").build(postid);
		Link link = new Link();
		link.setUri(postURI.toString());
		link.setRel(rel);
		link.setTitle("Post " + postid);
		// link.setType(MediaType.INFORMER_API_POST);
		return link;
	}

	public final static Link buildURINeutroPostId(UriInfo uriInfo, int postid, String rel) {
		URI postURI = uriInfo.getBaseUriBuilder().path(PostResource.class).path(PostResource.class, "eliminarVoto").build(postid);
		Link link = new Link();
		link.setUri(postURI.toString());
		link.setRel(rel);
		link.setTitle("Post " + postid);
		// link.setType(MediaType.INFORMER_API_POST);
		return link;
	}

	public final static Link buildURIDenunciarPostId(UriInfo uriInfo, int postid, String rel) {
		URI postURI = uriInfo.getBaseUriBuilder().path(PostResource.class).path(PostResource.class, "denunciaPost").build(postid);
		Link link = new Link();
		link.setUri(postURI.toString());
		link.setRel(rel);
		link.setTitle("Post " + postid);
		// link.setType(MediaType.INFORMER_API_POST);
		return link;
	}

	public final static Link buildURIRankingPosts(UriInfo uriInfo, String rank, int offset, int length, String rel) {
		URI postURI = uriInfo.getBaseUriBuilder().path(PostResource.class).path(PostResource.class, "getRanking").queryParam("o", offset).queryParam("l", length).build(rank);
		Link link = new Link();
		link.setUri(postURI.toString());
		link.setRel(rel);
		link.setTitle("Ranking " + rank);
		link.setType(MediaType.INFORMER_API_POST_COLLECTION);
		return link;
	}

	public final static Link buildURIModificarPostId(UriInfo uriInfo, int postid, String rel) {
		URI postURI = uriInfo.getBaseUriBuilder().path(PostResource.class).path(PostResource.class, "updatePost").build(postid);
		Link link = new Link();
		link.setUri(postURI.toString());
		link.setRel(rel);
		link.setTitle("Post " + postid);
		link.setType(MediaType.INFORMER_API_POST);
		return link;
	}

	public final static Link buildURIDeletePostId(UriInfo uriInfo, int postid, String rel) {
		URI postURI = uriInfo.getBaseUriBuilder().path(PostResource.class).path(PostResource.class, "deletePost").build(postid);
		Link link = new Link();
		link.setUri(postURI.toString());
		link.setRel(rel);
		link.setTitle("Post " + postid);
		// link.setType(MediaType.INFORMER_API_POST);
		return link;
	}

}