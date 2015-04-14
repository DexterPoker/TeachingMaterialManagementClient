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

import com.alibaba.fastjson.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;
import cn.edu.zucc.zql.bean.MessageItem;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;
import cn.edu.zucc.zql.dbhelper.MessageDBHelper;
import cn.edu.zucc.zql.teachingmaterialmanager.MyApp;
import cn.edu.zucc.zql.teachingmaterialmanager.R;

public class SelectClassActivity extends Activity {

	private Spinner spinner;
	private ArrayAdapter<String> mAdapter;
	private MyApp myapp;
	private List<String> mlist = new ArrayList<String>();
	private String myclassName;
	private String myclassId;
	private JSONObject result;
	private boolean isExit = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_class);
		myapp = (MyApp) getApplication();
		System.out.println(myapp.getClasses());
		Button btnConfirm = (Button) findViewById(R.id.buttonSelectBottom);
		spinner = (Spinner)findViewById(R.id.classes);
		
		initData();
		mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mlist);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);

		
		
		
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(spinner.getSelectedItem()== null||spinner.getSelectedItem().equals("")){
					Toast.makeText(getApplicationContext(), "用户没有选课!!!",Toast.LENGTH_SHORT).show();
					return;
				}
				
				myclassName = (String) spinner.getSelectedItem();
				myclassId = myclassName.substring(0, myclassName.indexOf("."));
				myclassName = myclassName.substring(myclassName.indexOf(".")+1);
				myapp.setClassid(myclassId);
				myapp.setClassname(myclassName);
				
				
				Thread getClassLevel = new Thread(classList);
				getClassLevel.start();
	        	try {
	        		getClassLevel.join();
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
	            	myapp.setLevel(result.getString("classLevel"));
	            	
					Intent intent = new Intent(SelectClassActivity.this,MainActivity.class);
					SelectClassActivity.this.startActivity(intent);
					SelectClassActivity.this.finish();
	        	}
	            else if(result.getIntValue("errorCode") == 301){
					Toast.makeText(getApplicationContext(), "课程不存在!!!",Toast.LENGTH_SHORT).show();
					return;
				}
	            else if (result.getIntValue("errorCode") == 300) {
					Toast.makeText(getApplicationContext(), "用户不属于该课程!!!",Toast.LENGTH_SHORT).show();
					return;
	            }
	            else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!",Toast.LENGTH_SHORT).show();
					return;
	            }
			}
		});
				
				
	}

	Runnable classList = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map<String, String> params = new HashMap<String, String>();
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            params.put("id", myapp.getClassid());
            params.put("studentId", myapp.getId());
            params.put("timestamp", Tools.getTimestamp());
            params.put("signature", Tools.signature(params));
            
            NameValuePair pair1 = new BasicNameValuePair("id", myapp.getClassid());
            NameValuePair pair2 = new BasicNameValuePair("studentId", myapp.getId());
            NameValuePair pair3 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
            NameValuePair pair4 = new BasicNameValuePair("signature",Tools.signature(params));
            
            pairList.add(pair1);
            pairList.add(pair2);
            pairList.add(pair3);
            pairList.add(pair4);
            
            try
            {
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                        pairList);
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "login/classList");
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
    

	
	private void initData() {
		// TODO Auto-generated method stub
		Map<String, String> classes = myapp.getClasses();
		Iterator iterator = classes.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			mlist.add(key + "." + classes.get(key));
		}
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_class, menu);
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
