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

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.ComentariosAPILinkBuilder;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Comentario;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.ComentarioCollection;

@Path("/posts/{postid}/comentarios")
public class ComentarioResource {

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
		if ((offset == null) || (length == null)) {
			offset = "0";
			length = "10";
		}
			
		int ioffset, ilength;
		try {
			ioffset = Integer.parseInt(offset);
			if (ioffset < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new BadRequestException("offset must be an integer greater or equal than 0.");
		}
		try {
			ilength = Integer.parseInt(length);
			if (ilength < 1)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new BadRequestException("length must be an integer greater or equal than 0.");
		}

		// Sting for each one and store them in the StingCollection.
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			String query;
			query = "select * FROM comentarios WHERE id_post="+postid+" ORDER BY publicacion_date desc LIMIT " + offset + ", " + length + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Comentario c = new Comentario();
				c.setIdentificador(rs.getInt("identificador"));
				c.setUsername(rs.getString("username"));
				c.setVisibilidad(rs.getInt("visibilidad"));
				c.setContenido(rs.getString("contenido"));
				c.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				c.setRevisado(rs.getInt("revisado"));
				c.setWho_revisado(rs.getString("who_revisado"));
				c.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, Integer.parseInt(postid), c.getIdentificador(), "self"));
				comentarios.add(c);
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
		int prev = ioffset - ilength;
		int next = ioffset + ilength;
		comentarios.addLink(ComentariosAPILinkBuilder.buildURIComentarios(uriInfo, offset, length, null, "self"));
		if (prev > 0)
			comentarios.addLink(ComentariosAPILinkBuilder.buildURIComentarios(uriInfo, Integer.toString(prev), length, null, "prev"));
		comentarios.addLink(ComentariosAPILinkBuilder.buildURIComentarios(uriInfo, Integer.toString(next), length, null, "next"));
		// TODO: limitar el next
		return comentarios;
	}

	@GET
	@Path("/{comentarioid}")
	@Produces(MediaType.INFORMER_API_COMENTARIO)
	public Response getComentario(@PathParam("comentarioid") String comentarioid,@PathParam("postid") String postid, @Context Request req) {
		// GET: /posts/{postid}/comentarios/{comentarioid}   (Registered)(admin)
		// TODO: Hace falta que el comentarioid pertenzca a postid?
		CacheControl cc = new CacheControl();
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		Comentario c = new Comentario();
		try {
			stmt = con.createStatement();
			String query = "SELECT * FROM comentarios WHERE id_post=" + postid + " and identificador="+comentarioid+";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				c.setIdentificador(rs.getInt("identificador"));
				c.setUsername(rs.getString("username"));
				c.setVisibilidad(rs.getInt("visibilidad"));
				c.setContenido(rs.getString("contenido"));
				c.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				c.setRevisado(rs.getInt("revisado"));
				c.setWho_revisado(rs.getString("who_revisado"));
				c.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, Integer.parseInt(postid), c.getIdentificador(), "self"));
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

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Integer.toString(c.getPublicacion_date().hashCode()));

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
		rb = Response.ok(c).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	@POST
	@Consumes(MediaType.INFORMER_API_COMENTARIO)
	@Produces(MediaType.INFORMER_API_COMENTARIO)
	public Comentario createComentario(@PathParam("postid") String postid, Comentario comentario) {
		// TODO: POST: /posts{postid}/comentarios/   (Registered)(admin)
		if (comentario.getContenido().length() > 2048)
			throw new BadRequestException("Longitud del asunto excede el limite de 2048 caracteres.");
		if (comentario.getVisibilidad() < 0 || comentario.getVisibilidad() > 2)
			throw new BadRequestException("Visibilidad incorrecta.");
		comentario.setUsername(security.getUserPrincipal().getName());

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
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
			String update = "INSERT INTO comentarios (id_post,username,visibilidad,contenido) VALUES ("+postid+", '"+comentario.getUsername()+"', "+comentario.getVisibilidad()+", '"+comentario.getContenido()+"')";
			//TODO: Apostrofe y acentos
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int identificador = rs.getInt(1);
				rs.close();

				rs = stmt.executeQuery("SELECT * FROM comentarios WHERE identificador='" + identificador + "';");
				rs.next();

				comentario.setIdentificador(rs.getInt("identificador"));
				comentario.setUsername(rs.getString("username"));
				comentario.setVisibilidad(rs.getInt("visibilidad"));
				comentario.setContenido(rs.getString("contenido"));
				comentario.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				comentario.setRevisado(rs.getInt("revisado"));
				comentario.setWho_revisado(rs.getString("who_revisado"));
				comentario.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, Integer.parseInt(postid), comentario.getIdentificador(), "self"));
			} else {
				throw new ComentarioNotFoundException();
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
		return comentario;
	}

	@POST
	@Path("/{comentarioid}/denunciar")
	public void denunciaComentario(@PathParam("postid") String postid,@PathParam("comentarioid") String comentarioid) {
		//POST: /posts/{postid}/comentarios/{comentarioid} /denunciar (1=denunciar, 0 desdenunciar)  (Registered)
		Comentario c = new Comentario();
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
			query = "SELECT COUNT(id) FROM denuncias_comentario WHERE id_comentario='" + comentarioid + "';";
			rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) >= MAX_DENUNCIAS) {
				insert = "UPDATE comentarios SET visibilidad=5 WHERE identificador='" + comentarioid + "';";
				c.setVisibilidad(5);
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
	public Comentario updateComentario(@PathParam("comentarioid") String comentarioid,@PathParam("postid") String postid, Comentario comentario) {
		// TODO: PUT: /posts/{postid}/comentarios/{comentarioid}  (Registered-Propietario) => visibilidad.
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
			// String username = "ropnom";
			String update = "UPDATE comentarios SET visibilidad=" + comentario.getVisibilidad() + " WHERE identificador=" + comentarioid + " and username='" + username + "';";
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
		comentario.setIdentificador(Integer.parseInt(postid));
		comentario.addLink(ComentariosAPILinkBuilder.buildURIComentarioId(uriInfo, Integer.parseInt(postid), comentario.getIdentificador(), "self"));
		return comentario;
	}

	@PUT
	@Path("/{comentarioid}/moderar")
	@Consumes(MediaType.INFORMER_API_COMENTARIO)
	@Produces(MediaType.INFORMER_API_COMENTARIO)
	public void moderarComentario(@PathParam("postid") String postid, @PathParam("comentarioid") String comentarioid) {
		// TODO: PUT: /posts/{postid}/comentarios/{comentarioid}/moderar   (admin) => revisado y who_revisado
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
			// String username = "ropnom";

			String update = "UPDATE comentarios SET revisado=revisado+1, who_revisado='" + username + "' WHERE identificador=" + comentarioid + ";";
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
		return;
	}

	@DELETE
	@Path("/{comentarioid}")
	public void deleteComentario(@PathParam("comentarioid") String comentarioid,@PathParam("postid") String postid) {
		// DELETE: /posts/{postid}/commentarios/{comentarioid} (Registered-Propietario)
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "DELETE FROM comentarios WHERE identificador=" + comentarioid + " and id_post="+postid+";";
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
	}
}
