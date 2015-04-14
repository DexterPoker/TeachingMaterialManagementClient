package cn.edu.zucc.zql.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;
import cn.edu.zucc.zql.teachingmaterialmanager.MyApp;
import cn.edu.zucc.zql.teachingmaterialmanager.R;

import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushManager;

public class LoginActivity extends Activity {

	private MyApp myapp ;
	AlertDialog ipDailog;
	EditText useridEd,userpwdEd;
	String userid, userpwd;
    JSONObject result = null;
    private boolean isExit = false;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		myapp = (MyApp) getApplication();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
//		LayoutInflater factory = LayoutInflater.from(this);
//		final View myView = factory.inflate(R.layout.ipset, null);
//		Button ipButton = (Button)findViewById(R.id.IPButton);
	    

	    
		Button loginButton = (Button)findViewById(R.id.buttonLogin);
		useridEd = (EditText)findViewById(R.id.userID);
		userpwdEd = (EditText)findViewById(R.id.userPassword);
		

//		PushManager.getInstance().initialize(this.getApplicationContext());
		
		loginButton.setOnClickListener(new login());
		
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

	Runnable loginRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

		    if(myapp.getCid() == null||myapp.equals("")){
		    	PushManager.getInstance().initialize(getApplicationContext());
				myapp.setCid(PushManager.getInstance().getClientid(getApplicationContext()));
		    }
			
		    Map<String, String> params = new HashMap<String, String>();
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            params.put("id", userid);
            params.put("password", Tools.md5(userpwd));
            params.put("cid", myapp.getCid());
            params.put("timestamp", Tools.getTimestamp());
            
            
            NameValuePair pair1 = new BasicNameValuePair("id", userid);
            NameValuePair pair2 = new BasicNameValuePair("password", Tools.md5(userpwd));
            NameValuePair pair3 = new BasicNameValuePair("cid", myapp.getCid());
            NameValuePair pair4 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
            NameValuePair pair5 = new BasicNameValuePair("signature",Tools.signature(params));
            
            pairList.add(pair1);
            pairList.add(pair2);
            pairList.add(pair3);
            pairList.add(pair4);
            pairList.add(pair5);
            
            result = null;
            
            try
            {
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                        pairList);
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "login/check");
                System.out.println(Constant.getBaseURI());
                httpPost.setEntity(requestHttpEntity);
                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
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
	
	class login implements OnClickListener{  
        //  
        public void onClick(View v) {
        	userid = useridEd.getText().toString();
        	userpwd = userpwdEd.getText().toString();
        	
            if(userid == null ||userid.equals("")){
				Toast.makeText(getApplicationContext(), "请输入账号!!!",Toast.LENGTH_SHORT).show();
				return;
            }
            
            if(userpwd == null ||userpwd.equals("")){
				Toast.makeText(getApplicationContext(), "请输入密码!!!",Toast.LENGTH_SHORT).show();
				return;
            }
            
        	Thread logincheck = new Thread(loginRun);
        	logincheck.start();
        	try {
				logincheck.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	if(result == null){
        		Toast.makeText(getApplicationContext(), "请求超时!!!",Toast.LENGTH_SHORT).show();
				return;
        	}
        		
        	
        	if(result.getIntValue("errorCode") == 0)
            {
            	myapp.setId(result.getInteger("userId").toString());
            	myapp.setLevel(result.getString("userLevel"));
            	myapp.setName(result.getString("userName"));
            	JSONObject classes = result.getJSONObject("classes");
            	Map<String, String> mMap = new HashMap<String, String>();
            	Iterator iterator = classes.keySet().iterator();
            	while (iterator.hasNext()) {
        			String key = (String) iterator.next();
        			mMap.put(key, classes.getString(key));
        		}
            	myapp.setClasses(mMap);
            	
            	myapp.showInfo();
            	
            	
	        	Intent intent = new Intent(LoginActivity.this,SelectClassActivity.class);
	        	LoginActivity.this.startActivity(intent); 
	        	LoginActivity.this.finish();
        	}
            else if(result.getIntValue("errorCode") == 201){
				Toast.makeText(getApplicationContext(), "密码不正确!!!",Toast.LENGTH_SHORT).show();
				return;
			}
            else if (result.getIntValue("errorCode") == 200) {
				Toast.makeText(getApplicationContext(), "用户不存在!!!",Toast.LENGTH_SHORT).show();
				return;
            }
            else if(result.getIntValue("errorCode") == 100){
				Toast.makeText(getApplicationContext(), "签名出错!!!",Toast.LENGTH_SHORT).show();
				return;
            }
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
	
}
