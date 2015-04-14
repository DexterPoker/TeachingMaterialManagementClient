package cn.edu.zucc.zql.dbhelper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.edu.zucc.zql.bean.HomeItem;
import cn.edu.zucc.zql.bean.ManagerItem;

public class ManagerDBHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "TMMmanager";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "managerItem";
	public final static String ID = "id"; // ÁÐÃû
	public final static String F_ID = "fileID";
	public final static String F_NAME = "fileName";
	public final static String F_TYPE = "fileType";
	public final static String F_DESC = "fileDesc";

	public ManagerDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + TABLE_NAME + "(" 
				+ ID + " integer  primary key autoincrement,"
				+ F_ID + " text,"
				+ F_NAME + " text,"
				+ F_TYPE + " text,"
				+ F_DESC + " text"
				+ ")";
		db.execSQL(sql);
//		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql=" DROP TABLE IF EXISTS "+TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
//		db.close();
	}

	public void insert(ManagerItem item)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("fileID", item.getFileID());
		contentValues.put("fileName", item.getFileName());
		contentValues.put("fileType",item.getFileType());
		contentValues.put("fileDesc",item.getFileDesc());
		db.insert(TABLE_NAME, null, contentValues);
//		db.close();
	}
	public List<ManagerItem> getAll() {  
        List<ManagerItem> item = new ArrayList<ManagerItem>();  
        SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select * from managerItem", null);  
        while (cursor.moveToNext()) {
        	ManagerItem managerItem = new ManagerItem();
        	managerItem.setFileID(cursor.getString(1));
        	managerItem.setFileName(cursor.getString(2));
        	managerItem.setFileType(cursor.getString(3));
        	managerItem.setFileDesc(cursor.getString(4));
        	item.add(managerItem);  
        }  
        cursor.close();
//        db.close();
        return item;
    }
	
	public ManagerItem getManagerItem(String fileId){
		SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select * from managerItem where fileID = " + fileId, null);
        ManagerItem managerItem = new ManagerItem();
        while (cursor.moveToNext()) {
        	managerItem.setFileID(cursor.getString(1));
        	managerItem.setFileName(cursor.getString(2));
        	managerItem.setFileType(cursor.getString(3));
        	managerItem.setFileDesc(cursor.getString(4));
        }
        cursor.close();
        db.close();
        return managerItem; 
	}
	
}
