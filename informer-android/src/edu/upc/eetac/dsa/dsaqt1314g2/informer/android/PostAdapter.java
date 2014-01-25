package edu.upc.eetac.dsa.dsaqt1314g2.informer.android;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.InformerAPI;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.Post;

public class PostAdapter extends BaseAdapter {
	private final static String TAG = PostAdapter.class.toString();
	private ArrayList<Post> data;
	private LayoutInflater inflater;
	private Context context;

	public PostAdapter(Context context, ArrayList<Post> data) {
		super();
		inflater = LayoutInflater.from(context);
		this.data = data;
		this.context = context;
	}

	private static class ViewHolder { // elementos del View del elemento
		TextView tvSubject;
		TextView tvUsername;
		TextView tvDate;
		TextView tvContent;
		TextView tvMeGusta;
		TextView tvNoMeGusta;
		TextView tvComentar;
		TextView tvComentarios;
	}

	// Metodos explicados en el PDF
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// return Long.parseLong(((Post)getItem(position)).getIdentificador());
		return ((Post) getItem(position)).getIdentificador();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) { // cuando no hay nada que reciclar
			convertView = inflater.inflate(R.layout.list_row_post, null); // inflamos
			viewHolder = new ViewHolder();
			viewHolder.tvSubject = (TextView) convertView.findViewById(R.id.tvSubject);
			viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			viewHolder.tvMeGusta = (TextView) convertView.findViewById(R.id.tvMeGusta);
			viewHolder.tvNoMeGusta = (TextView) convertView.findViewById(R.id.tvNoMeGusta);
			viewHolder.tvComentar = (TextView) convertView.findViewById(R.id.tvComentar);
			viewHolder.tvComentarios = (TextView) convertView.findViewById(R.id.tvComentarios);
			convertView.setTag(viewHolder); // le pone el tag a nivel de vista
		} else {
			viewHolder = (ViewHolder) convertView.getTag(); // recupera de la
															// vista qe toca
		}
		String subject = data.get(position).getAsunto();
		String username = data.get(position).getUsername();
		String content = data.get(position).getContenido();
		String date = SimpleDateFormat.getInstance().format(data.get(position).getPublicacion_date());
		viewHolder.tvSubject.setText(subject); // representar los datos
		viewHolder.tvUsername.setText(username);
		viewHolder.tvDate.setText(date);
		viewHolder.tvContent.setText(content);
		if (data.get(position).getNumcomentarios() == 1) //TODO: GET @STRING
			viewHolder.tvComentarios.setText(data.get(position).getNumcomentarios() + " comentario");
		else
			viewHolder.tvComentarios.setText(data.get(position).getNumcomentarios() + " comentario"+"s");
		if (data.get(position).getLiked() == 2)
			viewHolder.tvMeGusta.setTextColor(Color.GREEN);
		else if (data.get(position).getLiked() == 1)
			viewHolder.tvNoMeGusta.setTextColor(Color.RED);
		viewHolder.tvMeGusta.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "ME GUSTA Me ha pulsado " + data.get(position).getIdentificador());
				// int i = 0;
				// boolean encontrado = false;
				// int limit = data.get(position).getLinks().size();
				// while (i < limit && encontrado == false) {
				// if
				// (data.get(position).getLinks().get(i).getRel().equals("like"))
				// encontrado = true;
				// i++;
				// }
				// String URL;
				// Log.d(TAG,limit+"   "+data.get(position).getLinks().get(i).getUri());
				// if (encontrado == true) {
				// Log.d(TAG,"bieeen");
				// URL = data.get(position).getLinks().get(i).getUri();
				// (new LikeTask()).execute(URL);
				// }
				String URL = "http://192.168.1.128:8080/informer-api/posts/" + data.get(position).getIdentificador() + "/like";
				(new LikeTask()).execute(URL);
				((TextView) v).setTextColor(Color.GREEN);
			}
		});
		viewHolder.tvNoMeGusta.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "NO ME GUSTA Me ha pulsado " + data.get(position).getIdentificador());
				String URL = "http://192.168.1.128:8080/informer-api/posts/" + data.get(position).getIdentificador() + "/dislike";
				(new LikeTask()).execute(URL);
				((TextView) v).setTextColor(Color.RED);
			}
		});
		viewHolder.tvComentar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "COMENTAR Me ha pulsado " + data.get(position).getIdentificador());
			}
		});
		return convertView;
	}

	private class LikeTask extends AsyncTask<String, Void, String> {
		// private ProgressDialog pd;
		private InformerAPI api = new InformerAPI();

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				api.postCalificacion(new URL(params[0]));
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
			return params[0];
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}
}
