package cn.edu.zucc.zql.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zucc.zql.common.Constant;
import cn.edu.zucc.zql.common.Tools;
import cn.edu.zucc.zql.function.CallbackBundle;
import cn.edu.zucc.zql.function.OpenFileDialog;
import cn.edu.zucc.zql.teachingmaterialmanager.MyApp;
import cn.edu.zucc.zql.teachingmaterialmanager.R;

import com.alibaba.fastjson.JSONObject;

public class UploadActivity extends Activity {

	static private int openfileDialogId = 0; 
	MyApp myapp;
	Spinner spinFileType;
	EditText filenameEd,filedescEd;
	String filepath,filedesc,filetype;
	String id,classid,classname;
	JSONObject result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		myapp = (MyApp) getApplication();
		id = myapp.getId();
		classid = myapp.getClassid();
		classname = myapp.getClassname();
		
		spinFileType = (Spinner)findViewById(R.id.fileType);
		filenameEd = (EditText)findViewById(R.id.filename);
		filedescEd = (EditText)findViewById(R.id.fileDesc);
		
		TextView title = (TextView)findViewById(R.id.commomTitle);
		title.setText("文件上传");
		Button back = (Button)findViewById(R.id.commomBack);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		findViewById(R.id.chooseButton).setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View arg0) {  
                showDialog(openfileDialogId);
            }  
        });
		findViewById(R.id.uploadButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				result = null;
				
				filedesc = filedescEd.getText().toString();
				filetype = spinFileType.getSelectedItem().toString();
				
				if(filenameEd.getText().toString() == null || filenameEd.getText().toString().equals("")){
					Toast.makeText(getApplicationContext(), "请选择文件!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(filepath == null || filepath.equals("")){
					Toast.makeText(getApplicationContext(), "请选择文件!!!", Toast.LENGTH_SHORT).show();
					return;
				}
				if(filedesc == null || filedesc.equals("")){
					Toast.makeText(getApplicationContext(), "请填写文件描述!!!", Toast.LENGTH_SHORT).show();;
					return;
				}
				
				String filelocation = filepath.substring(0,filepath.lastIndexOf("/"));
				String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
				File file = new File(filelocation, filename);
				System.out.println("size---" + (double)(file.length()/1024/1024));
				if((double)(file.length()/1024/1024)>5){
					Toast.makeText(getApplicationContext(), "文件大于5M，请重新选择文件...", Toast.LENGTH_SHORT).show();
					return;
				}

				Thread uploadFile = new Thread(upload);
				uploadFile.start();
				
				filenameEd.setText("");
				
				Toast.makeText(getApplicationContext(), "开始后台上传", Toast.LENGTH_SHORT).show();

	        	UploadActivity.this.finish();
			}
		});
	}
	
	Runnable upload = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			String filelocation = filepath.substring(0,filepath.lastIndexOf("/"));
			String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
			
			
			
			JSONObject params = new JSONObject();
			
