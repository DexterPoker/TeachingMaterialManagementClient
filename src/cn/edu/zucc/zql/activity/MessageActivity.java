package cn.edu.zucc.zql.activity;

import java.util.ArrayList;
import java.util.HashMap;
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.edu.zucc.zql.activity.HomeActivity.DataLoadThread;
import cn.edu.zucc.zql.bean.Files;
import cn.edu.zucc.zql.bean.ManagerItem;
import cn.edu.zucc.zql.bean.MessageItem;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;
import cn.edu.zucc.zql.dbhelper.ManagerDBHelper;
import cn.edu.zucc.zql.dbhelper.MessageDBHelper;
import cn.edu.zucc.zql.teachingmaterialmanager.MyApp;
import cn.edu.zucc.zql.teachingmaterialmanager.R;

import com.alibaba.fastjson.JSONObject;

public class MessageActivity extends Activity implements OnScrollListener{
	
	private MyApp myapp;
	private Thread currentThread;
	private View footer;
	private SimpleAdapter mAdapter;
	private MessageItem initItem;
	private ManagerItem manageItem;
	private ManagerDBHelper manageHelper;
	private List<MessageItem> receiveItem;
	private MessageDBHelper helper;
	private List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
	private Map<String,Object> mMap;
	private ListView messageListView;
	private int now=0;
	private int cursor=0;
	private boolean isExit = false;
	private JSONObject result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		myapp = (MyApp) getApplication();
		
		if(getCount(myapp.getClassid()) == 0){
			getMessage();
		}
		else{
			appendData();
		}
		
		helper=new MessageDBHelper(MessageActivity.this);
		messageListView = (ListView)findViewById(R.id.messageListView);
		
