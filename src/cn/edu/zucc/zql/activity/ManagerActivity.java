package cn.edu.zucc.zql.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.edu.zucc.zql.activity.HomeActivity.DataLoadThread;
import cn.edu.zucc.zql.bean.ManagerItem;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.dbhelper.ManagerDBHelper;
import cn.edu.zucc.zql.teachingmaterialmanager.R;

public class ManagerActivity extends Activity implements OnScrollListener{

	private Thread currentThread;
	private View footer;
	private SimpleAdapter mAdapter;
	private ManagerItem initItem;
	private List<ManagerItem> receiveItem;
	ManagerDBHelper helper;
	private List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
	private Map<String,Object> mMap;
	private ListView managerListView;
	private int now=0;
	private boolean isExit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager);
		helper=new ManagerDBHelper(ManagerActivity.this);
		appendData();
		managerListView = (ListView)findViewById(R.id.managerListView);
		
		
		findViewById(R.id.managerFresh).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				appendData();
				if (currentThread == null || !currentThread.isAlive()) {
	                managerListView.addFooterView(footer, null, false);
	                currentThread = new DataLoadThread();
	                currentThread.start();
	            }
			}
		});
		
	    String[] mFrom = new String[]{"id","fileName","fileType","fileDesc"};
	    int[] mTo = new int[]{R.id.managerItemFileId,R.id.managerItemFileName,R.id.managerItemFileType,R.id.managerItemDescription};
    	
        mAdapter = new SimpleAdapter(this,mList,R.layout.manager_item,mFrom,mTo);
        managerListView.setAdapter(mAdapter);
        initWidget();
        managerListView.setOnScrollListener(this);
        managerListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                
            	mMap = mList.get(position);
				String filename = (String) mMap.get("fileName");
            	String filePath = Constant.downloadPath + filename;
				Intent intent  = openFile(filePath);
            	startActivity(intent);
            }
        });
	}

	private void initWidget() {
		managerListView = (ListView) findViewById(R.id.managerListView);
		footer = getLayoutInflater().inflate(R.layout.pullup_loading, null);
	}
	
	void appendData(){//�������
		receiveItem = helper.getAll();
		
		if (receiveItem.size() - now < 5 && receiveItem.size() - now> 0) {
			for (int i = 0; i < receiveItem.size() - now ; i++) {
				mMap = new HashMap<String, Object>();
				mMap.put("id", receiveItem.get(now + i).getFileID());
				mMap.put("fileName", receiveItem.get(now + i).getFileName());
				mMap.put("fileType", receiveItem.get(now + i).getFileType());
				mMap.put("fileDesc", receiveItem.get(now + i).getFileDesc());
				mList.add(mMap);
			}
			now = receiveItem.size();
		} else if (receiveItem.size() - now > 5) {
			for (int i = 0; i < 5; i++) {
				mMap = new HashMap<String, Object>();
				mMap.put("id", receiveItem.get(now + i).getFileID());
				mMap.put("fileName", receiveItem.get(now + i).getFileName());
				mMap.put("fileType", receiveItem.get(now + i).getFileType());
				mMap.put("fileDesc", receiveItem.get(now + i).getFileDesc());
				mList.add(mMap);
			}
			now = now + 5;
		}
	}
	
	class DataLoadThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                appendData();
                // ��ΪAndroid�ؼ�ֻ��ͨ�����̣߳�ui�̣߳����£������ô˷���
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // ������ϣ��Ƴ�β���ؼ�
                    	managerListView.removeFooterView(footer);
                        // �����ݸı�ʱ���ô˷���֪ͨview����
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
	    // ��ָ�Ӵ���Ļ����
	    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
	        // ��ָ�뿪��Ļ�����Ի���
	    case OnScrollListener.SCROLL_STATE_FLING:
	        // ������Ҫ���һ��ʱ��������
	        if (view.getLastVisiblePosition() == view.getCount() - 1) {
	            // ����ͨ������������ݵȡ�
	            // �ж��Ƿ����ڼ�����
	            if (currentThread == null || !currentThread.isAlive()) {
	                // ���listviewβ���ؼ�������
	                managerListView.addFooterView(footer, null, false);
	                // �����̼߳�������
	                currentThread = new DataLoadThread();
	                currentThread.start();
	            }
	        }
	        break;
	    // ������
	    case OnScrollListener.SCROLL_STATE_IDLE:
	        break;
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manager, menu);
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
	
	public static Intent openFile(String filePath){

		File file = new File(filePath);
		if(!file.exists()) return null;
		/* ȡ����չ�� */
		String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase(); 
		/* ����չ�������;���MimeType */
		if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
				end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
			return getAudioFileIntent(filePath);
		}else if(end.equals("3gp")||end.equals("mp4")){
			return getAudioFileIntent(filePath);
		}else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
				end.equals("jpeg")||end.equals("bmp")){
			return getImageFileIntent(filePath);
		}else if(end.equals("apk")){
			return getApkFileIntent(filePath);
		}else if(end.equals("ppt")){
			return getPptFileIntent(filePath);
		}else if(end.equals("xls")){
			return getExcelFileIntent(filePath);
		}else if(end.equals("doc")){
			return getWordFileIntent(filePath);
		}else if(end.equals("pdf")){
			return getPdfFileIntent(filePath);
		}else if(end.equals("chm")){
			return getChmFileIntent(filePath);
		}else if(end.equals("txt")){
			return getTextFileIntent(filePath,false);
		}else{
			return getAllIntent(filePath);
		}
	}
	
	//Android��ȡһ�����ڴ�APK�ļ���intent
	public static Intent getAllIntent( String param ) {

		Intent intent = new Intent();  
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri,"*/*"); 
		return intent;
	}
	//Android��ȡһ�����ڴ�APK�ļ���intent
	public static Intent getApkFileIntent( String param ) {

		Intent intent = new Intent();  
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri,"application/vnd.android.package-archive"); 
		return intent;
	}

	//Android��ȡһ�����ڴ�VIDEO�ļ���intent
	public static Intent getVideoFileIntent( String param ) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	//Android��ȡһ�����ڴ�AUDIO�ļ���intent
	public static Intent getAudioFileIntent( String param ){

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	//Android��ȡһ�����ڴ�Html�ļ���intent   
	public static Intent getHtmlFileIntent( String param ){

		Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	//Android��ȡһ�����ڴ�ͼƬ�ļ���intent
	public static Intent getImageFileIntent( String param ) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	//Android��ȡһ�����ڴ�PPT�ļ���intent   
	public static Intent getPptFileIntent( String param ){  

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		Uri uri = Uri.fromFile(new File(param ));   
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");   
		return intent;   
	}   

	//Android��ȡһ�����ڴ�Excel�ļ���intent   
	public static Intent getExcelFileIntent( String param ){  

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		Uri uri = Uri.fromFile(new File(param ));   
		intent.setDataAndType(uri, "application/vnd.ms-excel");   
		return intent;   
	}   

	//Android��ȡһ�����ڴ�Word�ļ���intent   
	public static Intent getWordFileIntent( String param ){  

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		Uri uri = Uri.fromFile(new File(param ));   
		intent.setDataAndType(uri, "application/msword");   
		return intent;   
	}   

	//Android��ȡһ�����ڴ�CHM�ļ���intent   
	public static Intent getChmFileIntent( String param ){   

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		Uri uri = Uri.fromFile(new File(param ));   
		intent.setDataAndType(uri, "application/x-chm");   
		return intent;   
	}   

	//Android��ȡһ�����ڴ��ı��ļ���intent   
	public static Intent getTextFileIntent( String param, boolean paramBoolean){   

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		if (paramBoolean){   
			Uri uri1 = Uri.parse(param );   
			intent.setDataAndType(uri1, "text/plain");   
		}else{   
			Uri uri2 = Uri.fromFile(new File(param ));   
			intent.setDataAndType(uri2, "text/plain");   
		}   
		return intent;   
	}  
	//Android��ȡһ�����ڴ�PDF�ļ���intent   
	public static Intent getPdfFileIntent( String param ){   

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		Uri uri = Uri.fromFile(new File(param ));   
		intent.setDataAndType(uri, "application/pdf");   
		return intent;   
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
	        Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();  
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
