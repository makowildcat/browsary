package fr.damienbrun.browsary;

import fr.damienbrun.browsary.webpage.WebpageDb;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class WebpageContentProvider extends ContentProvider {

	public static final String TAG = "WebpageContentProdiver";

	public static final String PROVIDER_NAME = "fr.damienbrun.browsary.webpagecontentprovider";
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ PROVIDER_NAME + "/webpages");
	private static final int WEBPAGES = 1;
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "webpages", WEBPAGES);
	}

	private WebpageDb mWebpageDb;

	@Override
	public boolean onCreate() {
		mWebpageDb = new WebpageDb(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (uriMatcher.match(uri) == WEBPAGES) {
			return mWebpageDb.getWebpage(selection, selectionArgs, sortOrder);
		} else {
			return null;
		}
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mWebpageDb.getWritableDatabase();
		try {
			db.insertOrThrow(WebpageDb.DATABASE_TABLE, null, values);
		} catch (SQLiteConstraintException e) {
			Log.v(TAG, e.getMessage() + "maybe there I'll update the date");
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mWebpageDb.getWritableDatabase();
		db.update(WebpageDb.DATABASE_TABLE, values, selection, selectionArgs);

		// instead of return number of updated data, I send back the rowId
		Cursor c = mWebpageDb.getWebpage(selection, selectionArgs, null);
		c.moveToFirst();
		return c.getInt(c.getColumnIndex(WebpageDb.KEY_ROW_ID));
	}

}