		findViewById(R.id.messageFresh).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getMessage();
				if (currentThread == null || !currentThread.isAlive()) {
	                messageListView.addFooterView(footer, null, false);
	                currentThread = new DataLoadThread();
	                currentThread.start();
	            }
			}
		});
		
		
	    String[] mFrom = new String[]{"fileName","fileType","fileDesc"};
	    int[] mTo = new int[]{R.id.messageItemFileName,R.id.messageItemFileType,R.id.messageItemDescription};
    	
        mAdapter = new SimpleAdapter(this,mList,R.layout.message_item,mFrom,mTo){
        	@Override
    			public View getView(int position, View convertView,
    					ViewGroup parent) {
    				final int p=position;
    				final View view = super.getView(position, convertView,
    						parent);
    				
    				view.findViewById(R.id.messageItemDownlaod).setOnClickListener(
    						new OnClickListener() {

    							@Override
    							public void onClick(View v) {
    								// TODO Auto-generated method stub
    								mMap = mList.get(p);
    								String currentFileId = (String) mMap.get("fileId");
    								String currentFilename = (String) mMap.get("fileName");
    								myapp.setCurrentFileId(currentFileId);
    								myapp.setCurrentFileName(currentFilename);
    								
    								
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

    				view.findViewById(R.id.messageItemSupport).setOnClickListener(
    						new OnClickListener() {

    							@Override
    							public void onClick(View v) {
    								// TODO Auto-generated method stub
    								
    								mMap = mList.get(p);
    								String currentFileId = (String) mMap.get("fileId");
    								myapp.setCurrentFileId(currentFileId);
    								
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
//    									Button supportIndexButton = (Button)layout.findViewById(R.id.homeItemSupport);
//    									supportIndexButton.setClickable(false);
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
    				return view;
    			}
    		};
        
        
        
        
        messageListView.setAdapter(mAdapter);
        initWidget();
        messageListView.setOnScrollListener(this);
        messageListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            }
        });
	}

	private void initWidget() {
		messageListView = (ListView) findViewById(R.id.messageListView);
		footer = getLayoutInflater().inflate(R.layout.pullup_loading, null);
	}

	private void initData(List<Files> file)
	{
		helper = new MessageDBHelper(MessageActivity.this);
		for(int i=0;i<file.size();i++)
		{
			System.out.println("inser fileid---" + file.get(i).getFileId());
			initItem = new MessageItem();
			initItem.setFileID(file.get(i).getFileId() + "");
			initItem.setFileName(file.get(i).getFileName());
			initItem.setFileType(file.get(i).getFileType());
			initItem.setFileDesc(file.get(i).getDescription());
			initItem.setFileClassId(file.get(i).getClassId() + "");
			helper.insert(initItem);
		}
		helper.close();
	}
	
	private void appendData(){
		helper = new MessageDBHelper(MessageActivity.this);
		receiveItem = helper.getAll(myapp.getClassid());
		int count = getCount(myapp.getClassid());
		if (count - now >= 5) {
			for (int i = 0; i < 5; i++) {
				mMap = new HashMap<String, Object>();
				mMap.put("fileId", receiveItem.get(now + i).getFileID());
				mMap.put("fileName", receiveItem.get(now + i).getFileName());
				mMap.put("fileType", receiveItem.get(now + i).getFileType());
				mMap.put("fileDesc", receiveItem.get(now + i).getFileDesc());
				mList.add(mMap);
			}
			now = now + 5;
		}
		else if(count - now < 5 && count - now > 0){
			for(int i = 0 ;i < count - now ; i ++){
				mMap = new HashMap<String, Object>();
				mMap.put("fileId", receiveItem.get(now + i).getFileID());
				mMap.put("fileName", receiveItem.get(now + i).getFileName());
				mMap.put("fileType", receiveItem.get(now + i).getFileType());
				mMap.put("fileDesc", receiveItem.get(now + i).getFileDesc());
				mList.add(mMap);
			}
			now = count;
		}
		else if(count == now){
			getMessage();
		}
		helper.close();
	}
	
	class DataLoadThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                appendData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    	messageListView.removeFooterView(footer);
                    	mAdapter.notifyDataSetChanged();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	    switch (scrollState) {
	    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
	    case OnScrollListener.SCROLL_STATE_FLING:
	        if (view.getLastVisiblePosition() == view.getCount() - 1) {
	            if (currentThread == null || !currentThread.isAlive()) {
	                messageListView.addFooterView(footer, null, false);
	                currentThread = new DataLoadThread();
	                currentThread.start();
	            }
	        }
	        break;
	    case OnScrollListener.SCROLL_STATE_IDLE:
	        break;
	    }
	}
	
	private int getCount(String classId){
		helper = new MessageDBHelper(MessageActivity.this);
		int mCount = helper.getCount(classId);
		helper.close();
		return mCount;
	}
	
	private void insertManageDB(MessageItem messageItem) {
		manageHelper = new ManagerDBHelper(MessageActivity.this);
		manageItem = new ManagerItem();
		manageItem.setFileID(messageItem.getFileID() + "");
		manageItem.setFileName(messageItem.getFileName());
		manageItem.setFileType(messageItem.getFileType());
		manageItem.setFileDesc(messageItem.getFileDesc());
		manageHelper.insert(manageItem);
		manageHelper.close();
	}
	
	private void getLastFileId(String classId){
		helper = new MessageDBHelper(MessageActivity.this);
		cursor = helper.getLastFileId(classId);
		helper.close();
	}
	
	private ManagerItem getFileContentManager(String fileid){
		manageHelper = new ManagerDBHelper(MessageActivity.this);
		manageItem = manageHelper.getManagerItem(fileid);
		helper.close();
		return manageItem;
	}
	
	private MessageItem getFileContentMessage(String fileid){
		helper = new MessageDBHelper(MessageActivity.this);
		MessageItem messageItem = helper.getCurrentFile(fileid);
		helper.close();
		return messageItem;
	}
	
	private void getMessage(){
		result = null;
		getLastFileId(myapp.getClassid());
		int i = 0;
		Thread getMessageTh = new Thread(getMessageRun);
		getMessageTh.start();
		while (result == null) {
			try {
				getMessageTh.join();
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (i == 3)
				break;
		}
		if (result == null) {
			// Toast.makeText(getApplicationContext(),
			// "请求超时!!!",Toast.LENGTH_SHORT).show();
			return;
		} else {
			String filesString = result.getString("files");
			if (filesString == null || filesString.equals("")) {
//				Looper.prepare();
//				Toast.makeText(getApplicationContext(), "没有新数据!!!",Toast.LENGTH_SHORT).show();
//				Looper.loop();
				return;
			} else {
				JSONObject tmp = JSONObject.parseObject(filesString);
				filesString = tmp.getString("files");
				List<Files> files = JSONObject.parseArray(filesString,Files.class);
				initData(files);
				appendData();
			}
		}
	}
	
	Runnable getMessageRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Map<String, String> params = new HashMap<String, String>();
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		    String timestamp = Tools.getTimestamp();
            params.put("id", myapp.getId());
            params.put("classId", myapp.getClassid());
            params.put("cursor", cursor + "");
            params.put("tiemstamp", timestamp);
            
            
            NameValuePair pair1 = new BasicNameValuePair("id", myapp.getId());
            NameValuePair pair2 = new BasicNameValuePair("classId", myapp.getClassid());
            NameValuePair pair3 = new BasicNameValuePair("cursor", cursor + "");
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
                HttpPost httpPost = new HttpPost(Constant.getBaseURI() + "message/getMessage");
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
            params.put("tiemstamp", timestamp);
            
            
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
						MessageItem messageitem = getFileContentMessage(myapp.getCurrentFileId());
						insertManageDB(messageitem);
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
	
	Runnable supportFileRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Map<String, String> params = new HashMap<String, String>();
		    List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		    String timestamp = Tools.getTimestamp();
            params.put("id", myapp.getId());
            params.put("fileId", myapp.getCurrentFileId());
            params.put("tiemstamp", timestamp);
            
            
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
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message, menu);
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
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
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
