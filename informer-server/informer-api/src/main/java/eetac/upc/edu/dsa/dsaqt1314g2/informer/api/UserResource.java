package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

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

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.UsersAPILinkBuilder;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.User;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.UserCollection;

@Path("/users")
public class UserResource {

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	UserCollection users = new UserCollection();

	@GET
	@Path("/{username}")
	@Produces(MediaType.INFORMER_API_USER)
	public Response getUser(@PathParam("username") String username, @Context Request req) {
		// TODO: GET: /users/{nombre} (Registered)(admin)
		// Create CacheControl cache por si me lohan pedido hace poco
		CacheControl cc = new CacheControl();
		User user = new User();
		Statement stmt = null;

		// arrancamos la conexion
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {

			e.printStackTrace();
			throw new ServiceUnavailableException(e.getMessage());
		}

		// hacemso la consulta del libro
		try {
			// creamos el statement y la consulta
			stmt = conn.createStatement();
			String sql = "select * from perfiles where username='" + username + "';";
			// realizamos la consulta
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				// creamos el libro
				user.setIdentificador(rs.getInt("identificador"));
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setCorreo(rs.getString("correo"));
				user.setGenero(rs.getBoolean("genero"));
				user.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
				user.setUni_escuela(rs.getInt("uni_escuela"));
				user.setFoto(rs.getString("foto"));
				user.setEstado_civil(rs.getInt("estado_civil"));
				user.setLugar_de_residencia(rs.getString("lugar_de_residencia"));
				user.setParticipar_GPS(rs.getBoolean("participar_GPS"));
				user.setLast_Update(rs.getTimestamp("last_Update"));
				user.setIsModerador(security.isUserInRole("moderador"));

				// TODO: Generar lso Links
				user.addLinks(UsersAPILinkBuilder.buildURIUserName(uriInfo, user.getUsername(), "self"));
				user.addLinks(UsersAPILinkBuilder.buildURIDeleteUser(uriInfo, user.getUsername(), "delete"));
				user.addLinks(UsersAPILinkBuilder.buildURISolicitud(uriInfo, user.getUsername(), "solicitud"));

			} else {
				throw new UserNotFoundException();
			}
			rs.close();
			if (username.equals(security.getUserPrincipal().getName())) {
				sql = "UPDATE perfiles SET last_Update=CURRENT_TIMESTAMP where username='" + username + "';";
				stmt.executeUpdate(sql);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		} finally {

			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Integer.toString(user.getLast_Update().hashCode()));

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
		rb = Response.ok(user).cacheControl(cc).tag(eTag);

		return rb.build();
		// return user;
	}

