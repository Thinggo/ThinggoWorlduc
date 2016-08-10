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

	//�����Ķ���
	private Context mContext;
	//����TypedArray������
	private TypedArray mTypedArray;
	//���ֲ���
	private LayoutParams params;
	private ImageLoader imageLoader = new ImageLoader(this.getContext(), 40);
	
	public CustomerTextView(Context context) {
		super(context);
	}
	
	public CustomerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		this.setOrientation(LinearLayout.VERTICAL);
		//��attrs.xml�ļ����Ǹ���ȡ�Զ�������
		mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.constomTextView);
	}
	
	public void setText(ArrayList<HashMap<String, String>> datas) {
		//����ArrayList
		for(HashMap<String, String> hashMap : datas) {
			//��ȡkeyΪ"type"��ֵ
			String type = hashMap.get("type");
			//���value=imaeg
			if(type.equals("image")){
				//��ȡ�Զ�����������
				int imagewidth = mTypedArray.getDimensionPixelOffset(R.styleable.constomTextView_image_width, 100);
				int imageheight = mTypedArray.getDimensionPixelOffset(R.styleable.constomTextView_image_height, 100);
				ImageView imageView = new ImageView(mContext);
				params = new LayoutParams(imagewidth, imageheight);
				params.gravity = Gravity.CENTER_HORIZONTAL;	//����
				imageView.setLayoutParams(params);
				//��ʾͼƬ
				imageView.setImageResource(R.drawable.no_image);
				//��imageView��ӵ�LinearLayout����
				addView(imageView);
				//�����첽�̸߳����첽��ʾͼƬ��Ϣ
				imageLoader.DisplayImage(hashMap.get("value"), imageView);
			}
			else {
				float textSize = mTypedArray.getDimension(R.styleable.constomTextView_textSize, 16);
				int textColor = mTypedArray.getColor(R.styleable.constomTextView_textColor, 0xFF0000FF);
				TextView textView = new TextView(mContext);
				
				textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
				textView.setText(Html.fromHtml(hashMap.get("value")));
				textView.setTextSize(textSize);		//���������С
				textView.setTextColor(textColor);	//����������ɫ
				addView(textView);
			}
		}
	}
}
