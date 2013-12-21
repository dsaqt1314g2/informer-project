package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

import java.util.Date;

public class Post {
	private int identificador;
	private int id_user;
	private int visibilidad; //0=anonimo, 1=nombre para amigos, 2=publico, 3=no visible, 5=pendiente de moderar
	private String asunto;
	private String contenido;
	private Date publicacion_date;
	private int numcomentarios;
	private int calificaciones_positivas;
	private int calificaciones_negativas;
	private int revisado;
	private int who_revised;

	public int getId_user() {
		return id_user;
	}

	public void setId_user(int id_user) {
		this.id_user = id_user;
	}

	public int getVisibilidad() {
		return visibilidad;
	}

	public void setVisibilidad(int visibilidad) {
		this.visibilidad = visibilidad;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
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

	public int getNumcomentarios() {
		return numcomentarios;
	}

	public void setNumcomentarios(int numcomentarios) {
		this.numcomentarios = numcomentarios;
	}

	public int getCalificaciones_positivas() {
		return calificaciones_positivas;
	}

	public void setCalificaciones_positivas(int calificaciones_positivas) {
		this.calificaciones_positivas = calificaciones_positivas;
	}

	public int getCalificaciones_negativas() {
		return calificaciones_negativas;
	}

	public void setCalificaciones_negativas(int calificaciones_negativas) {
		this.calificaciones_negativas = calificaciones_negativas;
	}

	public int getRevisado() {
		return revisado;
	}

	public void setRevisado(int revisado) {
		this.revisado = revisado;
	}

	public int getWho_revised() {
		return who_revised;
	}

	public void setWho_revised(int who_revised) {
		this.who_revised = who_revised;
	}

	public int getIdentificador() {
		return identificador;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

}
