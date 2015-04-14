package cn.edu.zucc.zql.dbhelper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.edu.zucc.zql.bean.HomeItem;
import cn.edu.zucc.zql.bean.MessageItem;

public class MessageDBHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "TMMmessage";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "messageItem";
	public final static String ID = "id"; // ÁÐÃû
	public final static String M_ID = "messageID";
	public final static String F_ID = "fileID";
	public final static String F_NAME = "fileName";
	public final static String F_TYPE = "fileType";
	public final static String F_DESC = "fileDesc";
	public final static String F_CLASSID = "fileClassId";

	public MessageDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + TABLE_NAME + "(" 
				+ ID + " integer  primary key autoincrement,"
				+ M_ID + " text,"
				+ F_ID + " text,"
				+ F_NAME + " text,"
				+ F_TYPE + " text,"
				+ F_DESC + " text,"
				+ F_CLASSID + " text"
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

	public void insert(MessageItem item)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("messageID", item.getMessageID());
		contentValues.put("fileID", item.getFileID());
		contentValues.put("fileName", item.getFileName());
		contentValues.put("fileType",item.getFileType());
		contentValues.put("fileDesc",item.getFileDesc());
		contentValues.put("fileClassId", item.getFileClassId());
		db.insert(TABLE_NAME, null, contentValues);
//		db.close();
	}
	public List<MessageItem> getAll(String classId) {  
        List<MessageItem> item = new ArrayList<MessageItem>();  
        SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select * from messageItem where fileClassId = '"+ classId +"'", null);  
        while (cursor.moveToNext()) {
        	MessageItem messageItem = new MessageItem();
        	messageItem.setMessageID(cursor.getString(1));
        	messageItem.setFileID(cursor.getString(2));
        	messageItem.setFileName(cursor.getString(3));
        	messageItem.setFileType(cursor.getString(4));
        	messageItem.setFileDesc(cursor.getString(5));
        	messageItem.setFileClassId(cursor.getString(6));
        	item.add(messageItem);  
        }  
        cursor.close();
        db.close();
        return item;
    }
	
	public int getCount(String classId){
		int count = 0;
		SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select * from messageItem where fileClassId = '"+ classId +"'" , null);
        count = cursor.getCount();
        System.out.println("count----" + count);
        cursor.close();
//        db.close();
        return count;
	}
	
	public int getLastFileId(String classId){
		int lastid = 0;
        SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select * from messageItem where fileClassId = '"+ classId +"'", null);
        if(cursor.moveToLast())
        	lastid = Integer.parseInt(cursor.getString(0));
    	System.out.println("lastid----" + lastid);
        cursor.close();
//        db.close();
        return lastid;
	}
	public MessageItem getCurrentFile(String fileid){
        SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select * from messageItem where fileID = " + fileid, null);
        MessageItem messageItem = new MessageItem();
        while (cursor.moveToNext()) {
        	messageItem.setFileID(cursor.getString(2));
        	messageItem.setFileName(cursor.getString(3));
        	messageItem.setFileType(cursor.getString(4));
        	messageItem.setFileDesc(cursor.getString(5));
        	messageItem.setFileClassId(cursor.getString(6));
        }  
        cursor.close();
        db.close();
        return messageItem; 
	}
}
