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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zucc.zql.bean.HomeItem;
import cn.edu.zucc.zql.bean.ManagerItem;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;
import cn.edu.zucc.zql.dbhelper.HomeDBHelper;
import cn.edu.zucc.zql.dbhelper.ManagerDBHelper;
import cn.edu.zucc.zql.teachingmaterialmanager.ModifyFileActivity;
import cn.edu.zucc.zql.teachingmaterialmanager.MyApp;
import cn.edu.zucc.zql.teachingmaterialmanager.R;

import com.alibaba.fastjson.JSONObject;

public class HomeItemDetailActivity extends Activity {

	private MyApp myapp;
	private Button downloadButton,supportButton,collectButton,modifyButton,deleteButton,recommendButton;
	private TextView filenameTv,filedescTv,filetypeTv,filedownloadcountTv,filesupportcountTv,fileuploaderTv,fileuploadtimeTv;
	private HomeDBHelper helper;
	private HomeItem currentFile;
	private JSONObject result;
	private ManagerItem manageItem;
	private ManagerDBHelper manageHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_item_detail);
		myapp = (MyApp) getApplication();
		downloadButton = (Button)findViewById(R.id.homeDetailFileDownloadButton);
		supportButton = (Button)findViewById(R.id.homeDetailFileSupportButton);
		collectButton = (Button)findViewById(R.id.homeDetailCollection);
		modifyButton = (Button)findViewById(R.id.homeDetailEdit);
		deleteButton = (Button)findViewById(R.id.homeDetailDelete);
		recommendButton = (Button)findViewById(R.id.homeDetailRecommend);
		
		filenameTv = (TextView)findViewById(R.id.homeDetailFileName);
		filedescTv = (TextView)findViewById(R.id.homeDetailFileDesc);
		filetypeTv = (TextView)findViewById(R.id.homeDetailFileType);
		filedownloadcountTv = (TextView)findViewById(R.id.homeDetailFileDownload);
		filesupportcountTv = (TextView)findViewById(R.id.homeDetailFileSupport);
		fileuploaderTv = (TextView)findViewById(R.id.homeDetailFileUploader);
		fileuploadtimeTv = (TextView)findViewById(R.id.homeDetailFileUploadTime);
		
		currentFile = getFileContent(myapp.getCurrentFileId());
		
		filenameTv.setText(currentFile.getFileName());
		filedescTv.setText(currentFile.getFileDesc());
		filetypeTv.setText(currentFile.getFileType());
		filedownloadcountTv.setText(currentFile.getFileDownloadCount());
		filesupportcountTv.setText(currentFile.getFileSupportCount());
		fileuploaderTv.setText(currentFile.getFileUploader());
		fileuploadtimeTv.setText(currentFile.getFileUploadTime());
		
		if(myapp.getLevel().toLowerCase().equals("admin")){
			findViewById(R.id.homeDetailRecommend).setVisibility(View.GONE);
			findViewById(R.id.homeDetailCollection).setVisibility(View.GONE);
		}
		else if(myapp.getLevel().toLowerCase().equals("student")||
				myapp.getLevel().toLowerCase().equals("assistant")){
			findViewById(R.id.homeDetailButtonLayout).setVisibility(View.GONE);
		}
		
		
		Button back = (Button)findViewById(R.id.homeDetailBack);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		downloadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Thread isExistTh = new Thread(isExistFileRun);
				isExistTh.start();
				int i = 0;
				result = null;
				while(result == null){
					try {
						isExistTh.join();
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
					Toast.makeText(getApplicationContext(), "开始后台下载!!!", Toast.LENGTH_SHORT).show();
					Thread downloadTh = new Thread(downloadRun);
					downloadTh.start();
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
				else if(result.getIntValue("errorCode") == 402){
					Toast.makeText(getApplicationContext(), "文件已删除!!!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
		
		supportButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Thread supportTh = new Thread(supportFileRun);
				supportTh.start();
				int i = 0;
				result = null;
				while(result == null){
					try {
						supportTh.join();
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
					Toast.makeText(getApplicationContext(), "点赞成功!!!", Toast.LENGTH_SHORT).show();
					supportButton.setClickable(false);
				}
				else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 400){
					Toast.makeText(getApplicationContext(), "文件不存在!!!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
		collectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Thread collectTh = new Thread(collectRun);
				collectTh.start();
				int i = 0;
				result = null;
				while(result == null){
					try {
						collectTh.join();
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
					Toast.makeText(getApplicationContext(), "收藏成功!!!", Toast.LENGTH_SHORT).show();
					supportButton.setClickable(false);
				}
				else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!", Toast.LENGTH_SHORT).show();
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
		
		
		modifyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeItemDetailActivity.this,ModifyFileActivity.class);
				HomeItemDetailActivity.this.startActivity(intent);
			}
		});
		
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Thread deleteTh = new Thread(deleteFileRun);
				deleteTh.start();
				int i = 0;
				result = null;
				while(result == null){
					try {
						deleteTh.join();
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
					Toast.makeText(getApplicationContext(), "删除成功!!!", Toast.LENGTH_SHORT).show();
					supportButton.setClickable(false);
				}
				else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!", Toast.LENGTH_SHORT).show();
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
		
		recommendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Thread recommendTh = new Thread(recommendFileRun);
				recommendTh.start();
				int i = 0;
				result = null;
				while(result == null){
					try {
						recommendTh.join();
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
					Toast.makeText(getApplicationContext(), "推荐成功!!!", Toast.LENGTH_SHORT).show();
					supportButton.setClickable(false);
				}
				else if(result.getIntValue("errorCode") == 100){
					Toast.makeText(getApplicationContext(), "签名出错!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 400){
					Toast.makeText(getApplicationContext(), "文件不存在!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 402){
					Toast.makeText(getApplicationContext(), "文件已删除!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 404){
					Toast.makeText(getApplicationContext(), "没有修改文件权限!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(result.getIntValue("errorCode") == 501){
					Toast.makeText(getApplicationContext(), "及时推送消息失败!!!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_item_detail, menu);
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
		helper = new HomeDBHelper(HomeItemDetailActivity.this);
		HomeItem homeItem = helper.getCurrentFile(fileid);
		helper.close();
		return homeItem;
	}
	
	Runnable isExistFileRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Map<String, String> params = new HashMap<String, String>();
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		    String timestamp = Tools.getTimestamp();
            params.put("id", myapp.getId());
            params.put("classId", myapp.getClassid());
            params.put("fileId", myapp.getCurrentFileId());
            params.put("timestamp", timestamp);
            
            
            NameValuePair pair1 = new BasicNameValuePair("id", myapp.getId());
            NameValuePair pair2 = new BasicNameValuePair("classId", myapp.getClassid());
            NameValuePair pair3 = new BasicNameValuePair("fileId", myapp.getCurrentFileId());
            NameValuePair pair4 = new BasicNameValuePair("timestamp", timestamp);
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
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "file/isExist");
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
		}
	};
	
	Runnable supportFileRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Map<String, String> params = new HashMap<String, String>();
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		    String timestamp = Tools.getTimestamp();
            params.put("id", myapp.getId());
            params.put("fileId", myapp.getCurrentFileId());
            params.put("timestamp", timestamp);
            
            
            NameValuePair pair1 = new BasicNameValuePair("id", myapp.getId());
            NameValuePair pair2 = new BasicNameValuePair("fileId", myapp.getCurrentFileId());
            NameValuePair pair3 = new BasicNameValuePair("timestamp", timestamp);
            NameValuePair pair4 = new BasicNameValuePair("signature",Tools.signature(params));
            
            pairList.add(pair1);
            pairList.add(pair2);
            pairList.add(pair3);
            pairList.add(pair4);
            
            result = null;
            
            try
            {
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                        pairList);
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "file/supportTime");
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
		}
	};
	
	Runnable downloadRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            
            NameValuePair pair1 = new BasicNameValuePair("fileId", myapp.getCurrentFileId());
            
            pairList.add(pair1);
            
            try
            {
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                        pairList);
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "file/download");
                System.out.println(Constant.getBaseURI());
                httpPost.setEntity(requestHttpEntity);
                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
                HttpResponse response = httpClient.execute(httpPost);
                if(Tools.getFile(response,myapp.getCurrentFileName())){
                	Looper.prepare();
                	Toast.makeText(getApplicationContext(), "下载成功!!!", Toast.LENGTH_SHORT).show();
                	
                	ManagerItem managerItem = getFileContentManager(myapp.getCurrentFileId());
                	System.out.println("managerItem---" + managerItem.getFileID());
					if (managerItem.getFileID() == null) {
						HomeItem homeItem = getFileContentHome(myapp.getCurrentFileId());
						insertManageDB(homeItem);
                	}
                	
                	Looper.loop();
                }
                else{
                	Looper.prepare();
                	Toast.makeText(getApplicationContext(), "下载失败!!!", Toast.LENGTH_SHORT).show();
                	Looper.loop();
                }
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
		}
	};
	
	
	Runnable collectRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Map<String, String> params = new HashMap<String, String>();
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		    String timestamp = Tools.getTimestamp();
            params.put("id", myapp.getId());
            params.put("fileId", myapp.getCurrentFileId());
            params.put("classId", myapp.getClassid());
            params.put("timestamp", timestamp);
            
            NameValuePair pair1 = new BasicNameValuePair("fileId", myapp.getCurrentFileId());
            NameValuePair pair2 = new BasicNameValuePair("id", myapp.getId());
            NameValuePair pair3 = new BasicNameValuePair("classId", myapp.getClassid());
            NameValuePair pair4 = new BasicNameValuePair("timestamp", timestamp);
            NameValuePair pair5 = new BasicNameValuePair("signature", Tools.signature(params));
            
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
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "file/collectFile");
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
		}
	};
	
	Runnable deleteFileRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Map<String, String> params = new HashMap<String, String>();
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		    String timestamp = Tools.getTimestamp();
            params.put("id", myapp.getId());
            params.put("fileId", myapp.getCurrentFileId());
            params.put("classId", myapp.getClassid());
            params.put("timestamp", timestamp);
            
            NameValuePair pair1 = new BasicNameValuePair("fileId", myapp.getCurrentFileId());
            NameValuePair pair2 = new BasicNameValuePair("id", myapp.getId());
            NameValuePair pair3 = new BasicNameValuePair("classId", myapp.getClassid());
            NameValuePair pair4 = new BasicNameValuePair("timestamp", timestamp);
            NameValuePair pair5 = new BasicNameValuePair("signature", Tools.signature(params));
            
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
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "file/deleteFile");
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
		}
	};
	
	
	Runnable recommendFileRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Map<String, String> params = new HashMap<String, String>();
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		    String timestamp = Tools.getTimestamp();
            params.put("id", myapp.getId());
            params.put("fileId", myapp.getCurrentFileId());
            params.put("classId", myapp.getClassid());
            params.put("timestamp", timestamp);
            
            NameValuePair pair1 = new BasicNameValuePair("fileId", myapp.getCurrentFileId());
            NameValuePair pair2 = new BasicNameValuePair("id", myapp.getId());
            NameValuePair pair3 = new BasicNameValuePair("classId", myapp.getClassid());
            NameValuePair pair4 = new BasicNameValuePair("timestamp", timestamp);
            NameValuePair pair5 = new BasicNameValuePair("signature", Tools.signature(params));
            
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
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "file/recommendFile");
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
		}
	};
	
	private ManagerItem getFileContentManager(String fileid){
		manageHelper = new ManagerDBHelper(HomeItemDetailActivity.this);
		manageItem = manageHelper.getManagerItem(fileid);
		helper.close();
		return manageItem;
	}
	
	private HomeItem getFileContentHome(String fileid){
		helper = new HomeDBHelper(HomeItemDetailActivity.this);
		HomeItem homeItem = helper.getCurrentFile(fileid);
		helper.close();
		return homeItem;
	}
	
	private void insertManageDB(HomeItem homeItem) {
		manageHelper = new ManagerDBHelper(HomeItemDetailActivity.this);
		manageItem = new ManagerItem();
		manageItem.setFileID(homeItem.getFileID() + "");
		manageItem.setFileName(homeItem.getFileName());
		manageItem.setFileType(homeItem.getFileType());
		manageItem.setFileDesc(homeItem.getFileDesc());
		manageHelper.insert(manageItem);
		manageHelper.close();
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
