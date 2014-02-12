package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

public interface MediaType {
	public final static String INFORMER_API_USER = "application/vnd.informer.api.user+json";
	public final static String INFORMER_API_USER_COLLECTION = "application/vnd.informer.api.user.collection+json";
	
	public final static String INFORMER_API_POST = "application/vnd.informer.api.post+json";
	public final static String INFORMER_API_POST_COLLECTION = "application/vnd.informer.api.post.collection+json";
	
	public final static String INFORMER_API_COMENTARIO = "application/vnd.informer.api.comentario+json";
	public final static String INFORMER_API_COMENTARIO_COLLECTION = "application/vnd.informer.api.comentario.collection+json";
	
	public final static String INFORMER_API_SALA = "application/vnd.informer.api.sala+json";
	public final static String INFORMER_API_SALA_COLLECTION = "application/vnd.informer.api.sala.collection+json";
	
	public final static String INFORMER_API_MENSAJE = "application/vnd.informer.api.mensaje+json";
	public final static String INFORMER_API_MENSAJE_COLLECTION = "application/vnd.informer.api.mensaje.collection+json";
	
	public final static String INFORMER_API_ERROR = "application/vnd.dsa.informer.error+json";
	
	public final static String INFORMER_API_NOTIFICATION = "application/vnd.dsa.informer.notification+json";
	
	public final static String INFORMER_API_LINK = "application/vnd.dsa.informer.link+json";
	public final static String INFORMER_API_LINK_COLLECTION = "application/vnd.dsa.informer.link.collection+json";
}