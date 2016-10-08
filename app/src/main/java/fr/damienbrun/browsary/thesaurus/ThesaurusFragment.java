package fr.damienbrun.browsary.thesaurus;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import android.app.Fragment;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fr.damienbrun.browsary.R;
import fr.damienbrun.browsary.tool.Network;

public class ThesaurusFragment extends Fragment {

	private static final String TAG = "ThesaurusFragment";

	/*
	 * I let it this fake URL, to try with my own server for the "cache-part",
	 * but there is just two word "have" and "love". (of course you'll have to
	 * uncomment the other API_TYPE too.)
	 */
	// private static final String API_URL = "http://drawhanzi.com/thesaurus/";
	// private static final String API_TYPE = "/result.json";
	private static final String API_URL = "http://words.bighugelabs.com/api/2/";
	private static final String API_KEY = "7a088854b5dd04c276f794772dd239e9";
	private static final String API_TYPE = "/json";

	private TextView word;
	private TextView category;
	private TextView content;

	private BigHugeThesaurusParser mBigHugeThesaurusParser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/*
		 * to make HTTP Request keep in Cache, this work quite well, except if
		 * the server doesn't want, that's the case of the Web API
		 * Bighugelabs...
		 */
		try {
			File httpCacheDir = new File(getActivity().getCacheDir(), "http");
			long httpCacheSize = 10 * 1024 * 1024;
			HttpResponseCache.install(httpCacheDir, httpCacheSize);
		} catch (IOException e) {
			Log.i(TAG, "HTTP response cache installation failed:" + e);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_thesaurus, container, false);

		word = (TextView) v.findViewById(R.id.textview_word);
		category = (TextView) v.findViewById(R.id.textview_category);
		content = (TextView) v.findViewById(R.id.textview_content);

		mBigHugeThesaurusParser = new BigHugeThesaurusParser();
		return v;
	}

	@Override
	public void onStop() {
		HttpResponseCache cache = HttpResponseCache.getInstalled();
		if (cache != null) {
			cache.flush();
		}
		super.onStop();
	}

	public void setWord(String word) {
		word = word.trim().toLowerCase(Locale.getDefault());
		if (!this.word.getText().toString().equals(word)) {
			Log.v(TAG, "setword > launchAsync with : " + word);
			this.word.setText(word);
			new AsyncTaskGetThesaurus().execute(API_URL + API_KEY + "/" + word
					+ API_TYPE);
		}
	}

	private class AsyncTaskGetThesaurus extends AsyncTask<String, Void, Void> {

		protected Void doInBackground(String... urls) {
			mBigHugeThesaurusParser.toParse(Network.GET(urls[0]));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.v(TAG, "AsyncTaskGetThesaurus -> onPostExecute");
			category.setText(mBigHugeThesaurusParser.getCategory());
			if (mBigHugeThesaurusParser.getContent().length() == 0)
				content.setText(getString(R.string.not_found));
			else
				content.setText(Html.fromHtml(mBigHugeThesaurusParser
						.getContent()));
		}
	}
}
