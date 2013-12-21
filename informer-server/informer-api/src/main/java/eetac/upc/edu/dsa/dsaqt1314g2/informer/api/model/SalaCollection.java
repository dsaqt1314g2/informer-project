package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

import java.util.ArrayList;
import java.util.List;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.Link;


public class SalaCollection {
	
	private List<Sala> salas = new ArrayList<Sala>();		
	private List<Link> links = new ArrayList<Link>();
	
	public List<Sala> getSalas() {
		return salas;
	}
	public void setSalas(List<Sala> salas) {
		this.salas = salas;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public void add(Sala sala) {
		salas.add(sala);
	}

	public void addLink(Link link) {
		links.add(link);
	}

}
