package fr.damienbrun.browsary.webpage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import fr.damienbrun.browsary.tool.Image;
import fr.damienbrun.browsary.webpage.WebpageView.OnMovedListener;

public class WebpageFragment extends Fragment implements OnMovedListener {

	private static final String TAG = "WebpageFragment";

	private static final String KEY_URL = "url";
	private static final String KEY_INDEX = "index";

	private WebpageView mWebView;
	private int mIndex;

	private WebpageFragmentListener dataPasser;

	public static WebpageFragment newInstance(int index, String url) {
		WebpageFragment f = new WebpageFragment();

		Bundle args = new Bundle();
		args.putInt(KEY_INDEX, index);
		args.putString(KEY_URL, url);
		f.setArguments(args);

		return f;
	}

	/*
	 * lifecycle
	 */

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		dataPasser = (WebpageFragmentListener) a;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mIndex = getArguments().getInt(KEY_INDEX);
		mWebView = new WebpageView(getActivity(), this);

		// mWebView.setDrawingCacheEnabled(true);
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDisplayZoomControls(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient() {

			boolean loading = false;

			@Override
			public void onProgressChanged(WebView view, int progress) {
				if (progress < 100) {
					if (!loading) {
						dataPasser.onStartLoading();
						loading = true;
					}
				} else {
					dataPasser.onEndLoading();
					loading = false;
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				dataPasser.onUpdateTitleUrl(getIndex(), title,
						lightUrl(view.getUrl()));
				dataPasser.onUpdateIcon(getIndex(), view.getFavicon());
			}

			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				Log.v(TAG, "onReceivedIcon");
				dataPasser.onUpdateIcon(getIndex(), icon);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				if (mWebView.getPushLong()
						& message.split(WebpageView.EMPTY).length > 0) {
					dataPasser.onUpdateTextSelected(message
							.split(WebpageView.EMPTY)[1]);
					result.confirm();
					return true;
				}
				if (mWebView.getMoved() < 5)
					dataPasser.hideSpecialBar();
				result.confirm();
				return true;
			}

		});

		this.loadUrl(getArguments().getString(KEY_URL));

		return mWebView;
	}

	@Override
	public void onResume() {
		mWebView.onResume();
		super.onResume();
	}

	@Override
	public void onPause() {
		mWebView.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		mWebView.destroy();
		super.onDestroy();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (hidden) {
			mWebView.stopLoading();
			mWebView.onPause();
		} else {
			mWebView.onResume();
		}
		super.onHiddenChanged(hidden);
	}

	/*
	 * getters
	 */

	private int getIndex() {
		return mIndex;
	}

	public Bitmap getScreenshot() {
		// return mWebView.getDrawingCache();
		return Image.loadBitmapFromView(mWebView);
	}

	public String getWebViewUrl() {
		return mWebView.getUrl();
	}

	public boolean canGoBack() {
		return mWebView.canGoBack();
	}

	/*
	 * setters
	 */

	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public void loadUrl(String s) {
		mWebView.stopLoading();
		mWebView.loadUrl(realUrl(s));
	}

	public void reload() {
		mWebView.reload();
	}

	public void goBack() {
		goToPageList(-1);
		mWebView.goBack();
	}

	public void goForward() {
		if (!mWebView.canGoForward())
			return;
		goToPageList(1);
		mWebView.goForward();
	}

	/*
	 * tools
	 */

	private String lightUrl(String url) {
		return (url.contains("http://")) ? url.substring(7, url.length()) : url;
	}

	private String realUrl(String url) {
		int nbDot = url.length() - url.replace(".", "").length();
		if (nbDot == 0)
			return "http://www.google.com/search?q=".concat(url);
		if (nbDot == 1)
			return "http://www.".concat(url);
		if (!url.contains("://"))
			url = "http://" + url;
		return url;
	}

	private void goToPageList(int index) {
		WebHistoryItem webHistoryItem = mWebView.copyBackForwardList()
				.getItemAtIndex(
						mWebView.copyBackForwardList().getCurrentIndex()
								+ index);
		String historyUrl = lightUrl(webHistoryItem.getUrl());

		dataPasser.onUpdateTitleUrl(getIndex(), webHistoryItem.getTitle(),
				historyUrl);
		dataPasser.onUpdateIcon(getIndex(), webHistoryItem.getFavicon());
	}

	@Override
	public void onMoved(boolean hasMoved) {
		if (!hasMoved)
			dataPasser.hideSpecialBar();
	}

}
