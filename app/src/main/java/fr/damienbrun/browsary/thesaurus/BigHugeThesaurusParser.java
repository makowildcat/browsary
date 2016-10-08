package fr.damienbrun.browsary.thesaurus;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BigHugeThesaurusParser {

	private String mCategory;
	private String mContent;

	BigHugeThesaurusParser() {
	}

	/*
	 * Usually we have only "noun / verb" and then "syn. / ant.", but sometimes
	 * we also have "adjective / adverb", "rel. / sim.", and maybe few others
	 * that I still don't know. That why here I use iterator, so then I can
	 * handle whatever and I format them a little bit.
	 */
	public void toParse(String jsonString) {
		StringBuilder category = new StringBuilder();
		StringBuilder content = new StringBuilder();
		if (jsonString != null) {
			try {
				JSONObject jsonWord = new JSONObject(jsonString);
				Iterator<?> iWord = jsonWord.keys();
				while (iWord.hasNext()) {
					String categoryString = (String) iWord.next();
					category.append(categoryString);
					JSONObject jsonCategory = jsonWord
							.getJSONObject(categoryString);
					Iterator<?> iCategory = jsonCategory.keys();
					while (iCategory.hasNext()) {
						String key = (String) iCategory.next();
						content.append("<i><b>").append(key)
								.append(".</b></i> ");
						JSONArray values = jsonCategory.getJSONArray(key);
						for (int i = 0; i < values.length(); i++) {
							content.append(values.getString(i));
							if (i >= (values.length() - 1)) {
								if (iWord.hasNext() || iCategory.hasNext()) {
									content.append("<br>");
									category.append("\n");
								}
							} else
								content.append(", ");
						}
					}
				}
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		mCategory = category.toString();
		mContent = content.toString();
	}

	public String getCategory() {
		return mCategory;
	}

	public String getContent() {
		return mContent;
	}

}
