package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Post;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.PostCollection;

@Path("/posts")
public class PostResource {

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	PostCollection posts = new PostCollection();

	@GET
	@Produces(MediaType.INFORMER_API_POST_COLLECTION)
	public PostCollection getPosts() {
		// TODO: GETs: /posts?{offset}{length} (Registered)(admin)
		return posts;
	}

	@GET
	@Path("/{postid}")
	@Produces(MediaType.INFORMER_API_POST)
	public Response getPost(@PathParam("postid") String postid, @Context Request req) {
		// TODO: GET: /posts/{postid} (Registered)(admin)
		
	}

	@GET
	@Path("/ranking/{categoria}")
	@Produces(MediaType.INFORMER_API_POST)
	public Response getRanking(@PathParam("categoria") String categoria, @Context Request req) {
		// TODO: GETs: /posts/ranking/{categoria} (Registered)(admin)
		
	}

	@POST
	@Consumes(MediaType.INFORMER_API_POST)
	@Produces(MediaType.INFORMER_API_POST)
	public Post createPost(Post post) {
		// TODO: POST: /posts (Registered)(admin)
		return post;
	}

	@POST
	@Path("/{postid}/like")
	@Consumes(MediaType.INFORMER_API_POST)
	@Produces(MediaType.INFORMER_API_POST)
	public Post likePost() {
		// TODO: POST: /posts/{postid}/like (1=like, 0 dislike)
		// (Registered)(admin)

	}

	@POST
	@Path("/{postid}/denunciar")
	@Consumes(MediaType.INFORMER_API_POST)
	@Produces(MediaType.INFORMER_API_POST)
	public Post denunciaPost() {
		// TODO: POST: /posts/{postid}/denunciar (1=denunciar, 0 desdenunciar) (Registered)(admin)

	}

	@PUT
	@Path("/{postid}/moderar")
	@Consumes(MediaType.INFORMER_API_POST)
	@Produces(MediaType.INFORMER_API_POST)
	public Post updatePost(@PathParam("postid") String postid, Post post) {
		// TODO: PUT: /posts/{postid} (Registered-Propietario) => visibilidad.
		return post;
	}

	@PUT
	@Path("/{postid}")
	@Consumes(MediaType.INFORMER_API_POST)
	@Produces(MediaType.INFORMER_API_POST)
	public Post moderarPost(@PathParam("postid") String postid, Post post) {
		// TODO: PUT: /posts/{postid}/moderar (admin) => revisado y who_revisado
		return post;
	}

	@DELETE
	@Path("/{postid}")
	public void deleteComentario(@PathParam("postid") String postid) {
		// TODO: DELETE: /posts/{postid} (admin)
	}
}
