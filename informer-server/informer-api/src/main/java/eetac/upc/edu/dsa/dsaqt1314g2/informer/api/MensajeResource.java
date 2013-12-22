package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

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
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.MensajesAPILinkBuilder;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Comentario;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Mensaje;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.MensajeCollection;

@Path("/salas/{salaid}/mensajes")
public class MensajeResource {

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	MensajeCollection mensajes = new MensajeCollection();

	@GET
	@Produces(MediaType.INFORMER_API_COMENTARIO_COLLECTION)
	public MensajeCollection getMensajes(@PathParam("salaid") String salaid, @PathParam("mensajeid") String mensajeid, @QueryParam("o") String offset,
			@QueryParam("l") String length, @QueryParam("f") String fecha) {
		// GETs: /salas/{salaid}/mensajes?{offset}{length} (Registered)(admin)
		// (publicas y donde yo estoy)
		// TODO: offset y fecha corregir
		if ((offset == null) || (length == null)) {
			offset = "0";
			length = "10";
		}
		long ifecha = 0;
		if (fecha == null)
			ifecha = 2147483647;
		else
			ifecha = Integer.parseInt(fecha);
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
		int sala = Integer.parseInt(salaid);
		if (sala < 0)
			throw new SalaNotFoundException();
		
		Connection con = null;
		Statement stmt = null;
		String username = security.getUserPrincipal().getName();
		int mensajes_encontrados=0;
		
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			String query;
			if (sala == 0)
				query = "SELECT mensajes_chat.* FROM mensajes_chat LEFT JOIN rel_sala_user ON rel_sala_user.id_sala=mensajes_chat.id_sala and rel_sala_user.username='"+username+"' WHERE last_update<"+fecha+" ORDER BY last_update DESC LIMIT "+ offset + ", " + (ilength+1) + ";";
			else
				query = "SELECT * FROM mensajes_chat WHERE id_sala="+sala+" and last_update<"+fecha+" ORDER BY last_update DESC LIMIT "+ offset + ", " + (ilength+1) + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				sala = rs.getInt(1);
				String query_sala = "select * from mensajes_chat where id_sala='" + sala + "' and last_update<" + ifecha + " ORDER BY last_update DESC LIMIT "
						+ offset + ", " + (ilength+1) + ";";
				ResultSet rss = stmt.executeQuery(query_sala);
				while (rs.next()) {
					if (mensajes_encontrados++ > ilength)
						break;
					Mensaje m = new Mensaje();
					m.setIdentificador(rs.getInt("identificador"));
					m.setId_sala(rs.getInt("id_sala"));
					m.setUsername(rs.getString("username"));
					m.setContenido(rs.getString("contenido"));
					m.setLast_update(rs.getTimestamp("last_update"));
//					m.addLink(MensajesAPILinkBuilder.buildURIMensajeId(uriInfo, Integer.parseInt(mensajeid), m.getIdentificador(), "self"));
					mensajes.add(m);
				}
				rss.close();
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
//		mensajes.addLink(MensajesAPILinkBuilder.buildURIMensajes(uriInfo, offset, length, null, "self"));
//		if ((ioffset-ilength) > 0)
//			mensajes.addLink(MensajesAPILinkBuilder.buildURIMensajes(uriInfo, Integer.toString((ioffset-ilength)), length, null, "prev"));
//		if (mensajes_encontrados > ilength)
//			mensajes.addLink(MensajesAPILinkBuilder.buildURIMensajes(uriInfo, Integer.toString((ioffset+ilength)), length, null, "next"));
		return mensajes;
	}

