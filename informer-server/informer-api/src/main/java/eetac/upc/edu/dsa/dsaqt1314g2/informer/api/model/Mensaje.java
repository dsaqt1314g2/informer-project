package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

import java.util.ArrayList;
import java.util.Date;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.Link;

public class Mensaje {

	private int identificador;
	private int id_sala;
	private String username;
	private String contenido;
	private Date last_update;

	private ArrayList<Link> links = new ArrayList<Link>();

	public void addLink(Link link) {
		links.add(link);
		return;
	}

	public int getIdentificador() {
		return identificador;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

	public int getId_sala() {
		return id_sala;
	}

	public void setId_sala(int id_sala) {
		this.id_sala = id_sala;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public Date getLast_update() {
		return last_update;
	}

	public void setLast_update(Date last_update) {
		this.last_update = last_update;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}

}
