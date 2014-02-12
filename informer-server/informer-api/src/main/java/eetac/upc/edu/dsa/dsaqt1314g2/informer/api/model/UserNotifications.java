package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

public class UserNotifications {

	// primary key las dos idnt y username
	private String username;

	private int numamigos;
	private int numpost;
	private int numcomment;
	private int numlikes;
	private int numdislikes;
	private int participacion;

	private int n_s_amistad;
	private int n_i_sala;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getNumamigos() {
		return numamigos;
	}

	public void setNumamigos(int numamigos) {
		this.numamigos = numamigos;
	}

	public int getNumpost() {
		return numpost;
	}

	public void setNumpost(int numpost) {
		this.numpost = numpost;
	}

	public int getNumcomment() {
		return numcomment;
	}

	public void setNumcomment(int numcomment) {
		this.numcomment = numcomment;
	}

	public int getNumlikes() {
		return numlikes;
	}

	public void setNumlikes(int numlikes) {
		this.numlikes = numlikes;
	}

	public int getNumdislikes() {
		return numdislikes;
	}

	public void setNumdislikes(int numdislikes) {
		this.numdislikes = numdislikes;
	}

	public int getParticipacion() {
		return participacion;
	}

	public void setParticipacion(int participacion) {
		this.participacion = participacion;
	}

	public int getN_s_amistad() {
		return n_s_amistad;
	}

	public void setN_s_amistad(int n_s_amistad) {
		this.n_s_amistad = n_s_amistad;
	}

	public int getN_i_sala() {
		return n_i_sala;
	}

	public void setN_i_sala(int n_i_sala) {
		this.n_i_sala = n_i_sala;
	}

}
