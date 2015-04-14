package cn.edu.zucc.zql.teachingmaterialmanager;

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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zucc.zql.bean.HomeItem;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;
import cn.edu.zucc.zql.dbhelper.HomeDBHelper;

import com.alibaba.fastjson.JSONObject;

public class ModifyFileActivity extends Activity {

	private MyApp myapp;
	private EditText filedescEd;
	private TextView filenameTv;
	private Spinner filetypeSp;
	private Button confirmBtn;
	private JSONObject result;
	private String filetype,filedesc,fileid;
	private HomeDBHelper helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_file);
		myapp = (MyApp) getApplication();
		filedescEd = (EditText)findViewById(R.id.modifyFiledesc);
		filenameTv = (TextView)findViewById(R.id.modifyFilename);
		filetypeSp = (Spinner)findViewById(R.id.modifyFiletype);
		confirmBtn = (Button)findViewById(R.id.modifyFileConfirmButton);
		
		TextView title = (TextView)findViewById(R.id.commomTitle);
		title.setText("文件信息修改");
		Button back = (Button)findViewById(R.id.commomBack);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		fileid =myapp.getCurrentFileId();
		HomeItem homeItem = getFileContent(fileid);
		
		filedescEd.setText(homeItem.getFileDesc());
		filenameTv.setText(homeItem.getFileName());
		
		
		confirmBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				filetype = filetypeSp.getSelectedItem().toString();
				filedesc = filedescEd.getText().toString();
				
				if(filedesc == null || filedesc.equals("")){
					Toast.makeText(getApplicationContext(), "请输入文件描述!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				
				Thread modifyFileTh = new Thread(modifyFileRun);
				modifyFileTh.start();
				int i = 0;
				result = null;
				while(result == null){
					try {
						modifyFileTh.join();
						Thread.sleep(1000);
						i++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(i == 3)
						break;
				}
				if(result == null){
					Toast.makeText(getApplicationContext(), "请求超时!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 0){
					Toast.makeText(getApplicationContext(), "修改成功!!!", Toast.LENGTH_SHORT).show();
					ModifyFileActivity.this.finish();
				}
				else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 301){
					Toast.makeText(getApplicationContext(), "课程不存在!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 400){
					Toast.makeText(getApplicationContext(), "文件不存在!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 404){
					Toast.makeText(getApplicationContext(), "没有修改文件权限!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modify_file, menu);
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
	
	private HomeItem getFileContent(String fileid){
		helper = new HomeDBHelper(ModifyFileActivity.this);
		HomeItem homeItem = helper.getCurrentFile(fileid);
		helper.close();
		return homeItem;
	}
	
	Runnable modifyFileRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Map<String, String> params = new HashMap<String, String>();
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		    String timestamp = Tools.getTimestamp();
            params.put("id", myapp.getId());
            params.put("fileId", myapp.getCurrentFileId());
            params.put("classId", myapp.getClassid());
            params.put("fileType", filetype);
            params.put("fileDesc", filedesc);
            params.put("tiemstamp", timestamp);
            
            
            NameValuePair pair1 = new BasicNameValuePair("id", myapp.getId());
            NameValuePair pair2 = new BasicNameValuePair("fileId", myapp.getCurrentFileId());
            NameValuePair pair3 = new BasicNameValuePair("classId", myapp.getClassid());
            NameValuePair pair4 = new BasicNameValuePair("fileDesc", filedesc);
            NameValuePair pair5 = new BasicNameValuePair("fileType", filetype);
            NameValuePair pair6 = new BasicNameValuePair("timestamp", timestamp);
            NameValuePair pair7 = new BasicNameValuePair("signature",Tools.signature(params));
            
            pairList.add(pair1);
            pairList.add(pair2);
            pairList.add(pair3);
            pairList.add(pair4);
            pairList.add(pair5);
            pairList.add(pair6);
            pairList.add(pair7);
            
            result = null;
            
            try
            {
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                        pairList,HTTP.UTF_8);
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "file/modifyFile");
                System.out.println(Constant.getBaseURI());
                httpPost.setEntity(requestHttpEntity);
                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
                HttpResponse response = httpClient.execute(httpPost);
                result = Tools.getResponse(response);
                System.out.println("modifyFile---------" + result);
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
