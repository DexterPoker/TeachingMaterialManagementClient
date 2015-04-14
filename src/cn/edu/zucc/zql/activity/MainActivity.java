package cn.edu.zucc.zql.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zucc.zql.teachingmaterialmanager.R;

public class MainActivity extends TabActivity {

	private TabHost tabHost;
	private static final String HOME = "主页";
	private static final String MESSAGE = "教师推荐";
	private static final String MANAGE = "已下载";
	private static final String ME = "我";
	private boolean isExit = false;
	
	//
	private Intent homeIntent;
	private Intent messageIntent;
	private Intent manageIntent;
	private Intent meIntent;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost);

		tabHost = this.getTabHost();
		tabHost.setFocusable(true);
		prepareIntent();
		setupIntent();

	}

	private void setupIntent() {
		tabHost.addTab(buildTabSpec(HOME, R.drawable.icon_home, homeIntent));
		tabHost.addTab(buildTabSpec(MESSAGE, R.drawable.icon_message, messageIntent));
		tabHost.addTab(buildTabSpec(MANAGE, R.drawable.icon_manage, manageIntent));
		tabHost.addTab(buildTabSpec(ME, R.drawable.icon_me, meIntent));
	}

	private TabSpec buildTabSpec(String tag, int icon, Intent intent) {
		View view = View.inflate(MainActivity.this, R.layout.tab, null);
		((ImageView) view.findViewById(R.id.tab_iv_icon)).setImageResource(icon);
		((TextView) view.findViewById(R.id.tab_tv_text)).setText(tag);
		return tabHost.newTabSpec(tag).setIndicator(view).setContent(intent);
	}

	private void prepareIntent() {
		homeIntent = new Intent(this, HomeActivity.class);
		messageIntent = new Intent(this, MessageActivity.class);
		manageIntent = new Intent(this, ManagerActivity.class);
		meIntent = new Intent(this, MeActivity.class);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	public void onBackPressed() {
		exitBy2Click();
		return;
	}

	private void exitBy2Click() {  
	    Timer tExit = null;  
	    if (isExit == false) {  
	        isExit = true;
	        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
	        tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	                isExit = false;
	            }  
	        }, 2000);
	  
	    } else {  
	        finish();  
	        System.exit(0);  
	    }  
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
