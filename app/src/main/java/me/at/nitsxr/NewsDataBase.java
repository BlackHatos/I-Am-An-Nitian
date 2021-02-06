package me.at.nitsxr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class NewsDataBase extends SQLiteOpenHelper
{
    public static final String NEWS_TABLE = "NEWS_TABLE";
    public static final String LOCAL_ID = "ID";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String STATUS = "STATUS";
    public static final String COUNT = "COUNT";
    public static final String DATE = "DATE";
    public static final String REMOTE_ID = "REMOTE_ID";
    public static final String IMAGE_URL = "IMAGE_URL";

    public NewsDataBase(@Nullable Context context)
    {
        super(context,"news.db", null, 1);
    }

    // this method is called when first time you try to access database object
    // so put here statements to create table
    @Override
    public void onCreate(SQLiteDatabase db)
    {
         String createTableStatement = "CREATE TABLE " + NEWS_TABLE + " (" + LOCAL_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT," +
                " " + DESCRIPTION + " TEXT, " + STATUS + " INTEGER," +
                " " + COUNT + " INTEGER, " + DATE + " TEXT, " + REMOTE_ID + " INTEGER," +
                 " " + IMAGE_URL + " TEXT)";
        db.execSQL(createTableStatement);
    }

    // it is called when database version number changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public void addOne(NewsGetterSetter newsGetterSetter)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE, newsGetterSetter.getNewsTitle());
        cv.put(DESCRIPTION, newsGetterSetter.getNewsDescp());
        cv.put(STATUS, newsGetterSetter.getStatus());
        cv.put(COUNT, newsGetterSetter.getCount());
        cv.put(DATE, newsGetterSetter.getNewsDate());
        cv.put(REMOTE_ID, newsGetterSetter.getNewsId());
        cv.put(IMAGE_URL, newsGetterSetter.getImageUrl());

        if(!isPresent(newsGetterSetter.getNewsId()))
            db.insert(NEWS_TABLE, null, cv);
    }

    public List<NewsGetterSetter> getData()
    {
        List<NewsGetterSetter> mList = new ArrayList<>();
        String queryString = "SELECT * FROM "+ NEWS_TABLE +" ORDER BY "+ LOCAL_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst())
        {
            do
            {
                int localId = cursor.getInt(0);
                String newsTitle = cursor.getString(1);
                String newsDescription = cursor.getString(2);
                int newsStatus = cursor.getInt(3);
                int newsCount = cursor.getInt(4);
                String newsDate = cursor.getString(5);
                String remoteId = cursor.getString(6);
                String imageUrl = cursor.getString(7);

                NewsGetterSetter newsGetterSetter = new NewsGetterSetter();
                newsGetterSetter.setLocalId(localId+"");
                newsGetterSetter.setNewsTitle(newsTitle);
                newsGetterSetter.setNewsDescp(newsDescription);
                newsGetterSetter.setStatus(newsStatus+"");
                newsGetterSetter.setCount(newsCount+"");
                newsGetterSetter.setNewsDate(newsDate);
                newsGetterSetter.setNewsId(remoteId);
                newsGetterSetter.setImageUrl(imageUrl);
                mList.add(newsGetterSetter);
            } while(cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return mList;
    }

    public void deleteRecord(String remoteId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ NEWS_TABLE +" WHERE REMOTE_ID = "+ remoteId);
        db.close();
    }

    public boolean isPresent(String remoteId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM "+ NEWS_TABLE + " WHERE REMOTE_ID = "+ remoteId;
        int status = db.rawQuery(sql, null).getCount();
        return status > 0 ? true : false;
    }

    public void updateRecord(String remoteId, String status, String count)
    {
        String sql = "UPDATE "+ NEWS_TABLE + " SET "+ STATUS + " = "+status+
                ", "+ COUNT + " = " + count +" WHERE "+ REMOTE_ID +" = "+remoteId;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }
}
