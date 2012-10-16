package studip.app.db;

import java.util.ArrayList;

import studip.app.util.Serializer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_NAME = "studipappdb";
	 
    public static final String TABLE_NEWS = "news";
	public static final String TABLE_USERS = "users";
	public static final String TABLE_COURSES = "courses";
	public static final String TABLE_ACTIVITIES = "activities";
	public static final String TABLE_SEMESTERS = "semesters";
	public static final String TABLE_DOCUMENTS = "documents";
	
    private static final String KEY_ID = "id";
    private static final String KEY_VALUE = "value";
	
    public static DatabaseHandler instance;
    
	private DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static void initialize(Context context) {
		instance = new DatabaseHandler(context);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_VALUE + " BLOB" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NEWS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_VALUE + " BLOB" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COURSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_VALUE + " BLOB" + ")");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVITIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_VALUE + " BLOB" + ")");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SEMESTERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_VALUE + " BLOB" + ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
	}
	
	public void close() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.close();
	}
	
	public void addObject(Object object, String table) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_VALUE, Serializer.serializeObject(object));
	    
	    db.insert(table, null, values);
	    db.close();
	}
	
	public Object getObject(int id, String table) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    Cursor cursor = db.query(table, new String[] { KEY_ID, KEY_VALUE }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    byte[] data = cursor.getBlob(1);
	    cursor.close();
	    db.close();
	    
	    return Serializer.deserializeObject(data);
	}
	
	public ArrayList<Object> getAllObjects(String table) {
	    ArrayList<Object> list = new ArrayList<Object>();

	    String selectQuery = "SELECT  * FROM " + table;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    if (cursor.moveToFirst()) {
	        do {
	        	list.add(Serializer.deserializeObject(cursor.getBlob(1)));
	        } while (cursor.moveToNext());
	    }
	    
	    cursor.close();
	    db.close();
	    
	    return list;
	}
}
