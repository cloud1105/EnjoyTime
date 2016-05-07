package com.leo.enjoytime.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leo.enjoytime.model.GanChaiEntry;
import com.leo.enjoytime.model.GanhuoEntry;

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

    public synchronized void insertData(GanhuoEntry entry){
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(EnjoyDataHelper.ITEM_COLUMN_URL, entry.getUrl());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_TYPE, entry.getType());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_CREATE_AT, entry.getPublishedAt());
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

    public synchronized void insertDigest(GanChaiEntry entry){
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(EnjoyDataHelper.ITEM_COLUMN_DIGEST_ID, entry.getId());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_DIGEST_SOURCE_URL, entry.getSource());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_DIGEST_TYPE, entry.getType());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_DIGEST_SUMMARY, entry.getSummary());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_FAVOR, entry.getFavor_flag());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_DIGEST_THUMBNAIL, entry.getThumbnail());
        cv.put(EnjoyDataHelper.ITEM_COLUMN_DIGEST_TITLE, entry.getTitle());
        long result = db.replace(EnjoyDataHelper.TABLE_NAME_DIGEST,null,cv);
        if(result != -1) {
            db.setTransactionSuccessful();
        }else{
            Log.e(TAG,"insertDigest error!");
        }
        db.endTransaction();
        db.close();
    }

    public synchronized void changeArticleToLikeOrUnlike(GanhuoEntry entry){
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(EnjoyDataHelper.ITEM_COLUMN_FAVOR, entry.getFavor_flag());
        long result = db.update(EnjoyDataHelper.TABLE_NAME_ITEM, cv,
                EnjoyDataHelper.ITEM_COLUMN_URL + " = ?", new String[]{entry.getUrl()});
        if(result != -1) {
            db.setTransactionSuccessful();
        }else{
            Log.e(TAG,"changeArticleToLikeOrUnlike: updateData error!");
        }
        db.endTransaction();
        db.close();
    }

    public synchronized void changeDigestToLikeOrUnlike(GanChaiEntry entry){
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(EnjoyDataHelper.ITEM_COLUMN_FAVOR, entry.getFavor_flag());
        long result = db.update(EnjoyDataHelper.TABLE_NAME_DIGEST,cv,
                EnjoyDataHelper.ITEM_COLUMN_DIGEST_SOURCE_URL +" = ?",new String[]{entry.getSource()});
        if(result != -1) {
            db.setTransactionSuccessful();
        }else{
            Log.e(TAG,"changeDigestToLikeOrUnlike: updateData error!");
        }
        db.endTransaction();
        db.close();
    }

    public GanhuoEntry getDataByUrl(String url){
        GanhuoEntry data = null;
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

    public GanChaiEntry getDigestByUrl(String url){
        GanChaiEntry data = null;
        db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM "+EnjoyDataHelper.TABLE_NAME_DIGEST+" where "+EnjoyDataHelper.ITEM_COLUMN_DIGEST_SOURCE_URL+
                " = ? ";
        Log.d(TAG, "getDataByUrl sql:" + sql);
        Cursor cursor = db.rawQuery(sql, new String[]{url});
        if (cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
            data = cursorToDigestData(cursor);
        }
        cursor.close();
        return data;
    }

    public List<GanhuoEntry> getDataList(int count,int pageSize,String type){
        List<GanhuoEntry> list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        //offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
        String sql = "SELECT * FROM " + EnjoyDataHelper.TABLE_NAME_ITEM +" where "+EnjoyDataHelper.ITEM_COLUMN_TYPE+
                " = ? "+"order by "+EnjoyDataHelper.ITEM_COLUMN_CREATE_AT +" desc limit "+ count +" offset "+pageSize*count;
        Log.d(TAG,"getDataList sql:"+sql);
        Cursor cursor = db.rawQuery(sql,new String[]{type});
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                GanhuoEntry data = cursorToData(cursor);
                list.add(data);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    public List<GanChaiEntry> getDigestList(int count,int pageSize,String type){
        List<GanChaiEntry> list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        //offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
        String sql = "SELECT * FROM " + EnjoyDataHelper.TABLE_NAME_DIGEST +" where "+EnjoyDataHelper.ITEM_COLUMN_TYPE+
                " = ? "+"order by "+EnjoyDataHelper.ITEM_COLUMN_DIGEST_ID +" desc limit "+ count +" offset "+pageSize*count;
        Log.d(TAG,"getDigestList sql:"+sql);
        Cursor cursor = db.rawQuery(sql,new String[]{type});
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                GanChaiEntry data = cursorToDigestData(cursor);
                list.add(data);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    public List<GanhuoEntry> getFavorlist(int count, int pageSize) {
        List<GanhuoEntry> list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        //offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
        String sql = "SELECT DESC,CREATE_AT,URL FROM " + EnjoyDataHelper.TABLE_NAME_ITEM +" where "+EnjoyDataHelper.ITEM_COLUMN_FAVOR+
                " = 1 "+"order by "+EnjoyDataHelper.ITEM_COLUMN_CREATE_AT +" desc limit "+ count +" offset "+pageSize*count;
        Log.d(TAG,"getFavorlist sql:"+sql);
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                GanhuoEntry data = cursorToData(cursor);
                list.add(data);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    private GanChaiEntry cursorToDigestData(Cursor cursor) {
        GanChaiEntry entry = new GanChaiEntry();
        int id = cursor.getInt(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_DIGEST_ID));
        String type = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_DIGEST_TYPE));
        String url = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_DIGEST_SOURCE_URL));
        String summary = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_DIGEST_SUMMARY));
        int favor_flag = cursor.getInt(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_FAVOR));
        String thumbnail = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_DIGEST_THUMBNAIL));
        String title = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_DIGEST_TITLE));
        entry.setSource(url);
        entry.setSummary(summary);
        entry.setFavor_flag(favor_flag);
        entry.setThumbnail(thumbnail);
        entry.setTitle(title);
        return  entry;
    }

    private GanhuoEntry cursorToData(Cursor cursor) {
        GanhuoEntry entry = new GanhuoEntry();
        if (cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_TYPE) != -1) {
            String type = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_TYPE));
            entry.setType(type);
        }
        if (cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_FAVOR) != -1) {
            int favor_flag = cursor.getInt(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_FAVOR));
            entry.setFavor_flag(favor_flag);
        }
        String url = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_URL));
        String desc = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_DESC));
        String create_at = cursor.getString(cursor.getColumnIndex(EnjoyDataHelper.ITEM_COLUMN_CREATE_AT));
        entry.setUrl(url);
        entry.setDesc(desc);
        entry.setPublishedAt(create_at);
        return entry;
    }

}
