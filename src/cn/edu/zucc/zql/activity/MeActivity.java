package cn.edu.zucc.zql.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import cn.edu.zucc.zql.teachingmaterialmanager.AddClassUserSingleActivity;
import cn.edu.zucc.zql.teachingmaterialmanager.ClassDeleteActivity;
import cn.edu.zucc.zql.teachingmaterialmanager.DeleteClassUserActivity;
import cn.edu.zucc.zql.teachingmaterialmanager.MyApp;
import cn.edu.zucc.zql.teachingmaterialmanager.R;
import cn.edu.zucc.zql.teachingmaterialmanager.RegisterClassActivity;
import cn.edu.zucc.zql.teachingmaterialmanager.ReuseFileActivity;

public class MeActivity extends Activity implements OnScrollListener {

	private MyApp myapp;
	private boolean isExit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me);
		myapp = (MyApp) getApplication();
		TextView name = (TextView)findViewById(R.id.meUserName);
		name.setText(myapp.getName());
		
		if(myapp.getLevel().toLowerCase().equals("admin"))
			findViewById(R.id.meClassManageFileLayout).setVisibility(View.GONE);
		else if(myapp.getLevel().toLowerCase().equals("teacher")||
				myapp.getLevel().toLowerCase().equals("master")){
			findViewById(R.id.meUserManageLayout).setVisibility(View.GONE);
			findViewById(R.id.meClassManageLayout).setVisibility(View.GONE);
		}
		else if(myapp.getLevel().toLowerCase().equals("student")||
				myapp.getLevel().toLowerCase().equals("assistant")){
			findViewById(R.id.meUserManageLayout).setVisibility(View.GONE);
			findViewById(R.id.meClassManageLayout).setVisibility(View.GONE);
			findViewById(R.id.meClassManageTeacherLayout).setVisibility(View.GONE);
			findViewById(R.id.meClassManageFileLayout).setVisibility(View.GONE);
		}
		
		findViewById(R.id.passwordChangeButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MeActivity.this,ChangePasswordActivity.class);
				MeActivity.this.startActivity(intent);
			}
		});
		
		
		findViewById(R.id.logoutButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MeActivity.this,LoginActivity.class);
				MeActivity.this.startActivity(intent);
			}
		});
		
		findViewById(R.id.switchButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MeActivity.this,SelectClassActivity.class);
				MeActivity.this.startActivity(intent);
			}
		});
		
		findViewById(R.id.userSystemRegisterButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MeActivity.this,SystemRegisterActivity.class);
				MeActivity.this.startActivity(intent);
			}
		});
		
		findViewById(R.id.userSystemDeleteButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MeActivity.this,SystemDeleteUserActivity.class);
				MeActivity.this.startActivity(intent);
			}
		});
		findViewById(R.id.classRegisterButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MeActivity.this,RegisterClassActivity.class);
				MeActivity.this.startActivity(intent);
			}
		});
		findViewById(R.id.classUserAddButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MeActivity.this,AddClassUserSingleActivity.class);
				MeActivity.this.startActivity(intent);
			}
		});
		findViewById(R.id.classDeleteButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MeActivity.this,ClassDeleteActivity.class);
						MeActivity.this.startActivity(intent);
					}
				});
		findViewById(R.id.classUserdeleteButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MeActivity.this,DeleteClassUserActivity.class);
						MeActivity.this.startActivity(intent);
					}
				});
		
		findViewById(R.id.meclassFileReuseButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MeActivity.this,ReuseFileActivity.class);
						MeActivity.this.startActivity(intent);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.me, menu);
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
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
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
