package edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api;

import java.util.ArrayList;
import java.util.List;



public class UserCollection {
	
	private List<User> users = new ArrayList<User>();	
	private List<Link> links = new ArrayList<Link>();
	
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public void add(User user) {
		users.add(user);
	}

	public void addLink(Link link) {
		links.add(link);
	}
	
}
