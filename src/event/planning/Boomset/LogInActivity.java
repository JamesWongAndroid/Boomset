package event.planning.Boomset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class LogInActivity extends SherlockActivity {

	private ProgressDialog pdia;
	TextView forgotPasswordButton, whatIsButton, whatIsLink;
	Button sendSignupButton, cancelButton, signupButton;
	private String email, password, error, signName, signEmail, signPassword;
	String response = null;
	EditText loginEmail, loginPassword, signupName, signupEmail, signupPassword;
	private final int GONE = 8;
	private final int VISIBLE = 0;
	private boolean loginSuccessful = true;
	private boolean signinSuccessful = true;
	private String cookie;
	LoginDataBaseAdapter loginDataBaseAdapter;
	SharedPreferences.Editor editor;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Sherlock___Theme_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);
		
		getSupportActionBar().hide();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		loginDataBaseAdapter = new LoginDataBaseAdapter(this);
		loginDataBaseAdapter = loginDataBaseAdapter.open();
		
		loginEmail = (EditText) findViewById(R.id.emailfield);	
		loginEmail.setText("jameswong@boomset.com");
		loginPassword = (EditText) findViewById(R.id.passwordfield);
		loginPassword.setText("boomset");
		signupName = (EditText) findViewById(R.id.signupName);
		signupEmail = (EditText) findViewById(R.id.signUpEmail);
		signupPassword = (EditText) findViewById(R.id.signUpPassword);
		
		forgotPasswordButton = (TextView) findViewById(R.id.forgotText);
		forgotPasswordButton.setPaintFlags(forgotPasswordButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, 
				Uri.parse("https://www.boomset.com/apps/accounts/password_reset/")));
				
			}
		});
		
		whatIsLink = (TextView) findViewById(R.id.whatIsLink);
		whatIsLink.setPaintFlags(whatIsLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		whatIsLink.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, 
						Uri.parse("http://vimeo.com/61186360#at=0")));
				
			}
		});
		
		final Button loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setBackgroundColor(Color.parseColor("#d7d7d7"));
	//	loginButton.getBackground().setColorFilter(Color.parseColor("#d7d7d7"), PorterDuff.Mode.ADD);
		loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				email = loginEmail.getText().toString();
				password = loginPassword.getText().toString();
				editor.putString("userName", email);
				editor.putString("password", password);
				new loginTask().execute();
			}
		});
		
		signupButton = (Button) findViewById(R.id.signUpButton);
		signupButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				loginEmail.setVisibility(GONE);
				loginPassword.setVisibility(GONE);
				loginButton.setVisibility(GONE);
				forgotPasswordButton.setVisibility(GONE);
				signupButton.setVisibility(GONE);
				whatIsLink.setVisibility(GONE);
				signupEmail.setVisibility(VISIBLE);
				signupName.setVisibility(VISIBLE);
				signupPassword.setVisibility(VISIBLE);
				cancelButton.setVisibility(VISIBLE);
				sendSignupButton.setVisibility(VISIBLE);		

			}
		});
		
		sendSignupButton = (Button) findViewById(R.id.sendSigninButton);
		
		sendSignupButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				signName = signupName.getText().toString();
				signEmail = signupEmail.getText().toString();
				signPassword = signupPassword.getText().toString();
				editor.putString("userName", signEmail);
				editor.putString("password", signPassword);
				
				
				if (signName.equals("")||signEmail.equals("")||signPassword.equals("")) {
					Toast.makeText(getApplicationContext(), "Field Vaccant", Toast.LENGTH_SHORT).show();
				} else {
					
					new signInTask().execute();
					
				}
				
			}
		});
		
		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setBackgroundColor(Color.parseColor("#d7d7d7"));
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loginEmail.setVisibility(VISIBLE);
				loginPassword.setVisibility(VISIBLE);
				loginButton.setVisibility(VISIBLE);
				forgotPasswordButton.setVisibility(VISIBLE);
				signupButton.setVisibility(VISIBLE);
				whatIsLink.setVisibility(VISIBLE);
				signupEmail.setVisibility(GONE);
				signupName.setVisibility(GONE);
				signupPassword.setVisibility(GONE);
				cancelButton.setVisibility(GONE);
				sendSignupButton.setVisibility(GONE);
				
			}
		});	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		loginDataBaseAdapter.close();
	}
	
	private class loginTask extends AsyncTask<Void, Integer, Void> {

		private String userID;

		@Override
		  protected void onPreExecute() {
			super.onPreExecute();
			pdia = new ProgressDialog(LogInActivity.this);
		    pdia.setTitle("Loading");
		    pdia.setMessage("One Moment");       
		    pdia.setIndeterminate(false);
		    pdia.setCancelable(false);
		    pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    pdia.show();
		}
		
		
		@Override
		protected Void doInBackground(Void... params) {
			HttpURLConnection connection;
		    OutputStreamWriter request = null;
		    URL url = null;   
	        
	        String parameters = "username="+ email + "&password=" + password + "&csum=XYZ";
	        
	        try {
				url = new URL("https://rsvpme.in/apps/restapi/accounts/login/xhr");
			//	url = new URL("https://www.boomset.com/apps/restapi/accounts/login/xhr");
				trustEveryone();
				connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(true);
	            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	            connection.setRequestMethod("POST");  
	            String headerName = null;
	            request = new OutputStreamWriter(connection.getOutputStream());
	            request.write(parameters);
	            request.flush();
	            request.close();
	            
	            for (int i = 1; (headerName = connection
						.getHeaderFieldKey(i)) != null; i++) {
					if (headerName.equals("Set-Cookie")) {
						cookie = connection.getHeaderField(i);
						editor.putString("cookie", cookie);
						editor.commit();
					}
				}
	            
	            String line = "";               
	            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
	            BufferedReader reader = new BufferedReader(isr);
	            StringBuilder sb = new StringBuilder();
	            while ((line = reader.readLine()) != null)
	            {
	                sb.append(line + "\n");
	            }
	            // Response from server after login process will be stored in response variable.                
	            response = sb.toString();
	            // You can perform UI operations here
	       //     Toast.makeText(this,"Message from Server: \n"+ response, 0).show();             
	            isr.close();
	            reader.close();
	            
	            try {
					JSONObject jObject = new JSONObject(response);
					String aJsonString = jObject.getString("s");
					if (aJsonString.equals("1")) {
						loginSuccessful = true;
						JSONArray firstArray = jObject.getJSONArray("userData");
						userID = firstArray.getString(0);
						String userFirstName = firstArray.getString(1);
						String userLastName = firstArray.getString(2);
						editor.putString("userID", userID);
						editor.putString("userFirstName", userFirstName);
						editor.putString("userLastName", userLastName);
						editor.commit();
						Intent eventIntent = new Intent(getApplicationContext(), EventList.class);
						startActivity(eventIntent);
						finish();
						
					}
					
					if (aJsonString.equals("2")) {
	//					Toast.makeText(getApplicationContext(), "Login Fail", Toast.LENGTH_SHORT).show();
						error = "Login Fail";
						loginSuccessful = false;
						
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					loginSuccessful = false;
				}
	            
	            
			} catch (MalformedURLException e) {
				error = "Login Fail";
				loginSuccessful = false;
		//		Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				error = "Login Fail";
				loginSuccessful = false;
		//		Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
	        
	        return null;
		}
		
		  @Override
		  protected void onPostExecute(Void result) {
			 pdia.dismiss();
			 if (!loginSuccessful) {
				Toast.makeText(getApplicationContext(), "Login Fail", Toast.LENGTH_SHORT).show();
			}
		  }
	}
	
	private class signInTask extends AsyncTask<Void, Integer, Void> {

		@Override
		  protected void onPreExecute() {
			super.onPreExecute();
			pdia = new ProgressDialog(LogInActivity.this);
		    pdia.setTitle("Loading");
		    pdia.setMessage("One Moment");       
		    pdia.setIndeterminate(false);
		    pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    pdia.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			HttpURLConnection connection;
		    OutputStreamWriter request = null;
		    URL url = null;   
	        String response = null;
	        String parameters = "fullname=" + signName + "&username="+ signEmail + "&password=" + signPassword + "&csum=XYZ";
	        
	        try {
				url = new URL("https://rsvpme.in/apps/restapi/accounts/xhr/signMeUp");
				trustEveryone();
		//		url = new URL("https://www.boomset.com/apps/restapi/accounts/xhr/signMeUp");
				connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(true);
	            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	            connection.setRequestMethod("POST");  
	            String headerName = null;
	            request = new OutputStreamWriter(connection.getOutputStream());
	            request.write(parameters);
	            request.flush();
	            request.close();
	            
	            for (int i = 1; (headerName = connection
						.getHeaderFieldKey(i)) != null; i++) {
					if (headerName.equals("Set-Cookie")) {
						cookie = connection.getHeaderField(i);
						editor.putString("cookie", cookie);
						editor.commit();
					}
				}
	            
	            String line = "";               
	            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
	            BufferedReader reader = new BufferedReader(isr);
	            StringBuilder sb = new StringBuilder();
	            while ((line = reader.readLine()) != null)
	            {
	                sb.append(line + "\n");
	            }
	            // Response from server after login process will be stored in response variable.                
	            response = sb.toString();
	            // You can perform UI operations here
	       //     Toast.makeText(this,"Message from Server: \n"+ response, 0).show();             
	            isr.close();
	            reader.close();
	            
	            try {
					JSONObject jObject = new JSONObject(response);

					String aJsonString = jObject.getString("s");
					
					
					
					if (aJsonString.equals("1")) {
						String dataJsonString = jObject.getString("data");
						JSONObject jdataObject = new JSONObject(dataJsonString);
						
						String userID = jdataObject.getString("user_id");
						String fullName = jdataObject.getString("fullname");
						editor.putString("userID", userID);
						editor.putString("userFullName", fullName);
						editor.commit();
						
						signinSuccessful = true;
						Intent eventIntent = new Intent(getApplicationContext(), EventList.class);
						startActivity(eventIntent);
						finish();
					}
					
					if (aJsonString.equals("2")) {
				//		Toast.makeText(getApplicationContext(), "Login Fail", Toast.LENGTH_SHORT).show();
						signinSuccessful = false;
						connection.disconnect();
					}
					
				} catch (JSONException e) {
	//				Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					signinSuccessful = false;
					connection.disconnect();
				}
	            
	            
			} catch (MalformedURLException e) {
	//			Toast.makeText(getApplicationContext(), "fail1", Toast.LENGTH_SHORT).show();
				signinSuccessful = false;
				error = "Login Fail";
				
			} catch (IOException e) {
	//			Toast.makeText(getApplicationContext(), "fail2", Toast.LENGTH_SHORT).show();
				signinSuccessful = false;
				error = "Login Fail";
			}
			return null;
		}
		
		@Override
		  protected void onPostExecute(Void result) {
			 pdia.dismiss();
			 if (!signinSuccessful) {
				Toast.makeText(getApplicationContext(), "Sign Up Fail", Toast.LENGTH_SHORT).show();
			}
		  }
		
	}
	
	private void trustEveryone() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
	    			public boolean verify(String hostname, SSLSession session) {
	    				return true;
	    			}});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[]{new X509TrustManager(){
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {}
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {}
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}}}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(
					context.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}	
}
}