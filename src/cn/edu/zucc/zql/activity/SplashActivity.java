package cn.edu.zucc.zql.activity;

import cn.edu.zucc.zql.teachingmaterialmanager.MyApp;
import cn.edu.zucc.zql.teachingmaterialmanager.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.igexin.sdk.PushManager;
public class SplashActivity extends Activity {

	private MyApp myapp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myapp = (MyApp) getApplication();
		PushManager.getInstance().initialize(this.getApplicationContext());
		
        setContentView(R.layout.activity_splash);
		Handler splash = new Handler();
		splash.postDelayed(new splashhandler(), 1000);
	}

	class splashhandler implements Runnable{

        public void run() {

    		myapp.setCid(PushManager.getInstance().getClientid(getApplicationContext()));
    		
    		myapp.showInfo();
            startActivity(new Intent(getApplication(),LoginActivity.class));
            SplashActivity.this.finish();
        }
        
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			finish();
			return;
		}
	}
}
