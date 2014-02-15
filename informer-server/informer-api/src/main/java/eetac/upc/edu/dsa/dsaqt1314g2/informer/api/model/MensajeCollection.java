package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

import java.util.ArrayList;
import java.util.List;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.Link;

public class MensajeCollection {

	private List<Mensaje> mensajes = new ArrayList<Mensaje>();
	private ArrayList<Link> links = new ArrayList<Link>();

	public void addLink(Link link) {
		links.add(link);
		return;
	}

	

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}

	public void add(Mensaje mensaje) {
		mensajes.add(mensaje);
	}



	public List<Mensaje> getMensajes() {
		return mensajes;
	}



	public void setMensajes(List<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}
	
	
}
