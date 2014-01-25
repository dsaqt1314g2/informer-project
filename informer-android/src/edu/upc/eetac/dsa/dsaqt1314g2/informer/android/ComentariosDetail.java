package edu.upc.eetac.dsa.dsaqt1314g2.informer.android;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;
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

	String uri;
	String contenido;
	int visibilidad = -1;

	int offset = 0;
	final int length = 7;
	boolean end = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "VIENDO COMENTARIOS()");

		Intent intent = getIntent();
		postid = intent.getStringExtra("postid");
		uri = intent.getStringExtra("comentarios");

		SharedPreferences prefs = getSharedPreferences("informer-profile", Context.MODE_PRIVATE);
		username = prefs.getString("username", null);

		setTitle("Comentarios");
		setContentView(R.layout.comentarios_layout);

		comentarioList = new ArrayList<Comentario>();
		adapter = new ComentarioAdapter(this, comentarioList);
		setListAdapter(adapter);

		api = new InformerAPI();

		getListView().setOnScrollListener(new OnScrollListener() {
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (getListView().getLastVisiblePosition() == (adapter.getCount() - 1) && end) {
					getComentarios();
					end = false;
				}
			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
		});
		getComentarios();

	}

	private void getComentarios() {
		URL url = null;
		try {
			url = new URL(uri + "?o=" + offset + "&l=" + length);
			offset += length;
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
				if (comentarioList.size() != 0)
					Toast.makeText(ComentariosDetail.this, getString(R.string.notify_comentarios_nomore), Toast.LENGTH_SHORT).show();
				else {
					result = new ComentarioCollection();
					Comentario c = new Comentario();
					c.setContenido(getString(R.string.no_hay_comentarios));
					c.setUsername("");
					result.add(c);
				}
			}
			if (result != null) {
				addComentarios(result);
				end = true;
			}
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
		contenido = ((EditText) findViewById(R.id.contenido)).getText().toString();
		if (contenido == "")
			return;
		final String items[] = { "Anónimo", "Sólo amigos", "Público" };
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("Privacidad");
		ab.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int choice) {
				visibilidad = choice;
				(new PublicarTask()).execute(uri, contenido, Integer.toString(visibilidad));
				((EditText) findViewById(R.id.contenido)).setText("");
				Log.d(TAG, "jasidjansiodnsadn" + Integer.toString(visibilidad));
			}
		});
		ab.show();
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
			return api.createComentario(url, params[1], params[2]);
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
						String URL = comentarioList.get(position).getLinkByRel("denunciar");
						(new DenunciarComentarioTask()).execute(URL, Integer.toString(position));
					} else
						Toast.makeText(ComentariosDetail.this, "No existe esta acción", Toast.LENGTH_SHORT).show();
				} else {
					if (choice <= 3) {
						String URL = comentarioList.get(position).getLinkByRel("modificar");
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
