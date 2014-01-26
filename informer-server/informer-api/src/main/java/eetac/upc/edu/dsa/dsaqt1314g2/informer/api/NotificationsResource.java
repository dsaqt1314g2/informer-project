package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.UserNotifications;

@Path("/user/{username}/notifications")
public class NotificationsResource {

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private UserNotifications notificacion = new UserNotifications();

	@GET
	@Produces(MediaType.INFORMER_API_NOTIFICATION)
	public Response getmynotificacion(@PathParam("username") String username,
			@Context Request req) {

		String nombre = security.getUserPrincipal().getName();
		
		if(!nombre.equals(username))
		{
			throw new ForbiddenException("You are nor allowed");
		}
		
		CacheControl cc = new CacheControl();
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();

			String query = "SELECT COUNT(*) AS rowcount FROM amigos  where amigos.username='"
					+ username + "' ;";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {

				notificacion.setNumamigos(rs.getInt("rowcount"));
			}
			rs.close();
			query = "SELECT COUNT(*) AS rowcount FROM comentarios  where comentarios.username='"
					+ username + "' ;";
			rs = stmt.executeQuery(query);
			if (rs.next()) {

				notificacion.setNumcomment(rs.getInt("rowcount"));
			}			
			rs.close();
			query = "SELECT COUNT(*) AS rowcount FROM posts  where posts.username='"
					+ username + "' ;";
			rs = stmt.executeQuery(query);
			if (rs.next()) {

				notificacion.setNumpost(rs.getInt("rowcount"));
			}
			rs.close();
			query = "SELECT COUNT(*) AS rowcount FROM calificacion, posts  where calificacion.estado =1 and calificacion.id_post=posts.identificador and posts.username='"
					+ username + "' ;";
			rs = stmt.executeQuery(query);
			if (rs.next()) {

				notificacion.setNumlikes(rs.getInt("rowcount"));
			}
			rs.close();
			query = "SELECT COUNT(*) AS rowcount FROM calificacion, posts  where calificacion.estado =2 and calificacion.id_post=posts.identificador and posts.username='"
					+ username + "' ;";
			rs = stmt.executeQuery(query);
			if (rs.next()) {

				notificacion.setNumdislikes(rs.getInt("rowcount"));
			}
			rs.close();
			query = "SELECT COUNT(*) AS rowcount FROM calificacion where calificacion.username='"
					+ username + "' ;";
			rs = stmt.executeQuery(query);
			if (rs.next()) {

				notificacion.setParticipacion(rs.getInt("rowcount"));
			}
			rs.close();
			query = "SELECT COUNT(*) AS rowcount FROM amigos where amigos.username='"
					+ username + "' and amigos.estado=0;";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				notificacion.setN_s_amistad(rs.getInt("rowcount"));
			}
			rs.close();
			query = "SELECT COUNT(*) AS rowcount FROM rel_sala_user where rel_sala_user.username='"
					+ username + "' and rel_sala_user.estado=0;";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				notificacion.setN_i_sala(rs.getInt("rowcount"));
			}
			rs.close();

		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(
				Integer.toString(notificacion.hashCode()));

		// Verify if it matched with etag available in http request
		Response.ResponseBuilder rb = req.evaluatePreconditions(eTag);

		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}

		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(notificacion).cacheControl(cc).tag(eTag);

		return rb.build();
	}

}
