package fr.damienbrun.browsary.webpage;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.damienbrun.browsary.R;

public class WebpageArrayAdapter extends ArrayAdapter<WebpageModel> {

	private final Context context;
	private final ArrayList<WebpageModel> webpageArrayList;

	public WebpageArrayAdapter(Context context, ArrayList<WebpageModel> objects) {
		super(context, R.layout.item_webpage, objects);
		this.context = context;
		this.webpageArrayList = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// manage "+" item in listview
		if (webpageArrayList.get(position) == null) {
			View view = inflater.inflate(R.layout.item_webpage_add, parent,
					false);
			ImageView imageView = (ImageView) view
					.findViewById(R.id.imageview_add);
			imageView.setColorFilter(
					context.getResources().getColor(
							R.color.background_secondary), Mode.MULTIPLY);
			return view;
		}

		View rowView = inflater.inflate(R.layout.item_webpage, parent, false);
		TextView textViewTitle = (TextView) rowView
				.findViewById(R.id.textview_title);
		ImageView imageViewRemove = (ImageView) rowView
				.findViewById(R.id.imageview_remove);
		ImageView imageViewFavIcon = (ImageView) rowView
				.findViewById(R.id.imageview_favicon);

		textViewTitle.setText(webpageArrayList.get(position).getTitle());
		imageViewRemove.setColorFilter(context.getResources().getColor(
				R.color.background_secondary));
		if (webpageArrayList.get(position).getIconBitmap() != null) {
			imageViewFavIcon.setImageBitmap(webpageArrayList.get(position)
					.getIconBitmap());
		} else {
			imageViewFavIcon.setImageResource(R.drawable.ic_action_web_site);
			imageViewFavIcon.setColorFilter(context.getResources().getColor(
					R.color.text_tertiary));
		}

		return rowView;
	}

}
