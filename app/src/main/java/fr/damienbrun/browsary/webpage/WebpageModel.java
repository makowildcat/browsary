package fr.damienbrun.browsary.webpage;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class WebpageModel {

	public static final String KEY_TITLE = "title";
	public static final String KEY_URL = "url";

	public static final String FAVICON_NAME = "favicon";
	public static final String FAVICON_EXT = ".png";

	public static final String THUMBNAIL_NAME = "thumbnail";
	public static final String THUMBNAIL_EXT = ".jpg";
	
	private String mTitle;
	private Bitmap mFavIcon;
	private String mUrl;

	public WebpageModel(String title, String url) {
		this.setTitle(title);
		this.setUrl(url);
	}

	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(KEY_TITLE, this.mTitle);
			obj.put(KEY_URL, this.mUrl);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public Bitmap getIconBitmap() {
		return mFavIcon;
	}

	public void setIconBitmap(Bitmap iconBitmap) {
		this.mFavIcon = iconBitmap;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}
}
