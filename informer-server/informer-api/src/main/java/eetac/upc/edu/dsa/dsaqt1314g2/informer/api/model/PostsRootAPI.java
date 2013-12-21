package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

import java.util.ArrayList;
import java.util.List;

import eetac.upc.edu.dsa.rodrigo.libros.api.links.Link;


public class PostsRootAPI {

	private List<Link> rw;

	public PostsRootAPI() {
		rw = new ArrayList<Link>();
	}

	public void addLink(Link link) {
		rw.add(link);
	}

	public void setRw(List<Link> rw) {
		this.rw = rw;
	}

	public List<Link> getRw() {
		return rw;
	}

}
