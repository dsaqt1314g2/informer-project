package edu.upc.eetac.dsa.dsaqt1314g2.informer.android;

import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.InformerAPI;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.Post;

public class WritePost extends Activity {

	private final static String TAG = WritePost.class.toString();
	 
	private class PostStingTask extends AsyncTask<String, Void, Post> {
		private URL url;
		private ProgressDialog pd;
 
		public PostStingTask(URL url) {
			super();
			this.url = url;
		}
 
		@Override
		protected Post doInBackground(String... params) {
			InformerAPI api = new InformerAPI();
			Post post = api.createPost(url, params[0], params[1]);
			return post;
		}
 
		@Override
		protected void onPostExecute(Post result) {
			showStings();
			if (pd != null) {
				pd.dismiss();
			}
		}
 
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(WritePost.this);
 
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}
 
	}
 
	private URL url;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_post_layout);
		url = (URL) getIntent().getExtras().get("url");
	}
 
	public void cancel(View v) {
		finish();
	}
 
	public void postSting(View v) {
		EditText etSubject = (EditText) findViewById(R.id.etSubject);
		EditText etContent = (EditText) findViewById(R.id.etContent);
 
		String subject = etSubject.getText().toString();
		String content = etContent.getText().toString();
 
		(new PostStingTask(url)).execute(subject, content);
	}
	
	private void showStings(){
		Intent intent = new Intent(this, Informer.class);
		startActivity(intent);
		finish();
	}
}
