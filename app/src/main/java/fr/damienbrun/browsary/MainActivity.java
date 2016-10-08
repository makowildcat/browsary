package fr.damienbrun.browsary;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebIconDatabase;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import fr.damienbrun.browsary.thesaurus.ThesaurusFragment;
import fr.damienbrun.browsary.tool.Image;
import fr.damienbrun.browsary.webpage.WebpageArrayAdapter;
import fr.damienbrun.browsary.webpage.WebpageDb;
import fr.damienbrun.browsary.webpage.WebpageFragment;
import fr.damienbrun.browsary.webpage.WebpageFragmentListener;
import fr.damienbrun.browsary.webpage.WebpageModel;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnFocusChangeListener, OnEditorActionListener,
		WebpageFragmentListener, LoaderCallbacks<Cursor>, TextWatcher {

	private static final String TAG = "MainActivity";
	private static final String KEY_WEBPAGE = "wp";
	private static final String KEY_CURRENT_POSITION = "cp";

	private static final String DEFAULT_URL = "www.google.com";
	private static final String DEFAULT_TITLE = "Google";

	private static final int LOADER_FAVORITE = 0;
	private static final int LOADER_HISTORY = 1;
	private static final int LOADER_ISFAVORITE = 2;
	private static final int LOADER_ALL = 3;

	private DrawerLayout mDrawerLayout;
	private ListView mWebpageListView;
	private ArrayList<WebpageModel> models;
	private WebpageArrayAdapter navDrawerAdapter;

	private AutoCompleteTextView mUrlAutoCompleteTextView;
	private ProgressBar mLoadingProgressBar;
	private ImageButton mRefreshImageButton;
	private ImageButton mClrFavImageButton;

	private ArrayList<WebpageFragment> fragments;
	private int current_position = 0;

	private CursorAdapterActionBarUrl mAdapterActionBarUrl;
	private CursorAdapterFavorite mAdapterFavorite;
	private CursorAdapterHistory mAdapterHistory;
	private ContentValues mContentValues;
	private String[] mSelectionArgs = { "%", "%" };
	private boolean favorite;

	private ThesaurusFragment thesaurusFragment;

	private ClipboardManager myClipboardManager;

	/*
	 * lifecycle
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*
		 *  instanciate views
		 */
		LayoutInflater inflater = LayoutInflater.from(this);
		View actionBarView = inflater.inflate(R.layout.actionbar_web, null);
		ImageButton webpageImageButton = (ImageButton) actionBarView
				.findViewById(R.id.imagebutton_webpage);
		mRefreshImageButton = (ImageButton) actionBarView
				.findViewById(R.id.imagebutton_refresh);
		mLoadingProgressBar = (ProgressBar) actionBarView
				.findViewById(R.id.progressbar_loading);
		mUrlAutoCompleteTextView = (AutoCompleteTextView) actionBarView
				.findViewById(R.id.autocompletetextview_url);
		mClrFavImageButton = (ImageButton) actionBarView
				.findViewById(R.id.imagebutton_clearfavorite);
		ImageButton forwardImageButton = (ImageButton) actionBarView
				.findViewById(R.id.imagebutton_forward);
		ImageButton homeImageButton = (ImageButton) actionBarView
				.findViewById(R.id.imagebutton_home);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mWebpageListView = (ListView) findViewById(R.id.listview_webpage);
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		GridView favoriteGridView = (GridView) findViewById(R.id.gridview_favorite);
		ListView historyListView = (ListView) findViewById(R.id.listview_history);
		EditText historyEditTxt = (EditText) findViewById(R.id.edittext_history);

		/*
		 *  instanciate models, fragments & cie
		 */
		SharedPreferences mPrefs = getSharedPreferences(TAG, 0);
		current_position = mPrefs.getInt(KEY_CURRENT_POSITION, 0);
		models = loadWebpageFromSharePref();
		if (models.size() == 0) {
			models.add(new WebpageModel(DEFAULT_TITLE, DEFAULT_URL));
		}
		fragments = new ArrayList<WebpageFragment>();
		for (WebpageModel wpm : models) {
			fragments.add(WebpageFragment.newInstance(fragments.size(),
					wpm.getUrl()));
		}
		models.add(null);
		thesaurusFragment = new ThesaurusFragment();
		mContentValues = new ContentValues();
		getFragmentManager().beginTransaction()
				.add(R.id.container, fragments.get(current_position)).commit();

		/*
		 *  instanciate adapters
		 */
		navDrawerAdapter = new WebpageArrayAdapter(this, models);
		mAdapterActionBarUrl = new CursorAdapterActionBarUrl(this, null, 0);
		mAdapterFavorite = new CursorAdapterFavorite(this, null, 0);
		mAdapterHistory = new CursorAdapterHistory(this, null, 0);

		/*
		 *  populate AdapterView
		 */
		mWebpageListView.setAdapter(navDrawerAdapter);
		mUrlAutoCompleteTextView.setAdapter(mAdapterActionBarUrl);
		favoriteGridView.setAdapter(mAdapterFavorite);
		historyListView.setAdapter(mAdapterHistory);

		/*
		 *  few settings
		 */
		getActionBar().setCustomView(actionBarView);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
			WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());

		// tabhost
		tabHost.setup();
		tabHost.addTab(tabHost
				.newTabSpec("fav")
				.setContent(R.id.gridview_favorite)
				.setIndicator(
						"",
						getResources().getDrawable(
								R.drawable.ic_action_important)));
		tabHost.addTab(tabHost
				.newTabSpec("hist")
				.setContent(R.id.history)
				.setIndicator("",
						getResources().getDrawable(R.drawable.ic_action_time)));
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			ImageView iconImageView = (ImageView) tabHost.getTabWidget()
					.getChildAt(i).findViewById(android.R.id.icon);
			iconImageView.setColorFilter(
					getResources().getColor(R.color.background_secondary),
					Mode.MULTIPLY);
		}

		// launch asynch
		getLoaderManager().initLoader(LOADER_FAVORITE, null, this);
		getLoaderManager().initLoader(LOADER_HISTORY, null, this);
		getLoaderManager().initLoader(LOADER_ISFAVORITE, null, this);
		getLoaderManager().initLoader(LOADER_ALL, null, this);

		// set listeners
		mUrlAutoCompleteTextView.setOnFocusChangeListener(this);
		mUrlAutoCompleteTextView.setOnEditorActionListener(this);
		mUrlAutoCompleteTextView.setOnItemClickListener(this);
		forwardImageButton.setOnClickListener(this);
		mRefreshImageButton.setOnClickListener(this);
		mClrFavImageButton.setOnClickListener(this);
		webpageImageButton.setOnClickListener(this);
		homeImageButton.setOnClickListener(this);
		favoriteGridView.setOnItemClickListener(this);
		historyListView.setOnItemClickListener(this);
		historyEditTxt.addTextChangedListener(this);
		mWebpageListView.setOnItemClickListener(this);

		/*
		 *  part only to make it work with f*%#ing emulator...
		 */
		tabHost.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {

			@Override
			public void onViewDetachedFromWindow(View v) {
			}

			@Override
			public void onViewAttachedToWindow(View v) {
				v.getViewTreeObserver().removeOnTouchModeChangeListener(
						(TabHost) v);
			}
		});

		/*
		 *  workaround for old version
		 */
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
			myClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			myClipboardManager
					.addPrimaryClipChangedListener(new OnPrimaryClipChangedListener() {

						@Override
						public void onPrimaryClipChanged() {
							ClipData abc = myClipboardManager.getPrimaryClip();
							ClipData.Item item = abc.getItemAt(0);
							String text = item.getText().toString();
							onUpdateTextSelected(text);
							// to avoid "same copy" which won't launch listener
							abc = ClipData.newPlainText("text", text + " ");
							myClipboardManager.setPrimaryClip(abc);
						}
					});
		}

	}

	@Override
	protected void onResume() {
		Intent intent = getIntent();
		String linkUrl = intent.getDataString();
		if (linkUrl != null)
			fragments.get(current_position).loadUrl(linkUrl);
		super.onResume();
	}

	@Override
	protected void onStop() {
		SharedPreferences mPrefs = getSharedPreferences(TAG, 0);
		SharedPreferences.Editor editor = mPrefs.edit();

		JSONArray obj = new JSONArray();

		for (int i = 0; i < models.size() - 1; i++) {
			obj.put(models.get(i).getJSONObject());
			try {
				FileOutputStream ostream = openFileOutput(
						WebpageModel.FAVICON_NAME + Integer.toString(i)
								+ WebpageModel.FAVICON_EXT,
						Context.MODE_PRIVATE);
				models.get(i).getIconBitmap()
						.compress(Bitmap.CompressFormat.PNG, 100, ostream);
				ostream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		editor.putString(KEY_WEBPAGE, obj.toString());
		editor.putInt(KEY_CURRENT_POSITION, current_position);
		editor.commit();

		super.onStop();
	}

	public ArrayList<WebpageModel> loadWebpageFromSharePref() {
		SharedPreferences mPrefs = getSharedPreferences(TAG, 0);

		ArrayList<WebpageModel> webpages = new ArrayList<WebpageModel>();
		JSONArray obj = null;
		try {
			String s = mPrefs.getString(KEY_WEBPAGE, null);
			if (s == null)
				return webpages;
			obj = new JSONArray(s);
			for (int i = 0; i < obj.length(); i++) {
				JSONObject jsonObject;
				jsonObject = obj.getJSONObject(i);
				String title = jsonObject.getString(WebpageModel.KEY_TITLE);
				String url = jsonObject.getString(WebpageModel.KEY_URL);
				WebpageModel webpage = new WebpageModel(title, url);
				File favIconFile = new File(getFilesDir(),
						WebpageModel.FAVICON_NAME + Integer.toString(i)
								+ WebpageModel.FAVICON_EXT);
				if (favIconFile.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(favIconFile
							.getAbsolutePath());
					webpage.setIconBitmap(bitmap);
				}
				webpages.add(webpage);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return webpages;
	}

	/*
	 * LoaderCursor, here mainly to set adapter asynchronously
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case LOADER_FAVORITE:
			return new CursorLoader(this, WebpageContentProvider.CONTENT_URI,
					null, WebpageDb.KEY_FAVORITE + " LIKE 1", null,
					WebpageDb.KEY_TITLE + " asc ");
		case LOADER_HISTORY:
			return new CursorLoader(this, WebpageContentProvider.CONTENT_URI,
					null, WebpageDb.KEY_TITLE + " LIKE ? OR "
							+ WebpageDb.KEY_URL + " LIKE ?", mSelectionArgs,
					WebpageDb.KEY_ROW_ID + " desc ");
		case LOADER_ISFAVORITE:
			return new CursorLoader(this, WebpageContentProvider.CONTENT_URI,
					null, WebpageDb.KEY_URL + " LIKE ? AND "
							+ WebpageDb.KEY_FAVORITE + " LIKE 1",
					new String[] { mUrlAutoCompleteTextView.getText()
							.toString() }, null);
		case LOADER_ALL:
			return new CursorLoader(this, WebpageContentProvider.CONTENT_URI,
					null, null, null, null);
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case LOADER_FAVORITE:
			mAdapterFavorite.swapCursor(data);
			break;
		case LOADER_HISTORY:
			mAdapterHistory.swapCursor(data);
			break;
		case LOADER_ISFAVORITE:
			if (data.getCount() > 0) {
				mClrFavImageButton
						.setImageResource(R.drawable.ic_action_important);
				favorite = true;
			} else {
				mClrFavImageButton
						.setImageResource(R.drawable.ic_action_not_important);
				favorite = false;
			}
			break;
		case LOADER_ALL:
			mAdapterActionBarUrl.swapCursor(data);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
		case LOADER_FAVORITE:
			mAdapterFavorite.swapCursor(null);
		case LOADER_HISTORY:
			mAdapterHistory.swapCursor(null);
			break;
		case LOADER_ISFAVORITE:
			mAdapterActionBarUrl.swapCursor(null);
			break;
		}
	}

	/*
	 * New page (add fragment)
	 */
	private void addNewPage() {
		if (fragments.size() >= 5)
			return;
		models.add(models.size() - 1, new WebpageModel(DEFAULT_TITLE,
				DEFAULT_URL));
		navDrawerAdapter.notifyDataSetChanged();
		mUrlAutoCompleteTextView
				.setText(models.get(models.size() - 2).getUrl());
		fragments.add(WebpageFragment.newInstance(fragments.size(),
				models.get(models.size() - 2).getUrl()));
		getFragmentManager().beginTransaction()
				.hide(fragments.get(current_position))
				.add(R.id.container, fragments.get(fragments.size() - 1))
				.commit();
		current_position = fragments.size() - 1;
		hideSpecialBar();
	}

	/*
	 * Cancel tab (remove fragment), had to check position and make it change if needed
	 */
	public void clickCancel(View v) {
		if (models.size() <= 2) // if there is only one webpage
			return;
		int position = mWebpageListView
				.getPositionForView((View) v.getParent());
		File favIconFile = new File(getFilesDir(), WebpageModel.FAVICON_NAME
				+ Integer.toString(position) + WebpageModel.FAVICON_EXT);
		if (favIconFile.exists())
			favIconFile.delete();
		models.remove(position);
		navDrawerAdapter.notifyDataSetChanged();
		FragmentManager fm = getFragmentManager();
		if (fragments.get(position).isAdded())
			fm.beginTransaction().remove(fragments.get(position)).commit();
		fragments.remove(position);
		updateAllIndex();
		if (position == current_position) {
			if (position == fragments.size()) {
				if (fragments.get(position - 1).isAdded())
					fm.beginTransaction().show(fragments.get(position - 1))
							.commit();
				else
					fm.beginTransaction()
							.add(R.id.container, fragments.get(position - 1))
							.commit();
				current_position = position - 1;
			} else {
				if (fragments.get(position).isAdded())
					fm.beginTransaction().show(fragments.get(position))
							.commit();
				else
					fm.beginTransaction()
							.add(R.id.container, fragments.get(position))
							.commit();
			}
		} else {
			if (position < current_position)
				current_position--;
		}

		mUrlAutoCompleteTextView.setText(models.get(current_position).getUrl());
		getLoaderManager().restartLoader(LOADER_ISFAVORITE, null, this);
		hideSpecialBar();
	}

	private void updateAllIndex() {
		for (int i = 0; i < models.size() - 1; i++) {
			fragments.get(i).setmIndex(i);
		}
	}

	/*
	 * better behavior for Web browser
	 */
	@Override
	public void onBackPressed() {
		if (fragments.get(current_position).canGoBack()) {
			fragments.get(current_position).goBack();
			return;
		}
		super.onBackPressed();
	}

	/*
	 * Listener - WebpageFragment (mainly loading and update events)
	 */

	@Override
	public void onStartLoading() {
		hideSpecialBar();
		mRefreshImageButton.setVisibility(View.INVISIBLE);
		mLoadingProgressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onEndLoading() {
		mRefreshImageButton.setVisibility(View.VISIBLE);
		mLoadingProgressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onUpdateTitleUrl(int index, String title, String url) {
		models.get(index).setTitle(title);
		mUrlAutoCompleteTextView.setText(url);
		models.get(index).setUrl(url);
		navDrawerAdapter.notifyDataSetChanged();

		// Add to history
		mContentValues.put(WebpageDb.KEY_TITLE, title);
		mContentValues.put(WebpageDb.KEY_URL, url);
		getContentResolver().insert(WebpageContentProvider.CONTENT_URI,
				mContentValues);
		mContentValues.clear();
		getLoaderManager().restartLoader(LOADER_HISTORY, null, this);
		getLoaderManager().restartLoader(LOADER_ISFAVORITE, null, this);
	}

	@Override
	public void onUpdateIcon(int index, Bitmap icon) {
		models.get(index).setIconBitmap(icon);
		navDrawerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onUpdateTextSelected(String text_selected) {
		showThesaurusBar();
		thesaurusFragment.setWord(text_selected);
	}

	@Override
	public void hideSpecialBar() {
		if (thesaurusFragment.isAdded())
			getFragmentManager().beginTransaction().remove(thesaurusFragment)
					.commit();
	}

	/*
	 * listener - onClick (= all icons)
	 */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imagebutton_refresh:
			fragments.get(current_position).reload();
			break;
		case R.id.imagebutton_forward:
			fragments.get(current_position).goForward();
			break;
		case R.id.imagebutton_webpage:
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			else
				mDrawerLayout.openDrawer(Gravity.LEFT);
			break;
		case R.id.imagebutton_home:
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			else
				mDrawerLayout.openDrawer(Gravity.RIGHT);
			break;
		case R.id.imagebutton_clearfavorite:
			if (mUrlAutoCompleteTextView.hasFocus()) {
				mUrlAutoCompleteTextView.setText("");
			} else {
				if (favorite) { 	// Remove to Favorite
					favorite = false;
					mClrFavImageButton
							.setImageResource(R.drawable.ic_action_not_important);
					mContentValues.put(WebpageDb.KEY_FAVORITE, 0);
				} else { 			// Add to Favorite
					favorite = true;
					mClrFavImageButton
							.setImageResource(R.drawable.ic_action_important);
					mContentValues.put(WebpageDb.KEY_FAVORITE, 1);
				}
				int rowid = getContentResolver().update(
						WebpageContentProvider.CONTENT_URI, mContentValues,
						WebpageDb.KEY_URL + " LIKE ?",
						new String[] { models.get(current_position).getUrl() });
				mContentValues.clear();

				String fileName = WebpageModel.THUMBNAIL_NAME
						+ Integer.toString(rowid) + WebpageModel.THUMBNAIL_EXT;
				File thumbnailFile = new File(getFilesDir(), fileName);
				if (thumbnailFile.exists())
					thumbnailFile.delete();
				else {
					Bitmap bitmap = Image.scaleProportionalBitmap(
							fragments.get(current_position).getScreenshot(),
							getResources().getDimensionPixelSize(
									R.dimen.gridview_image_width_favorite),
							getResources().getDimensionPixelSize(
									R.dimen.gridview_image_height_favorite));
					try {
						FileOutputStream ostream = openFileOutput(fileName,
								Context.MODE_PRIVATE);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 90, ostream);
						ostream.close();
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
				getLoaderManager().restartLoader(LOADER_FAVORITE, null, this);
			}
			return;
		}
		mUrlAutoCompleteTextView.clearFocus();
	}

	/*
	 * listener - onItemClick (listview and gridview)
	 */

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		onEndLoading();
		mUrlAutoCompleteTextView.clearFocus();
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		mDrawerLayout.closeDrawer(Gravity.RIGHT);
		hideSpecialBar();

		switch (parent.getId()) {
		case R.id.listview_webpage:
			if (current_position == position)
				return;
			if (position == models.size() - 1) {
				addNewPage();
				return;
			}
			mUrlAutoCompleteTextView.setText(models.get(position).getUrl());
			if (fragments.get(position).isAdded())
				getFragmentManager().beginTransaction()
						.hide(fragments.get(current_position))
						.show(fragments.get(position)).commit();
			else
				getFragmentManager().beginTransaction()
						.add(R.id.container, fragments.get(position))
						.hide(fragments.get(current_position)).commit();
			current_position = position;
			getLoaderManager().restartLoader(LOADER_ISFAVORITE, null, this);
			break;
		case -1: { // little trick to get the dropdown of autoCompleteTextView
			Cursor c = (Cursor) parent.getItemAtPosition(position);
			fragments.get(current_position).loadUrl(
					c.getString(c.getColumnIndex(WebpageDb.KEY_URL)));
		}
			break;
		case R.id.gridview_favorite: {
			Cursor c = (Cursor) parent.getItemAtPosition(position);
			fragments.get(current_position).loadUrl(
					c.getString(c.getColumnIndex(WebpageDb.KEY_URL)));
			mUrlAutoCompleteTextView.setText(c.getString(c
					.getColumnIndex(WebpageDb.KEY_URL)));
		}
			break;
		case R.id.listview_history: {
			Cursor c = (Cursor) parent.getItemAtPosition(position);
			fragments.get(current_position).loadUrl(
					c.getString(c.getColumnIndex(WebpageDb.KEY_URL)));
			mUrlAutoCompleteTextView.setText(c.getString(c
					.getColumnIndex(WebpageDb.KEY_URL)));
		}
			break;
		}
	}

	/*
	 * listener - urlAutoCompleteTextView
	 */

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus)
			mClrFavImageButton.setImageResource(R.drawable.ic_action_cancel);
		else {
			if (favorite)
				mClrFavImageButton
						.setImageResource(R.drawable.ic_action_important);
			else
				mClrFavImageButton
						.setImageResource(R.drawable.ic_action_not_important);

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			v.clearFocus();
			fragments.get(current_position).loadUrl(v.getText().toString());
		}
		return false;
	}

	/*
	 * Listener - historyEditText
	 */

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mSelectionArgs[0] = "%" + s.toString() + "%";
		mSelectionArgs[1] = "%" + s.toString() + "%";
		getLoaderManager().restartLoader(LOADER_HISTORY, null,
				MainActivity.this);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	/*
	 * thesaurus
	 */
	private void showThesaurusBar() {
		if (!thesaurusFragment.isAdded()) {
			getFragmentManager().beginTransaction()
					.setCustomAnimations(R.animator.fade_in, 0)
					.add(R.id.specialbar, thesaurusFragment).commit();
			getFragmentManager().executePendingTransactions();
		}
	}

}
