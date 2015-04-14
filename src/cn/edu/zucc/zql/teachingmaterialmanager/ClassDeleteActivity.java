package cn.edu.zucc.zql.teachingmaterialmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import cn.edu.zucc.zql.activity.MeActivity;
import cn.edu.zucc.zql.activity.SystemDeleteUserActivity;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;

import com.alibaba.fastjson.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ClassDeleteActivity extends Activity {

	private MyApp myapp;
	private Button confirm;
	private Spinner classSpn;
	private String classid,id;
	private JSONObject result;
	private ArrayAdapter mAdapter;
	private List<String> mlist = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_delete);
		myapp = (MyApp) getApplication();
		
		confirm = (Button)findViewById(R.id.classDeleteButton);
		classSpn = (Spinner)findViewById(R.id.classDeleteClassid);
		
		TextView title = (TextView)findViewById(R.id.commomTitle);
		title.setText("课程删除");
		Button back = (Button)findViewById(R.id.commomBack);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		int i = 0;
		Thread getAllClassesTh = new Thread(getAllClassesRun);
		getAllClassesTh.start();
		while(result == null){
			try {
				getAllClassesTh.join();
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
			JSONObject mapUser = result.getJSONObject("classes");
			Iterator iterator = mapUser.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				mlist.add(key + "." + mapUser.get(key));
				System.out.println(mlist);
			}

			mAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, mlist);
			mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			classSpn.setAdapter(mAdapter);
		}
		
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				classid = classSpn.getSelectedItem().toString();
				classid = classid.substring(0,classid.indexOf("."));
				
				result = null;
				
				Thread deleteClassTh = new Thread(deleteClassRun);
				deleteClassTh.start();
	        	try {
	        		deleteClassTh.join();
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
	        		Toast.makeText(getApplicationContext(), "删除课程成功!!!",Toast.LENGTH_SHORT).show();
	        		
		        	Intent intent = new Intent(ClassDeleteActivity.this,MeActivity.class);
		        	ClassDeleteActivity.this.finish();
	        	}
	            else if (result.getIntValue("errorCode") == 301) {
					Toast.makeText(getApplicationContext(), "课程不存在!!!",Toast.LENGTH_SHORT).show();
					return;
	            }
	            else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!",Toast.LENGTH_SHORT).show();
					return;
	            }
	            else if (result.getIntValue("errorCode") == 305) {
					Toast.makeText(getApplicationContext(), "没有权限删除课程!!!",Toast.LENGTH_SHORT).show();
					return;
	            }
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.class_delete, menu);
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
	
	Runnable deleteClassRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			JSONObject params = new JSONObject();
			
			params.put("timestamp", Tools.getTimestamp());
			params.put("id", myapp.getId());
			params.put("classid", classid);
			
			myapp.showInfo();
			
			 NameValuePair pair1 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
			 NameValuePair pair2 = new BasicNameValuePair("id", myapp.getId());
			 NameValuePair pair3 = new BasicNameValuePair("classid", classid);
			 NameValuePair pair4 = new BasicNameValuePair("signature", Tools.signature(params));
			 
	            
	         List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	         pairList.add(pair1);
	         pairList.add(pair2);
	         pairList.add(pair3);
	         pairList.add(pair4);
	         result = null;
	         try
	         {
	             HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
	                        pairList);
	             HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "classes/deleteClazz");
	             httpPost.setEntity(requestHttpEntity);
	             HttpClient httpClient = new DefaultHttpClient();
	             httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
	             httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
	             HttpResponse response = httpClient.execute(httpPost);
	             result = Tools.getResponse(response);
	             System.out.println("deleteClazz--------" + result);
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
	
	
	Runnable getAllClassesRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			JSONObject params = new JSONObject();
			
			params.put("timestamp", Tools.getTimestamp());
			params.put("id", myapp.getId());
			
			NameValuePair pair1 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
			NameValuePair pair2 = new BasicNameValuePair("id", myapp.getId());
			NameValuePair pair3 = new BasicNameValuePair("signature", Tools.signature(params));
			 
	            
	        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	        pairList.add(pair1);
	        pairList.add(pair2);
	        pairList.add(pair3);
	        result = null;
	        try
	        {
	            HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
	                       pairList);
	            HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "classes/getAllClasses");
	            httpPost.setEntity(requestHttpEntity);
	            HttpClient httpClient = new DefaultHttpClient();
	            httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
	            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
	            HttpResponse response = httpClient.execute(httpPost);
	            result = Tools.getResponse(response);
	            System.out.println("getAllClasses--------" + result);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }			
			int i = 1;
			while (result == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (i == 5)
					break;
				i++;
			}
			System.out.println(result);
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
	
}
