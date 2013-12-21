package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.PostsAPILinkBuilder;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Post;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.PostCollection;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Sala;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.SalaCollection;

@Path("/salas")
public class SalasResource {

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	SalaCollection salas = new SalaCollection();

	@GET
	@Produces(MediaType.INFORMER_API_POST_COLLECTION)
	public SalaCollection getmySalas(@QueryParam("o") String offset,
			@QueryParam("l") String length) {
		int ioffset = 0, ilength = 10;
		if ((offset == null) || (length == null)) {
			offset = "0";
			length = "10";
		} else {
			try {
				ioffset = Integer.parseInt(offset);
				if (ioffset < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException(
						"offset must be an integer greater or equal than 0.");
			}
			try {
				ilength = Integer.parseInt(length);
				if (ilength < 1)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException(
						"length must be an integer greater or equal than 0.");
			}

		}

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "SELECT salas_chat.* FROM salas_chat, rel_sala_user  where  and rel_sala_user.id_sala = salas_chat.identificador and ";
			String username = security.getUserPrincipal().getName();
			query += "rel_sala_user.username = '" + username
					+ "' ORDER BY nombre_sala asc LIMIT " + offset + ", "
					+ length + ";";

			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Sala sala = new Sala();
				sala.setIdentificador(rs.getInt("identificacor"));
				sala.setUsername(rs.getString("username"));
				sala.setNombre_sala(rs.getString("nombre_sala"));
				sala.setVisibilidad(rs.getInt("visibilidad"));
				sala.setLast_update(rs.getTimestamp("last_update"));
				sala.setPassword(rs.getNString("password"));

				// TODO Links

				salas.add(sala);
			}
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		int prev = ioffset - ilength;
		int next = ioffset + ilength;

		// TODO Links

		// posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo, offset,
		// length, null, "self"));
		// if (prev > 0)
		// posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo,
		// Integer.toString(prev), length, null, "prev"));
		// posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo,
		// Integer.toString(next), length, null, "next"));
		// TODO: limitar el next
		return salas;
	}

	@GET
	@Path("/{salaid}")
	@Produces(MediaType.INFORMER_API_SALA)
	public Response getPost(@PathParam("salaid") String salaid,
			@Context Request req) {
		// GET: /posts/{postid} (Registered)(admin)

		CacheControl cc = new CacheControl();
		Sala sala = new Sala();
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "SELECT * FROM salas_chat WHERE identificador="
					+ salaid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				sala.setIdentificador(rs.getInt("identificador"));
				sala.setUsername(rs.getString("username"));
				sala.setNombre_sala(rs.getString("nombre_sala"));
				sala.setVisibilidad(rs.getInt("visibilidad"));
				sala.setLast_update(rs.getTimestamp("last_update"));

				if (sala.getVisibilidad() < 1) {
					sala.setPassword(rs.getNString("password"));
				} else {
					sala.setPassword("********");
				}

				// TODO Links

			} else
				throw new SalaNotFoundException();
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
		EntityTag eTag = new EntityTag(Integer.toString(sala.getLast_update()
				.hashCode()));

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
		rb = Response.ok(sala).cacheControl(cc).tag(eTag);

