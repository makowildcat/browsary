package fr.damienbrun.browsary.webpage;

import android.graphics.Bitmap;

public interface WebpageFragmentListener {

	public void onStartLoading();

	public void onEndLoading();

	public void onUpdateTitleUrl(int index, String title, String url);

	public void onUpdateIcon(int index, Bitmap icon);

	public void onUpdateTextSelected(String text_selected);

	public void hideSpecialBar();
}
