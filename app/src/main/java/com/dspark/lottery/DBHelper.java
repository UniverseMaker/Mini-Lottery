package com.dspark.lottery;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // String 보다 StringBuffer가 Query 만들기 편하다.
        //StringBuffer sb = new StringBuffer();
        //sb.append(" CREATE TABLE RECORD ( ID INTEGER PRIMARY KEY AUTOINCREMENT,  NAME TEXT,  TARGET TEXT,  WINNER TEXT,  DATE TEXT ) ");

        // SQLite Database로 쿼리 실행
        db.execSQL("CREATE TABLE RECORD ( ID INTEGER PRIMARY KEY AUTOINCREMENT,  NAME TEXT,  DATA TEXT,  DATE TEXT )");
        db.execSQL("CREATE TABLE MEMBERSET ( ID INTEGER PRIMARY KEY AUTOINCREMENT,  NAME TEXT,  MEMBER TEXT )");
        db.execSQL("CREATE TABLE PRIZESET ( ID INTEGER PRIMARY KEY AUTOINCREMENT,  NAME TEXT,  PRIZE TEXT )");
        //Toast.makeText(context, "Table 생성완료", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Toast.makeText(context, "버전이 올라갔습니다.", Toast.LENGTH_SHORT).show();
    }

    public void testDB() {
        SQLiteDatabase db = getReadableDatabase();
    }

    public void querySQL(String sql)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(sql);
    }

    public Map<Integer, ArrayList<String>> getRecord()
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT ID, NAME, DATA, DATE FROM RECORD ORDER BY ID DESC", null);
        Map<Integer, ArrayList<String>> data = new TreeMap<Integer, ArrayList<String>>(Collections.reverseOrder());

        // moveToNext 다음에 데이터가 있으면 true 없으면 false
        while( cursor.moveToNext() ) {
            data.put(cursor.getInt(0), new ArrayList<String>());
            data.get(cursor.getInt(0)).add(cursor.getString(1));
            data.get(cursor.getInt(0)).add(cursor.getString(2));
            data.get(cursor.getInt(0)).add(cursor.getString(3));
        }

        return data;
    }

    public void insertRecord(String name, String data, String date)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("INSERT INTO RECORD (  NAME, DATA, DATE )  VALUES (  '" + name  + "', '" + data + "', '" + date + "' )");
    }

    public void removeRecord(String id)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM RECORD WHERE ID=" + id);
    }

    public Map<Integer, ArrayList<String>> getMember()
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT ID, NAME, MEMBER FROM MEMBERSET ORDER BY ID DESC", null);
        Map<Integer, ArrayList<String>> data = new TreeMap<Integer, ArrayList<String>>(Collections.reverseOrder());

        // moveToNext 다음에 데이터가 있으면 true 없으면 false
        while( cursor.moveToNext() ) {
            data.put(cursor.getInt(0), new ArrayList<String>());
            data.get(cursor.getInt(0)).add(cursor.getString(1));
            data.get(cursor.getInt(0)).add(cursor.getString(2));
        }

        return data;
    }

    public void insertMember(String name, String member)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("INSERT INTO MEMBERSET (  NAME, MEMBER )  VALUES (  '" + name  + "', '" + member + "' )");
    }

    public void updateMember(String id, String name, String member)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("UPDATE MEMBERSET SET NAME='" + name + "', MEMBER='" + member + "' WHERE ID=" + id);
    }

    public void removeMember(String id)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM MEMBERSET WHERE ID=" + id);
    }

    public Map<Integer, ArrayList<String>> getPrize()
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT ID, NAME, PRIZE FROM PRIZESET ORDER BY ID DESC", null);
        Map<Integer, ArrayList<String>> data = new TreeMap<>(Collections.reverseOrder());

        // moveToNext 다음에 데이터가 있으면 true 없으면 false
        while( cursor.moveToNext() ) {
            data.put(cursor.getInt(0), new ArrayList<String>());
            data.get(cursor.getInt(0)).add(cursor.getString(1));
            data.get(cursor.getInt(0)).add(cursor.getString(2));
        }

        return data;
    }

    public void insertPrize(String name, String prize)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("INSERT INTO PRIZESET ( NAME, PRIZE )  VALUES ('" + name  + "', '" + prize + "' )");
    }

    public void updatePrize(String id, String name, String prize)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("UPDATE PRIZESET SET NAME='" + name + "', PRIZE='" + prize + "' WHERE ID=" + id);
    }

    public void removePrize(String id)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM PRIZESET WHERE ID=" + id);
    }


}
