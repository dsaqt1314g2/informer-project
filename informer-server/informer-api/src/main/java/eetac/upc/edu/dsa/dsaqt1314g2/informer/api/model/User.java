package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

import java.util.ArrayList;
import java.util.Date;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.Link;

public class User {

	//primary key las dos idnt y username
	private int identificador;
	private String username;
	
	private String name;
	private String correo;
	// True Macho False Hembra
	private Boolean genero;
	private Date fecha_nacimiento;
	//Pendiente de idear una enumeraicon por universidades y esculas
	private int uni_escuela;
	private String foto;
	// 0 Soltero, 1 sin lazos, 2 destrozado por ex/falta de mimos 3 follamiga, 4 relacionabierta, 5 relaciona a distancia, 6 con relacion, 7 comprometido, 
	private int estado_civil;
	private String lugar_de_residencia;
	// si tiene GPS activado o no
	private Boolean participar_GPS;
	private Date last_Update;
	private Boolean isModerador;
	
	public Boolean getIsModerador() {
		return isModerador;
	}

	public void setIsModerador(Boolean isModerador) {
		this.isModerador = isModerador;
	}

	private ArrayList <Link> links = new ArrayList<Link>();

	public Date getLast_Update() {
		return last_Update;
	}

	public void setLast_Update(Date last_Update) {
		this.last_Update = last_Update;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public Boolean getGenero() {
		return genero;
	}

	public void setGenero(Boolean genero) {
		this.genero = genero;
	}

	public Date getFecha_nacimiento() {
		return fecha_nacimiento;
	}

	public void setFecha_nacimiento(Date fecha_nacimiento) {
		this.fecha_nacimiento = fecha_nacimiento;
	}

	public int getUni_escuela() {
		return uni_escuela;
	}

	public void setUni_escuela(int uni_escuela) {
		this.uni_escuela = uni_escuela;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public int getEstado_civil() {
		return estado_civil;
	}

	public void setEstado_civil(int estado_civil) {
		this.estado_civil = estado_civil;
	}

	public String getLugar_de_residencia() {
		return lugar_de_residencia;
	}

	public void setLugar_de_residencia(String lugar_de_residencia) {
		this.lugar_de_residencia = lugar_de_residencia;
	}

	public Boolean getParticipar_GPS() {
		return participar_GPS;
	}

	public void setParticipar_GPS(Boolean participar_GPS) {
		this.participar_GPS = participar_GPS;
	}

	public ArrayList <Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList <Link> links) {
		this.links = links;
	}
	
	public void addLinks(Link link) {
		this.links.add(link);
	}
	
	
	
	

}
