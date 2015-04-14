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
import cn.edu.zucc.zql.activity.MeActivity;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;

import com.alibaba.fastjson.JSONObject;

public class DeleteClassUserActivity extends Activity {
	
	private MyApp myapp;
	private Button confirm;
	private Spinner studentSpn;
	private String studentid,classid,id;
	private JSONObject result;
	private ArrayAdapter mAdapter;
	private List<String> mlist = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_class_user);
		myapp = (MyApp) getApplication();
		confirm = (Button)findViewById(R.id.deleteClassUserConfirmButton);
		studentSpn = (Spinner)findViewById(R.id.deleteClassUserUserId);
		
		TextView title = (TextView)findViewById(R.id.commomTitle);
		title.setText("课程用户删除");
		Button back = (Button)findViewById(R.id.commomBack);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		int i = 0;
		Thread getStudentsTh = new Thread(getStudentsRun);
		getStudentsTh.start();
		while(result == null){
			try {
				getStudentsTh.join();
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
    		return;
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
			studentSpn.setAdapter(mAdapter);
		}
		
		
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				studentid = studentSpn.getSelectedItem().toString();
				studentid = studentid.substring(0,studentid.indexOf("."));
				
				classid = myapp.getClassid();
				id = myapp.getId();
				
				if(studentid == null || studentid.equals("")){
					Toast.makeText(getApplicationContext(), "请选择学生号!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				if(classid == null || classid.equals("")){
					Toast.makeText(getApplicationContext(), "获取用户当前课程号失败!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				if(id == null || id.equals("")){
					Toast.makeText(getApplicationContext(), "获取用户当前用户账号失败!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Thread deleteClassUserTh = new Thread(deleteClassUserRun);
				deleteClassUserTh.start();
				try {
					deleteClassUserTh.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(result == null){
					Toast.makeText(getApplicationContext(), "请求超时!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(result.getIntValue("errorCode") == 0){
					Toast.makeText(getApplicationContext(), "删除学生成功!!!", Toast.LENGTH_SHORT).show();
					Intent intent  = new Intent(DeleteClassUserActivity.this,MeActivity.class);
					DeleteClassUserActivity.this.finish();
				}
				else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 301){
					Toast.makeText(getApplicationContext(), "课程不存在!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 300){
					Toast.makeText(getApplicationContext(), "用户不属于该课程!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delete_class_user, menu);
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
	
	
	Runnable getStudentsRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			JSONObject params = new JSONObject();
			
			params.put("timestamp", Tools.getTimestamp());
			params.put("id", myapp.getId());
			params.put("classid", myapp.getClassid());
			
			 NameValuePair pair1 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
			 NameValuePair pair2 = new BasicNameValuePair("id", myapp.getId());
			 NameValuePair pair3 = new BasicNameValuePair("classid", myapp.getClassid());
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
	             HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "users/getAllStuents");
	             httpPost.setEntity(requestHttpEntity);
	             HttpClient httpClient = new DefaultHttpClient();
	             httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
	             httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
	             HttpResponse response = httpClient.execute(httpPost);
	             result = Tools.getResponse(response);
	             System.out.println("getAllStuents--------" + result);
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
	
	Runnable deleteClassUserRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			JSONObject params = new JSONObject();
			
			params.put("timestamp", Tools.getTimestamp());
			params.put("id", myapp.getClassid());
			params.put("studentId",studentid);
			
			 NameValuePair pair1 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
			 NameValuePair pair2 = new BasicNameValuePair("id", myapp.getClassid());
			 NameValuePair pair3 = new BasicNameValuePair("studentId",studentid );
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
	             HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "classes/deleteClazzUser");
	             httpPost.setEntity(requestHttpEntity);
	             HttpClient httpClient = new DefaultHttpClient();
	             httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
	             httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
	             HttpResponse response = httpClient.execute(httpPost);
	             result = Tools.getResponse(response);
	             System.out.println("deleteClazzUser--------" + result);
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
