package edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class InformerAPI {
	private final static String TAG = InformerAPI.class.toString();
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public PostCollection getPosts(URL url) { // URL direccion del recurso -->
												// http://s:p/beeter-api/sings?o=x&&l=Y
		PostCollection posts = new PostCollection();
		
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
		
			urlConnection.setRequestProperty("Accept", MediaType.INFORMER_API_POST_COLLECTION);
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect(); // se hace la conexion
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			// sb.toString() es la respuesta JSON
			JSONObject jsonObject = new JSONObject(sb.toString()); // org.json
			JSONArray jsonLinks = jsonObject.getJSONArray("links"); // org.json
			parseLinks(jsonLinks, posts.getLinks());
			
			JSONArray jsonPosts = jsonObject.getJSONArray("posts");
			
			for (int i = 0; i < jsonPosts.length(); i++) {
				JSONObject jsonPost = jsonPosts.getJSONObject(i);
				Post post = parsePost(jsonPost);
				posts.add(post);
			}
			Log.d(TAG,"holaaa");
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}

		return posts;
	}

	public Post getPost(URL url) {
		Post post = new Post();

		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Accept", MediaType.INFORMER_API_POST);
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			JSONObject jsonPost = new JSONObject(sb.toString());
			post = parsePost(jsonPost);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}

		return post;
	}

	public User getUser(final String username, final String password, String url_string) {
		User user = new User();
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(url_string);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Accept", MediaType.INFORMER_API_USER);
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password.toCharArray());
				}
			});
			urlConnection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			JSONObject jsonUser = new JSONObject(sb.toString());
			user = parseUser(jsonUser);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return user;
	}

	private void parseLinks(JSONArray source, List<Link> links) throws JSONException {
		for (int i = 0; i < source.length(); i++) {
			JSONObject jsonLink = source.getJSONObject(i);
			Link link = new Link();
			link.setRel(jsonLink.getString("rel"));
			link.setTitle(jsonLink.getString("title"));
			link.setType(jsonLink.getString("type"));
			link.setUri(jsonLink.getString("uri"));
			links.add(link);
		}
	}

	private Post parsePost(JSONObject source) throws JSONException, ParseException {
		Post post = new Post();
		if (source.has("content"))
			post.setContenido(source.getString("contenido"));
		String tsLastModified = source.getString("publicacion_date").replace("T", " ");
		post.setPublicacion_date(sdf.parse(tsLastModified));
		post.setIdentificador(source.getInt("identificador"));
		post.setAsunto(source.getString("asunto"));
		post.setUsername(source.getString("username"));

//		JSONArray jsonStingLinks = source.getJSONArray("links");
//		parseLinks(jsonStingLinks, post.getLinks());
		return post;
	}

	private User parseUser(JSONObject source) throws JSONException, ParseException {
		User user = new User();
		if (source.has("username"))
			user.setUsername(source.getString("username"));
		return user;
	}

	public Post createPost(URL url, String asunto, String contenido) {
		Post post = new Post();
		post.setAsunto(asunto);
		post.setContenido(contenido);

		HttpURLConnection urlConnection = null;
		try {
			JSONObject jsonPost = createJsonPost(post);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Accept", MediaType.INFORMER_API_POST);
			urlConnection.setRequestProperty("Content-Type", MediaType.INFORMER_API_POST);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.connect();

			PrintWriter writer = new PrintWriter(urlConnection.getOutputStream());
			writer.println(jsonPost.toString());
			writer.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			jsonPost = new JSONObject(sb.toString());
			post = parsePost(jsonPost);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}

		return post;
	}

	private JSONObject createJsonPost(Post post) throws JSONException {
		JSONObject jsonSting = new JSONObject();
		jsonSting.put("asunto", post.getAsunto());
		jsonSting.put("contenido", post.getContenido());

		return jsonSting;
	}
}
