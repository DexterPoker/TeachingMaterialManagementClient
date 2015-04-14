package cn.edu.zucc.zql.teachingmaterialmanager;

import java.util.Map;

import cn.edu.zucc.zql.common.Constant;
import android.app.Application;
import android.provider.SyncStateContract.Constants;

public class MyApp extends Application{
	private String id ;
	private String level;
	private String name;
	private Map<String, String> classes;
	private String classid;
	private String classname;
	private String currentFileId;
	private String currentFileName;
	private String cid;
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public  String getId() {
		return id;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, String> getClasses() {
		return classes;
	}
	public void setClasses(Map<String, String> classes) {
		this.classes = classes;
	}
	public String getClassid() {
		return classid;
	}
	public void setClassid(String classid) {
		this.classid = classid;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCurrentFileId() {
		return currentFileId;
	}
	public void setCurrentFileId(String currentFileId) {
		this.currentFileId = currentFileId;
	}
	
	public String getCurrentFileName() {
		return currentFileName;
	}
	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}
	public void showInfo(){
		System.out.println("------appinfo----");
		System.out.println("id---"+ id +",level---" + level + ",name---"+ name + ",classes---" + classes
				+",classid---" + classid + ",classname---" + classname + ",cid---" + cid);
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	@Override 
	    public void onCreate() { 
	        // TODO Auto-generated method stub 
	        super.onCreate();
	        setName("");
	        setClassid("");
	        setId("");
	        setClasses(null);
	        setLevel("");
	        setCurrentFileId("");
	        setClassname("");
	    }  
	
}