	@POST
	@Path("/search")
	@Consumes(MediaType.INFORMER_API_USER_COLLECTION)
	@Produces(MediaType.INFORMER_API_USER_COLLECTION)
	public Response getSearch(@QueryParam("o") String offset, @QueryParam("l") String length, @Context Request req, User busqueda) {
		// TODO: Search: GET ? {nombre},{escula},{sexo},{edad},{estadocivil}
		// (Registered)(admin)

		if ((offset == null) || (length == null))
			throw new BadRequestException("offset and length are mandatory parameters");
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

		CacheControl cc = new CacheControl();
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		boolean coma = false;
		String query = "Select * from perfiles where ";
		if (busqueda.getUsername() != null) {
			coma = true;
			query += " username like '%" + busqueda.getUsername() + "%' ";
		}
		if (busqueda.getUni_escuela() != 0) {
			if (coma) {
				query += " and ";
			} else {
				coma = true;
			}
			query += " uni_escuela =" + busqueda.getUni_escuela();
		}
		if (busqueda.getGenero() != null) {
			if (coma) {
				query += " and ";
			} else {
				coma = true;
			}
			query += " genero =" + busqueda.getGenero();
		}
		if (busqueda.getFecha_nacimiento() != null) {
			if (coma) {
				query += " and ";
			} else {
				coma = true;
			}
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
			String nacimiento = dt1.format(busqueda.getFecha_nacimiento());
			query += " fecha_nacimiento <='" + nacimiento + "'";
		}
		if (busqueda.getEstado_civil() != -1) {
			if (coma) {
				query += " and ";
			} else {
				coma = true;
			}

			query += " estado_civil <=" + busqueda.getEstado_civil();
		}

		query += " ORDER BY username asc LIMIT " + offset + ", " + length + ";";
		try {
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				User user = new User();
				user.setIdentificador(rs.getInt("identificador"));
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setCorreo(rs.getString("correo"));
				user.setGenero(rs.getBoolean("genero"));
				user.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
				user.setLast_Update(rs.getDate("last_Update"));
				user.setUni_escuela(rs.getInt("uni_escuela"));
				user.setFoto(rs.getString("foto"));
				user.setEstado_civil(rs.getInt("estado_civil"));
				user.setLugar_de_residencia(rs.getString("lugar_de_residencia"));
				user.setParticipar_GPS(rs.getBoolean("participar_GPS"));
				
				// TODO: Links
				user.addLinks(UsersAPILinkBuilder.buildURIUserName(uriInfo, user.getUsername(), "self"));
				user.addLinks(UsersAPILinkBuilder.buildURISolicitud(uriInfo, user.getUsername(), "solicitud"));

				users.add(user);
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

		int o = Integer.parseInt(offset);
		int l = Integer.parseInt(length);
		if ((o - l) >= o) {
			String offset2 = Integer.toString(o - l);
			users.addLink(UsersAPILinkBuilder.buildURIUsers(uriInfo, offset2, length, "prev"));
		}
		users.addLink(UsersAPILinkBuilder.buildURIUsers(uriInfo, offset, length, "self"));
		String offset3 = Integer.toString(o + l);
		users.addLink(UsersAPILinkBuilder.buildURIUsers(uriInfo, offset3, length, "next"));
		// users.addLink();

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Integer.toString(users.hashCode()));

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
		rb = Response.ok(users).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	@GET
	@Path("{username}/amigos")
	@Produces(MediaType.INFORMER_API_USER_COLLECTION)
	public Response getAmigos(@QueryParam("o") String offset, @QueryParam("l") String length, @Context Request req, @PathParam("username") String username) {
		// TODO: Search: GET ? {nombre},{escula},{sexo},{edad},{estadocivil}
		// (Registered)(admin)

		if ((offset == null) || (length == null))
			throw new BadRequestException("offset and length are mandatory parameters");
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

		CacheControl cc = new CacheControl();
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		// Todo Sub query
		String query = "Select * from perfiles where username IN (Select friend from amigos where username ='" + username + "' and estado = 1) ";
		query += " ORDER BY username asc LIMIT " + offset + ", " + length + ";";
		try {
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				User user = new User();
				user.setIdentificador(rs.getInt("identificador"));
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setCorreo(rs.getString("correo"));
				user.setGenero(rs.getBoolean("genero"));
				user.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
				user.setUni_escuela(rs.getInt("uni_escuela"));
				user.setFoto(rs.getString("foto"));
				user.setEstado_civil(rs.getInt("estado_civil"));
				user.setLugar_de_residencia(rs.getString("lugar_de_residencia"));
				user.setParticipar_GPS(rs.getBoolean("participar_GPS"));
				user.setLast_Update(rs.getDate("last_Update"));
				// TODO: Links
				user.addLinks(UsersAPILinkBuilder.buildURIUserName(uriInfo, user.getUsername(), "self"));
				user.addLinks(UsersAPILinkBuilder.buildURIEliminarAmigo(uriInfo, user.getUsername(), "del_friend"));

				users.add(user);
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

		int o = Integer.parseInt(offset);
		int l = Integer.parseInt(length);
		if ((o - l) >= o) {
			String offset2 = Integer.toString(o - l);
			users.addLink(UsersAPILinkBuilder.buildURIUsers(uriInfo, offset2, length, "prev"));
		}
		users.addLink(UsersAPILinkBuilder.buildURIUsers(uriInfo, offset, length, "self"));
		String offset3 = Integer.toString(o + l);
		users.addLink(UsersAPILinkBuilder.buildURIUsers(uriInfo, offset3, length, "next"));
		// users.addLink();

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Integer.toString(users.hashCode()));

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
		rb = Response.ok(users).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	@GET
	@Path("/solicitudes")
	@Produces(MediaType.INFORMER_API_USER_COLLECTION)
	public Response getSolicitudes(@QueryParam("o") String offset, @QueryParam("l") String length, @Context Request req) {
		// TODO: Search: GET ? {nombre},{escula},{sexo},{edad},{estadocivil}
		// (Registered)(admin)

		if ((offset == null) || (length == null))
			throw new BadRequestException("offset and length are mandatory parameters");
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

		CacheControl cc = new CacheControl();
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		// Todo Sub query
		String query = "Select * from perfiles where username IN (Select friend from amigos where username ='" + security.getUserPrincipal().getName() + "' and estado = 0) ";
		query += " ORDER BY username asc LIMIT " + offset + ", " + length + ";";
		try {
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				User user = new User();
				user.setIdentificador(rs.getInt("identificador"));
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setCorreo(rs.getString("correo"));
				user.setGenero(rs.getBoolean("genero"));
				user.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
				user.setUni_escuela(rs.getInt("uni_escuela"));
				user.setFoto(rs.getString("foto"));
				user.setEstado_civil(rs.getInt("estado_civil"));
				user.setLugar_de_residencia(rs.getString("lugar_de_residencia"));
				user.setParticipar_GPS(rs.getBoolean("participar_GPS"));
				// TODO: Links
				user.addLinks(UsersAPILinkBuilder.buildURIUserName(uriInfo, user.getUsername(), "self"));
				user.addLinks(UsersAPILinkBuilder.buildURIEliminarAmigo(uriInfo, user.getUsername(), "del_solicitud"));
				user.addLinks(UsersAPILinkBuilder.buildURIAceptarSolicitud(uriInfo, user.getUsername(), "acept_solicitud"));

				users.add(user);
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

		int o = Integer.parseInt(offset);
		int l = Integer.parseInt(length);
		if ((o - l) >= o) {
			String offset2 = Integer.toString(o - l);
			users.addLink(UsersAPILinkBuilder.buildURIUsers(uriInfo, offset2, length, "prev"));
		}
		users.addLink(UsersAPILinkBuilder.buildURIUsers(uriInfo, offset, length, "self"));
		String offset3 = Integer.toString(o + l);
		users.addLink(UsersAPILinkBuilder.buildURIUsers(uriInfo, offset3, length, "next"));
		// users.addLink();

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Integer.toString(users.hashCode()));

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
		rb = Response.ok(users).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	@GET
	@Path("/solicitud/{username}")
	public String SolicitudAmigo(@PathParam("username") String username, @Context Request req) {
		// TODO: GET: /users/{nombre} (Registered)(admin)
		// Create CacheControl cache por si me lohan pedido hace poco

		String mensaje = "Solicitud de amistad enviada: ";
		Statement stmt = null;

		// arrancamos la conexion
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {

			e.printStackTrace();
			throw new ServiceUnavailableException(e.getMessage());
		}

		// hacemso la consulta del libro
		try {
			// creamos el statement y la consulta
			stmt = conn.createStatement();
			String sql = "select * from amigos where username='" + security.getUserPrincipal().getName() + "' and friend='" + username + "';";
			// realizamos la consulta
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				throw new BadRequestException("No puedes enviar una solicitud de amigos si el usuario no existe o ya es tu amigo/ tiene solicitud de amigo");

			} else {

				rs.close();
				String update = "insert into amigos (username, friend, estado) values ('" + security.getUserPrincipal().getName() + "','" + username + "',1),('" + username + "','" + security.getUserPrincipal().getName() + "',0) ;";

				stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
				rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					int identificador = rs.getInt(1);
					mensaje += username + " relacion: " + identificador;
					rs.close();
				} else {
					throw new UserNotFoundException();
				}
				rs.close();

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		} finally {

			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mensaje="";
		return mensaje;
	}

	@GET
	@Path("/{username}/aceptfriend")
	public String AceptarSolicitud(@PathParam("username") String username, @Context Request req) {
		// TODO: GET: /users/{nombre} (Registered)(admin)
		// Create CacheControl cache por si me lohan pedido hace poco

		String mensaje = "Solicitud de amistad aceptada, " + username + " ahora es tu amigo.";
		Statement stmt = null;

		// arrancamos la conexion
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {

			e.printStackTrace();
			throw new ServiceUnavailableException(e.getMessage());
		}

		// hacemso la consulta del libro
		try {
			// creamos el statement y la consulta
			stmt = conn.createStatement();
			String sql = "select * from amigos where username='" + security.getUserPrincipal().getName() + "' and friend='" + username + "';";
			// realizamos la consulta
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {

				sql = "update  amigos SET amigos.estado=1 where username='" + security.getUserPrincipal().getName() + "' and friend='" + username + "' and estado =0 ;";
				// realizamos la consulta

				int rows = stmt.executeUpdate(sql);

				if (rows == 0)
					throw new UserNotFoundException();

			} else {

				throw new BadRequestException("No puedes Aceptar una solicitud de amigos si el usuario ya es tu amigo o no tiene solicitud de amigo");

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		} finally {

			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return mensaje;
	}

	@DELETE
	@Path("/{username}/deletefriend")
	public String Deletefriend(@PathParam("username") String username, @Context Request req) {
		// TODO: GET: /users/{nombre} (Registered)(admin)
		// Create CacheControl cache por si me lohan pedido hace poco

		String mensaje = "Solicitud/amistad de " + username + " eliminada.";
		Statement stmt = null;

		// arrancamos la conexion
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {

			e.printStackTrace();
			throw new ServiceUnavailableException(e.getMessage());
		}

		// hacemso la consulta del libro
		try {
			// creamos el statement y la consulta
			stmt = conn.createStatement();
			String sql = "Delete from  amigos where (username='" + security.getUserPrincipal().getName() + "' and friend = '" + username + "') or (username='" + username + "' and friend = '" + security.getUserPrincipal().getName() + "')";
			// realizamos la consulta

			int rows = stmt.executeUpdate(sql);
			if (rows == 0)
				throw new UserNotFoundException();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		} finally {

			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mensaje="";
		return mensaje;
	}

	@POST
	@Consumes(MediaType.INFORMER_API_USER)
	@Produces(MediaType.INFORMER_API_USER)
	public User createUser(User user) {
		Statement stmt = null;
		if (!security.isUserInRole("admin")) {
			throw new ForbiddenException("You are not allowed");
		}
		// realizamos conexion
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ServiceUnavailableException(e.getMessage());
		}

		// leemos lo que nos manda y lo insertamos enla base de datos
		try {
			// realizamos la consulta
			stmt = conn.createStatement();
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println(user.getFecha_nacimiento());
			String fechanacimineto = dt1.format(user.getFecha_nacimiento());
			// creamos el statement y la consulta
			String sql = "select * from perfiles where username='" + user.getUsername() + "';";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next())
				throw new UserNameIsUsedException();
			sql = "insert into perfiles (username, name, correo, genero, fecha_nacimiento, uni_escuela, estado_civil)";
			sql += "values ('" + user.getUsername() + "', '" + user.getName() + "', '" + user.getCorreo() + "'," + user.getGenero() + ",'" + fechanacimineto + "'," + user.getUni_escuela() + "," + user.getEstado_civil() + ");";
			stmt.executeUpdate(sql);
			rs.close();
			sql = "select identificador,last_Update from perfiles where username='" + user.getUsername() + "';";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				int id = rs.getInt("identificador");
				user.setIdentificador(id);
				user.setLast_Update(rs.getTimestamp("last_Update"));
				user.addLinks(UsersAPILinkBuilder.buildURIUserName(uriInfo, user.getUsername(), "self"));
				user.addLinks(UsersAPILinkBuilder.buildURISolicitud(uriInfo, user.getUsername(), "solicitud"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	@PUT
	@Path("/{username}")
	@Consumes(MediaType.INFORMER_API_USER)
	@Produces(MediaType.INFORMER_API_USER)
	public User updateUser(@PathParam("username") String username, User user) {
		// TODO: Put: /users/{nombre} (Registered-Propietario)(admin)

		Statement stmt = null;

		if (!security.isUserInRole("registered") && !security.isUserInRole("admin")) {

			throw new ForbiddenException("You are nor allowed");

		}
		if (security.isUserInRole("registered")) {

			if (!security.getUserPrincipal().getName().equals(username)) {

				throw new ForbiddenException("You are nor allowed");
			}

		}

		// arrancamos la conexion
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceUnavailableException(e.getMessage());
		}

		// hacemso la consulta
		try {
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
			String fechanacimineto = dt1.format(user.getFecha_nacimiento());
			// creamos el statement y la consulta
			stmt = conn.createStatement();
			String sql = "update  perfiles SET perfiles.name='" + user.getName() + "',perfiles.correo='" + user.getCorreo() + "', perfiles.fecha_nacimiento='" + fechanacimineto + "', perfiles.uni_escuela=" + user.getUni_escuela() + ", perfiles.foto='" + user.getFoto() + "', perfiles.estado_civil="
					+ user.getEstado_civil() + ", perfiles.lugar_de_residencia='" + user.getLugar_de_residencia() + "', perfiles.participar_GPS=" + user.getParticipar_GPS() + " where username='" + username + "';";
			// realizamos la consulta

			int rows = stmt.executeUpdate(sql);

			if (rows == 0)
				throw new UserNotFoundException();

			sql = "select * from perfiles where username='" + username + "'";

			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				user.setIdentificador(rs.getInt("identificador"));
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setCorreo(rs.getString("correo"));
				user.setGenero(rs.getBoolean("genero"));
				user.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
				user.setUni_escuela(rs.getInt("uni_escuela"));
				user.setFoto(rs.getString("foto"));
				user.setEstado_civil(rs.getInt("estado_civil"));
				user.setLugar_de_residencia(rs.getString("lugar_de_residencia"));
				user.setParticipar_GPS(rs.getBoolean("participar_GPS"));

				// TODO: Generar lso Links
				// añadimos los links
				user.addLinks(UsersAPILinkBuilder.buildURIUserName(uriInfo, user.getUsername(), "self"));

			} else {
				throw new UserNotFoundException();
			}
			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		} finally {

			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return user;
	}

	@DELETE
	@Path("/{username}")
	public String deleteUser(@PathParam("username") String username) {
		// TODO: Delete: /users/{nombre} (admin)
		Statement stmt = null;
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceUnavailableException(e.getMessage());
		}

		// hacemso la consulta y el array de stings
		try {
			// creamos el statement y la consulta
			stmt = conn.createStatement();
			String sql = "Delete from  perfiles where username='" + username + "'";
			// realizamos la consulta

			int rows = stmt.executeUpdate(sql);
			if (rows == 0)
				throw new UserNotFoundException();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		} finally {

			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ("Eliminado satisfactoriamente a " + username);
	}

}