//			id = new StringBody(id,Charset.forName("UTF-8")).toString();
//			classid = new StringBody(classid,Charset.forName("UTF-8")).toString();
//			classname = new StringBody(classname,Charset.forName("UTF-8")).toString();
//			String timestamp = new StringBody(Tools.getTimestamp(),Charset.forName("UTF-8")).toString();
//			filename = new StringBody(filename,Charset.forName("UTF-8")).toString();
//			filetype = new StringBody(filetype,Charset.forName("UTF-8")).toString();
//			filedesc = new StringBody(filedesc,Charset.forName("UTF-8")).toString();
			
			StringBody idSB,classidSB,classnameSB,timestampSB,filenameSB,filetypeSB,filedescSB;
			try {
				idSB = new StringBody(id,Charset.forName("UTF-8"));
				classidSB = new StringBody(classid,Charset.forName("UTF-8"));
				classnameSB = new StringBody(classname,Charset.forName("UTF-8"));
				timestampSB = new StringBody(Tools.getTimestamp(),Charset.forName("UTF-8"));
				filenameSB = new StringBody(filename,Charset.forName("UTF-8"));
				filetypeSB = new StringBody(filetype,Charset.forName("UTF-8"));
				filedescSB = new StringBody(filedesc,Charset.forName("UTF-8"));
				
//				params.put("id", idSB.toString());
//				params.put("classId", classidSB.toString());
//				params.put("className", classnameSB.toString());
//				params.put("timestamp", timestampSB.toString());
//				params.put("fileName", filenameSB.toString());
//				params.put("fileType", filetypeSB.toString());
//				params.put("fileDesc", filedescSB.toString());
				
				File file = new File(filelocation, filename);
				System.out.println("size---" + (double)(file.length()/1024/1024));
				
				
				
		    	try {
		    	     HttpClient client = new DefaultHttpClient();  
		    	     String postURL = Constant.getBaseURI() + "file/uploadFile";
		    	     HttpPost post = new HttpPost(postURL);
		    	     FileBody bin = new FileBody(file);
		    	     MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"));
		    	     reqEntity.addPart("file", bin);
		    	     reqEntity.addPart("fileName", filenameSB);
		    	     reqEntity.addPart("fileType", filetypeSB);
		    	     reqEntity.addPart("fileDesc", filedescSB);
		    	     reqEntity.addPart("id", idSB);
		    	     reqEntity.addPart("classId", classidSB);
		    	     reqEntity.addPart("className", classnameSB);
		    	     reqEntity.addPart("timestamp",timestampSB);
//		    	     reqEntity.addPart("signature",new StringBody(Tools.signature(params)));
		    	     post.setEntity(reqEntity);
		    	     HttpResponse response = client.execute(post);
		    	     result = Tools.getResponse(response);

						if(result == null){
							Looper.prepare();
							Toast.makeText(getApplicationContext(), "还在上传!!!",Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;
						}
						
						if(result.getIntValue("errorCode") == 0)
			            {	
							Looper.prepare();
							Toast.makeText(getApplicationContext(), "上传成功!!!",Toast.LENGTH_SHORT).show();
				        	Looper.loop();
			        	}
			            else if(result.getIntValue("errorCode") == 301){
			            	Looper.prepare();
							Toast.makeText(getApplicationContext(), "课程不存在!!!",Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;
						}
			            else if (result.getIntValue("errorCode") == 200) {
			            	Looper.prepare();
							Toast.makeText(getApplicationContext(), "用户不存在!!!",Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;
			            }
			            else if(result.getIntValue("errorCode") == 100){
			            	Looper.prepare();
							Toast.makeText(getApplicationContext(), "签名出错!!!",Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;
			            }
			            else if(result.getIntValue("errorCode") == 401){
			            	Looper.prepare();
							Toast.makeText(getApplicationContext(),"文件存储失败!!!",Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;
			            }
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
        	System.out.println(result);
		}
	};
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload, menu);
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
            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹  
            images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root);   // 根目录图标  
            images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up);    //返回上一层的图标  
            images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder);   //文件夹图标  
            images.put("wav", R.drawable.filedialog_wavfile);   //wav文件图标  
            images.put("mp3", R.drawable.mp3file);
            images.put("doc", R.drawable.docfile);
            images.put("docx", R.drawable.docfile);
            images.put("rtf", R.drawable.docfile);
            images.put("ppt", R.drawable.pptfile);
            images.put("pptx", R.drawable.pptfile);
            images.put("xls", R.drawable.xlsfile);
            images.put("xlsx",R.drawable.xlsfile);
            images.put("pdf", R.drawable.pdffile);
            images.put("rar", R.drawable.rarfile);
            images.put("zip", R.drawable.zipfile);
            images.put("txt", R.drawable.txtfile);
            images.put("jpg", R.drawable.imgfile);
            images.put("png", R.drawable.imgfile);
            images.put("html", R.drawable.htmlfile);
            images.put("jsp", R.drawable.htmlfile);
            images.put("apk", R.drawable.apkfile);
            images.put("log", R.drawable.txtfile);
            images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);  
            Dialog dialog = OpenFileDialog.createDialog(id, this, "打开文件", new CallbackBundle() {  
                @Override  
                public void callback(Bundle bundle) {  
                    filepath = bundle.getString("path");
                    System.out.println("filepath----------" + filepath);
                    filenameEd.setText(filepath.substring(filepath.lastIndexOf("/") + 1));
                    setTitle(filepath); // 把文件路径显示在标题上  
                }  
            },   
            ".wav;.mp3;.doc;.docx;.rtf;.ppt;.pptx;.xls;.xlsx;.pdf;.rar;.zip;.txt;.jpg;.png;.html;.jsp;.apk;.log;",  
            images);  
            return dialog;  
        }  
        return null;  
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
