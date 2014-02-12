package edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api;

import java.util.ArrayList;
import java.util.List;

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
