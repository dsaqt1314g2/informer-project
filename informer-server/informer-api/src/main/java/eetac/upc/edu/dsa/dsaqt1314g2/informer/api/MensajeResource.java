package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.MensajesAPILinkBuilder;
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
	@Produces(MediaType.INFORMER_API_MENSAJE_COLLECTION)
	public MensajeCollection getMensajes(@PathParam("salaid") String salaid, @QueryParam("o") String offset, @QueryParam("l") String length, @QueryParam("f") String fecha, @QueryParam("id") String identificador) {
		// GETs: /salas/{salaid}/mensajes?{offset}{length}{fecha}
		// (Registered)(admin)
		// (publicas y donde yo estoy)
		try {
			int p = Integer.parseInt(salaid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new SalaNotFoundException();
		}
		// System.out.println(new Date().getTime());
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
		int iidentificador = 0;
		if (identificador == null)
			identificador = "0";
		else {
			try {
				iidentificador = Integer.parseInt(identificador);
				if (iidentificador < 1)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException("Identificador debe ser un entero mayor o igual a 0.");
			}
		}
		
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		String ifecha;
		if (fecha == null) {
			fecha = "0";
			ifecha = dt.format(new Date(0));
		}
		else {
			try {
				ifecha = dt.format(new Date(Long.parseLong(fecha)));
			} catch (Exception e) {
				throw new BadRequestException("Fecha debe ser un entero mayor o igual a 0.");
			}
		}

		int sala = Integer.parseInt(salaid);
		if (sala < 0)
			throw new SalaNotFoundException();

		Connection con = null;
		Statement stmt = null;
		String username = security.getUserPrincipal().getName();
		int mensajes_encontrados = 0;

		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			String query;
			ResultSet rs;
			if (sala == 0)
				query = "SELECT * FROM mensajes_chat, rel_sala_user WHERE last_update>'" + ifecha + "' and rel_sala_user.username='" + username + "' and mensajes_chat.id_sala=rel_sala_user.id_sala ORDER BY last_update DESC LIMIT " + offset + ", " + (ilength + 1) + ";";
			else {
				query = "SELECT 1 FROM rel_sala_user WHERE id_sala=" + salaid + ";";
				rs = stmt.executeQuery(query);
				if (!rs.next())
					throw new SalaNotFoundException();
				query = "SELECT 1 FROM rel_sala_user WHERE id_sala=" + salaid + " and username='" + username + "' and estado=1;";
				rs = stmt.executeQuery(query);
				if (!rs.next())
					throw new UserNotFoundInSalaException();
				query = "(SELECT * FROM mensajes_chat, rel_sala_user WHERE mensajes_chat.id_sala=" + salaid + " and mensajes_chat.identificador>" + iidentificador + " and rel_sala_user.username='" + username + "' and mensajes_chat.id_sala=rel_sala_user.id_sala ORDER BY last_update DESC LIMIT " + ioffset + ", "
						+ (ilength) + ") ORDER BY last_update ASC;";
			}
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				Mensaje m = new Mensaje();
				m.setIdentificador(rs.getInt("identificador"));
				m.setId_sala(rs.getInt("id_sala"));
				m.setUsername(rs.getString("username"));
				m.setContenido(rs.getString("contenido"));
				m.setLast_update(rs.getTimestamp("last_update"));
				m.addLink(MensajesAPILinkBuilder.buildURIMensajeId(uriInfo, salaid, m.getIdentificador(), "self"));
				mensajes.add(m);
				mensajes_encontrados++;
			}
			rs.close();
			if (mensajes_encontrados == 0)
				throw new MensajeCollectionNotFoundException();
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
			mensajes.addLink(MensajesAPILinkBuilder.buildURIMensajes(uriInfo, prev, length, fecha, salaid, "prev"));
		mensajes.addLink(MensajesAPILinkBuilder.buildURIMensajes(uriInfo, ioffset, length, fecha, salaid, "self"));
		if (mensajes_encontrados == ilength)
			mensajes.addLink(MensajesAPILinkBuilder.buildURIMensajes(uriInfo, next, length, fecha, salaid, "next"));
		return mensajes;
	}

	@GET
	@Path("/{mensajeid}")
	@Produces(MediaType.INFORMER_API_MENSAJE)
	public Response getMensaje(@PathParam("salaid") String salaid, @PathParam("mensajeid") String mensajeid, @Context Request req) {
		// GET: /posts/{postid}/comentarios/{comentarioid} (Registered)(admin)
		try {
			int p = Integer.parseInt(salaid);
			int a = Integer.parseInt(mensajeid);
			if (p < 0)
				throw new NumberFormatException();
			if (a < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new SalaNotFoundException();
		}
		CacheControl cc = new CacheControl();
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		Mensaje m = new Mensaje();
		String username = security.getUserPrincipal().getName();
		try {
			stmt = con.createStatement();
			String query;
			ResultSet rs;
			query = "SELECT 1 FROM rel_sala_user WHERE id_sala=" + salaid + ";";
			rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new SalaNotFoundException();
			query = "SELECT 1 FROM rel_sala_user WHERE id_sala=" + salaid + " and username='" + username + "' and estado=1;";
			rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new UserNotFoundInSalaException();
			query = "SELECT * FROM mensajes_chat WHERE mensajes_chat.id_sala=" + salaid + " and mensajes_chat.identificador=" + mensajeid + ";";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				m.setIdentificador(rs.getInt("identificador"));
				m.setId_sala(rs.getInt("id_sala"));
				m.setUsername(rs.getString("username"));
				m.setContenido(rs.getString("contenido"));
				m.setLast_update(rs.getTimestamp("last_update"));
				m.addLink(MensajesAPILinkBuilder.buildURIMensajeId(uriInfo, salaid, m.getIdentificador(), "self"));
			} else
				throw new MensajeNotFoundException();
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
		EntityTag eTag = new EntityTag(Integer.toString(m.getLast_update().hashCode()));
		Response.ResponseBuilder rb = req.evaluatePreconditions(eTag);
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
		rb = Response.ok(m).cacheControl(cc).tag(eTag);
		return rb.build();
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
		String username = security.getUserPrincipal().getName();
		try {
			stmt = con.createStatement();
			String query;
			ResultSet rs;
			query = "SELECT 1 FROM rel_sala_user WHERE id_sala=" + salaid + ";";
			rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new SalaNotFoundException();
			query = "SELECT 1 FROM rel_sala_user WHERE id_sala=" + salaid + " and username='" + username + "' and estado=1;";
			rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new UserNotFoundInSalaException();
			String update = "INSERT INTO mensajes_chat (id_sala,username,contenido) VALUES (" + salaid + ", '" + mensaje.getUsername() + "', '" + mensaje.getContenido().replace("'", "´") + "');";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();
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
//				System.out.println(mensaje.getLast_update() + " <-- --> " + mensaje.getLast_update().getTime());
				mensaje.addLink(MensajesAPILinkBuilder.buildURIMensajeId(uriInfo, salaid, mensaje.getIdentificador(), "self"));
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

	@DELETE
	@Path("/{mensajeid}")
	public void deleteMensaje(@PathParam("salaid") String salaid, @PathParam("mensajeid") String mensajeid) {
		// GET: /posts/{postid}/comentarios/{comentarioid} (Registered)(admin)
		if (!security.isUserInRole("admin")) {
			throw new ForbiddenException("You are not allowed...");
		}
		try {
			int p = Integer.parseInt(salaid);
			int a = Integer.parseInt(mensajeid);
			if (p < 0)
				throw new NumberFormatException();
			if (a < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new SalaNotFoundException();
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
			String query;
			ResultSet rs;
			query = "SELECT 1 FROM rel_sala_user WHERE id_sala=" + salaid + ";";
			rs = stmt.executeQuery(query);
			if (!rs.next())
				throw new SalaNotFoundException();
			// query =
			// "SELECT 1 FROM rel_sala_user WHERE id_sala="+salaid+" and username='"+username+"' and estado=1;";
			// rs = stmt.executeQuery(query);
			// if (!rs.next())
			// throw new UserNotFoundInSalaException();
			query = "DELETE FROM mensajes_chat WHERE mensajes_chat.id_sala=" + salaid + " and mensajes_chat.identificador=" + mensajeid + ";";
			int rows = stmt.executeUpdate(query);
			if (rows == 0)
				throw new MensajeNotFoundException();
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
		return;
	}

}
