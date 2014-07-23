package event.planning.Boomset;

import com.crashlytics.android.Crashlytics;
import event.planning.Boomset.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;

public class SplashActivity extends Activity {
	private String userName;
	private String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	Crashlytics.start(this);
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		userName = preferences.getString("userName", "");
		password = preferences.getString("password", "");
		if (userName.equals("") || password.equals("")) {
			setContentView(R.layout.splashscreen);
			
			CountDownTimer countdown = new CountDownTimer(2000, 1000) {
				
				@Override
				public void onTick(long millisUntilFinished) {
					
				}
				
				@Override
				public void onFinish() {
					Intent loginIntent = new Intent(SplashActivity.this, LogInActivity.class);
					startActivity(loginIntent);
					finish();
				}
			};
			
			countdown.start();
			
		} else {
			Intent eventListIntent = new Intent(SplashActivity.this, EventList.class);
			startActivity(eventListIntent);
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		return;
	}

}
