package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

import java.util.ArrayList;
import java.util.Date;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.Link;

public class User {

	private int identificador;
	private String username;
	private String name;
	private String correo;
	private Boolean genero;
	private Date fecha_nacimiento;
	private int uni_escuela;
	private String foto;
	private int estado_civil;
	private String lugar_de_residencia;
	private Boolean participar_GPS;
	
	private ArrayList <Link> links = new ArrayList<Link>();;

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
	
	public void setaddLinks(Link link) {
		this.links.add(link);
	}
	public Link  getaLinks(int a) {
		return(this.links.get(a));
	}
	
	
	

}
