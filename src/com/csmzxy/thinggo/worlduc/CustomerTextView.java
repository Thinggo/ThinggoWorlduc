package com.csmzxy.thinggo.worlduc;

import java.util.ArrayList;
import java.util.HashMap;

import com.csmzxy.thinggo.worlduc.R;
import com.csmzxy.thinggo.worlduc.core.ImageLoader;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomerTextView extends LinearLayout{

	//上下文对象
	private Context mContext;
	//声明TypedArray的引用
	private TypedArray mTypedArray;
	//布局参数
	private LayoutParams params;
	private ImageLoader imageLoader = new ImageLoader(this.getContext(), 40);
	
	public CustomerTextView(Context context) {
		super(context);
	}
	
	public CustomerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		this.setOrientation(LinearLayout.VERTICAL);
		//从attrs.xml文件中那个获取自定义属性
		mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.constomTextView);
	}
	
	public void setText(ArrayList<HashMap<String, String>> datas) {
		//遍历ArrayList
		for(HashMap<String, String> hashMap : datas) {
			//获取key为"type"的值
			String type = hashMap.get("type");
			//如果value=imaeg
			if(type.equals("image")){
				//获取自定义属性属性
				int imagewidth = mTypedArray.getDimensionPixelOffset(R.styleable.constomTextView_image_width, 100);
				int imageheight = mTypedArray.getDimensionPixelOffset(R.styleable.constomTextView_image_height, 100);
				ImageView imageView = new ImageView(mContext);
				params = new LayoutParams(imagewidth, imageheight);
				params.gravity = Gravity.CENTER_HORIZONTAL;	//居中
				imageView.setLayoutParams(params);
				//显示图片
				imageView.setImageResource(R.drawable.no_image);
				//将imageView添加到LinearLayout当中
				addView(imageView);
				//启动异步线程更新异步显示图片信息
				imageLoader.DisplayImage(hashMap.get("value"), imageView);
			}
			else {
				float textSize = mTypedArray.getDimension(R.styleable.constomTextView_textSize, 16);
				int textColor = mTypedArray.getColor(R.styleable.constomTextView_textColor, 0xFF0000FF);
				TextView textView = new TextView(mContext);
				
				textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
				textView.setText(Html.fromHtml(hashMap.get("value")));
				textView.setTextSize(textSize);		//设置字体大小
				textView.setTextColor(textColor);	//设置字体颜色
				addView(textView);
			}
		}
	}
}
