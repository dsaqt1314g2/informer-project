package edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
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

	public PostCollection getPosts(URL url) {	//URL direccion del recurso --> http://s:p/beeter-api/sings?o=x&&l=Y
		PostCollection posts = new PostCollection();

		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();	//conexion http movil y el server REST

			urlConnection.setRequestProperty("Accept", MediaType.INFORMER_API_POST_COLLECTION);
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true); //Puedes leer de la InputString de la conexion que se establezca entre los dos.
			urlConnection.connect(); //se hace la conexion

			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); //lee la respuesta del servidor
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			//sb.toString() es la respuesta JSON
			JSONObject jsonObject = new JSONObject(sb.toString()); //org.json
			JSONArray jsonLinks = jsonObject.getJSONArray("links"); //org.json
			parseLinks(jsonLinks, posts.getLinks()); //pone los elementos del array links en el arraylist links definido en sting collection

			JSONArray jsonStings = jsonObject.getJSONArray("stings");
			for (int i = 0; i < jsonStings.length(); i++) {
				JSONObject jsonSting = jsonStings.getJSONObject(i);
				Post post= parsePost(jsonSting); //convertir cada elemento del array en un objeto sting

				posts.add(post);
			}
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
			urlConnection.setRequestProperty("Accept",
					MediaType.INFORMER_API_POST);
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
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
		}finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
	 
		return post;
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

		JSONArray jsonStingLinks = source.getJSONArray("links");
		parseLinks(jsonStingLinks, post.getLinks());
		return post;
	}
	
	public Post createPost(URL url, String asunto, String contenido) {
		Post post = new Post();
		post.setAsunto(asunto);
		post.setContenido(contenido);
		
		HttpURLConnection urlConnection = null;
		try {
			JSONObject jsonPost = createJsonPost(post);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Accept",
					MediaType.INFORMER_API_POST);
			urlConnection.setRequestProperty("Content-Type",
					MediaType.INFORMER_API_POST);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.connect();
		
			PrintWriter writer = new PrintWriter(
					urlConnection.getOutputStream());
			writer.println(jsonPost.toString());
			writer.close();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
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
