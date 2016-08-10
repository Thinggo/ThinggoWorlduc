package com.csmzxy.thinggo.worlduc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.csmzxy.thinggo.worlduc.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	int REQUIRED_SIZE = 70;

	public ImageLoader(Context context, int imgSize) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
		REQUIRED_SIZE = imgSize;
	}

	final int stub_id = R.drawable.none_image;

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		// 从sd卡
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// 从网络
		try {
			Bitmap bitmap = null;
			WorlducUtils.GetImageFromUrl(url, f);
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	// 解码图像用来减少内存消耗
	private Bitmap decodeFile(File f) {
		try {
			// 解码图像大小
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// 找到正确的刻度值，它应该是2的幂。
			
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// 任务队列
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 用于显示位图在UI线程
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}
	
	public class MImageGetter implements ImageGetter {

		private Context context;
		private TextView tv;

		public MImageGetter(TextView tv,Context context) {
			this.context = context;
			this.tv = tv;
		}

		@Override
		public Drawable getDrawable(String source) {
			// 不存在文件时返回默认图片，并异步加载网络图片
			Resources res = context.getResources();
			Bitmap bitmap = memoryCache.get(source);
			if(bitmap!=null){
				BitmapDrawable d = new BitmapDrawable(res,bitmap);
				return d;
			}
			
			URLDrawable drawable = new URLDrawable(
					res.getDrawable(R.drawable.no_image));
			new ImageAsync(drawable).execute(source);
			return drawable;

		}

		private class ImageAsync extends AsyncTask<String, Integer, Drawable> {

			private URLDrawable drawable;

			public ImageAsync(URLDrawable drawable) {
				this.drawable = drawable;
			}

			@Override
			protected Drawable doInBackground(String... params) {
				// TODO Auto-generated method stub
				String url = params[0];
				Bitmap bitmap = getBitmap(url);
				BitmapDrawable d = new BitmapDrawable(context.getResources(), bitmap);
				return d;
			}

			@Override
			protected void onPostExecute(Drawable result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (result != null) {
					drawable.setDrawable(result);
					tv.setText(tv.getText()); // 通过这里的重新设置 TextView 的文字来更新UI
				}
			}
		}

		public class URLDrawable extends BitmapDrawable {

			private Drawable drawable;

			public URLDrawable(Drawable defaultDraw) {
				setDrawable(defaultDraw);
			}

			private void setDrawable(Drawable nDrawable) {
				drawable = nDrawable;
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
			}

			@Override
			public void draw(Canvas canvas) {
				// TODO Auto-generated method stub
				drawable.draw(canvas);
			}

		}
	}
}
