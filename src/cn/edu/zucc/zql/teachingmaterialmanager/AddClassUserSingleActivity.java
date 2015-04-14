package cn.edu.zucc.zql.teachingmaterialmanager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zucc.zql.activity.MeActivity;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;
import cn.edu.zucc.zql.function.CallbackBundle;
import cn.edu.zucc.zql.function.OpenFileDialog;

import com.alibaba.fastjson.JSONObject;

public class AddClassUserSingleActivity extends Activity {

	static private int openfileDialogId = 0; 
	private MyApp myapp;
	private String studentId,filepath,classid;
	private EditText studentidEd,filenameEd;
	private Button mutlyConfirm,singleConfirm,choose,back;
	private JSONObject result;
	private TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_class_user_single);
		myapp = (MyApp) getApplication();
		
		title = (TextView)findViewById(R.id.commomTitle);
		title.setText("课程用户添加");
		back = (Button)findViewById(R.id.commomBack);
		studentidEd = (EditText) findViewById(R.id.addClassUserSingleStudentid);
		filenameEd = (EditText)findViewById(R.id.addClassUserMultyFileName);
		choose = (Button)findViewById(R.id.addClassUserMultyChooseButton);
		mutlyConfirm = (Button)findViewById(R.id.addClassUserMultyConfirmButton);
		singleConfirm = (Button)findViewById(R.id.addClassUserSingleConfirmButton);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		choose.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View arg0) {  
                showDialog(openfileDialogId);
            }  
        });
		
		singleConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				studentId = studentidEd.getText().toString();
				if(studentId == null || studentId.equals("")){
					Toast.makeText(getApplicationContext(), "请输入学生编号!!!", Toast.LENGTH_SHORT);
					return;
				}
				
				
				Thread addClassUser = new Thread(addClazzUserSingleRun);
				addClassUser.start();
				try {
					addClassUser.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(result == null){
					Toast.makeText(getApplicationContext(), "请求超时!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(result.getIntValue("errorCode") == 0){
					Toast.makeText(getApplicationContext(), "添加学生成功!!!", Toast.LENGTH_SHORT).show();
					Intent intent  = new Intent(AddClassUserSingleActivity.this,MeActivity.class);
					AddClassUserSingleActivity.this.startActivity(intent);
					AddClassUserSingleActivity.this.finish();
				}
				else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 205){
					Toast.makeText(getApplicationContext(), "不是学生或已经删除!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 303){
					Toast.makeText(getApplicationContext(), "学生已存在!!!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
		mutlyConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String filename = filenameEd.getText().toString();
				

				if(filename == null || filename.equals("")){
					Toast.makeText(getApplicationContext(), "请选择文件!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Thread multyupload = new Thread(upload);
				multyupload.start();
				try {
					multyupload.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				filenameEd.setText("");
				
				if(result == null){
					Toast.makeText(getApplicationContext(), "请求超时!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(result.getIntValue("errorCode") == 0){
					Toast.makeText(getApplicationContext(), "添加学生成功!!!", Toast.LENGTH_SHORT).show();
//					Intent intent  = new Intent(AddClassUserSingleActivity.this,MeActivity.class);
//					AddClassUserSingleActivity.this.startActivity(intent);
					AddClassUserSingleActivity.this.finish();
				}
				else if(result.getIntValue("errorCode") == 405){
					Toast.makeText(getApplicationContext(), "excel文件内容不匹配!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 205){
					Toast.makeText(getApplicationContext(), "不是学生或已经删除!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 303){
					Toast.makeText(getApplicationContext(), "学生已存在!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 301){
					Toast.makeText(getApplicationContext(), "课程不存在!!!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_class_user_single, menu);
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
    protected Dialog onCreateDialog(int id) {  
        if(id==openfileDialogId){  
            Map<String, Integer> images = new HashMap<String, Integer>();  
            images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root);
            images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up);
            images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder);
            images.put("xls", R.drawable.xlsfile);
            images.put("xlsx",R.drawable.xlsfile);
            images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);  
            Dialog dialog = OpenFileDialog.createDialog(id, this, "打开文件", new CallbackBundle() {  
                @Override  
                public void callback(Bundle bundle) {  
                    filepath = bundle.getString("path");
                    System.out.println("filepath----------" + filepath);
                    filenameEd.setText(filepath.substring(filepath.lastIndexOf("/") + 1));
                    setTitle(filepath);
                }  
            },   
            ".xls;.xlsx;",  
            images);  
            return dialog;
        }  
        return null;  
    } 
	
	
	Runnable addClazzUserSingleRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			classid = myapp.getClassid();
			
			JSONObject params = new JSONObject();
			
			params.put("timestamp", Tools.getTimestamp());
			params.put("id", classid);
			params.put("studentId",studentId);
			
			 NameValuePair pair1 = new BasicNameValuePair("timestamp", Tools.getTimestamp());
			 NameValuePair pair2 = new BasicNameValuePair("id", classid);
			 NameValuePair pair3 = new BasicNameValuePair("studentId", studentId);
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
	                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "classes/addClazzUser");
	                httpPost.setEntity(requestHttpEntity);
	                HttpClient httpClient = new DefaultHttpClient();
	                httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
	                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
	                HttpResponse response = httpClient.execute(httpPost);
	                result = Tools.getResponse(response);
	                System.out.println("addClazzUser--------" + result);
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
	
	
	Runnable upload = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String filelocation = filepath.substring(0,filepath.lastIndexOf("/"));
			String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
			
			StringBody idSB,timestampSB,filenameSB;
			try {
				idSB = new StringBody(myapp.getClassid(),Charset.forName("UTF-8"));
				filenameSB = new StringBody(filename,Charset.forName("UTF-8"));
				timestampSB = new StringBody(Tools.getTimestamp(),Charset.forName("UTF-8"));
				
				File file = new File(filelocation, filename);
		    	try {
		    	     HttpClient client = new DefaultHttpClient();  
		    	     String postURL = Constant.getBaseURI() + "classes/addClazzUserMulty";
		    	     HttpPost post = new HttpPost(postURL);
		    	     FileBody bin = new FileBody(file);
		    	     MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"));
		    	     reqEntity.addPart("file", bin);
		    	     reqEntity.addPart("fileName", filenameSB);
		    	     reqEntity.addPart("id", idSB);
		    	     reqEntity.addPart("timestamp",timestampSB);
//		    	     reqEntity.addPart("signature",new StringBody(Tools.signature(params)));
		    	     post.setEntity(reqEntity);
		    	     HttpResponse response = client.execute(post);
		    	     result = Tools.getResponse(response);
		                System.out.println("addClazzUserMulty--------" + result);
		    	} catch (Exception e) {
		    	    e.printStackTrace();
		    	}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	    	int i = 1;
            while(result == null){
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	if(i == 10)
            		break;
            	i ++;
            }
        	System.out.println("result---" + result);
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