		return rb.build();

	}

	@GET
	@Path("/visible/{categoria}")
	@Produces(MediaType.INFORMER_API_SALA_COLLECTION)
	public SalaCollection getVisibilidad(
			@PathParam("categoria") String categoria,
			@QueryParam("o") String offset, @QueryParam("l") String length,
			@Context Request req) {
		// GETs: /posts/ranking/{categoria} (Registered)(admin)

		int ioffset = 0, ilength = 10, icategoria = 0;
		if ((offset == null) || (length == null)) {
			offset = "0";
			length = "10";
		} else {
			try {
				ioffset = Integer.parseInt(offset);
				if (ioffset < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException(
						"offset must be an integer greater or equal than 0.");
			}
			try {
				ilength = Integer.parseInt(length);
				if (ilength < 1)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException(
						"length must be an integer greater or equal than 0.");
			}
			try {
				icategoria = Integer.parseInt(categoria);
				if (icategoria < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException(
						" categoria must be an integer greater or equal than 0.");
			}
		}

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "";
			if (icategoria == 0)
				query = "SELECT * FROM salas_chat where visibilidad=0 ORDER BY nombre_sala asc LIMIT "
						+ offset + ", " + length + ";";
			else if (icategoria == 1)
				query = "SELECT * FROM salas_chat where visibilidad=1 ORDER BY nombre_sala asc LIMIT "
						+ offset + ", " + length + ";";
			else if (icategoria == 2) {
				String username = security.getUserPrincipal().getName();
				query = "SELECT salas_chat.* FROM salas_chat, rel_sala_user  where salas_chatvisibilidad=2 and rel_sala_user.id_sala = salas_chat.identificador and ";
				query += "rel_sala_user.username = '" + username
						+ "' ORDER BY nombre_sala asc LIMIT " + offset + ", "
						+ length + ";";
			} else if (icategoria == 3) {
				String username = security.getUserPrincipal().getName();
				query = "SELECT salas_chat.* FROM salas_chat, rel_sala_user  where rel_sala_user.id_sala = salas_chat.identificador and rel_sala_user.estado=0 and ";
				query += "rel_sala_user.username = '" + username
						+ "' ORDER BY nombre_sala asc LIMIT " + offset + ", "
						+ length + ";";
			}

			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Sala sala = new Sala();
				sala.setIdentificador(rs.getInt("identificador"));
				sala.setUsername(rs.getString("username"));
				sala.setNombre_sala(rs.getString("nombre_sala"));
				sala.setVisibilidad(rs.getInt("visibilidad"));
				sala.setLast_update(rs.getTimestamp("last_update"));

				if (sala.getVisibilidad() < 1 || icategoria == 3) {
					sala.setPassword(rs.getNString("password"));
				} else {
					sala.setPassword("********");
				}

				// TODO Links

				salas.add(sala);
			}
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		int prev = ioffset - ilength;
		int next = ioffset + ilength;

		// TODO Links

		// posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo, offset,
		// length, null, "self"));
		// if (prev > 0)
		// posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo,
		// Integer.toString(prev), length, null, "prev"));
		// posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo,
		// Integer.toString(next), length, null, "next"));
		// TODO: limitar el next
		return salas;

	}

//	@POST
//	@Consumes(MediaType.INFORMER_API_SALA)
//	@Produces(MediaType.INFORMER_API_SALA)
//	public Post createSala(Post post) {
//		// TODO: POST: /posts (Registered)(admin)
//		if (post.getAsunto().length() > 50)
//			throw new BadRequestException(
//					"Longitud del asunto excede el limite de 50 caracteres.");
//		if (post.getContenido().length() > 2048)
//			throw new BadRequestException(
//					"Longitud del asunto excede el limite de 2048 caracteres.");
//
//		post.setUsername(security.getUserPrincipal().getName());
//
//		Connection con = null;
//		Statement stmt = null;
//		try {
//			con = ds.getConnection();
//		} catch (SQLException e) {
//			throw new ServiceUnavailableException(e.getMessage());
//		}
//
//		try {
//			stmt = con.createStatement();
//			String update = "insert into posts(username, visibilidad, contenido) values ('"
//					+ post.getUsername()
//					+ "','"
//					+ post.getVisibilidad()
//					+ "','" + post.getContenido() + "');";
//			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
//			ResultSet rs = stmt.getGeneratedKeys();
//			if (rs.next()) {
//				int identificador = rs.getInt(1);
//				rs.close();
//
//				rs = stmt
//						.executeQuery("SELECT * FROM posts WHERE identificador='"
//								+ identificador + "';");
//				rs.next();
//
//				post.setIdentificador(rs.getInt("identificador"));
//				post.setUsername(rs.getString("username"));
//				post.setPublicacion_date(rs.getTimestamp("publicacion_date"));
//				post.setNumcomentarios(rs.getInt("numcomentarios"));
//				post.setCalificaciones_positivas(rs
//						.getInt("calificaciones_positivas"));
//				post.setCalificaciones_negativas(rs
//						.getInt("calificaciones_negativas"));
//				post.setRevisado(rs.getInt("revisado"));
//				post.setWho_revised(rs.getInt("who_revisado"));
//				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo,
//						post.getIdentificador(), "self"));
//			} else {
//				throw new PostNotFoundException();
//			}
//		} catch (SQLException e) {
//			throw new InternalServerException(e.getMessage());
//		} finally {
//			try {
//				con.close();
//				stmt.close();
//			} catch (Exception e) {
//			}
//		}
//		return post;
//	}
//
//	@POST
//	@Path("/{postid}/like")
//	@Produces(MediaType.INFORMER_API_POST)
//	public Post likePost(@PathParam("postid") String postid,
//			@QueryParam("l") String like, @QueryParam("d") String dislike) {
//		// POST: /posts/{postid}/like (1=like, 0 dislike) (Registered)(admin)
//		if ((like == null) && (dislike == null))
//			throw new BadRequestException("Formato de datos incorrecto");
//		else if ((like != null) && (dislike != null))
//			throw new BadRequestException("Formato de datos incorrecto");
//		int estado = -1;
//		if (like != null)
//			estado = 1;
//		else
//			estado = 0;
//
//		Post post = new Post();
//		Connection con = null;
//		Statement stmt = null;
//		try {
//			con = ds.getConnection();
//		} catch (SQLException e) {
//			throw new ServiceUnavailableException(e.getMessage());
//		}
//
//		try {
//			stmt = con.createStatement();
//			String username = security.getUserPrincipal().getName();
//			// a ver si existe el post
//			String query = "SELECT COUNT(identificador) FROM posts Where identificador='"
//					+ postid + "';";
//			ResultSet rs = stmt.executeQuery(query);
//			rs.next();
//			if (rs.getInt(1) == 0)
//				throw new PostNotFoundException();
//
//			// ver si ya a denunciado
//			query = "SELECT COUNT(id) FROM calificacion Where id_post='"
//					+ postid + "' and username='" + username + "';";
//			rs = stmt.executeQuery(query);
//			rs.next();
//			if (rs.getInt(1) != 0)
//				throw new LikeAlreadyFoundException();
//
//			// like: estado de las relaciones --> 0=dislike, 1=like
//			String insert = "INSERT INTO calificacion (username,id_post,estado) values ('"
//					+ username + "'," + postid + "," + estado + ");";
//			stmt.executeUpdate(insert);
//
//			// actualizar contador del post
//			String update;
//			if (like != null)
//				update = "UPDATE posts SET posts.calificaciones_positivas=posts.calificaciones_positivas+1 WHERE posts.identificador='"
//						+ postid + "';";
//			else
//				update = "UPDATE posts SET posts.calificaciones_negativas=posts.calificaciones_negativas+1 WHERE posts.identificador='"
//						+ postid + "';";
//			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
//			rs = stmt
//					.executeQuery("SELECT posts.calificaciones_positivas, posts.calificaciones_negativas FROM posts WHERE posts.identificador='"
//							+ postid + "';");
//			if (rs.next()) {
//				post.setIdentificador(Integer.parseInt(postid));
//				post.setCalificaciones_positivas(rs
//						.getInt("calificaciones_positivas"));
//				post.setCalificaciones_negativas(rs
//						.getInt("calificaciones_negativas"));
//				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo,
//						post.getIdentificador(), "self"));
//			} else {
//				throw new PostNotFoundException();
//			}
//		} catch (SQLException e) {
//			throw new InternalServerException(e.getMessage());
//		} finally {
//			try {
//				con.close();
//				stmt.close();
//			} catch (Exception e) {
//			}
//		}
//		return post;
//	}
//
//	@POST
//	@Path("/{postid}/denunciar")
//	// @Produces(MediaType.INFORMER_API_POST)
//	public void denunciaPost(@PathParam("postid") String postid,
//			@QueryParam("d") String denuncia) {
//		// POST: /posts/{postid}/denunciar (1=denunciar, 0 desdenunciar)
//		// (Registered)(admin)
//
//		if (denuncia == null)
//			throw new BadRequestException("Formato de datos incorrecto");
//		Post post = new Post();
//		Connection con = null;
//		Statement stmt = null;
//		try {
//			con = ds.getConnection();
//		} catch (SQLException e) {
//			throw new ServiceUnavailableException(e.getMessage());
//		}
//
//		try {
//			stmt = con.createStatement();
//			String username = security.getUserPrincipal().getName();
//			String query = "SELECT COUNT(identificador) FROM posts Where identificador='"
//					+ postid + "';";
//			ResultSet rs = stmt.executeQuery(query);
//			rs.next();
//			if (rs.getInt(1) == 0)
//				throw new PostNotFoundException();
//			// ver si ya a denunciado
//			query = "SELECT COUNT(id) FROM denuncias_post Where id_post='"
//					+ postid + "' and username='" + username + "';";
//			rs = stmt.executeQuery(query);
//			rs.next();
//			if (rs.getInt(1) != 0)
//				throw new DenunciaAlreadyFoundException();
//
//			// denunciar
//			String insert = "INSERT INTO denuncias_post (username,id_post) values ('"
//					+ username + "'," + postid + ");";
//			stmt.executeUpdate(insert);
//
//			// comprobar si hay mas de 20 denuncias y cambiar visibilidad a 5 si
//			// los hay
//			int MAX_DENUNCIAS = 20;
//			query = "SELECT COUNT(id) FROM denuncias_post WHERE id_post='"
//					+ postid + "';";
//			rs = stmt.executeQuery(query);
//			rs.next();
//			if (rs.getInt(1) >= MAX_DENUNCIAS) {
//				insert = "UPDATE posts SET visibilidad=5 WHERE identificador='"
//						+ postid + "';";
//				post.setVisibilidad(5);
//				stmt.executeUpdate(insert);
//			}
//		} catch (SQLException e) {
//			throw new InternalServerException(e.getMessage());
//		} finally {
//			try {
//				con.close();
//				stmt.close();
//			} catch (Exception e) {
//			}
//		}
//		// return post;
//	}
//
//	@PUT
//	@Path("/{postid}/moderar")
//	public void moderarPost(@PathParam("postid") String postid) {
//		// TODO: PUT: /posts/{postid}/moderar (admin) => revisado y who_revisado
//
//		// TODO: Cambiar rol a moderador
//		// if (security.isUserInRole("registered")) {
//		// throw new ForbiddenException("You are not allowed...");
//		// }
//
//		Connection con = null;
//		Statement stmt = null;
//		try {
//			con = ds.getConnection();
//		} catch (SQLException e) {
//			throw new ServiceUnavailableException(e.getMessage());
//		}
//		try {
//			stmt = con.createStatement();
//			// comprobar que el que modifica es quien ha creado el post
//			String username = security.getUserPrincipal().getName();
//			// String username = "ropnom";
//
//			String update = "UPDATE posts SET revisado=revisado+1, who_revisado='"
//					+ username + "' WHERE identificador=" + postid + ";";
//			stmt.executeUpdate(update);
//		} catch (SQLException e) {
//			throw new InternalServerException(e.getMessage());
//		} finally {
//			try {
//				con.close();
//				stmt.close();
//			} catch (Exception e) {
//			}
//		}
//		return;
//	}
//
//	@PUT
//	@Path("/{postid}")
//	@Consumes(MediaType.INFORMER_API_POST)
//	@Produces(MediaType.INFORMER_API_POST)
//	public Post updatePost(@PathParam("postid") String postid, Post post) {
//		// TODO: PUT: /posts/{postid} (Registered-Propietario) => visibilidad.
//		if (post.getVisibilidad() < 0 || post.getVisibilidad() > 2)
//			throw new BadRequestException("Visibilidad incorrecta.");
//		Connection con = null;
//		Statement stmt = null;
//		try {
//			con = ds.getConnection();
//		} catch (SQLException e) {
//			throw new ServiceUnavailableException(e.getMessage());
//		}
//		try {
//			stmt = con.createStatement();
//			// comprobar que el que modifica es quien ha creado el post
//			String username = security.getUserPrincipal().getName();
//			// String username = "ropnom";
//			String update = "UPDATE posts SET visibilidad="
//					+ post.getVisibilidad() + " WHERE identificador=" + postid
//					+ " and username='" + username + "';";
//			stmt.executeUpdate(update);
//		} catch (SQLException e) {
//			throw new InternalServerException(e.getMessage());
//		} finally {
//			try {
//				con.close();
//				stmt.close();
//			} catch (Exception e) {
//			}
//		}
//		post.setIdentificador(Integer.parseInt(postid));
//		post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo,
//				post.getIdentificador(), "self"));
//		return post;
//	}
//
//	@DELETE
//	@Path("/{postid}")
//	public void deleteComentario(@PathParam("postid") String postid) {
//		// TODO: DELETE: /posts/{postid} (admin)
//		Connection con = null;
//		Statement stmt = null;
//		try {
//			con = ds.getConnection();
//		} catch (SQLException e) {
//			throw new ServiceUnavailableException(e.getMessage());
//		}
//		try {
//			stmt = con.createStatement();
//			String query = "DELETE FROM posts WHERE identificador=" + postid
//					+ ";";
//			int rows = stmt.executeUpdate(query);
//			if (rows == 0)
//				throw new PostNotFoundException();
//		} catch (SQLException e) {
//			throw new InternalServerException(e.getMessage());
//		} finally {
//			try {
//				con.close();
//				stmt.close();
//			} catch (Exception e) {
//			}
//		}
//	}
}
