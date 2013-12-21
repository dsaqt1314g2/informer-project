package eetac.upc.edu.dsa.dsaqt1314g2.informer.api.model;

import java.util.ArrayList;
import java.util.List;

import eetac.upc.edu.dsa.dsaqt1314g2.informer.api.links.Link;

public class PostCollection {

	private List<Post> posts = new ArrayList<Post>();
	private ArrayList<Link> links = new ArrayList<Link>();

	public void addLink(Link link) {
		links.add(link);
		return;
	}

	public void add(Post post) {
		posts.add(post);
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}

}
