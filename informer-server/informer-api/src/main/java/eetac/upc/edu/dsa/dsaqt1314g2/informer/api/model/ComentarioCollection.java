package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

import java.util.ArrayList;
import java.util.List;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.Link;


public class ComentarioCollection {
	
	private List<Comentario> comentarios = new ArrayList<Comentario>();
	private ArrayList<Link> links = new ArrayList<Link>();
	
	public void addLink(Link link) {
		links.add(link);
		return;
	}

	public void add(Comentario comentario) {
		comentarios.add(comentario);
	}
	
	public List<Comentario> getComentarios() {
		return comentarios;
	}

	public void setComentarios(List<Comentario> comentarios) {
		this.comentarios = comentarios;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	
}
