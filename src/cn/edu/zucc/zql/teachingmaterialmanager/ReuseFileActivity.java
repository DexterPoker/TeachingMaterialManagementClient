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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zucc.zql.activity.MeActivity;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;

import com.alibaba.fastjson.JSONObject;

public class ReuseFileActivity extends Activity {

	private MyApp myapp;
	private String classidBefore;
	private Spinner classIdBeforeSp;
	private Button confirmBtn;
	private JSONObject result;
	private ArrayAdapter mAdapter;
	private List<String> mlist = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reuse_file);
		myapp = (MyApp) getApplication();
		classIdBeforeSp = (Spinner)findViewById(R.id.reuseFileClassId);
		confirmBtn = (Button)findViewById(R.id.reuseFileConfirmButton);
		
		TextView title = (TextView)findViewById(R.id.commomTitle);
		title.setText("导入收藏文件");
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
    		return;
    	}
		if(result.getIntValue("errorCode") == 0){
			JSONObject mapClass = result.getJSONObject("classes");
			Iterator iterator = mapClass.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				mlist.add(key + "." + mapClass.get(key));
				System.out.println(mlist);
			}

			mAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, mlist);
			mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			classIdBeforeSp.setAdapter(mAdapter);
		}
		else if(result.getIntValue("errorCode") == 304){
			Toast.makeText(getApplicationContext(), "没有权限获取课程!!!",Toast.LENGTH_SHORT).show();
    		return;
		}
		else if(result.getIntValue("errorCode") == 306){
			Toast.makeText(getApplicationContext(), "导入源与当前课程相同!!!",Toast.LENGTH_SHORT).show();
    		return;
		}
		else if(result.getIntValue("errorCode") == 100){
			Toast.makeText(getApplicationContext(), "签名出错!!!",Toast.LENGTH_SHORT).show();
    		return;
		}
		
		confirmBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				classidBefore = classIdBeforeSp.getSelectedItem().toString();
				classidBefore = classidBefore.substring(0,classidBefore.indexOf("."));
				
				Thread reuseFileTh = new Thread(reuseFileRun);
				reuseFileTh.start();
				try {
					reuseFileTh.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(result == null){
					Toast.makeText(getApplicationContext(), "请求超时!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(result.getIntValue("errorCode") == 0){
					Toast.makeText(getApplicationContext(), "导入课程收藏文件成功!!!", Toast.LENGTH_SHORT).show();
					Intent intent  = new Intent(ReuseFileActivity.this,MeActivity.class);
					ReuseFileActivity.this.finish();
				}
				else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 200){
					Toast.makeText(getApplicationContext(), "用户不存在!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 404){
					Toast.makeText(getApplicationContext(), "没有修改文件权限!!!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
	}

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
	
	Runnable reuseFileRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			JSONObject params = new JSONObject();
			
			params.put("timestamp", Tools.getTimestamp());
			params.put("id", myapp.getId());
			params.put("className",myapp.getClassname());
			params.put("classIdBefore", classidBefore);
			params.put("classIdAfter", myapp.getClassid());
			
			 NameValuePair pair1 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
			 NameValuePair pair2 = new BasicNameValuePair("id", myapp.getId());
			 NameValuePair pair3 = new BasicNameValuePair("classIdBefore", classidBefore);
			 NameValuePair pair4 = new BasicNameValuePair("classIdAfter", myapp.getClassid());
			 NameValuePair pair5 = new BasicNameValuePair("className", myapp.getClassname());
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
	             HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "file/reuseFile");
	             httpPost.setEntity(requestHttpEntity);
	             HttpClient httpClient = new DefaultHttpClient();
	             httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
	             httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
	             HttpResponse response = httpClient.execute(httpPost);
	             result = Tools.getResponse(response);
	             System.out.println("reuseFile--------" + result);
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
		getMenuInflater().inflate(R.menu.reuse_file, menu);
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
