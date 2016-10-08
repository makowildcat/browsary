package fr.damienbrun.browsary.webpage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WebpageDb extends SQLiteOpenHelper {

	private static final String DBNAME = "webpages";

	private static final int VERSION = 1;

	public static final String DATABASE_TABLE = "wp";
	public static final String KEY_ROW_ID = "_id";
	public static final String KEY_TITLE = "wp_title";
	public static final String KEY_URL = "wp_url";
	public static final String KEY_FAVORITE = "wp_fav";

	private SQLiteDatabase mDb;

	public WebpageDb(Context context) {
		super(context, DBNAME, null, VERSION);
		this.mDb = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROW_ID
				+ " INTEGER PRIMARY KEY autoincrement , " + KEY_TITLE
				+ " TEXT  , " + KEY_URL + "  TEXT UNIQUE, " + KEY_FAVORITE
				+ " INTEGER DEFAULT 0); ";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public Cursor getWebpage(String selection, String[] selectionArgs,
			String sortOrder) {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROW_ID, KEY_TITLE,
				KEY_URL, KEY_FAVORITE }, selection, selectionArgs, null, null,
				sortOrder);
	}

}
