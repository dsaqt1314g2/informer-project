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

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Comentario;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.MensajeCollection;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Post;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.PostCollection;

@Path("/posts/{postid}/comentarios")
public class ComentarioResource {

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	MensajeCollection mensajes = new MensajeCollection();

	@GET
	@Produces(MediaType.INFORMER_API_MENSAJE_COLLECTION)
	public MensajeCollection getPosts() {
		// TODO: GETs: /posts/{postid}/comentarios?{offset}{length}  (Registered)(admin)
		return mensajes;
	}

	@GET
	@Path("/{comentarioid}")
	@Produces(MediaType.INFORMER_API_MENSAJE)
	public Response getComentario(@PathParam("comentarioid") String comentarioid, @Context Request req) {
		// TODO: GET: /posts/{postid}/comentarios/{comentarioid}   (Registered)(admin)		
	}

	@POST
	@Consumes(MediaType.INFORMER_API_MENSAJE)
	@Produces(MediaType.INFORMER_API_MENSAJE)
	public Comentario createComentario(Comentario comentario) {
		// TODO: POST: /posts{postid}/comentarios/   (Registered)(admin)
		return comentario;
	}

	@POST
	@Path("/{comentarioid}/denunciar")
	@Consumes(MediaType.INFORMER_API_MENSAJE)
	@Produces(MediaType.INFORMER_API_MENSAJE)
	public Post denunciaComentario() {
		// TODO: POST: /posts/{postid}/comentarios/{comentarioid} /denunciar (1=denunciar, 0 desdenunciar)  (Registered)
	}

	@PUT
	@Path("/{comentarioid}")
	@Consumes(MediaType.INFORMER_API_MENSAJE)
	@Produces(MediaType.INFORMER_API_MENSAJE)
	public Comentario updateComentario(@PathParam("comentarioid") String comentarioid, Comentario comentario) {
		// TODO: PUT: /posts/{postid}/comentarios/{comentarioid}  (Registered-Propietario) => visibilidad.
		return comentario;
	}

	@PUT
	@Path("/{comentarioid}/moderar")
	@Consumes(MediaType.INFORMER_API_MENSAJE)
	@Produces(MediaType.INFORMER_API_MENSAJE)
	public Comentario moderarComentario(@PathParam("comentarioid") String comentarioid, Comentario comentario) {
		// TODO: PUT: /posts/{postid}/comentarios/{comentarioid}/moderar   (admin) => revisado y who_revisado
		return comentario;
	}

	@DELETE
	@Path("/{comentarioid}")
	public void deleteComentario(@PathParam("postid") String postid) {
		// TODO: DELETE: /posts/{postid}/commentarios/{comentarioid} (Registered-Propietario)
	}
}
