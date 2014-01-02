package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
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

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.ComentariosAPILinkBuilder;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Comentario;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.ComentarioCollection;

@Path("/posts/{postid}/comentarios")
public class ComentarioResource {

	private final static String anonymous = "Anónimo";

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	ComentarioCollection comentarios = new ComentarioCollection();

	@GET
	@Produces(MediaType.INFORMER_API_COMENTARIO_COLLECTION)
	public ComentarioCollection getComentarios(@PathParam("postid") String postid, @QueryParam("o") String offset, @QueryParam("l") String length) {
		// GETs: /posts?{offset}{length} (Registered)(admin)
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
		}
		int ioffset = 0, ilength = 10;
		if (offset == null)
			offset = "0";
		else {
			try {
				ioffset = Integer.parseInt(offset);
				if (ioffset < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException("Offset debe ser un entero mayor o igual a 0.");
			}
		}
		if (length == null)
			length = "10";
		else {
			try {
				ilength = Integer.parseInt(length);
				if (ilength < 1)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException("Length debe ser un entero mayor o igual a 0.");
			}
		}

		// Sting for each one and store them in the StingCollection.
		Connection con = null;
		Statement stmt = null;
		String username = security.getUserPrincipal().getName();
		int comentarios_encontrados = 0;
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			String query = "SELECT amigos.friend, comentarios.*, posts.visibilidad FROM comentarios LEFT JOIN amigos ON amigos.friend='" + username
					+ "' and amigos.username=comentarios.username and amigos.estado=1 LEFT JOIN posts ON comentarios.id_post=posts.identificador WHERE comentarios.id_post=" + postid
					+ " and posts.visibilidad<3 and comentarios.identificador NOT IN(SELECT id_comentario FROM comentarios,denuncias_comentario WHERE denuncias_comentario.id_comentario=comentarios.identificador and denuncias_comentario.username='" + username
					+ "')ORDER BY comentarios.publicacion_date desc LIMIT " + offset + ", " + (ilength + 1) + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (comentarios_encontrados++ == ilength)
					break;
				Comentario c = new Comentario();
				c.setIdentificador(rs.getInt("identificador"));
				c.setId_post(rs.getInt("id_post"));
				c.setVisibilidad(rs.getInt("visibilidad"));
				c.setContenido(rs.getString("contenido"));
				c.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				c.setRevisado(rs.getInt("revisado"));
				c.setWho_revisado(rs.getString("who_revisado"));
				c.setUsername(comentarioAnonimo(username, rs.getString("username"), rs.getString("friend"), c.getVisibilidad()));
				if (c.getIdentificador() != 1)
					c.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, c.getIdentificador() - 1, "prev"));
				c.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, c.getIdentificador(), "self"));
				c.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, c.getIdentificador() + 1, "next"));
				c.addLink(ComentariosAPILinkBuilder.buildURIDenunciarComentarioId(uriInfo, postid, c.getIdentificador(), "denunciar"));
				if (username.equals(c.getUsername()) || security.isUserInRole("moderador"))
					c.addLink(ComentariosAPILinkBuilder.buildURIModificarComentarioId(uriInfo, postid, c.getIdentificador(), "modificar"));
				if (username.equals(c.getUsername()) || security.isUserInRole("moderador"))
					c.addLink(ComentariosAPILinkBuilder.buildURIDeleteComentarioId(uriInfo, postid, c.getIdentificador(), "eliminar"));
				comentarios.add(c);
			}
			rs.close();
			if (comentarios_encontrados == 0)
				throw new ComentarioCollectionNotFoundException();
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
		if (prev >= 0)
			comentarios.addLink(ComentariosAPILinkBuilder.buildURIComentarios(uriInfo, prev, length, postid, "prev"));
		comentarios.addLink(ComentariosAPILinkBuilder.buildURIComentarios(uriInfo, ioffset, length, postid, "self"));
		if (comentarios_encontrados > ilength)
			comentarios.addLink(ComentariosAPILinkBuilder.buildURIComentarios(uriInfo, next, length, postid, "next"));
		return comentarios;
	}

	@GET
	@Path("/{comentarioid}")
	@Produces(MediaType.INFORMER_API_COMENTARIO)
	public Response getComentario(@PathParam("comentarioid") String comentarioid, @PathParam("postid") String postid, @Context Request req) {
		// GET: /posts/{postid}/comentarios/{comentarioid} (Registered)(admin)
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
		}
		CacheControl cc = new CacheControl();
		Connection con = null;
		Statement stmt = null;
		String username = security.getUserPrincipal().getName();
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		Comentario c = new Comentario();
		try {
			stmt = con.createStatement();
			String query = "SELECT amigos.friend, comentarios.*, posts.visibilidad, denuncias_comentario.id_comentario FROM comentarios LEFT JOIN amigos ON amigos.friend='" + username
					+ "' and amigos.username=comentarios.username and amigos.estado=1 LEFT JOIN posts ON comentarios.id_post=posts.identificador LEFT JOIN denuncias_comentario ON denuncias_comentario.id_comentario=comentarios.identificador and denuncias_comentario.username='" + username
					+ "' WHERE comentarios.id_post=" + postid + " and comentarios.identificador=" + comentarioid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				if (rs.getInt("id_comentario") == rs.getInt("identificador"))
					throw new ComentarioDenunciadoException();
				if (rs.getInt("visibilidad") > 9)
					throw new ComentarioPendienteDeRevisionException();
				if (rs.getInt("visibilidad") == 3)
					throw new ComentarioNotFoundException();
				c.setIdentificador(rs.getInt("identificador"));
				c.setId_post(rs.getInt("id_post"));
				c.setVisibilidad(rs.getInt("visibilidad"));
				c.setContenido(rs.getString("contenido"));
				c.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				c.setRevisado(rs.getInt("revisado"));
				c.setWho_revisado(rs.getString("who_revisado"));
				c.setUsername(comentarioAnonimo(username, rs.getString("username"), rs.getString("friend"), c.getVisibilidad()));
				if (c.getIdentificador() != 1)
					c.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, c.getIdentificador() - 1, "prev"));
				c.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, c.getIdentificador(), "self"));
				c.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, c.getIdentificador() + 1, "next"));
				c.addLink(ComentariosAPILinkBuilder.buildURIDenunciarComentarioId(uriInfo, postid, c.getIdentificador(), "denunciar"));
				if (username.equals(c.getUsername()) || security.isUserInRole("moderador"))
					c.addLink(ComentariosAPILinkBuilder.buildURIModificarComentarioId(uriInfo, postid, c.getIdentificador(), "modificar"));
				if (username.equals(c.getUsername()) || security.isUserInRole("moderador"))
					c.addLink(ComentariosAPILinkBuilder.buildURIDeleteComentarioId(uriInfo, postid, c.getIdentificador(), "eliminar"));
			} else
				throw new ComentarioNotFoundException();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		EntityTag eTag = new EntityTag(Integer.toString(c.getPublicacion_date().hashCode()));
		Response.ResponseBuilder rb = req.evaluatePreconditions(eTag);
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
		rb = Response.ok(c).cacheControl(cc).tag(eTag);
		return rb.build();
	}

	@POST
	@Consumes(MediaType.INFORMER_API_COMENTARIO)
	@Produces(MediaType.INFORMER_API_COMENTARIO)
	public Comentario createComentario(@PathParam("postid") String postid, Comentario comentario) {
		// TODO: POST: /posts{postid}/comentarios/ (Registered)(admin)
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
		}
		if (comentario.getContenido().length() > 2048)
			throw new BadRequestException("Longitud del comentario excede el limite de 2048 caracteres.");
		if (comentario.getContenido().length() < 3)
			throw new BadRequestException("Longitud del comentario muy pequeña.");
		if (comentario.getVisibilidad() < 0 || comentario.getVisibilidad() > 2)
			throw new BadRequestException("Visibilidad incorrecta.");
		comentario.setUsername(security.getUserPrincipal().getName());

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			stmt = con.createStatement();
			String query = "SELECT COUNT(identificador) FROM posts Where identificador='" + postid + "';";
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) == 0)
				throw new PostNotFoundException();
			String update = "UPDATE posts SET numcomentarios=numcomentarios+1 WHERE identificador=" + postid + ";";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			update = "INSERT INTO comentarios (id_post,username,visibilidad,contenido) VALUES (" + postid + ", '" + comentario.getUsername() + "', " + comentario.getVisibilidad() + ", '" + comentario.getContenido().replace("'", "´") + "')";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			con.commit();
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int identificador = rs.getInt(1);
				rs.close();
				rs = stmt.executeQuery("SELECT * FROM comentarios WHERE identificador='" + identificador + "';");
				rs.next();
				comentario.setIdentificador(rs.getInt("identificador"));
				comentario.setId_post(rs.getInt("id_post"));
				comentario.setUsername(rs.getString("username"));
				comentario.setVisibilidad(rs.getInt("visibilidad"));
				comentario.setContenido(rs.getString("contenido"));
				comentario.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				comentario.setRevisado(rs.getInt("revisado"));
				comentario.setWho_revisado(rs.getString("who_revisado"));
				if (comentario.getIdentificador() != 1)
					comentario.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, comentario.getIdentificador() - 1, "prev"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, comentario.getIdentificador(), "self"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, comentario.getIdentificador() + 1, "next"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIDenunciarComentarioId(uriInfo, postid, comentario.getIdentificador(), "denunciar"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIModificarComentarioId(uriInfo, postid, comentario.getIdentificador(), "modificar"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIDeleteComentarioId(uriInfo, postid, comentario.getIdentificador(), "eliminar"));
			} else {
				throw new ComentarioNotFoundException();
			}
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new ServiceUnavailableException(e.getMessage());
			}
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return comentario;
	}

	@POST
	@Path("/{comentarioid}/denunciar")
	public void denunciaComentario(@PathParam("postid") String postid, @PathParam("comentarioid") String comentarioid) {
		// POST: /posts/{postid}/comentarios/{comentarioid} /denunciar
		// (1=denunciar, 0 desdenunciar) (Registered)
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
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
			String username = security.getUserPrincipal().getName();
			String query = "SELECT COUNT(identificador) FROM posts Where identificador='" + postid + "';";
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) == 0)
				throw new PostNotFoundException();
			query = "SELECT COUNT(identificador) FROM comentarios Where identificador='" + comentarioid + "';";
			rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) == 0)
				throw new ComentarioNotFoundException();
			// ver si ya a denunciado
			query = "SELECT COUNT(id) FROM denuncias_comentario Where id_comentario='" + comentarioid + "' and username='" + username + "';";
			rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) != 0)
				throw new DenunciaAlreadyFoundException();

			// denunciar
			String insert = "INSERT INTO denuncias_comentario (username,id_comentario) values ('" + username + "'," + comentarioid + ");";
			stmt.executeUpdate(insert);

			// comprobar si hay mas de 20 denuncias y cambiar visibilidad a 5 si
			// los hay
			int MAX_DENUNCIAS = 20;
			query = "SELECT COUNT(id), revisado FROM denuncias_comentario, comentarios WHERE id_comentario='" + comentarioid + "' and id_comentario=comentarios.identificador;";
			rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) >= MAX_DENUNCIAS * (rs.getInt(2) + 1)) {
				insert = "UPDATE comentarios SET visibilidad=visibilidad+10 WHERE identificador='" + comentarioid + "';";
				stmt.executeUpdate(insert);
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
	}

	@PUT
	@Path("/{comentarioid}")
	@Consumes(MediaType.INFORMER_API_COMENTARIO)
	@Produces(MediaType.INFORMER_API_COMENTARIO)
	public Comentario updateComentario(@PathParam("comentarioid") String comentarioid, @PathParam("postid") String postid, Comentario comentario) {
		// PUT: /posts/{postid}/comentarios/{comentarioid}
		// (Registered-Propietario) => visibilidad.
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
		}
		if (comentario.getVisibilidad() < 0 || comentario.getVisibilidad() > 2)
			throw new BadRequestException("Visibilidad incorrecta.");
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			// comprobar que el que modifica es quien ha creado el post
			String username = security.getUserPrincipal().getName();
			String query = "SELECT 1 FROM posts WHERE identificador='" + postid + "';";
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new PostNotFoundException();

			query = "SELECT 1 FROM comentarios, posts WHERE comentarios.identificador='" + comentarioid + "' and comentarios.identificador=posts.identificador;";
			rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new ComentarioNotFoundException();

			String update = "UPDATE comentarios SET visibilidad=" + comentario.getVisibilidad() + " WHERE identificador=" + comentarioid + " and username='" + username + "';";
			int lineas_afectadas = stmt.executeUpdate(update);
			if (lineas_afectadas == 0)
				throw new ComentarioNotYoursException();

			query = "SELECT amigos.friend, comentarios.* FROM comentarios LEFT JOIN amigos ON amigos.friend='" + username + "' and amigos.username=comentarios.username and amigos.estado=1 WHERE id_post=" + postid + " and identificador=" + comentarioid + ";";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				comentario.setIdentificador(rs.getInt("identificador"));
				comentario.setId_post(rs.getInt("id_post"));
				comentario.setVisibilidad(rs.getInt("visibilidad"));
				comentario.setContenido(rs.getString("contenido"));
				comentario.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				comentario.setRevisado(rs.getInt("revisado"));
				comentario.setWho_revisado(rs.getString("who_revisado"));
				comentario.setUsername(comentarioAnonimo(username, rs.getString("username"), rs.getString("friend"), comentario.getVisibilidad()));
				if (comentario.getIdentificador() != 1)
					comentario.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, comentario.getIdentificador() - 1, "prev"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, comentario.getIdentificador(), "self"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, postid, comentario.getIdentificador() + 1, "next"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIDenunciarComentarioId(uriInfo, postid, comentario.getIdentificador(), "denunciar"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIModificarComentarioId(uriInfo, postid, comentario.getIdentificador(), "modificar"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIDeleteComentarioId(uriInfo, postid, comentario.getIdentificador(), "eliminar"));
			} else
				throw new ComentarioNotFoundException();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return comentario;
	}

	@PUT
	@Path("/{comentarioid}/moderar")
	@Consumes(MediaType.INFORMER_API_COMENTARIO)
	@Produces(MediaType.INFORMER_API_COMENTARIO)
	public String moderarComentario(@PathParam("postid") String postid, @PathParam("comentarioid") String comentarioid) {
		// PUT: /posts/{postid}/comentarios/{comentarioid}/moderar (admin) =>
		// revisado y who_revisado
		if (!security.isUserInRole("moderador")) {
			throw new ForbiddenException("You are not allowed...");
		}
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
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
			// comprobar que el que modifica es quien ha creado el post
			String username = security.getUserPrincipal().getName();
			String update = "UPDATE comentarios SET revisado=revisado+1, who_revisado='" + username + "', publicacion_date=publicacion_date, visibilidad=visibilidad-10 WHERE identificador=" + comentarioid + " and visibilidad>9;";
			stmt.executeUpdate(update);
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return "MODERADO";
	}

	@PUT
	@Path("/{comentarioid}")
	public String deleteComentarioVisibilidad(@PathParam("comentarioid") String comentarioid, @PathParam("postid") String postid) {
		// PUT: /posts/{postid}/comentarios/{comentarioid}
		// (Registered-Propietario) => visibilidad.
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
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
			// comprobar que el que modifica es quien ha creado el post
			String username = security.getUserPrincipal().getName();
			String query = "SELECT 1 FROM posts WHERE identificador='" + postid + "';";
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new PostNotFoundException();
			query = "SELECT 1 FROM comentarios, posts WHERE comentarios.identificador='" + comentarioid + "' and comentarios.identificador=posts.identificador and comentarios.visibilidad!=3;";
			rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new ComentarioNotFoundException();
			String update;
			if (!security.isUserInRole("moderador")) {
				update = "UPDATE comentarios SET visibilidad=3 WHERE identificador=" + comentarioid + " and username='" + username + "';";
			} else
				update = "UPDATE comentarios SET visibilidad=3 WHERE identificador=" + comentarioid + ";";
			int lineas_afectadas = stmt.executeUpdate(update);
			if (lineas_afectadas == 0)
				throw new ComentarioNotYoursException();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return "DELETED " + comentarioid;
	}

	@DELETE
	@Path("/{comentarioid}")
	public String deleteComentario(@PathParam("comentarioid") String comentarioid, @PathParam("postid") String postid) {
		// DELETE: /posts/{postid}/commentarios/{comentarioid}
		// (Registered-Propietario)
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
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
			String query = "SELECT 1 FROM posts WHERE identificador='" + postid + "';";
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new PostNotFoundException();
			query = "SELECT 1 FROM comentarios, posts WHERE comentarios.identificador='" + comentarioid + "' and comentarios.identificador=posts.identificador and comentarios.visibilidad!=3;";
			rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new ComentarioNotFoundException();
			query = "DELETE FROM comentarios WHERE identificador=" + comentarioid + " and id_post=" + postid + ";";
			int rows = stmt.executeUpdate(query);
			if (rows == 0)
				throw new ComentarioNotFoundException();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return "ELIMINADO";
	}

	private String comentarioAnonimo(String yo, String autor, String amigo, int visibilidad) {
		if (yo.equals(autor))
			return yo;
		if (visibilidad == 0) {
			return anonymous;
		}
		if (visibilidad == 1) {
			if (amigo == null)
				return anonymous;
			return autor;
		}
		// if (visibilidad == 2)
		return autor;
	}
}
