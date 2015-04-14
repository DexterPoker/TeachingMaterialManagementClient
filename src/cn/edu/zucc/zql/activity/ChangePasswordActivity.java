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
import android.widget.TextView;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;
import cn.edu.zucc.zql.teachingmaterialmanager.MyApp;
import cn.edu.zucc.zql.teachingmaterialmanager.R;

import com.alibaba.fastjson.JSONObject;

@SuppressLint("NewApi")
public class ChangePasswordActivity extends Activity {
	
	private MyApp myapp;
	EditText pwdoldEd,pwdnew1Ed,pwdnew2Ed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		myapp = (MyApp) getApplication();
		System.out.println(myapp.getId());
//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
		pwdoldEd = (EditText)findViewById(R.id.oldPassword);
		pwdnew1Ed = (EditText)findViewById(R.id.newPassword);
		pwdnew2Ed = (EditText)findViewById(R.id.newPassword2);
		TextView title = (TextView)findViewById(R.id.commomTitle);
		title.setText("修改密码");
		Button back = (Button)findViewById(R.id.commomBack);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		Button changepwd = (Button)findViewById(R.id.changeConfirmButton);
		changepwd.setOnClickListener(new changePassword());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_password, menu);
		return true;
	}

	class changePassword implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String pwdold = pwdoldEd.getText().toString();
			String pwdnew1 = pwdnew1Ed.getText().toString();
			String pwdnew2 = pwdnew2Ed.getText().toString();
			myapp = (MyApp) getApplication();
//	    	if(!pwdnew1.equals(pwdnew2)){
//	    		Toast.makeText(ChangePasswordActivity.this, "两次密码不一致!!!", Toast.LENGTH_LONG).show();
//	    		return;
//	    	}
			
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("id" , myapp.getId());
			System.out.println(myapp.getId());
            params.put("passwordold", Tools.md5(pwdold));
            params.put("passwordnew", Tools.md5(pwdnew1));
            params.put("timestamp", Tools.getTimestamp());
            
            NameValuePair pair1 = new BasicNameValuePair("id", myapp.getId());
            NameValuePair pair2 = new BasicNameValuePair("passwordold", Tools.md5(pwdold));
            NameValuePair pair3 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
            NameValuePair pair4 = new BasicNameValuePair("signature",Tools.signature(params));
            NameValuePair pair5 = new BasicNameValuePair("passwordnew", Tools.md5(pwdnew1));
            
            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            pairList.add(pair1);
            pairList.add(pair2);
            pairList.add(pair3);
            pairList.add(pair4);
            pairList.add(pair5);

            JSONObject result = null;
            
            try
            {
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                        pairList);
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "users/changePassword");
                httpPost.setEntity(requestHttpEntity);
                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,10000);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,10000);
                HttpResponse response = httpClient.execute(httpPost);
                result = Tools.getResponse(response);
                System.out.println(result);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        	
            if(result == null)
            	return;
            
            if(result.getIntValue("errorCode") == 0)
            {
//            	Toast.makeText(SystemRegisterActivity.this,"add system user success", Toast.LENGTH_SHORT).show();
//				try {
//					Thread.sleep(Toast.LENGTH_SHORT);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
            	
            	
//            	Intent intent = new Intent(ChangePasswordActivity.this,MainActivity.class);
//            	ChangePasswordActivity.this.startActivity(intent); 
            	ChangePasswordActivity.this.finish();
            }
		}
	};
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			finish();
			return;
		}
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
}
