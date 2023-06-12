package com.example.kuzevcoursepaperweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SqliteDatabase extends SQLiteOpenHelper {

    private	static final int DATABASE_VERSION =	5;
    private	static final String	DATABASE_NAME = "citiesstore";
    private	static final String DATABASE_TABLE = "cities";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "cityname";
    private static final String COLUMN_URL = "cityurl";

    public SqliteDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public SqliteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE	TABLE " + DATABASE_TABLE + "(" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT,"
                + COLUMN_URL + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public ArrayList<CityModel> listCities() {
        String sql = "SELECT * FROM " + DATABASE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CityModel> storeCities = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                String url = cursor.getString(2);
                storeCities.add(new CityModel(id, name, url));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storeCities;
    }

    public void addCity(CityModel cityModel) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, cityModel.getName());
        values.put(COLUMN_URL, "https://www.foreca.ru/" + cityModel.getUrl());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE, null, values);
    }

    public void updateCities(CityModel cityModel) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, cityModel.getName());
        values.put(COLUMN_URL, cityModel.getUrl());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DATABASE_TABLE, values, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(cityModel.getId()) });
    }

    public CityModel findCity(String name) {
        String query = "SELECT * FROM "	+ DATABASE_TABLE + " WHERE " + COLUMN_NAME + " = " + "name";
        SQLiteDatabase db = this.getWritableDatabase();
        CityModel cityModel = null;
        Cursor cursor = db.rawQuery(query,	null);
        if	(cursor.moveToFirst()) {
            int id = Integer.parseInt(cursor.getString(0));
            String cityName = cursor.getString(1);
            String cityUrl = cursor.getString(2);
            cityModel = new CityModel(id, cityName, cityUrl);
        }
        cursor.close();
        return cityModel;
    }

    public void deleteCity(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id) });
    }
}