	@GET
	@Path("/{mensajeid}")
	@Produces(MediaType.INFORMER_API_MENSAJE)
	public Response getMensaje(@PathParam("salaid") String salaid, @PathParam("mensajeid") String mensajeid, @Context Request req) {
		// GET: /posts/{postid}/comentarios/{comentarioid} (Registered)(admin)
		CacheControl cc = new CacheControl();
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		Mensaje m = new Mensaje();
		try {
			stmt = con.createStatement();
			String query = "SELECT * FROM mensajes_chat WHERE id_sala=" + salaid + " and identificador=" + mensajeid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				m.setIdentificador(rs.getInt("identificador"));
				m.setId_sala(rs.getInt("id_sala"));
				m.setUsername(rs.getString("username"));
				m.setContenido(rs.getString("contenido"));
				m.setLast_update(rs.getTimestamp("last_update"));
//				m.addLink(MensajesAPILinkBuilder.buildURIMensajeId(uriInfo, Integer.parseInt(mensajeid), m.getIdentificador(), "self"));
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
		EntityTag eTag = new EntityTag(Integer.toString(m.getLast_update().hashCode()));

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
		rb = Response.ok(m).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	@GET
	@Produces(MediaType.INFORMER_API_MENSAJE_COLLECTION)
	public MensajeCollection getTodosMensajes(@PathParam("salaid") String salaid, @PathParam("mensajeid") String mensajeid, @QueryParam("o") String offset,
			@QueryParam("l") String length) {
		// GETs: /salas/{salaid}/mensajes?{offset}{length} (Registered)(admin)
		// (publicas y donde yo estoy)
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

		Connection con = null;
		Statement stmt = null;
		int mensajes_encontrados=0;
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			String query;
			query = "select * FROM mensajes_chat WHERE id_sala=" + salaid + " ORDER BY publicacion_date desc LIMIT " + offset + ", " + (ilength+1) + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (mensajes_encontrados++ > ilength)
					break;
				Mensaje m = new Mensaje();
				m.setIdentificador(rs.getInt("identificador"));
				m.setId_sala(rs.getInt("id_sala"));
				m.setUsername(rs.getString("username"));
				m.setContenido(rs.getString("contenido"));
				m.setLast_update(rs.getTimestamp("last_update"));
//				m.addLink(MensajesAPILinkBuilder.buildURIMensajeId(uriInfo, Integer.parseInt(mensajeid), m.getIdentificador(), "self"));
				mensajes.add(m);
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
//		mensajes.addLink(MensajesAPILinkBuilder.buildURIMensajes(uriInfo, offset, length, null, "self"));
//		if (prev > 0)
//			mensajes.addLink(MensajesAPILinkBuilder.buildURIMensajes(uriInfo, Integer.toString(prev), length, null, "prev"));
//		if (mensajes_encontrados > ilength)
//			mensajes.addLink(MensajesAPILinkBuilder.buildURIMensajes(uriInfo, Integer.toString(next), length, null, "next"));
		return mensajes;
	}

	@POST
	@Consumes(MediaType.INFORMER_API_MENSAJE)
	@Produces(MediaType.INFORMER_API_MENSAJE)
	public Mensaje createMensaje(@PathParam("salaid") String salaid, Mensaje mensaje) {
		// POST: /salas/{salaid}/mensajes (Registered)(admin) -> cambiar
		// lastupdate sala
		if (mensaje.getContenido().length() > 255)
			throw new BadRequestException("Longitud del mensaje excede el limite de 255 caracteres.");
		mensaje.setUsername(security.getUserPrincipal().getName());

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			stmt = con.createStatement();
			String update = "INSERT INTO mensajes_chat (id_sala,username,contenido) VALUES (" + salaid + ", '" + mensaje.getUsername() + "', '"
					+ mensaje.getContenido() + "');";
			// TODO: Apostrofe y acentos
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int identificador = rs.getInt(1);
				rs.close();

				rs = stmt.executeQuery("SELECT * FROM mensajes_chat WHERE identificador='" + identificador + "';");
				rs.next();

				mensaje.setIdentificador(rs.getInt("identificador"));
				mensaje.setId_sala(rs.getInt("id_sala"));
				mensaje.setUsername(rs.getString("username"));
				mensaje.setContenido(rs.getString("contenido"));
				mensaje.setLast_update(rs.getTimestamp("last_update"));
//				mensaje.addLink(MensajesAPILinkBuilder.buildURIMensajeId(uriInfo, Integer.parseInt(salaid), mensaje.getIdentificador(), "self"));
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
		return mensaje;
	}

}
