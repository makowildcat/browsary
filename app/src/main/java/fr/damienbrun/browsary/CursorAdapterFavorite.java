package fr.damienbrun.browsary;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.damienbrun.browsary.webpage.WebpageDb;
import fr.damienbrun.browsary.webpage.WebpageModel;

public class CursorAdapterFavorite extends CursorAdapter {

	private LayoutInflater mInflater;

	public CursorAdapterFavorite(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.item_favorite, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView titleTextView = (TextView) view
				.findViewById(R.id.textview_title);
		ImageView thumbnailImageView = (ImageView) view
				.findViewById(R.id.gridview_item_image);
		int rowId = cursor.getInt(cursor.getColumnIndex(WebpageDb.KEY_ROW_ID));
		String fileName = WebpageModel.THUMBNAIL_NAME + Integer.toString(rowId)
				+ WebpageModel.THUMBNAIL_EXT;
		File imgFile = new File(context.getFilesDir(), fileName);

		titleTextView.setText(cursor.getString(cursor
				.getColumnIndex(WebpageDb.KEY_TITLE)));
		if (imgFile.exists()) {
			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
					.getAbsolutePath());
			thumbnailImageView.setImageBitmap(myBitmap);
		} else
			thumbnailImageView.setImageResource(R.drawable.ic_action_web_site);
	}
}
