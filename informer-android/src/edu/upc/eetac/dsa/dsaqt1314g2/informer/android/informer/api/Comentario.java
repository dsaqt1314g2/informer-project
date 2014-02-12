package edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api;

import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

public class Comentario {

	private int identificador;
	private int id_post;
	private String username;
	private int visibilidad; // 0=anonimo, 1=nombre para amigos, 2=publico, 3=no
								// visible, 5=pendiente de moderar
	private String contenido;
	private Date publicacion_date;
	private int revisado;
	private String who_revisado;

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

	public int getId_post() {
		return id_post;
	}

	public void setId_post(int id_post) {
		this.id_post = id_post;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getVisibilidad() {
		return visibilidad;
	}

	public void setVisibilidad(int visibilidad) {
		this.visibilidad = visibilidad;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public Date getPublicacion_date() {
		return publicacion_date;
	}

	public void setPublicacion_date(Date publicacion_date) {
		this.publicacion_date = publicacion_date;
	}

	public int getRevisado() {
		return revisado;
	}

	public void setRevisado(int revisado) {
		this.revisado = revisado;
	}

	public String getWho_revisado() {
		return who_revisado;
	}

	public void setWho_revisado(String who_revisado) {
		this.who_revisado = who_revisado;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	
	public String getLinkByRel(String rel) {
		int i = 0;
		while(!links.get(i).getRel().equals(rel)) {
			i++;
		}
		return links.get(i).getUri();
	}

}
