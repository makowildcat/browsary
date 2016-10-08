package fr.damienbrun.browsary.tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class Image {

	public static Bitmap loadBitmapFromView(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.layout(0, 0, view.getWidth(), view.getHeight());
		view.draw(canvas);
		return bitmap;
	}

	public static Bitmap scaleProportionalBitmap(Bitmap src, int width,
			int height) {

		if (src.getWidth() < src.getHeight())
			return Bitmap
					.createBitmap(
							Bitmap.createScaledBitmap(
									src,
									width,
									Math.round((float) src.getHeight()
											* ((float) width / (float) src
													.getWidth())), false), 0,
							0, width, height);
		else
			return Bitmap.createBitmap(Bitmap.createScaledBitmap(
					src,
					Math.round((float) src.getWidth()
							* ((float) height / (float) src.getHeight())),
					height, false), 0, 0, width, height);
	}
}
