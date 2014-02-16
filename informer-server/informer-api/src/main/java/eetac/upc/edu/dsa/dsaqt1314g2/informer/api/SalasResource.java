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

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.SalasAPILinkBuilder;
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
	@Path("/administrarsalas")
	@Produces(MediaType.INFORMER_API_SALA_COLLECTION)
	public Response getmySalas(@QueryParam("o") String offset, @QueryParam("l") String length, @Context Request req) {
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
				throw new BadRequestException("offset must be an integer greater or equal than 0.");
			}
			try {
				ilength = Integer.parseInt(length);
				if (ilength < 1)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException("length must be an integer greater or equal than 0.");
			}

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
			String query = "SELECT salas_chat.* FROM salas_chat  where salas_chat.username='";
			String username = security.getUserPrincipal().getName();
			query += username + "' ORDER BY nombre_sala asc LIMIT " + offset + ", " + length + ";";

			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Sala sala = new Sala();
				sala.setIdentificador(rs.getInt("identificador"));
				sala.setUsername(rs.getString("username"));
				sala.setNombre_sala(rs.getString("nombre_sala"));
				sala.setVisibilidad(rs.getInt("visibilidad"));
				sala.setLast_update(rs.getTimestamp("last_update"));
				sala.setPassword(rs.getNString("password"));

				// TODO Links
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "GET", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "PUT", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "DELETE", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISala(uriInfo, "POST"));
				if (sala.getVisibilidad() == 0)
					sala.addLinks(SalasAPILinkBuilder.buildTemplatedURISalasUnirse(uriInfo, sala.getIdentificador(), false));
				// else if (sala.getVisibilidad() == 1)
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasUnirse(uriInfo,
				// sala.getIdentificador(), true));
				// else
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasUnirse(uriInfo,
				// sala.getIdentificador(), true));
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasInvitar(uriInfo,
				// sala.getIdentificador()));
				sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIAbandonarSala(uriInfo, sala.getIdentificador()));
				sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIDenegarInvitacion(uriInfo, sala.getIdentificador()));

				salas.add(sala);
			}

			rs.close();
			query = "SELECT Count(identificador) FROM salas_chat  where salas_chat.username='" + username + "'";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				salas.setCount(rs.getInt("Count(identificador)"));
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

		salas.addLink(SalasAPILinkBuilder.buildURISalasVision(uriInfo, offset, length, 0, "Categorias"));
		salas.addLink(SalasAPILinkBuilder.buildURISalasVision(uriInfo, offset, length, 1, "Categorias"));
		salas.addLink(SalasAPILinkBuilder.buildURISalasVision(uriInfo, offset, length, 2, "Categorias"));
		salas.addLink(SalasAPILinkBuilder.buildURISalasVision(uriInfo, offset, length, 2, "Subcripciones"));
		salas.addLink(SalasAPILinkBuilder.buildTemplatedURIgetInvitaciones(uriInfo));

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Integer.toString(salas.hashCode()));

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
		rb = Response.ok(salas).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	@GET
	@Path("/{salaid}")
	@Produces(MediaType.INFORMER_API_SALA)
	public Response getSala(@PathParam("salaid") String salaid, @Context Request req) {
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
			String query = "SELECT * FROM salas_chat WHERE identificador=" + salaid + ";";
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
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "GET", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "PUT", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "DELETE", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISala(uriInfo, "POST"));
				if (sala.getVisibilidad() == 0)
					sala.addLinks(SalasAPILinkBuilder.buildTemplatedURISalasUnirse(uriInfo, sala.getIdentificador(), false));
				// else if (sala.getVisibilidad() == 1)
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasUnirse(uriInfo,
				// sala.getIdentificador(), true));
				// else
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasUnirse(uriInfo,
				// sala.getIdentificador(), true));
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasInvitar(uriInfo,
				// sala.getIdentificador()));
				sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIAbandonarSala(uriInfo, sala.getIdentificador()));
				sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIDenegarInvitacion(uriInfo, sala.getIdentificador()));

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
		EntityTag eTag = new EntityTag(Integer.toString(sala.getLast_update().hashCode()));

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
	public Response getVisibilidad(@PathParam("categoria") String categoria, @QueryParam("o") String offset, @QueryParam("l") String length, @Context Request req) {
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
				throw new BadRequestException("offset must be an integer greater or equal than 0.");
			}
			try {
				ilength = Integer.parseInt(length);
				if (ilength < 1)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException("length must be an integer greater or equal than 0.");
			}
			try {
				icategoria = Integer.parseInt(categoria);
				if (icategoria < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException(" categoria must be an integer greater or equal than 0.");
			}
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
			String query = "";
			String query2 = "";
			if (icategoria == 0) {
				query = "SELECT * FROM salas_chat where visibilidad=0 ORDER BY nombre_sala asc LIMIT " + offset + ", " + length + ";";
				query2 = "SELECT Count(identificador) FROM salas_chat where visibilidad=0;";
			} else if (icategoria == 1) {
				query = "SELECT * FROM salas_chat where visibilidad=1 ORDER BY nombre_sala asc LIMIT " + offset + ", " + length + ";";
				query2 = "SELECT Count(identificador) FROM salas_chat where visibilidad=1;";
			} else if (icategoria == 2) {
				String username = security.getUserPrincipal().getName();
				query = "SELECT salas_chat.* FROM salas_chat, rel_sala_user  where salas_chat.visibilidad=2 and rel_sala_user.id_sala = salas_chat.identificador and ";
				query += "rel_sala_user.username = '" + username + "' ORDER BY nombre_sala asc LIMIT " + offset + ", " + length + ";";
				query2 = "SELECT Count(identificador) FROM salas_chat, rel_sala_user  where salas_chat.visibilidad=2 and rel_sala_user.id_sala = salas_chat.identificador and ";
				query2 += "rel_sala_user.username = '" + username + "';";

			} else if (icategoria == 3) {
				String username = security.getUserPrincipal().getName();
				query = "SELECT salas_chat.* FROM salas_chat, rel_sala_user  where rel_sala_user.id_sala = salas_chat.identificador and rel_sala_user.estado=1 and ";
				query += "rel_sala_user.username = '" + username + "' ORDER BY nombre_sala asc LIMIT " + offset + ", " + length + ";";
				query2 = "SELECT Count(identificador) FROM salas_chat, rel_sala_user  where rel_sala_user.id_sala = salas_chat.identificador and rel_sala_user.estado=1 and ";
				query2 += "rel_sala_user.username = '" + username + "';";
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
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "GET", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "PUT", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "DELETE", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISala(uriInfo, "POST"));
				if (sala.getVisibilidad() == 0)
					sala.addLinks(SalasAPILinkBuilder.buildTemplatedURISalasUnirse(uriInfo, sala.getIdentificador(), false));
				// else if (sala.getVisibilidad() == 1)
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasUnirse(uriInfo,
				// sala.getIdentificador(), true));
				// else
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasUnirse(uriInfo,
				// sala.getIdentificador(), true));
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasInvitar(uriInfo,
				// sala.getIdentificador()));
				sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIAbandonarSala(uriInfo, sala.getIdentificador()));
				sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIDenegarInvitacion(uriInfo, sala.getIdentificador()));

				salas.add(sala);
			}
			rs.close();
			rs = stmt.executeQuery(query2);

			if (rs.next()) {
				salas.setCount(rs.getInt("Count(identificador)"));
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

		// TODO Links

		salas.addLink(SalasAPILinkBuilder.buildURISalasVision(uriInfo, offset, length, 0, "Categorias"));
		salas.addLink(SalasAPILinkBuilder.buildURISalasVision(uriInfo, offset, length, 1, "Categorias"));
		salas.addLink(SalasAPILinkBuilder.buildURISalasVision(uriInfo, offset, length, 2, "Categorias"));
		salas.addLink(SalasAPILinkBuilder.buildURISalasVision(uriInfo, offset, length, 2, "Subcripciones"));

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Integer.toString(salas.hashCode()));

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
		rb = Response.ok(salas).cacheControl(cc).tag(eTag);

		return rb.build();

	}

	@GET
	@Path("/invitaciones")
	@Produces(MediaType.INFORMER_API_SALA_COLLECTION)
	public Response getInvitaciones(@QueryParam("o") String offset, @QueryParam("l") String length, @Context Request req) {

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
				throw new BadRequestException("offset must be an integer greater or equal than 0.");
			}
			try {
				ilength = Integer.parseInt(length);
				if (ilength < 1)
					throw new NumberFormatException();
			} catch (NumberFormatException e) {
				throw new BadRequestException("length must be an integer greater or equal than 0.");
			}

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
			String query = "SELECT salas_chat.* FROM salas_chat, rel_sala_user  where  rel_sala_user.estado=0 and rel_sala_user.id_sala = salas_chat.identificador and ";
			String username = security.getUserPrincipal().getName();
			query += "rel_sala_user.username = '" + username + "' ORDER BY nombre_sala asc LIMIT " + offset + ", " + length + ";";

			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Sala sala = new Sala();
				sala.setIdentificador(rs.getInt("identificador"));
				sala.setUsername(rs.getString("username"));
				sala.setNombre_sala(rs.getString("nombre_sala"));
				sala.setVisibilidad(rs.getInt("visibilidad"));
				sala.setLast_update(rs.getTimestamp("last_update"));
				sala.setPassword(rs.getNString("password"));

				// TODO Links
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "GET", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "PUT", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "DELETE", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISala(uriInfo, "POST"));
				if (sala.getVisibilidad() == 0)
					sala.addLinks(SalasAPILinkBuilder.buildTemplatedURISalasUnirse(uriInfo, sala.getIdentificador(), false));
				// else if (sala.getVisibilidad() == 1)
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasUnirse(uriInfo,
				// sala.getIdentificador(), true));
				// else
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasUnirse(uriInfo,
				// sala.getIdentificador(), true));
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasInvitar(uriInfo,
				// sala.getIdentificador()));
				sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIAbandonarSala(uriInfo, sala.getIdentificador()));
				sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIDenegarInvitacion(uriInfo, sala.getIdentificador()));

				salas.add(sala);
			}
			rs.close();
			String query2 = "SELECT Count(identificador) FROM salas_chat, rel_sala_user  where  rel_sala_user.estado=0 and rel_sala_user.id_sala = salas_chat.identificador and ";
			query2 += "rel_sala_user.username = '" + security.getUserPrincipal().getName() + "';";
			rs = stmt.executeQuery(query2);
			if (rs.next()) {
				salas.setCount(rs.getInt("Count(identificador)"));
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

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Integer.toString(salas.hashCode()));

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
		rb = Response.ok(salas).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	@POST
	@Consumes(MediaType.INFORMER_API_SALA)
	@Produces(MediaType.INFORMER_API_SALA)
	public Sala createSala(Sala sala) {
		// TODO: POST: /posts (Registered)(admin)
		if (sala.getNombre_sala().length() < 3 || sala.getNombre_sala().length() > 50)
			throw new BadRequestException("Longitud del nombre excede el limite de 50 caracteres o es inferior a 3.");
		if (sala.getPassword().length() > 255)
			throw new BadRequestException("Longitud de la contraseñaexcede el limite de 255 caracteres.");

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			stmt = con.createStatement();
			String update = "insert into salas_chat (username, nombre_sala, visibilidad, password) values ('" + sala.getUsername() + "','" + sala.getNombre_sala() + "'," + sala.getVisibilidad() + ",'" + sala.getPassword() + "');";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int identificador = rs.getInt(1);
				rs.close();

				rs = stmt.executeQuery("SELECT * FROM salas_chat WHERE identificador='" + identificador + "';");
				rs.next();

				sala.setIdentificador(rs.getInt("identificador"));
				sala.setUsername(rs.getString("username"));
				sala.setNombre_sala(rs.getString("nombre_sala"));
				sala.setVisibilidad(rs.getInt("visibilidad"));
				sala.setLast_update(rs.getTimestamp("last_update"));
				sala.setPassword("*********");

				// TODO Links
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "GET", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "PUT", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "DELETE", "self"));
				sala.addLinks(SalasAPILinkBuilder.buildURISala(uriInfo, "POST"));
				if (sala.getVisibilidad() == 0)
					sala.addLinks(SalasAPILinkBuilder.buildTemplatedURISalasUnirse(uriInfo, sala.getIdentificador(), false));
				// else if (sala.getVisibilidad() == 1)
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasUnirse(uriInfo,
				// sala.getIdentificador(), true));
				// else
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasUnirse(uriInfo,
				// sala.getIdentificador(), true));
				// sala.addLinks(SalasAPILinkBuilder
				// .buildTemplatedURISalasInvitar(uriInfo,
				// sala.getIdentificador()));
				sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIAbandonarSala(uriInfo, sala.getIdentificador()));
				sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIDenegarInvitacion(uriInfo, sala.getIdentificador()));

			} else {
				throw new SalaNotFoundException();
			}

			update = "insert into rel_sala_user (username, id_sala, estado) values ('" + security.getUserPrincipal().getName() + "'," + sala.getIdentificador() + ",1);";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();
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
		return sala;
	}

	@GET
	@Path("/{salaid}/unirse")
	public String Unirse(@PathParam("salaid") String salaid, @QueryParam("pass") String pass, @Context Request req) {
		// GET: /posts/{postid} (Registered)(admin)

		String mensaje = "La contraseña es incorrecta;";

		if (pass == null) {
			pass = "password";
		}
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
			String query = "SELECT * FROM salas_chat WHERE identificador=" + salaid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				sala.setIdentificador(rs.getInt("identificador"));
				sala.setUsername(rs.getString("username"));
				sala.setNombre_sala(rs.getString("nombre_sala"));
				sala.setVisibilidad(rs.getInt("visibilidad"));
				sala.setLast_update(rs.getTimestamp("last_update"));

				if (sala.getVisibilidad() < 1) {
					sala.setPassword("password");
				} else {
					sala.setPassword(rs.getNString("password"));
				}

				// TODO Links

			} else
				throw new SalaNotFoundException();

			if (pass.equals(sala.getPassword())) {
				rs.close();

				query = "SELECT * FROM rel_sala_user WHERE id_sala=" + sala.getIdentificador() + " and username = '" + security.getUserPrincipal().getName() + "';";
				rs = stmt.executeQuery(query);
				if (!rs.next()) {

					String update = "insert into rel_sala_user (username, id_sala, estado) values ('" + security.getUserPrincipal().getName() + "'," + sala.getIdentificador() + ",1);";
					stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
					rs = stmt.getGeneratedKeys();
					if (rs.next()) {
						int identificador = rs.getInt(1);
						rs.close();
						mensaje = "Usuario Unido Correctamente, relacion : " + identificador;
					} else {
						throw new SalaNotFoundException();
					}
				} else {
					// TODO: execepciones
					throw new SalaUserExistException();
				}

			} else {
				throw new SalaUserExistException();
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

		return (mensaje);
	}

	@GET
	@Path("/{salaid}/invitar")
	public void Invitar(@PathParam("salaid") String salaid, @QueryParam("username") String username, @Context Request req) {
		// GET: /posts/{postid} (Registered)(admin)
		//String mensaje = "No puedes invitar a una sala en al que no estas subscrito";
		if (username == null) {
			throw new BadRequestException("Es encesario indicar el username a invitar");
		}
		//Sala sala = new Sala();
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "SELECT * FROM rel_sala_user WHERE estado = 1 and id_sala=" + salaid + " and username='" + security.getUserPrincipal().getName() + "';";
			ResultSet rs = stmt.executeQuery(query);
			boolean invitador = false;
			if (rs.next()) {
				invitador = true;
			}
			rs.close();

			if (invitador) {
				boolean invitado = false;
				query = "SELECT * FROM rel_sala_user WHERE estado = 0 and id_sala=" + salaid + " and username='" + username + "';";
				rs = stmt.executeQuery(query);
				if (rs.next()) {
					invitado = true;
				}
				rs.close();
				if (!invitado) {
					String update = "insert into rel_sala_user (username, id_sala, estado) values ('" + username + "'," + salaid + ",0);";
					stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
					rs = stmt.getGeneratedKeys();
					if (rs.next()) {
						//int identificador = rs.getInt(1);
						rs.close();
						//mensaje = "Usuario " + username + " Invitado Correctamente, relacion : " + identificador;
					} else {
						throw new SalaNotFoundException();
					}

				} else {
					throw new BadRequestException("El usuario ya ha sido invitado a la sala");
				}
			} else {
				throw new BadRequestException("Es encesario pertenecer ala sala para invitar");
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
		return;
	}

	@GET
	@Path("/{salaid}/aceptar")
	public String AceptarInvitacion(@PathParam("salaid") String salaid) {
		// TODO: DELETE: /posts/{postid} (admin)
		Connection con = null;
		Statement stmt = null;
		String mensaje = "Has sido añadido satisfactoriamente a la sala " + salaid;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String update = "UPDATE rel_sala_user SET estado=1 WHERE id_sala=" + salaid + " and username ='" + security.getUserPrincipal().getName() + "'and estado= 0;";

			int rows = stmt.executeUpdate(update);
			if (rows == 0)
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
		return (mensaje);
	}

	@PUT
	@Path("/{salaid}")
	@Consumes(MediaType.INFORMER_API_SALA)
	@Produces(MediaType.INFORMER_API_SALA)
	public Sala updateSala(@PathParam("salaid") String salaid, Sala sala) {
		// TODO: PUT: /posts/{postid} (Registered-Propietario) => visibilidad.

		if (sala.getNombre_sala().length() < 3 || sala.getNombre_sala().length() > 50)
			throw new BadRequestException("Longitud del nombre excede el limite de 50 caracteres.");
		if (sala.getPassword().length() > 255)
			throw new BadRequestException("Longitud de la contraseñaexcede el limite de 255 caracteres.");

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "SELECT * FROM salas_chat WHERE identificador=" + salaid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				sala.setUsername(rs.getString("username"));

			} else {
				throw new SalaNotFoundException();
			}
			rs.close();

			String username = security.getUserPrincipal().getName();
			if (sala.getUsername().equals(username)) {

				String update = "UPDATE salas_chat SET nombre_sala='" + sala.getNombre_sala() + "', visibilidad=" + sala.getVisibilidad() + ", password='" + sala.getPassword() + "' WHERE identificador=" + salaid + " ;";
				stmt.executeUpdate(update);
				sala.setPassword("***OK***");
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
		sala.setIdentificador(Integer.parseInt(salaid));
		// TODO Links
		sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "GET", "self"));
		sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "PUT", "self"));
		sala.addLinks(SalasAPILinkBuilder.buildURISalaId(uriInfo, sala, "DELETE", "self"));
		sala.addLinks(SalasAPILinkBuilder.buildURISala(uriInfo, "POST"));
		if (sala.getVisibilidad() == 0)
			sala.addLinks(SalasAPILinkBuilder.buildTemplatedURISalasUnirse(uriInfo, sala.getIdentificador(), false));
		// else if (sala.getVisibilidad() == 1)
		// sala.addLinks(SalasAPILinkBuilder
		// .buildTemplatedURISalasUnirse(uriInfo,
		// sala.getIdentificador(), true));
		// else
		// sala.addLinks(SalasAPILinkBuilder
		// .buildTemplatedURISalasUnirse(uriInfo,
		// sala.getIdentificador(), true));
		// sala.addLinks(SalasAPILinkBuilder
		// .buildTemplatedURISalasInvitar(uriInfo,
		// sala.getIdentificador()));
		sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIAbandonarSala(uriInfo, sala.getIdentificador()));
		sala.addLinks(SalasAPILinkBuilder.buildTemplatedURIDenegarInvitacion(uriInfo, sala.getIdentificador()));

		return sala;
	}

	@DELETE
	@Path("/{salaid}")
	public void deleteSala(@PathParam("salaid") String salaid) {
		// TODO: DELETE: /posts/{postid} (admin)
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "DELETE FROM salas_chat WHERE identificador=" + salaid + ";";
			int rows = stmt.executeUpdate(query);
			if (rows == 0)
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
	}

	@DELETE
	@Path("/{salaid}/abandonar")
	public String AbandonarSala(@PathParam("salaid") String salaid, @Context Request req) {
		// GET: /posts/{postid} (Registered)(admin)

		String mensaje = "Has abandonado la Sala";

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "DELETE FROM rel_sala_user WHERE estado=1 and username='" + security.getUserPrincipal().getName() + "' and id_sala=" + salaid + ";";
			int rows = stmt.executeUpdate(query);
			if (rows == 0)
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
		return mensaje;
	}

	@DELETE
	@Path("/{salaid}/denegarinvitacion")
	public String DenegarInvitacion(@PathParam("salaid") String salaid, @Context Request req) {
		// GET: /posts/{postid} (Registered)(admin)

		String mensaje = "Has Rechazado la Invitacion.";

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "DELETE FROM rel_sala_user WHERE estado=0 and username='" + security.getUserPrincipal().getName() + "' and id_sala=" + salaid + ";";
			int rows = stmt.executeUpdate(query);
			if (rows == 0)
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
		return mensaje;
	}
}
