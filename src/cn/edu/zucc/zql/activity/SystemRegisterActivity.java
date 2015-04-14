package cn.edu.zucc.zql.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;
import cn.edu.zucc.zql.teachingmaterialmanager.MyApp;
import cn.edu.zucc.zql.teachingmaterialmanager.R;

import com.alibaba.fastjson.JSONObject;

public class SystemRegisterActivity extends Activity {

	private MyApp myapp;
	EditText userpwd1Ed,userpwd2Ed,userNameEd;
	Spinner userLevelSpn;
	String username,userpwd1,userpwd2,userlevel;

    JSONObject result = null;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_register);
		myapp = (MyApp) getApplication();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());
		userpwd1Ed = (EditText)findViewById(R.id.systemUserPassword1);
		userpwd2Ed = (EditText)findViewById(R.id.systemUserPassword2);
		userNameEd = (EditText)findViewById(R.id.systemUserName);
		userLevelSpn = (Spinner)findViewById(R.id.systemUserLevel);

		TextView title = (TextView)findViewById(R.id.commomTitle);
		title.setText("系统用户注册");
		Button back = (Button)findViewById(R.id.commomBack);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		
		findViewById(R.id.systemButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

		    	username = userNameEd.getText().toString();
		    	userpwd1 = userpwd1Ed.getText().toString();
		    	userpwd2 = userpwd2Ed.getText().toString();
		    	userlevel = userLevelSpn.getItemAtPosition(userLevelSpn.getSelectedItemPosition()).toString();
		    	
		    	if(!userpwd1.equals(userpwd2)){
		    		Toast.makeText(getApplicationContext(), "两次密码不一致!!!", Toast.LENGTH_SHORT).show();
		    		return;
		    	}
				
				Thread adduserTh = new Thread(addSystemUser);
				adduserTh.start();
				try {
					adduserTh.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	            if(result == null){
	            	Toast.makeText(SystemRegisterActivity.this,"请求超时!!!", Toast.LENGTH_SHORT).show();
	            	return ;
	            }
	            if(result.getIntValue("errorCode") == 0)
	            {
	            	Toast.makeText(SystemRegisterActivity.this,"系统用户注册成功!!!", Toast.LENGTH_SHORT).show();
	            	Intent intent = new Intent(SystemRegisterActivity.this,MainActivity.class);
	            	SystemRegisterActivity.this.startActivity(intent); 
	            	SystemRegisterActivity.this.finish();
	        	}
	            else if(result.getIntValue("errorCode") == 100){
	            	Toast.makeText(SystemRegisterActivity.this,"签名出错!!!", Toast.LENGTH_SHORT).show();
	            	return;
	            }
			}
		});
		
	}

	Runnable addSystemUser = new Runnable(){  
	    @Override  
	    public void run() {
        	
	    	Map<String, String> params = new HashMap<String, String>();
            params.put("password", Tools.md5(userpwd1));
            params.put("name", username);
            params.put("level", userlevel);
            params.put("timestamp", Tools.getTimestamp());
            
            NameValuePair pair1 = new BasicNameValuePair("name", username);
            NameValuePair pair2 = new BasicNameValuePair("password", Tools.md5(userpwd1));
            NameValuePair pair3 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
            NameValuePair pair4 = new BasicNameValuePair("signature",Tools.signature(params));
            NameValuePair pair5 = new BasicNameValuePair("level", userlevel);
            
            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            pairList.add(pair1);
            pairList.add(pair2);
            pairList.add(pair3);
            pairList.add(pair4);
            pairList.add(pair5);

            result = null;
            
            try
            {
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                        pairList,HTTP.UTF_8);
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "users/addUser");
                httpPost.setEntity(requestHttpEntity);
                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,10000);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,10000);
                HttpResponse response = httpClient.execute(httpPost);
                result = Tools.getResponse(response);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            int i = 1;
            while(result == null){
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	if(i == 5)
            		break;
            	i ++;
            }
        	System.out.println(result);
        	
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.system_register, menu);
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
