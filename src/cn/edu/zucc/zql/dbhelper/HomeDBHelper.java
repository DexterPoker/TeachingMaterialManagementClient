package cn.edu.zucc.zql.dbhelper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cn.edu.zucc.zql.bean.HomeItem;

public class HomeDBHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "TMMhome";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "homeItem";
	public final static String ID = "id";
	public final static String F_ID = "fileID";
	public final static String F_NAME = "fileName";
	public final static String F_TYPE = "fileType";
	public final static String F_DESC = "fileDesc";
	public final static String F_CLASSID = "fileClassId";
	public final static String F_DONWLOADCOUNT = "fileDownloadCount";
	public final static String F_SUPPORTCOUNT = "fileSupportCount";
	public final static String F_UPLOADER = "fileUploader";
	public final static String F_UPLOADTIME = "fileUploadTime";

	public HomeDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + TABLE_NAME + "(" 
				+ ID + " integer primary key autoincrement,"
				+ F_ID + " text,"
				+ F_NAME + " text,"
				+ F_TYPE + " text,"
				+ F_DESC + " text,"
				+ F_CLASSID + " text,"
				+ F_DONWLOADCOUNT + " text,"
				+ F_SUPPORTCOUNT + " text,"
				+ F_UPLOADER + " text,"
				+ F_UPLOADTIME + " text"
				+ ")";
		db.execSQL(sql);
		Log.i("sql",sql);
		Log.i("create", "create Database------------->");  
//		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql="DROP TABLE IF EXISTS "+TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
//		db.close();
	}

	public void insert(HomeItem item)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("fileID", item.getFileID());
		contentValues.put("fileName", item.getFileName());
		contentValues.put("fileType",item.getFileType());
		contentValues.put("fileDesc",item.getFileDesc());
		contentValues.put("fileClassId",item.getFileClassID());
		contentValues.put("fileDownloadCount",item.getFileDownloadCount());
		contentValues.put("fileSupportCount",item.getFileSupportCount());
		contentValues.put("fileUploader",item.getFileUploader());
		contentValues.put("fileUploadTime",item.getFileUploadTime());
		db.insert(TABLE_NAME, null, contentValues);
//		db.close();
	}
	public List<HomeItem> getAll(String classId) {  
        List<HomeItem> item = new ArrayList<HomeItem>();  
        SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select * from homeItem where fileClassId = '"+ classId +"'", null);  
        while (cursor.moveToNext()) {
        	HomeItem homeItem = new HomeItem();
        	homeItem.setFileID(cursor.getString(1));
        	homeItem.setFileName(cursor.getString(2));
        	homeItem.setFileType(cursor.getString(3));
        	homeItem.setFileDesc(cursor.getString(4));
        	homeItem.setFileClassID(cursor.getString(5));
        	homeItem.setFileDownloadCount(cursor.getString(6));
        	homeItem.setFileSupportCount(cursor.getString(7));
        	homeItem.setFileUploader(cursor.getString(8));
        	homeItem.setFileUploadTime(cursor.getString(9));
        	item.add(homeItem);  
        }  
        cursor.close();
        db.close();
        return item;  
    }
	
	public int getLastFileId(String classId){
		int lastid = 0;
        SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select * from homeItem where fileClassId = '"+ classId +"'", null);
        if(cursor.moveToLast())
        	lastid = Integer.parseInt(cursor.getString(1));
    	System.out.println("lastid----" + lastid);
        cursor.close();
//        db.close();
        return lastid;
	}
	
	public int getCount(String classId){
		int count = 0;
		SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select * from homeItem where fileClassId = '"+ classId +"'" , null);
        count = cursor.getCount();
        System.out.println("count----" + count);
        cursor.close();
//        db.close();
        return count;
	}
	
	public HomeItem getCurrentFile(String fileid){
        SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select * from homeItem where fileID = " + fileid, null);
    	HomeItem homeItem = new HomeItem();
        while (cursor.moveToNext()) {
        	homeItem.setFileID(cursor.getString(1));
        	homeItem.setFileName(cursor.getString(2));
        	homeItem.setFileType(cursor.getString(3));
        	homeItem.setFileDesc(cursor.getString(4));
        	homeItem.setFileClassID(cursor.getString(5));
        	homeItem.setFileDownloadCount(cursor.getString(6));
        	homeItem.setFileSupportCount(cursor.getString(7));
        	homeItem.setFileUploader(cursor.getString(8));
        	homeItem.setFileUploadTime(cursor.getString(9));
        }  
        cursor.close();
        db.close();
        return homeItem; 
	}
}
