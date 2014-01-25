package edu.upc.eetac.dsa.dsaqt1314g2.informer.android;

import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.InformerAPI;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.Post;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.PostCollection;

public class Informer extends ListActivity {
	private final static String TAG = Informer.class.toString();

	// Adatador ---> Para unir una lista(View) Datos(Modelo) (adaptador)
	// private ArrayAdapter<String> adapter;
	private ArrayList<Post> postList;
	private PostAdapter adapter;

	// implementacion del fetchPoststask
	private InformerAPI api;

	String serverAddress = "";
	String serverPort = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "STARTING INFORMER()");

		AssetManager assetManager = getAssets();
		Properties config = new Properties();
		try {
			config.load(assetManager.open("config.properties"));
			serverAddress = config.getProperty("server.address");
			serverPort = config.getProperty("server.port");

			Log.d(TAG, "Configured server " + serverAddress + ":" + serverPort);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			finish();
		}

		SharedPreferences prefs = getSharedPreferences("informer-profile", Context.MODE_PRIVATE);
		final String username = prefs.getString("username", null);
		final String password = prefs.getString("userpass", null);

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password.toCharArray());
			}
		});

		setContentView(R.layout.informer_layout);

		postList = new ArrayList<Post>();
		adapter = new PostAdapter(this, postList);
		setListAdapter(adapter);

		api = new InformerAPI();
		URL url = null;
		try {
			url = new URL("http://" + serverAddress + ":" + serverPort + "/informer-api/posts?&o=0&l=100");
		} catch (MalformedURLException e) {
			Log.d(TAG, e.getMessage(), e);
			finish();
		}
		(new FetchPostsTask()).execute(url);

	}

	// Progresso void (indeterminado), result PostCollection (lo que devuelve)
	private class FetchPostsTask extends AsyncTask<URL, Void, PostCollection> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(Informer.this);
			pd.setTitle("Escuchando...");
			pd.setCancelable(false); // no es cancelable
			pd.setIndeterminate(true);
			pd.show();
		}

		@Override
		protected PostCollection doInBackground(URL... params) {
			PostCollection Posts = api.getPosts(params[0]);
			return Posts;
		}

		@Override
		protected void onPostExecute(PostCollection result) {
			// ArrayList<Post> Posts = new
			// ArrayList<Post>(result.getPosts());
			// for (Post s : Posts) {
			// Log.d(TAG, s.getPostId() + "-" + s.getSubject());
			// }
			addPosts(result);
			if (pd != null) {
				pd.dismiss();
			}
		}
	}

	private void addPosts(PostCollection Posts) {
		postList.addAll(Posts.getPosts());
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		View tv = ((ViewGroup) v).getChildAt(0);
		tv = ((ViewGroup) tv).getChildAt(1);
		if (tv.getVisibility() == View.GONE)
			tv.setVisibility(View.VISIBLE);
		else
			tv.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.informer_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.miWrite:
			URL url = null;
			try {
				url = new URL("http://" + serverAddress + ":" + serverPort + "/informer-api/posts");
			} catch (MalformedURLException e) {
				Log.d(TAG, e.getMessage(), e);
			}
			Intent intent = new Intent(this, WritePost.class);
			intent.putExtra("url", url);
			startActivity(intent);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
}
