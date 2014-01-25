package edu.upc.eetac.dsa.dsaqt1314g2.informer.android;

import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.InformerAPI;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.User;

public class Login extends Activity {
	private final static String TAG = Login.class.toString();
	private InformerAPI api = new InformerAPI();
	
	String serverAddress = "";
	String serverPort = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
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
		String username = prefs.getString("username", null);
		String password = prefs.getString("userpass", null);

		if ((username != null) && (password != null)) {
			Intent intent = new Intent(this, Informer.class);
			startActivity(intent);
			finish();
		}
		setContentView(R.layout.login_layout);
	}

	public void signIn(View v) {
		EditText etUsername = (EditText) findViewById(R.id.etUsername);
		EditText etPassword = (EditText) findViewById(R.id.etPassword);
		String username = etUsername.getText().toString();
		String password = etPassword.getText().toString();
		(new FetchUsersTask()).execute(username, password, "http://"+serverAddress+":"+serverPort+"/informer-api/users/" + username);
	}
	
	public void register(View v) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+serverAddress+":"+serverPort+"/informer-auth/register.jsp"));
		startActivity(browserIntent);
	}

	private void startInformerActivity() {
		String username = ((EditText) findViewById(R.id.etUsername)).getText().toString();
		String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
		SharedPreferences prefs = getSharedPreferences("informer-profile", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.putString("username", username);
		editor.putString("userpass", password);
		boolean done = editor.commit();
		if (done)
			Log.d(TAG, "preferences set");
		else
			Log.d(TAG, "preferences not set. THIS A SEVERE PROBLEM");
		Intent intent = new Intent(this, Informer.class);
		startActivity(intent);
		finish();
	}

	// Progresso void (indeterminado), result PostCollection (lo que devuelve)
	private class FetchUsersTask extends AsyncTask<String, Void, User> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(Login.this);
			pd.setTitle("Identificando...");
			pd.setCancelable(false); // no es cancelable
			pd.setIndeterminate(true);
			pd.show();
		}

		@Override
		protected User doInBackground(String... params) {
			User user = api.getUser(params[0], params[1], params[2]);
			return user;
		}

		@Override
		protected void onPostExecute(User result) {
			if (pd != null) {
				pd.dismiss();
			}
			if (result.getUsername() != "")
				startInformerActivity();
			//else
				
		}
	}

}
