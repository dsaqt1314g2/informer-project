package edu.upc.eetac.dsa.dsaqt1314g2.informer.android;

import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.Comentario;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.ComentarioCollection;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.InformerAPI;

public class ComentariosDetail extends ListActivity {
	private final static String TAG = ComentariosDetail.class.toString();

	// Adatador ---> Para unir una lista(View) Datos(Modelo) (adaptador)
	// private ArrayAdapter<String> adapter;
	private ArrayList<Comentario> comentarioList;
	private ComentarioAdapter adapter;

	// implementacion del fetchPoststask
	private InformerAPI api;

	String serverAddress = "";
	String serverPort = "";

	String postid;
	String username;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "VIENDO COMENTARIOS()");

		Intent intent = getIntent();
		postid = intent.getStringExtra("postid");

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
		username = prefs.getString("username", null);
		final String password = prefs.getString("userpass", null);

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password.toCharArray());
			}
		});

		setTitle("Comentarios");
		setContentView(R.layout.comentarios_layout);

		comentarioList = new ArrayList<Comentario>();
		adapter = new ComentarioAdapter(this, comentarioList);
		setListAdapter(adapter);

		api = new InformerAPI();
		URL url = null;
		try {
			url = new URL("http://" + serverAddress + ":" + serverPort + "/informer-api/posts/" + postid + "/comentarios?o=1&l=1000");
		} catch (MalformedURLException e) {
			Log.d(TAG, e.getMessage(), e);
			finish();
		}
		(new FetchComentariosTask()).execute(url);

	}

	// Progresso void (indeterminado), result PostCollection (lo que devuelve)
	private class FetchComentariosTask extends AsyncTask<URL, Void, ComentarioCollection> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ComentariosDetail.this);
			pd.setTitle("Leyendo...");
			pd.setCancelable(false); // no es cancelable
			pd.setIndeterminate(true);
			pd.show();
		}

		@Override
		protected ComentarioCollection doInBackground(URL... params) {
			ComentarioCollection Comentarios = api.getComentarios(params[0]);
			return Comentarios;
		}

		@Override
		protected void onPostExecute(ComentarioCollection result) {
			if (result == null) {
				result = new ComentarioCollection();
				Comentario c = new Comentario();
				c.setContenido(getString(R.string.no_hay_comentarios));
				c.setUsername("");
				result.add(c);
			}
			addComentarios(result);
			if (pd != null) {
				pd.dismiss();
			}
		}
	}

	private void addComentarios(ComentarioCollection Comentarios) {
		comentarioList.addAll(Comentarios.getComentarios());
		adapter.notifyDataSetChanged();
	}

	public void publicar(View v) {
		String URL = "http://" + serverAddress + ":" + serverPort + "/informer-api/posts/" + postid + "/comentarios";
		String contenido = ((EditText) findViewById(R.id.contenido)).getText().toString();
		(new PublicarTask()).execute(URL, contenido);
		((EditText) findViewById(R.id.contenido)).setText("");
	}

	private class PublicarTask extends AsyncTask<String, Void, Comentario> {
		// private ProgressDialog pd;
		private InformerAPI api = new InformerAPI();

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Comentario doInBackground(String... params) {
			URL url;
			try {
				url = new URL(params[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
			return api.createComentario(url, params[1]);
		}

		@Override
		protected void onPostExecute(Comentario result) {
			if (result != null) {
				if (comentarioList.get(0).getContenido() == getString(R.string.no_hay_comentarios))
					comentarioList.remove(0);
				comentarioList.add(0, result);
				adapter.notifyDataSetChanged();
			} else
				Toast.makeText(ComentariosDetail.this, "No se ha podido publicar el comentario", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, final int position, final long id) {
		String items[] = { "Denunciar", "Ocultar", "Sólo amigos", "Público" };
		final boolean soyyo = username.equals(comentarioList.get(position).getUsername());
		if (soyyo)
			items = Arrays.copyOfRange(items, 1, items.length);
		else
			items = Arrays.copyOfRange(items, 0, 1);

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("Acciones");
		ab.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int choice) {
				if (!soyyo) {
					if (choice == 0) {
						String URL = "http://" + serverAddress + ":" + serverPort + "/informer-api/posts/" + postid + "/comentarios/" + comentarioList.get(position).getIdentificador() + "/denunciar";
						(new DenunciarComentarioTask()).execute(URL, Integer.toString(position));
					} else
						Toast.makeText(ComentariosDetail.this, "No existe esta acción", Toast.LENGTH_SHORT).show();
				} else {
					if (choice <= 3) {
						String URL = "http://" + serverAddress + ":" + serverPort + "/informer-api/posts/" + postid + "/comentarios/" + comentarioList.get(position).getIdentificador();
						(new UpdateVisibilidadTask()).execute(URL, Integer.toString(choice));
					} else
						Toast.makeText(ComentariosDetail.this, "No existe esta acción", Toast.LENGTH_SHORT).show();
				}
			}
		});
		ab.show();
	}

	private class DenunciarComentarioTask extends AsyncTask<String, Void, String> {
		// private ProgressDialog pd;
		private InformerAPI api = new InformerAPI();

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			URL url;
			try {
				url = new URL(params[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
			api.denunciarComentario(url);
			return params[1];
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				comentarioList.remove(Integer.parseInt(result));
				adapter.notifyDataSetChanged();
				Toast.makeText(ComentariosDetail.this, "Comentario denunciado!", Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(ComentariosDetail.this, "No se ha podido publicar el comentario", Toast.LENGTH_SHORT).show();
		}
	}

	private class UpdateVisibilidadTask extends AsyncTask<String, Void, Comentario> {
		// private ProgressDialog pd;
		private InformerAPI api = new InformerAPI();

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Comentario doInBackground(String... params) {
			URL url;
			try {
				url = new URL(params[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
			return api.updateVisibilidadComentario(url, params[1]);
		}

		@Override
		protected void onPostExecute(Comentario result) {
			if (result != null) {
				Toast.makeText(ComentariosDetail.this, "Hecho!", Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(ComentariosDetail.this, "No se ha podido modificar la visibilidad del comentario", Toast.LENGTH_SHORT).show();
		}
	}
}
