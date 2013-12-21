package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

import java.util.ArrayList;
import java.util.Date;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.Link;


public class Sala {
	
	private int identificador;
	private String username;
	private String nombre_sala;
	private int visibilidad;
	private String password;
	private Date last_update;
	private ArrayList <Link> links = new ArrayList<Link>();
	
	public int getIdentificador() {
		return identificador;
	}
	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNombre_sala() {
		return nombre_sala;
	}
	public void setNombre_sala(String nombre_sala) {
		this.nombre_sala = nombre_sala;
	}
	public int getVisibilidad() {
		return visibilidad;
	}
	public void setVisibilidad(int visibilidad) {
		this.visibilidad = visibilidad;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ArrayList <Link> getLinks() {
		return links;
	}

	public Date getLast_update() {
		return last_update;
	}
	public void setLast_update(Date last_update) {
		this.last_update = last_update;
	}
	public void setLinks(ArrayList <Link> links) {
		this.links = links;
	}
	
	public void addLinks(Link link) {
		this.links.add(link);
	}
	
	
	
	
	
	
}
