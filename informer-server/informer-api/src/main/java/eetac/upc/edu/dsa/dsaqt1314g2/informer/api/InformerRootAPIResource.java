package eetac.upc.edu.dsa.dsaqt1314g2.informer.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class InformerRootAPIResource {

	@Context
	private UriInfo uriInfo;

//	@GET
//	@Produces(MediaType.INFORMER_API_LINK_COLLECTION)
//	public InformerRootAPIResource getLinkResources() {
		//TODO
//		InformerRootAPI bra = new InformerRootAPI();
//		bra.addLink(LibrosAPILinkBuilder.buildURIRootAPI(uriInfo));
//		bra.addLink(LibrosAPILinkBuilder.buildTemplatedURIStings(uriInfo,"libros",true)); //rel = que es este enlace
//		bra.addLink(LibrosAPILinkBuilder.buildTemplatedURIStings(uriInfo,"libros",false)); //rel = que es este enlace
//		//bra.addLink(LibrosAPILinkBuilder.buildURIStings(uriInfo, "0", "5", "manolo", "sting"));
//		return bra;		
//	}
}
