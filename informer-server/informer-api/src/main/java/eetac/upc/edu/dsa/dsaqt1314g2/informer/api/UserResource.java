package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

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
	public User getUser(@PathParam("username") String username,
			@Context Request req) {
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
			String sql = "select * from perfiles where username='" + username
					+ "';";
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

				// TODO: Generar lso Links
				// añadimos los links
				// user.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo,
				// libro.getLibroid(), "self"));

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

		// // Calculate the ETag on last modified date of user resource
		// EntityTag eTag = new EntityTag(Integer.toString(user.getLast_Update()
		// .hashCode()));
		//
		// // Verify if it matched with etag available in http request
		// Response.ResponseBuilder rb = req.evaluatePreconditions(eTag);
		//
		// // If ETag matches the rb will be non-null;
		// // Use the rb to return the response without any further processing
		// if (rb != null) {
		// return rb.cacheControl(cc).tag(eTag).build();
		// }
		//
		// // If rb is null then either it is first time request; or resource is
		// // modified
		// // Get the updated representation and return with Etag attached to it
		// rb = Response.ok(user).cacheControl(cc).tag(eTag);

		// return rb.build();
		return user;
	}

	// @GET
	// @Path("/search")
	// @Produces(MediaType.INFORMER_API_USER_COLLECTION)
	// public UserCollection getSearch(@Context Request req) {
	// // TODO: Search: GET ? {nombre},{escula},{sexo},{edad},{estadocivil}
	// // (Registered)(admin)
	// }

	@POST
	@Consumes(MediaType.INFORMER_API_USER)
	@Produces(MediaType.INFORMER_API_USER)
	public User createUser(User user) {
		// TODO: Post: /users (admin)
		Statement stmt = null;

		// if (!security.isUserInRole("admin")) {
		//
		// throw new ForbiddenException("You are nor allowed");
		//
		// }

		// realizamos conexion
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceUnavailableException(e.getMessage());
		}

		// leemos lo que nos manda y lo insertamos enla base de datos
		try {
			// realizamos la consulta
			stmt = conn.createStatement();
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
			String fechanacimineto = dt1.format(user.getFecha_nacimiento());
			// creamos el statement y la consulta
			String sql = "select * from perfiles where username='"
					+ user.getUsername() + "';";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next())
				throw new UserNameIsUsedException();

			sql = "insert into perfiles (username, name, correo, genero, fecha_nacimiento, uni_escuela, foto, estado_civil, lugar_de_residencia, participar_GPS)";
			sql += "values ('" + user.getUsername() + "', '" + user.getName()
					+ "', '" + user.getCorreo() + "'," + user.getGenero()
					+ ",'" + fechanacimineto + "'," + user.getUni_escuela()
					+ ",'" + user.getFoto() + "'," + user.getEstado_civil()
					+ ",'" + user.getLugar_de_residencia() + "',"
					+ user.getParticipar_GPS() + ");";

			// le indicamos que nso devuelva la primary key que le genere a la
			// nueva entrada
			stmt.executeUpdate(sql);
			rs = stmt.getGeneratedKeys();
			// leemos la primary key
			rs.close();
			sql = "select identificador,last_Update from perfiles where username=" + user.getUsername();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {

				int id = rs.getInt("identificador");

				user.setIdentificador(id);
				user.setLast_Update(rs.getTimestamp("last_Update"));

				// TODO: Generar lso Links
				// añadimos los links
				// user.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo,
				// libro.getLibroid(), "self"));
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

	@PUT
	@Path("/{username}")
	@Consumes(MediaType.INFORMER_API_USER)
	@Produces(MediaType.INFORMER_API_USER)
	public User updateUser(@PathParam("username") String username, User user) {
		// TODO: Put: /users/{nombre} (Registered-Propietario)(admin)

		Statement stmt = null;

		// if (!security.isUserInRole("registered")
		// || !security.isUserInRole("admin")) {
		//
		// throw new ForbiddenException("You are nor allowed");
		//
		// }
		// if (!security.isUserInRole("registered")) {
		//
		// if (!security.getUserPrincipal().getName().equals(username)) {
		//
		// throw new ForbiddenException("You are nor allowed");
		// }
		//
		// }

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
			String sql = "update  perfiles SET perfiles.name='"
					+ user.getName() + "',perfiles.correo='" + user.getCorreo()
					+ "', perfiles.fecha_nacimiento='" + fechanacimineto
					+ "', perfiles.uni_escuela=" + user.getUni_escuela()
					+ ", perfiles.foto='" + user.getFoto()
					+ "', perfiles.estado_civil=" + user.getEstado_civil()
					+ ", perfiles.lugar_de_residencia='"
					+ user.getLugar_de_residencia()
					+ "', perfiles.participar_GPS=" + user.getParticipar_GPS()
					+ " where username='" + username + "';";
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
				// user.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo,
				// libro.getLibroid(), "self"));

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
	public void deleteUser(@PathParam("username") String username) {
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

		// if (!security.isUserInRole("admin")) {
		//
		// throw new ForbiddenException("You are nor allowed");
		//
		// }

		// hacemso la consulta y el array de stings
		try {
			// creamos el statement y la consulta
			stmt = conn.createStatement();
			String sql = "Delete from  perfiles where username='" + username
					+ "'";
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
	}

}
