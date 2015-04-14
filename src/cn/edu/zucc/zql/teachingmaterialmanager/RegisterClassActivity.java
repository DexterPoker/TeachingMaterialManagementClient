package cn.edu.zucc.zql.teachingmaterialmanager;

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
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zucc.zql.activity.MeActivity;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;

import com.alibaba.fastjson.JSONObject;

public class RegisterClassActivity extends Activity {

	static private int openfileDialogId = 0; 
	private MyApp myapp;
	private JSONObject result;
	private ArrayAdapter teacherAdapter;
	private List<String> teacher = new ArrayList<String>();
	private String classname,teacherid,masterid,userlevel;
	private EditText filenameEd,classNameEd;
	private Spinner teacherSpn,masterSpn;
	private List<String> mlist = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_class);
		myapp = (MyApp) getApplication();

		myapp.showInfo();
		
		classNameEd = (EditText)findViewById(R.id.registerClassClassName);
		teacherSpn = (Spinner)findViewById(R.id.registerClassTeacher);
		masterSpn = (Spinner)findViewById(R.id.registerClassMaster);
		Button confirm = (Button)findViewById(R.id.registerClassSubmitButton);
		
		TextView title = (TextView)findViewById(R.id.commomTitle);
		title.setText("课程注册");
		Button back = (Button)findViewById(R.id.commomBack);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		int i = 0;
		Thread getTeacher = new Thread(getAllTeacher);
		getTeacher.start();
		while(result == null){
			try {
				getTeacher.join();
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

			teacherAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, mlist);
			teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			teacherSpn.setAdapter(teacherAdapter);
			masterSpn.setAdapter(teacherAdapter);
		}
		
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				classname = classNameEd.getText().toString();
				teacherid = teacherSpn.getSelectedItem().toString();
				masterid = masterSpn.getSelectedItem().toString();
				userlevel = myapp.getLevel();
				
				if(classname == null ||classname.equals("")){
					Toast.makeText(getApplicationContext(), "请填写课程名!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				if(teacherid == null || teacherid.equals("")){
					Toast.makeText(getApplicationContext(), "请选择教师!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				if(masterid == null || masterid.equals("")){
					Toast.makeText(getApplicationContext(), "请选择主任!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				result = null;
				
				Thread addClazz = new Thread(registerClass);
				addClazz.start();
				try {
					addClazz.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(result == null){
	        		Toast.makeText(getApplicationContext(), "请求超时!!!",Toast.LENGTH_SHORT).show();
					return;
	        	}
	        		
	        	
	        	if(result.getIntValue("errorCode") == 0)
	            {
	        		Toast.makeText(getApplicationContext(), "添加课程成功!!!",Toast.LENGTH_SHORT).show();
	        		
		        	Intent intent = new Intent(RegisterClassActivity.this,MeActivity.class);
		        	RegisterClassActivity.this.startActivity(intent); 
		        	RegisterClassActivity.this.finish();
	        	}
	            else if (result.getIntValue("errorCode") == 200) {
					Toast.makeText(getApplicationContext(), "用户不存在!!!",Toast.LENGTH_SHORT).show();
					return;
	            }
	            else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!",Toast.LENGTH_SHORT).show();
					return;
	            }
	            else if(result.getInteger("errorCode") == 302){
	            	Toast.makeText(getApplicationContext(), "没有权限添加课程!!!",Toast.LENGTH_SHORT).show();
					return;
	            }
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_class, menu);
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
	
	Runnable getAllTeacher = new Runnable() {
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
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "users/getAllTeacher");
                httpPost.setEntity(requestHttpEntity);
                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
                HttpResponse response = httpClient.execute(httpPost);
                result = Tools.getResponse(response);
                System.out.println("getTeacher--------" + result);
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
	
	
	Runnable registerClass = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			String classname = classNameEd.getText().toString();
			
			String teacherId = teacherSpn.getSelectedItem().toString();
			String masterId = masterSpn.getSelectedItem().toString();
			
			teacherId = teacherId.substring(0,teacherId.indexOf("."));
			masterId = masterId.substring(0,masterId.indexOf("."));
			
			JSONObject params = new JSONObject();
			
			params.put("timestamp", Tools.getTimestamp());
			params.put("teacherId", teacherId);
			params.put("masterId", masterId);
			params.put("name",classname);
			params.put("userLevel", myapp.getLevel());
			
			 NameValuePair pair1 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
			 NameValuePair pair2 = new BasicNameValuePair("teacherId", teacherId);
			 NameValuePair pair3 = new BasicNameValuePair("name", classname);
			 NameValuePair pair4 = new BasicNameValuePair("userLevel", myapp.getLevel());
			 NameValuePair pair5 = new BasicNameValuePair("masterId", masterId);
			 NameValuePair pair6 = new BasicNameValuePair("signature", Tools.signature(params));
			 
	            
	            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
	            pairList.add(pair1);
	            pairList.add(pair2);
	            pairList.add(pair3);
	            pairList.add(pair4);
	            pairList.add(pair5);
	            pairList.add(pair6);
	            result = null;
	            try
	            {
	                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
	                        pairList,HTTP.UTF_8);
	                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "classes/addClazz");
	                httpPost.setEntity(requestHttpEntity);
	                HttpClient httpClient = new DefaultHttpClient();
	                httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
	                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
	                HttpResponse response = httpClient.execute(httpPost);
	                result = Tools.getResponse(response);
	                System.out.println("addClazz--------" + result);
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			finish();
			return;
		}
	}
	
}
