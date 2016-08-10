package com.csmzxy.thinggo.worlduc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.csmzxy.thinggo.worlduc.R;
import com.csmzxy.thinggo.worlduc.WorlducCfg;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

public class MyImageGetter implements ImageGetter {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	Context context;
	private TextView tv;
	private String baseUrl;
	final int stub_id = R.drawable.none_image;

	ExecutorService executorService;
	int image_width = 40;

	public MyImageGetter(TextView tv, Context context, int image_width, String baseUrl) {
		this.context = context;
		this.baseUrl = baseUrl;
		this.tv = tv;
		tv.setMovementMethod(null);
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
		this.image_width = image_width;
	}

	@Override
	public Drawable getDrawable(String url) {

		Bitmap bitmap = memoryCache.get(url);
		Resources res = context.getResources();
		if (bitmap != null) {
			BitmapDrawable d = new BitmapDrawable(res, bitmap);
			return d;
		}
		// 不存在文件时返回默认图片，并异步加载网络图片
		URLDrawable drawable = new URLDrawable(
				res.getDrawable(R.drawable.no_image));
		queuePhoto(url, drawable);
		return drawable;
	}

	private void queuePhoto(String url, URLDrawable drawable) {
		PhotoToLoad p = new PhotoToLoad(url, drawable);
		executorService.submit(new PhotosLoader(p));
	}

	// 任务队列
	private class PhotoToLoad {
		public String url;
		public URLDrawable drawable;

		public PhotoToLoad(String u, URLDrawable i) {
			url = u;
			drawable = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);

			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) tv.getContext();
			a.runOnUiThread(bd);
		}
	}

	// 用于显示位图在UI线程
	// 用于显示位图在UI线程
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (bitmap != null)
				photoToLoad.drawable.setBitmap(bitmap);
			
			tv.setText(tv.getText());
		}
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
			if(!url.startsWith("http://")){
				url =baseUrl+url;
			}
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
			Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			if(bitmap.getWidth() > image_width)
				bitmap = zoomBitmap(bitmap, image_width);
			return bitmap;
		} catch (FileNotFoundException e) {
		}
		return null;
	}
	
	private Bitmap zoomBitmap(Bitmap bitmap, int width) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		matrix.postScale(scaleWidth, scaleWidth);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
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

		private void setBitmap(Bitmap bitmap) {
			Resources res = context.getResources();
			if (bitmap != null) {
				BitmapDrawable d = new BitmapDrawable(res, bitmap);
				setDrawable(d);
			}
		}

		@Override
		public void draw(Canvas canvas) {
			// TODO Auto-generated method stub
			drawable.draw(canvas);
		}

	}

}
