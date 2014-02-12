package edu.upc.eetac.dsa.dsaqt1314g2.informer.android;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.InformerAPI;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.Post;

public class PostDetail extends Activity {
	public static final String TAG = PostDetail.class.toString();
	private InformerAPI api;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_detail_layout);
		api = new InformerAPI();
		URL url = null;
		try {
			url = new URL((String) getIntent().getExtras().get("url"));
		} catch (MalformedURLException e) {
		}
		(new FetchStingTask()).execute(url);
	}
	
	private void loadPost(Post post) {
		TextView tvDetailSubject = (TextView) findViewById(R.id.tvDetailSubject);
		TextView tvDetailContent = (TextView) findViewById(R.id.tvDetailContent);
		TextView tvDetailUsername = (TextView) findViewById(R.id.tvDetailUsername);
		TextView tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);
	 
		tvDetailSubject.setText(post.getAsunto());
		tvDetailContent.setText(post.getContenido());
		tvDetailUsername.setText(post.getUsername());
		tvDetailDate.setText(SimpleDateFormat.getInstance().format(
				post.getPublicacion_date()));
	}
	
	private class FetchStingTask extends AsyncTask<URL, Void, Post> {
		private ProgressDialog pd;
	 
		@Override
		protected Post doInBackground(URL... params) {
			Post post = api.getPost(params[0]);
			return post;
		}
	 
		@Override
		protected void onPostExecute(Post result) {
			loadPost(result);
			if (pd != null) {
				pd.dismiss();
			}
		}
	 
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(PostDetail.this);
			pd.setTitle("Loading...");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}
	 
	}
	
}
