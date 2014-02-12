package edu.upc.eetac.dsa.dsaqt1314g2.informer.android;

import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.InformerAPI;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.Post;

public class WritePost extends Activity {

	//private final static String TAG = WritePost.class.toString();

	private class PostTask extends AsyncTask<String, Void, Post> {
		private URL url;
		private ProgressDialog pd;

		public PostTask(URL url) {
			super();
			this.url = url;
		}

		@Override
		protected Post doInBackground(String... params) {
			InformerAPI api = new InformerAPI();
			Post post = api.createPost(url, params[0], params[1], params[2]);
			return post;
		}

		@Override
		protected void onPostExecute(Post result) {
			showPosts();
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
		showPosts();
	}

	public void postPost(View v) {
		EditText etSubject = (EditText) findViewById(R.id.etSubject);
		EditText etContent = (EditText) findViewById(R.id.etContent);
		Spinner etvisibilidad = (Spinner) findViewById(R.id.spinner_visibilidad);

		String subject = etSubject.getText().toString();
		String content = etContent.getText().toString();
		int visibilidad = etvisibilidad.getSelectedItemPosition();

		(new PostTask(url)).execute(subject, content, Integer.toString(visibilidad));
	}

	private void showPosts() {
		Intent intent = new Intent(this, Informer.class);
		startActivity(intent);
		finish();
	}

	// 2.0 and above
	@Override
	public void onBackPressed() {
		showPosts();
	}

	// Before 2.0
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showPosts();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
