package com.example.moodscape.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.moodscape.models.MoodEntry;
import com.example.moodscape.models.DiaryEntry;
import com.example.moodscape.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "moodscape.db";
    private static final int DB_VERSION = 1;

    // Tables
    private static final String TABLE_USERS = "users";
    private static final String TABLE_MOOD_HISTORY = "mood_history";
    private static final String TABLE_DIARY = "diary";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "gender TEXT," +
                "age INTEGER," +
                "phone TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "profile_pic TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_MOOD_HISTORY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "phone TEXT NOT NULL," +
                "mood TEXT NOT NULL," +
                "intensity TEXT NOT NULL," +
                "trigger_point TEXT," +
                "reason TEXT," +
                "date_time TEXT NOT NULL," +
                "is_positive INTEGER DEFAULT 1" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_DIARY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "phone TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "emotion TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOOD_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY);
        onCreate(db);
    }

    // --- USER METHODS ---
    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", user.getName());
        cv.put("gender", user.getGender());
        cv.put("age", user.getAge());
        cv.put("phone", user.getPhone());
        cv.put("password", user.getPassword());
        long result = db.insert(TABLE_USERS, null, cv);
        db.close();
        return result != -1;
    }

    public boolean phoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE phone=?",
                new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public User loginUser(String phone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE phone=? AND password=?",
                new String[]{phone, password});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow("gender")));
            user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            user.setProfilePic(cursor.getString(cursor.getColumnIndexOrThrow("profile_pic")));
        }
        cursor.close();
        db.close();
        return user;
    }

    public boolean updatePassword(String phone, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);
        int rows = db.update(TABLE_USERS, cv, "phone=?", new String[]{phone});
        db.close();
        return rows > 0;
    }

    public boolean updateProfilePic(String phone, String picPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("profile_pic", picPath);
        int rows = db.update(TABLE_USERS, cv, "phone=?", new String[]{phone});
        db.close();
        return rows > 0;
    }

    public User getUserByPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE phone=?",
                new String[]{phone});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow("gender")));
            user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            user.setProfilePic(cursor.getString(cursor.getColumnIndexOrThrow("profile_pic")));
        }
        cursor.close();
        db.close();
        return user;
    }

    // --- MOOD HISTORY METHODS ---
    public boolean saveMoodEntry(MoodEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("phone", entry.getPhone());
        cv.put("mood", entry.getMood());
        cv.put("intensity", entry.getIntensity());
        cv.put("trigger_point", entry.getTriggerPoint());
        cv.put("reason", entry.getReason());
        cv.put("date_time", entry.getDateTime());
        cv.put("is_positive", entry.isPositive() ? 1 : 0);
        long result = db.insert(TABLE_MOOD_HISTORY, null, cv);
        db.close();
        return result != -1;
    }

    public List<MoodEntry> getMoodHistory(String phone) {
        List<MoodEntry> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_MOOD_HISTORY + " WHERE phone=? ORDER BY id DESC",
                new String[]{phone});
        if (cursor.moveToFirst()) {
            do {
                MoodEntry e = new MoodEntry();
                e.setMood(cursor.getString(cursor.getColumnIndexOrThrow("mood")));
                e.setIntensity(cursor.getString(cursor.getColumnIndexOrThrow("intensity")));
                e.setTriggerPoint(cursor.getString(cursor.getColumnIndexOrThrow("trigger_point")));
                e.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
                e.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
                e.setPositive(cursor.getInt(cursor.getColumnIndexOrThrow("is_positive")) == 1);
                list.add(e);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // --- DIARY METHODS ---
    public boolean saveDiaryEntry(DiaryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if entry for date exists, update or insert
        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_DIARY + " WHERE phone=? AND date=?",
                new String[]{entry.getPhone(), entry.getDate()});
        boolean result;
        ContentValues cv = new ContentValues();
        cv.put("phone", entry.getPhone());
        cv.put("date", entry.getDate());
        cv.put("content", entry.getContent());
        cv.put("emotion", entry.getEmotion());
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            result = db.update(TABLE_DIARY, cv, "id=?", new String[]{String.valueOf(id)}) > 0;
        } else {
            result = db.insert(TABLE_DIARY, null, cv) != -1;
        }
        cursor.close();
        db.close();
        return result;
    }

    public DiaryEntry getDiaryEntryByDate(String phone, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_DIARY + " WHERE phone=? AND date=?",
                new String[]{phone, date});
        DiaryEntry entry = null;
        if (cursor.moveToFirst()) {
            entry = new DiaryEntry();
            entry.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            entry.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
            entry.setEmotion(cursor.getString(cursor.getColumnIndexOrThrow("emotion")));
        }
        cursor.close();
        db.close();
        return entry;
    }

    public List<String> getDatesWithEntries(String phone) {
        List<String> dates = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT date FROM " + TABLE_DIARY + " WHERE phone=?",
                new String[]{phone});
        if (cursor.moveToFirst()) {
            do { dates.add(cursor.getString(0)); }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dates;
    }
}
