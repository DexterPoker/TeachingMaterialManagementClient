package cn.edu.zucc.zql.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;
import cn.edu.zucc.zql.teachingmaterialmanager.MyApp;
import cn.edu.zucc.zql.teachingmaterialmanager.R;

import com.alibaba.fastjson.JSONObject;

public class SystemDeleteUserActivity extends Activity {

	private MyApp myapp;
	private JSONObject result;
	private String selectId;
	private Spinner spinner;
	private ArrayAdapter mAdapter;
	private List<String> mlist = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_delete_user);
		myapp = (MyApp) getApplication();
		Button btnDelete = (Button) findViewById(R.id.systemDeleteUserButton);
		spinner = (Spinner)findViewById(R.id.systemDeleteUserId);
		
		TextView title = (TextView)findViewById(R.id.commomTitle);
		title.setText("系统用户删除");
		Button back = (Button)findViewById(R.id.commomBack);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		int i = 0;
		Thread getUsers = new Thread(getAllUser);
		getUsers.start();
		while(result == null){
			try {
				getUsers.join();
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(i==3)
				break;
		}
		if(result == null){
    		Toast.makeText(getApplicationContext(), "请求超时!!!",Toast.LENGTH_SHORT).show();
    	}else {
			JSONObject mapUser = result.getJSONObject("users");
			Iterator iterator = mapUser.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				mlist.add(key + "." + mapUser.get(key));
				System.out.println(mlist);
			}

			mAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, mlist);
			mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(mAdapter);
		}
		
		btnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectId = spinner.getSelectedItem().toString();
				selectId = selectId.substring(0,selectId.indexOf("."));
				
				result = null;
				
				Thread delete = new Thread(deleteUser);
				delete.start();
	        	try {
	        		delete.join();
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
	        		Toast.makeText(getApplicationContext(), "删除成功!!!",Toast.LENGTH_SHORT).show();
	        		
		        	Intent intent = new Intent(SystemDeleteUserActivity.this,MeActivity.class);
		        	SystemDeleteUserActivity.this.startActivity(intent); 
		        	SystemDeleteUserActivity.this.finish();
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
		});
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.system_delete_user, menu);
		return true;
	}

	Runnable getAllUser = new Runnable() {
		public void run() {
			Map<String, String> params = new HashMap<String, String>();
            params.put("id", myapp.getId());
            params.put("timestamp", Tools.getTimestamp());
            params.put("signature", Tools.signature(params));
            
            NameValuePair pair1 = new BasicNameValuePair("id", myapp.getId());
            NameValuePair pair2 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
            NameValuePair pair3 = new BasicNameValuePair("signature",Tools.signature(params));
            
            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            pairList.add(pair1);
            pairList.add(pair2);
            pairList.add(pair3);
            result = null;
            try
            {
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                        pairList);
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "users/getAllUser");
                httpPost.setEntity(requestHttpEntity);
                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
                HttpResponse response = httpClient.execute(httpPost);
                result = Tools.getResponse(response);
                System.out.println("getuser--------" + result);
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
	
	Runnable deleteUser = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map<String, String> params = new HashMap<String, String>();
            params.put("id", selectId);
            params.put("timestamp", Tools.getTimestamp());
            params.put("signature", Tools.signature(params));
            
            NameValuePair pair1 = new BasicNameValuePair("id", selectId);
            NameValuePair pair2 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
            NameValuePair pair3 = new BasicNameValuePair("signature",Tools.signature(params));
            
            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            pairList.add(pair1);
            pairList.add(pair2);
            pairList.add(pair3);
            result = null;
            try
            {
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                        pairList);
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "users/deleteUser");
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
