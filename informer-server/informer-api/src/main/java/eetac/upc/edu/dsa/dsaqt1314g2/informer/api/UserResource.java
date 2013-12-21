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
	public User getUser(@PathParam("username") String username, @Context Request req) {
		// TODO: GET: /users/{nombre}  (Registered)(admin)
	}
	
	@GET
	@Path("/search")
	@Produces(MediaType.INFORMER_API_USER_COLLECTION)
	public UserCollection getSearch(@Context Request req) {
		// TODO: Search: GET ?  {nombre},{escula},{sexo},{edad},{estadocivil}  (Registered)(admin)
	}	
	
	@POST
	@Consumes(MediaType.INFORMER_API_USER)
	@Produces(MediaType.INFORMER_API_USER)
	public User createUser(User user) {
		// TODO: Post: /users  (admin)
	}

	@PUT
	@Path("/{username}")
	@Consumes(MediaType.INFORMER_API_USER)
	@Produces(MediaType.INFORMER_API_USER)
	public User updateUser(@PathParam("username") String username, User user) {
		// TODO: Put: /users/{nombre}  (Registered-Propietario)(admin)
		return user;
	}
	
	@DELETE
	@Path("/{username}")
	public void deleteUser(@PathParam("username") String username) {
		// TODO: Delete: /users/{nombre} (admin)	
	}
	
	
}
