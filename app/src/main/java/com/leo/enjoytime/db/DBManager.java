package com.leo.enjoytime.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.model.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 16/3/6.
 */
public class DBManager {
    private SQLiteDatabase db;
    private EnjoyDataHelper dbHelper;
    private static final String TAG = DBManager.class.getSimpleName();

    public DBManager(Context context){
        dbHelper = EnjoyDataHelper.getInstance(context);
    }

    public synchronized void insertData(Entry entry){
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(EnjoyDataHelper.ITEM_COLUMN_URL, entry.getUrl());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_TYPE, entry.getType());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_CREATE_AT, entry.getCreate_at());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_FAVOR, entry.getFavor_flag());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_DESC, entry.getDesc());
        long result = db.replace(EnjoyDataHelper.TABLE_NAME_ITEM,null,cv);
        if(result != -1) {
            db.setTransactionSuccessful();
        }else{
            Log.e(TAG,"insertData error!");
        }
        db.endTransaction();
        db.close();
    }

    public synchronized void insertAllData(List<Entry> list){
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        boolean result = true;
        for(Entry entry : list) {
            ContentValues cv = new ContentValues();
            cv.put(EnjoyDataHelper.ITEM_COLUMN_URL, entry.getUrl());
            cv.put(EnjoyDataHelper.ITEM_COLUMN_TYPE, entry.getType());
            cv.put(EnjoyDataHelper.ITEM_COLUMN_CREATE_AT, entry.getCreate_at());
            cv.put(EnjoyDataHelper.ITEM_COLUMN_FAVOR, entry.getFavor_flag());
            cv.put(EnjoyDataHelper.ITEM_COLUMN_DESC, entry.getDesc());
            long index = db.replace(EnjoyDataHelper.TABLE_NAME_ITEM, null, cv);
            if(index == -1){
                result = false;
                break;
            }
        }
        if(result) {
            db.setTransactionSuccessful();
        }else{
            Log.e(TAG,"insertAllData error!");
        }
        db.endTransaction();
        db.close();
    }

    public synchronized void changeArticleToLikeOrUnlike(Entry entry){
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(EnjoyDataHelper.ITEM_COLUMN_FAVOR, entry.getFavor_flag());
        long result = db.update(EnjoyDataHelper.TABLE_NAME_ITEM,cv,
                EnjoyDataHelper.ITEM_COLUMN_URL +" = ?",new String[]{entry.getUrl()});
        if(result != -1) {
            db.setTransactionSuccessful();
        }else{
            Log.e(TAG,"updateData error!");
        }
        db.endTransaction();
        db.close();
    }

    public Entry getDataByUrl(String url){
        Entry data = null;
        db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM "+EnjoyDataHelper.TABLE_NAME_ITEM+" where "+EnjoyDataHelper.ITEM_COLUMN_URL+
                " = ? ";
        Log.d(TAG, "getDataByUrl sql:" + sql);
        Cursor cursor = db.rawQuery(sql, new String[]{url});
        if (cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
            data = cursorToData(cursor);
        }
        cursor.close();
        return data;

    }

    public List<Entry> getDataList(int count,int pageSize,String type){
        List<Entry> list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        //offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
        String sql = "SELECT * FROM " + EnjoyDataHelper.TABLE_NAME_ITEM +" where "+EnjoyDataHelper.ITEM_COLUMN_TYPE+
                " = ? "+"order by "+EnjoyDataHelper.ITEM_COLUMN_CREATE_AT +" desc limit "+ count +" offset "+pageSize*count;
        Log.d(TAG,"getDataList sql:"+sql);
        Cursor cursor = db.rawQuery(sql,new String[]{type});
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                Entry data = cursorToData(cursor);
                list.add(data);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    private Entry cursorToData(Cursor cursor) {
        String type = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_TYPE));
        String url = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_URL));
        String desc = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_DESC));
        int favor_flag = cursor.getInt(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_FAVOR));
        String create_at = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_CREATE_AT));
        return new Entry(type,url,create_at,desc,favor_flag);
    }

    public List<Entry> getFavorDataList(){
        List<Entry> list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        //offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
        String sql = "SELECT * FROM " + EnjoyDataHelper.TABLE_NAME_ITEM +
                "where "+EnjoyDataHelper.ITEM_COLUMN_FAVOR+"=? order by "+EnjoyDataHelper.ITEM_COLUMN_CREATE_AT;
        Log.d(TAG,"getFavorDataList sql:"+sql);
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(Const.LIKE)});
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                Entry data = cursorToData(cursor);
                list.add(data);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }


}
