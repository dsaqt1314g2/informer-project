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
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.PostsAPILinkBuilder;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.Post;
import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model.PostCollection;

@Path("/posts")
public class PostResource {

	private final static String anonymous = "Anónimo";
	private final static String anonymous_picture = "img/informer.png";

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	PostCollection posts = new PostCollection();

	@GET
	@Produces(MediaType.INFORMER_API_POST_COLLECTION)
	public PostCollection getPosts(@QueryParam("o") String offset, @QueryParam("l") String length) {
		// GETs: /posts?{offset}{length} (Registered)(admin)
		// TODO: Borramos los BadRequest y ponemos un valor por defecto
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

		Connection con = null;
		Statement stmt = null;

		int posts_encontrados = 0; // variable para saber si hay link_next
		String username = security.getUserPrincipal().getName();

		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			// usuario normal. No selecciona los de visiblidad > 2
			String query = "SELECT amigos.friend, posts.*, calificacion.estado, perfiles.foto FROM posts LEFT JOIN calificacion ON calificacion.id_post=posts.identificador and calificacion.username='" + username + "' LEFT JOIN amigos ON amigos.friend='" + username
					+ "' and amigos.username=posts.username and amigos.estado=1 LEFT JOIN perfiles ON perfiles.username=posts.username WHERE posts.visibilidad<3 and posts.identificador NOT IN(SELECT id_post FROM posts,denuncias_post WHERE denuncias_post.id_post=posts.identificador and denuncias_post.username='" + username
					+ "') ORDER BY identificador DESC LIMIT " + ioffset + ", " + (ilength + 1) + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (posts_encontrados++ == ilength)
					break;
				Post post = new Post();
				post.setIdentificador(rs.getInt("identificador"));
				post.setAsunto(rs.getString("asunto"));
				post.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				post.setNumcomentarios(rs.getInt("numcomentarios"));
				post.setCalificaciones_positivas(rs.getInt("calificaciones_positivas"));
				post.setCalificaciones_negativas(rs.getInt("calificaciones_negativas"));
				post.setRevisado(rs.getInt("revisado"));
				post.setWho_revised(rs.getString("who_revisado"));
				post.setLiked(rs.getInt("estado"));
				post.setVisibilidad(rs.getInt("visibilidad"));
				post.setContenido(rs.getString("contenido"));
				post.setImagen_usuario(postAnonimoFoto(username, rs.getString("username"), rs.getString("friend"), post.getVisibilidad(),rs.getString("foto")));
				post.setUsername(postAnonimo(username, rs.getString("username"), rs.getString("friend"), post.getVisibilidad()));
				if (post.getIdentificador() != 1)
					post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() - 1, "prev"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador(), "self"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() + 1, "next"));
				post.addLink(PostsAPILinkBuilder.buildURILikePostId(uriInfo, post.getIdentificador(), "like"));
				post.addLink(PostsAPILinkBuilder.buildURIDislikePostId(uriInfo, post.getIdentificador(), "dislike"));
				post.addLink(PostsAPILinkBuilder.buildURINeutroPostId(uriInfo, post.getIdentificador(), "neutro"));
				post.addLink(ComentariosAPILinkBuilder.buildURIComentarios(uriInfo, ioffset, length, Integer.toString(post.getIdentificador()), "comentarios"));
				post.addLink(PostsAPILinkBuilder.buildURIDenunciarPostId(uriInfo, post.getIdentificador(), "denunciar"));
				post.addLink(PostsAPILinkBuilder.buildURIModificarPostId(uriInfo, post.getIdentificador(), "modificar"));
				post.addLink(PostsAPILinkBuilder.buildURIDeletePostId(uriInfo, post.getIdentificador(), "eliminar"));
				posts.add(post);
			}
			rs.close();
			if (posts_encontrados == 0)
				throw new PostCollectionNotFoundException();
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
			posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo, Integer.toString(prev), length, null, "prev"));
		posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo, offset, length, null, "self"));
		if (posts_encontrados > ilength)
			posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo, Integer.toString(next), length, null, "next"));
		posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, "likes", 0, 10, "ranking likes"));
		posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, "dislikes", 0, 10, "ranking dislikes"));
		posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, "coments", 0, 10, "ranking coments"));
		return posts;
	}

	@GET
	@Path("/{postid}")
	@Produces(MediaType.INFORMER_API_POST)
	public Response getPost(@PathParam("postid") String postid, @Context Request req) {
		// GET: /posts/{postid} (Registered)(admin)
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
		}
		CacheControl cc = new CacheControl();
		Post post = new Post();
		Connection con = null;
		Statement stmt = null;
		String username = security.getUserPrincipal().getName();
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "SELECT amigos.friend, posts.*, calificacion.estado, denuncias_post.id_post FROM posts LEFT JOIN calificacion ON calificacion.id_post=posts.identificador and calificacion.username='" + username + "' LEFT JOIN amigos ON amigos.friend='" + username
					+ "' and amigos.username=posts.username and amigos.estado=1 LEFT JOIN denuncias_post ON denuncias_post.id_post=posts.identificador and denuncias_post.username='" + username + "' WHERE identificador=" + postid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				if (rs.getInt("id_post") == rs.getInt("identificador"))
					throw new PostDenunciadoException();
				if (rs.getInt("visibilidad") > 9)
					throw new PostPendienteDeRevisionException();
				if (rs.getInt("visibilidad") == 3)
					throw new PostNotFoundException();
				post.setIdentificador(rs.getInt("identificador"));
				post.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				post.setNumcomentarios(rs.getInt("numcomentarios"));
				post.setCalificaciones_positivas(rs.getInt("calificaciones_positivas"));
				post.setCalificaciones_negativas(rs.getInt("calificaciones_negativas"));
				post.setRevisado(rs.getInt("revisado"));
				post.setWho_revised(rs.getString("who_revisado"));
				post.setLiked(rs.getInt("estado"));
				post.setVisibilidad(rs.getInt("visibilidad"));
				post.setContenido(rs.getString("contenido"));
				post.setUsername(postAnonimo(username, rs.getString("username"), rs.getString("friend"), post.getVisibilidad()));
				if (post.getIdentificador() != 1)
					post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() - 1, "prev"));
				if (post.getIdentificador() != 1)
					post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() - 1, "prev"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador(), "self"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() + 1, "next"));
				post.addLink(PostsAPILinkBuilder.buildURILikePostId(uriInfo, post.getIdentificador(), "like"));
				post.addLink(PostsAPILinkBuilder.buildURIDislikePostId(uriInfo, post.getIdentificador(), "dislike"));
				post.addLink(PostsAPILinkBuilder.buildURINeutroPostId(uriInfo, post.getIdentificador(), "neutro"));
				post.addLink(PostsAPILinkBuilder.buildURIDenunciarPostId(uriInfo, post.getIdentificador(), "denunciar"));
				post.addLink(PostsAPILinkBuilder.buildURIModificarPostId(uriInfo, post.getIdentificador(), "modificar"));
				post.addLink(PostsAPILinkBuilder.buildURIDeletePostId(uriInfo, post.getIdentificador(), "eliminar"));
			} else
				throw new PostNotFoundException();
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

		EntityTag eTag = new EntityTag(Integer.toString(post.getPublicacion_date().hashCode()));
		Response.ResponseBuilder rb = req.evaluatePreconditions(eTag);
		if (rb != null)
			return rb.cacheControl(cc).tag(eTag).build();
		rb = Response.ok(post).cacheControl(cc).tag(eTag);
		return rb.build();
		// TODO: Testear el cacheable
	}

	@GET
	@Path("/ranking/{categoria}")
	@Produces(MediaType.INFORMER_API_POST_COLLECTION)
	public PostCollection getRanking(@PathParam("categoria") String categoria, @QueryParam("o") String offset, @QueryParam("l") String length, @Context Request req) {
		// GETs: /posts/ranking/{categoria} (Registered)(admin)
		if (!categoria.equals("likes") && !categoria.equals("dislikes") && !categoria.equals("coments"))
			throw new BadRequestException("Formato de peitición incorrecto");
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
		String username = security.getUserPrincipal().getName();
		Connection con = null;
		Statement stmt = null;
		int posts_encontrados = 0;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "SELECT amigos.friend, posts.*, calificacion.estado, perfiles.foto FROM posts LEFT JOIN calificacion ON calificacion.id_post=posts.identificador and calificacion.username='" + username + "' LEFT JOIN amigos ON amigos.friend='" + username
					+ "' and amigos.username=posts.username and amigos.estado=1 LEFT JOIN perfiles ON perfiles.username=posts.username WHERE posts.visibilidad<3 and posts.identificador NOT IN(SELECT id_post FROM posts,denuncias_post WHERE denuncias_post.id_post=posts.identificador and denuncias_post.username='" + username + "') ";
			if (categoria.equals("likes"))
				query += "ORDER BY calificaciones_positivas ";
			else if (categoria.equals("coments"))
				query += "ORDER BY numcomentarios ";
			else if (categoria.equals("dislikes"))
				query += "ORDER BY calificaciones_negativas ";
			query += "DESC LIMIT " + ioffset + ", " + (ilength + 1) + ";";

			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (posts_encontrados++ == ilength)
					break;
				Post post = new Post();
				post.setIdentificador(rs.getInt("identificador"));
				post.setAsunto(rs.getString("asunto"));
				post.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				post.setNumcomentarios(rs.getInt("numcomentarios"));
				post.setCalificaciones_positivas(rs.getInt("calificaciones_positivas"));
				post.setCalificaciones_negativas(rs.getInt("calificaciones_negativas"));
				post.setRevisado(rs.getInt("revisado"));
				post.setWho_revised(rs.getString("who_revisado"));
				post.setLiked(rs.getInt("estado"));
				post.setVisibilidad(rs.getInt("visibilidad"));
				post.setContenido(rs.getString("contenido"));
				post.setImagen_usuario(postAnonimoFoto(username, rs.getString("username"), rs.getString("friend"), post.getVisibilidad(),rs.getString("foto")));
				post.setUsername(postAnonimo(username, rs.getString("username"), rs.getString("friend"), post.getVisibilidad()));
				if (post.getIdentificador() != 1)
					post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() - 1, "prev"));
				if (post.getIdentificador() != 1)
					post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() - 1, "prev"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador(), "self"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() + 1, "next"));
				post.addLink(PostsAPILinkBuilder.buildURILikePostId(uriInfo, post.getIdentificador(), "like"));
				post.addLink(PostsAPILinkBuilder.buildURIDislikePostId(uriInfo, post.getIdentificador(), "dislike"));
				post.addLink(PostsAPILinkBuilder.buildURINeutroPostId(uriInfo, post.getIdentificador(), "neutro"));
				post.addLink(PostsAPILinkBuilder.buildURIDenunciarPostId(uriInfo, post.getIdentificador(), "denunciar"));
				post.addLink(PostsAPILinkBuilder.buildURIModificarPostId(uriInfo, post.getIdentificador(), "modificar"));
				post.addLink(PostsAPILinkBuilder.buildURIDeletePostId(uriInfo, post.getIdentificador(), "eliminar"));
				posts.add(post);
			}
			rs.close();
			if (posts_encontrados == 0)
				throw new PostCollectionNotFoundException();
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
			posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, categoria, prev, ilength, "prev"));
		posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, categoria, ioffset, ilength, "self"));
		if (posts_encontrados > ilength)
			posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, categoria, next, ilength, "next"));

		if (!categoria.equals("likes"))
			posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, "likes", 0, ilength, "ranking likes"));
		if (!categoria.equals("dislikes"))
			posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, "dislikes", 0, ilength, "ranking dislikes"));
		if (!categoria.equals("coments"))
			posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, "coments", 0, ilength, "ranking coments"));
		return posts;
	}
	
	@GET
	@Path("/denuncias")
	@Produces(MediaType.INFORMER_API_POST_COLLECTION)
	public PostCollection getDenuncias(@QueryParam("o") String offset, @QueryParam("l") String length, @Context Request req) {
		// GETs: /posts/ranking/{categoria} (Registered)(admin)
		if (!security.isUserInRole("moderador") && !security.isUserInRole("admin")) {
			throw new ForbiddenException("You are not allowed...");
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
		String username = security.getUserPrincipal().getName();
		Connection con = null;
		Statement stmt = null;
		int posts_encontrados = 0;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "SELECT amigos.friend, posts.*, calificacion.estado, perfiles.foto FROM posts LEFT JOIN calificacion ON calificacion.id_post=posts.identificador and calificacion.username='" + username + "' LEFT JOIN amigos ON amigos.friend='" + username
					+ "' and amigos.username=posts.username and amigos.estado=1 LEFT JOIN perfiles ON perfiles.username=posts.username WHERE posts.visibilidad>3 ORDER BY publicacion_date DESC LIMIT " + ioffset + ", " + (ilength + 1) + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (posts_encontrados++ == ilength)
					break;
				Post post = new Post();
				post.setIdentificador(rs.getInt("identificador"));
				post.setAsunto(rs.getString("asunto"));
				post.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				post.setNumcomentarios(rs.getInt("numcomentarios"));
				post.setCalificaciones_positivas(rs.getInt("calificaciones_positivas"));
				post.setCalificaciones_negativas(rs.getInt("calificaciones_negativas"));
				post.setRevisado(rs.getInt("revisado"));
				post.setWho_revised(rs.getString("who_revisado"));
				post.setLiked(rs.getInt("estado"));
				post.setVisibilidad(rs.getInt("visibilidad"));
				post.setContenido(rs.getString("contenido"));
				post.setImagen_usuario(postAnonimoFoto(username, rs.getString("username"), rs.getString("friend"), post.getVisibilidad(),rs.getString("foto")));
				post.setUsername(postAnonimo(username, rs.getString("username"), rs.getString("friend"), post.getVisibilidad()));
				if (post.getIdentificador() != 1)
					post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() - 1, "prev"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador(), "self"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() + 1, "next"));
				post.addLink(PostsAPILinkBuilder.buildURILikePostId(uriInfo, post.getIdentificador(), "like"));
				post.addLink(PostsAPILinkBuilder.buildURIDislikePostId(uriInfo, post.getIdentificador(), "dislike"));
				post.addLink(PostsAPILinkBuilder.buildURINeutroPostId(uriInfo, post.getIdentificador(), "neutro"));
				post.addLink(PostsAPILinkBuilder.buildURIDenunciarPostId(uriInfo, post.getIdentificador(), "denunciar"));
				post.addLink(PostsAPILinkBuilder.buildURIModificarPostId(uriInfo, post.getIdentificador(), "modificar"));
				post.addLink(PostsAPILinkBuilder.buildURIDeletePostId(uriInfo, post.getIdentificador(), "eliminar"));
				posts.add(post);
			}
			rs.close();
			if (posts_encontrados == 0)
				throw new PostCollectionNotFoundException();
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
			posts.addLink(PostsAPILinkBuilder.buildURIPostsDenunciados(uriInfo, prev, ilength, "prev"));
		posts.addLink(PostsAPILinkBuilder.buildURIPostsDenunciados(uriInfo, ioffset, ilength, "self"));
		if (posts_encontrados > ilength)
			posts.addLink(PostsAPILinkBuilder.buildURIPostsDenunciados(uriInfo, next, ilength, "next"));

		return posts;
	}

	@POST
	@Consumes(MediaType.INFORMER_API_POST)
	@Produces(MediaType.INFORMER_API_POST)
	public Post createPost(Post post) {
		// POST: /posts (Registered)(admin)
		if (post.getContenido().length() > 2048)
			throw new BadRequestException("Longitud del contenido excede el limite de 2048 caracteres.");
		if (post.getContenido().length() < 1)
			throw new BadRequestException("Longitud del contenido demasiado corto.");
		if (post.getVisibilidad() < 0 || post.getVisibilidad() > 2)
			throw new BadRequestException("Visibilidad incorrecta.");
		if (post.getAsunto().length() == 0)
			post.setAsunto(post.getContenido().substring(0, 10)+"...");
		post.setUsername(security.getUserPrincipal().getName());

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			stmt = con.createStatement();
			String update = "insert into posts(asunto, username, visibilidad, contenido) values ('" + post.getAsunto().replace("'", "´") + "','" + post.getUsername() + "','" + post.getVisibilidad() + "','" + post.getContenido().replace("'", "´") + "');";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int identificador = rs.getInt(1);
				rs.close();
				rs = stmt.executeQuery("SELECT * FROM posts WHERE identificador='" + identificador + "';");
				rs.next();
				post.setIdentificador(rs.getInt("identificador"));
				post.setUsername(rs.getString("username"));
				post.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				post.setNumcomentarios(rs.getInt("numcomentarios"));
				post.setCalificaciones_positivas(rs.getInt("calificaciones_positivas"));
				post.setCalificaciones_negativas(rs.getInt("calificaciones_negativas"));
				post.setRevisado(rs.getInt("revisado"));
				post.setWho_revised(rs.getString("who_revisado"));
				if (post.getIdentificador() != 1)
					post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() - 1, "prev"));
				if (post.getIdentificador() != 1)
					post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() - 1, "prev"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador(), "self"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() + 1, "next"));
				post.addLink(PostsAPILinkBuilder.buildURILikePostId(uriInfo, post.getIdentificador(), "like"));
				post.addLink(PostsAPILinkBuilder.buildURIDislikePostId(uriInfo, post.getIdentificador(), "dislike"));
				post.addLink(PostsAPILinkBuilder.buildURINeutroPostId(uriInfo, post.getIdentificador(), "neutro"));
				post.addLink(PostsAPILinkBuilder.buildURIDenunciarPostId(uriInfo, post.getIdentificador(), "denunciar"));
				post.addLink(PostsAPILinkBuilder.buildURIModificarPostId(uriInfo, post.getIdentificador(), "modificar"));
				post.addLink(PostsAPILinkBuilder.buildURIDeletePostId(uriInfo, post.getIdentificador(), "eliminar"));
			} else {
				throw new PostNotFoundException();
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
		return post;
	}

	@POST
	@Path("/{postid}/like")
	public String likePost(@PathParam("postid") String postid) {
		// POST: /posts/{postid}/like (1=like, 0 dislike) (Registered)(admin)
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
		}
		String result = "";
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
			// a ver si existe el post
			String insert;
			String query = "SELECT COUNT(identificador) FROM posts Where identificador='" + postid + "';";
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) == 0)
				throw new PostNotFoundException();
			rs.close();
			// ver si ya a denunciado
			query = "SELECT COUNT(id), estado FROM calificacion Where id_post='" + postid + "' and username='" + username + "';";
			rs = stmt.executeQuery(query);
			rs.next();
			int estado = rs.getInt("estado");
			if (rs.getInt(1) != 0) {
				if (estado == 2) // like: estado de las relaciones
									// --> 1=dislike, 2=like
					throw new LikeAlreadyFoundException();
				else
					insert = "UPDATE calificacion SET estado=2 WHERE id_post=" + postid + " and username='" + username + "';";
			} else
				insert = "INSERT INTO calificacion (username,id_post,estado) values ('" + username + "'," + postid + ",2);";
			rs.close();
			stmt.executeUpdate(insert);
			// actualizar contador del post
			String update;
			update = "UPDATE posts SET posts.calificaciones_positivas=posts.calificaciones_positivas+1, posts.publicacion_date=posts.publicacion_date WHERE posts.identificador='" + postid + "';";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			if (estado == 1) {
				update = "UPDATE posts SET posts.calificaciones_negativas=posts.calificaciones_negativas-1, posts.publicacion_date=posts.publicacion_date WHERE posts.identificador='" + postid + "';";
				stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			}
			rs = stmt.executeQuery("SELECT posts.calificaciones_positivas, posts.calificaciones_negativas FROM posts WHERE posts.identificador='" + postid + "';");
			if (rs.next()) {
				result = "{ \"calificaciones_positivas\": " + rs.getInt("calificaciones_positivas") + ", \"calificaciones_negativas\": " + rs.getInt("calificaciones_negativas") + "}";
			} else {
				throw new PostNotFoundException();
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
		return result;
	}

	@POST
	@Path("/{postid}/neutro")
	public String eliminarVoto(@PathParam("postid") String postid) {
		// POST: /posts/{postid}/like (1=like, 0 dislike) (Registered)(admin)
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
		}
		String result = "";
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
			// a ver si existe el post
			String query = "SELECT COUNT(identificador) FROM posts Where identificador='" + postid + "';";
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) == 0)
				throw new PostNotFoundException();
			rs.close();

			query = "SELECT COUNT(id), estado FROM calificacion Where id_post='" + postid + "' and username='" + username + "';";
			rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) != 0) {
				int estado = rs.getInt(2);
				rs.close();
				// like: estado de las relaciones --> 1=dislike, 2=like
				// Eliminar megusta/nomegusta
				con.setAutoCommit(false);
				String insert = "DELETE FROM calificacion WHERE username='" + username + "' and id_post=" + postid + ";";
				stmt.executeUpdate(insert);
				String update;
				if (estado == 2)
					update = "UPDATE posts SET posts.calificaciones_positivas=posts.calificaciones_positivas-1, posts.publicacion_date=posts.publicacion_date WHERE posts.identificador='" + postid + "' and posts.calificaciones_positivas>0;";
				else
					update = "UPDATE posts SET posts.calificaciones_negativas=posts.calificaciones_negativas-1, posts.publicacion_date=posts.publicacion_date WHERE posts.identificador='" + postid + "' and posts.calificaciones_negativas>0;";
				stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
				con.commit();
				rs = stmt.executeQuery("SELECT posts.calificaciones_positivas, posts.calificaciones_negativas FROM posts WHERE posts.identificador='" + postid + "';");
				if (rs.next()) {
					result = "{ \"calificaciones_positivas\": " + rs.getInt("calificaciones_positivas") + ", \"calificaciones_negativas\": " + rs.getInt("calificaciones_negativas") + "}";
				} else {
					throw new PostNotFoundException();
				}
				rs.close();
			} else {
				con.rollback();
				throw new PostVoteNotFoundException();
			}
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new InternalServerException(e.getMessage());
			}
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

	@POST
	@Path("/{postid}/dislike")
	public String dislikePost(@PathParam("postid") String postid) {
		// POST: /posts/{postid}/like (1=like, 0 dislike) (Registered)(admin)
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
		}
		String result = "";
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
			// a ver si existe el post
			String query = "SELECT COUNT(identificador) FROM posts Where identificador='" + postid + "';";
			String insert;
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) == 0)
				throw new PostNotFoundException();
			rs.close();
			// ver si ya a denunciado
			query = "SELECT COUNT(id), estado FROM calificacion Where id_post='" + postid + "' and username='" + username + "';";
			rs = stmt.executeQuery(query);
			rs.next();
			int estado = rs.getInt("estado");
			if (rs.getInt(1) != 0) {
				if (estado == 1) // like: estado de las relaciones
									// --> 1=dislike, 2=like
					throw new DislikeAlreadyFoundException();
				else
					insert = "UPDATE calificacion SET estado=1 WHERE id_post=" + postid + " and username='" + username + "';";
			} else
				insert = "INSERT INTO calificacion (username,id_post,estado) values ('" + username + "'," + postid + ",1);";
			rs.close();
			stmt.executeUpdate(insert);

			// actualizar contador del post
			String update = "UPDATE posts SET posts.calificaciones_negativas=posts.calificaciones_negativas+1, posts.publicacion_date=posts.publicacion_date WHERE posts.identificador='" + postid + "';";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			if (estado == 2) {
				update = "UPDATE posts SET posts.calificaciones_positivas=posts.calificaciones_positivas-1, posts.publicacion_date=posts.publicacion_date WHERE posts.identificador='" + postid + "';";
				stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			}
			rs = stmt.executeQuery("SELECT posts.calificaciones_positivas, posts.calificaciones_negativas FROM posts WHERE posts.identificador='" + postid + "';");
			if (rs.next()) {
				result = "{ \"calificaciones_positivas\": " + rs.getInt("calificaciones_positivas") + ", \"calificaciones_negativas\": " + rs.getInt("calificaciones_negativas") + "}";
			} else {
				throw new PostNotFoundException();
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
		return result;
	}

	@POST
	@Path("/{postid}/denunciar")
	public void denunciaPost(@PathParam("postid") String postid) {
		// POST: /posts/{postid}/denunciar (1=denunciar, 0 desdenunciar)
		// (Registered)(admin)
//		if (!security.isUserInRole("moderador") || !security.isUserInRole("admin")) {
//			throw new ForbiddenException("You are not allowed...");
//		}
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
			// ver si ya a denunciado
			query = "SELECT COUNT(id) FROM denuncias_post Where id_post='" + postid + "' and username='" + username + "';";
			rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) != 0)
				throw new DenunciaAlreadyFoundException();

			// denunciar
			String insert = "INSERT INTO denuncias_post (username,id_post) values ('" + username + "'," + postid + ");";
			stmt.executeUpdate(insert);

			int MAX_DENUNCIAS = 20;
			query = "SELECT COUNT(id), revisado FROM denuncias_post, posts WHERE id_post='" + postid + "' and id_post=identificador;";
			rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt(1) >= MAX_DENUNCIAS * (rs.getInt(2) + 1)) {
				insert = "UPDATE posts SET visibilidad=visibilidad+10, posts.publicacion_date=posts.publicacion_date WHERE identificador='" + postid + "';";
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
	@Path("/{postid}/moderar")
	public void moderarPost(@PathParam("postid") String postid) {
		// PUT: /posts/{postid}/moderar (admin) => revisado y who_revisado

		if (!security.isUserInRole("moderador") && !security.isUserInRole("admin")) {
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
			String update = "UPDATE posts SET revisado=revisado+1, who_revisado='" + username + "', publicacion_date=publicacion_date, visibilidad=visibilidad-10 WHERE identificador=" + postid + " and visibilidad>9;";
			int lineas_afectadas = stmt.executeUpdate(update);
			if (lineas_afectadas == 0)
				throw new PostNotRevisableException();
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

	@PUT
	@Path("/{postid}")
	@Consumes(MediaType.INFORMER_API_POST)
	@Produces(MediaType.INFORMER_API_POST)
	public Post updatePost(@PathParam("postid") String postid, Post post) {
		// PUT: /posts/{postid} (Registered-Propietario) => visibilidad.
		try {
			int p = Integer.parseInt(postid);
			if (p < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new PostNotFoundException();
		}
		if (post.getVisibilidad() < 0 || post.getVisibilidad() > 2)
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
			String update = "UPDATE posts SET visibilidad=" + post.getVisibilidad() + " WHERE identificador=" + postid + " and username='" + username + "';";

			int lineas_afectadas = stmt.executeUpdate(update);
			if (lineas_afectadas == 0)
				throw new PostNotYoursException();
			query = "SELECT amigos.friend, posts.*, calificacion.estado, denuncias_post.id_post FROM posts LEFT JOIN calificacion ON calificacion.id_post=posts.identificador and calificacion.username='" + username + "' LEFT JOIN amigos ON amigos.friend='" + username
					+ "' and amigos.username=posts.username and amigos.estado=1 LEFT JOIN denuncias_post ON denuncias_post.id_post=posts.identificador and denuncias_post.username='" + username + "' WHERE identificador=" + postid + ";";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				post.setIdentificador(rs.getInt("identificador"));
				post.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				post.setNumcomentarios(rs.getInt("numcomentarios"));
				post.setCalificaciones_positivas(rs.getInt("calificaciones_positivas"));
				post.setCalificaciones_negativas(rs.getInt("calificaciones_negativas"));
				post.setRevisado(rs.getInt("revisado"));
				post.setWho_revised(rs.getString("who_revisado"));
				post.setLiked(rs.getInt("estado"));
				post.setVisibilidad(rs.getInt("visibilidad"));
				post.setContenido(rs.getString("contenido"));
				post.setAsunto(rs.getString("asunto"));
				post.setUsername(postAnonimo(username, rs.getString("username"), rs.getString("friend"), post.getVisibilidad()));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() - 1, "prev"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador(), "self"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() + 1, "next"));
				post.addLink(PostsAPILinkBuilder.buildURILikePostId(uriInfo, post.getIdentificador(), "like"));
				post.addLink(PostsAPILinkBuilder.buildURIDislikePostId(uriInfo, post.getIdentificador(), "dislike"));
				post.addLink(PostsAPILinkBuilder.buildURINeutroPostId(uriInfo, post.getIdentificador(), "neutro"));
				post.addLink(PostsAPILinkBuilder.buildURIDenunciarPostId(uriInfo, post.getIdentificador(), "denunciar"));
			} else
				throw new PostNotFoundException();
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
		return post;
	}

	@PUT
	@Path("/{postid}/eliminar")
	public void deletePostVisibilidad(@PathParam("postid") String postid) {
		// DELETE: /posts/{postid} (admin)
//		if (!security.isUserInRole("moderador") && !security.isUserInRole("admin")) {
//			throw new ForbiddenException("You are not allowed...");
//		}
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
			String update;
			if (!security.isUserInRole("moderador")) {
				update = "UPDATE posts SET visibilidad=3, publicacion_date=publicacion_date WHERE identificador=" + postid + " and username='" + username + "' and visibilidad!=3;";
			} else
				update = "UPDATE posts SET visibilidad=3, publicacion_date=publicacion_date WHERE identificador=" + postid + " and visibilidad!=3;";
			int lineas_afectadas = stmt.executeUpdate(update);
			if (lineas_afectadas == 0)
				throw new PostNotYoursException();
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

	@DELETE
	@Path("/{postid}")
	public void deletePost(@PathParam("postid") String postid) {
		// DELETE: /posts/{postid} (admin)
		if (!security.isUserInRole("admin")) {
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
			String update = "DELETE FROM posts WHERE identificador=" + postid + ";";
			int lineas_afectadas = stmt.executeUpdate(update);
			if (lineas_afectadas == 0)
				throw new PostNotFoundException();
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
	@Path("/novedades/{username}")
	@Produces(MediaType.INFORMER_API_POST_COLLECTION)
	public PostCollection getNoticias(@PathParam("username") String username, @QueryParam("o") String offset, @QueryParam("l") String length) {
		// GETs: /posts?{offset}{length} (Registered)(admin)
		String receptor = security.getUserPrincipal().getName();
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

		Connection con = null;
		Statement stmt = null;

		int posts_encontrados = 0; // variable para saber si hay link_next

		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			// usuario normal. No selecciona los de visiblidad > 2
			String query = "SELECT amigos.friend, posts.*, calificacion.estado, perfiles.foto  FROM posts LEFT JOIN calificacion ON calificacion.id_post=posts.identificador and calificacion.username='" + username + "' LEFT JOIN amigos ON amigos.friend='" + username
					+ "' and amigos.username=posts.username and amigos.estado=1 LEFT JOIN perfiles ON perfiles.username=posts.username LEFT JOIN comentarios ON comentarios.id_post=posts.identificador and comentarios.username='" + username + "'" + "WHERE posts.visibilidad<3 and (posts.username='" + username
					+ "' or posts.username IN(select amigos.friend from amigos where amigos.username='" + username + "' and amigos.estado=1) or (posts.identificador=calificacion.id_post and calificacion.username='" + username + "') or (posts.identificador=comentarios.id_post and comentarios.username='" + username
					+ "')) ORDER BY publicacion_date DESC LIMIT " + ioffset + ", " + (ilength + 1) + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (posts_encontrados++ == ilength)
					break;
				Post post = new Post();
				post.setIdentificador(rs.getInt("identificador"));
				post.setAsunto(rs.getString("asunto"));
				post.setPublicacion_date(rs.getTimestamp("publicacion_date"));
				post.setNumcomentarios(rs.getInt("numcomentarios"));
				post.setCalificaciones_positivas(rs.getInt("calificaciones_positivas"));
				post.setCalificaciones_negativas(rs.getInt("calificaciones_negativas"));
				post.setRevisado(rs.getInt("revisado"));
				post.setWho_revised(rs.getString("who_revisado"));
				post.setLiked(rs.getInt("estado"));
				post.setVisibilidad(0);
				post.setContenido(rs.getString("contenido"));
				post.setImagen_usuario(postAnonimoFoto(receptor, rs.getString("username"), rs.getString("friend"), post.getVisibilidad(),rs.getString("foto")));
				post.setUsername(postAnonimo(receptor, rs.getString("username"), rs.getString("friend"), post.getVisibilidad()));
				if (post.getIdentificador() != 1)
					post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() - 1, "prev"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador(), "self"));
				post.addLink(PostsAPILinkBuilder.buildURIPostId(uriInfo, post.getIdentificador() + 1, "next"));
				post.addLink(PostsAPILinkBuilder.buildURILikePostId(uriInfo, post.getIdentificador(), "like"));
				post.addLink(PostsAPILinkBuilder.buildURIDislikePostId(uriInfo, post.getIdentificador(), "dislike"));
				post.addLink(PostsAPILinkBuilder.buildURINeutroPostId(uriInfo, post.getIdentificador(), "neutro"));
				post.addLink(ComentariosAPILinkBuilder.buildURIComentarios(uriInfo, ioffset, length, Integer.toString(post.getIdentificador()), "comentarios"));
				post.addLink(PostsAPILinkBuilder.buildURIDenunciarPostId(uriInfo, post.getIdentificador(), "denunciar"));
				post.addLink(PostsAPILinkBuilder.buildURIModificarPostId(uriInfo, post.getIdentificador(), "modificar"));
				post.addLink(PostsAPILinkBuilder.buildURIDeletePostId(uriInfo, post.getIdentificador(), "eliminar"));
				posts.add(post);
			}
			rs.close();
			if (posts_encontrados == 0)
				throw new PostCollectionNotFoundException();
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
			posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo, Integer.toString(prev), length, null, "prev"));
		posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo, offset, length, null, "self"));
		if (posts_encontrados > ilength)
			posts.addLink(PostsAPILinkBuilder.buildURIPosts(uriInfo, Integer.toString(next), length, null, "next"));
		posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, "likes", 0, 10, "ranking likes"));
		posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, "dislikes", 0, 10, "ranking dislikes"));
		posts.addLink(PostsAPILinkBuilder.buildURIRankingPosts(uriInfo, "coments", 0, 10, "ranking coments"));
		return posts;
	}

	private String postAnonimo(String yo, String autor, String amigo, int visibilidad) {
		if (yo.equals(autor))
			return autor;
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
	
	private String postAnonimoFoto(String yo, String autor, String amigo, int visibilidad, String foto) {
		if (yo.equals(autor))
			return foto;
		if (visibilidad == 0) {
			return anonymous_picture;
		}
		if (visibilidad == 1) {
			if (amigo == null)
				return anonymous_picture;
			return foto;
		}
		// if (visibilidad == 2)
		return foto;
	}
}
