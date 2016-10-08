package fr.damienbrun.browsary;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.damienbrun.browsary.webpage.WebpageDb;

public class CursorAdapterActionBarUrl extends CursorAdapter {

	private ContentResolver mContentResolver;
	private static final String[] mPROJECTION = { WebpageDb.KEY_URL };

	public CursorAdapterActionBarUrl(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mContentResolver = context.getContentResolver();
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.item_actionbarurl,
				parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView titleTextView = (TextView) view
				.findViewById(R.id.textview_title);
		TextView urlTextView = (TextView) view.findViewById(R.id.textview_url);
		ImageView favIconImageView = (ImageView) view
				.findViewById(R.id.imageview_icon);

		titleTextView.setText(cursor.getString(cursor
				.getColumnIndex(WebpageDb.KEY_TITLE)));
		urlTextView.setText(cursor.getString(cursor
				.getColumnIndex(WebpageDb.KEY_URL)));
		favIconImageView.setColorFilter(
				context.getResources().getColor(R.color.text_tertiary),
				Mode.MULTIPLY);
		if (cursor.getInt(cursor.getColumnIndex(WebpageDb.KEY_FAVORITE)) == 1)
			favIconImageView.setImageResource(R.drawable.ic_action_important);
		else
			favIconImageView.setImageResource(R.drawable.ic_action_time);
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (getFilterQueryProvider() != null) {
			return getFilterQueryProvider().runQuery(constraint);
		}
		return mContentResolver.query(WebpageContentProvider.CONTENT_URI,
				mPROJECTION, WebpageDb.KEY_URL + " LIKE ?", new String[] { "%"
						+ constraint.toString() + "%" }, null);
	}

	@Override
	public String convertToString(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(WebpageDb.KEY_URL));
	}

}
