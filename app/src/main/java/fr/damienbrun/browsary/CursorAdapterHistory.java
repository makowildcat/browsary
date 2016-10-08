package fr.damienbrun.browsary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import fr.damienbrun.browsary.R;
import fr.damienbrun.browsary.webpage.WebpageDb;

public class CursorAdapterHistory extends CursorAdapter {

	private LayoutInflater mInflater;

	public CursorAdapterHistory(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.item_history, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView titleTextView = (TextView) view
				.findViewById(R.id.textview_title);
		TextView urlTextView = (TextView) view.findViewById(R.id.textview_url);

		titleTextView.setText(cursor.getString(cursor
				.getColumnIndex(WebpageDb.KEY_TITLE)));
		urlTextView.setText(cursor.getString(cursor
				.getColumnIndex(WebpageDb.KEY_URL)));
	}

}
