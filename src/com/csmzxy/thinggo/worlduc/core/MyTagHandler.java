package com.csmzxy.thinggo.worlduc.core;

import org.xml.sax.XMLReader;

import android.content.Context;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.Html.TagHandler;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MyTagHandler implements TagHandler {
	int start;
	int stop;
	Context context;
	int color;

	public MyTagHandler(Context context, int color) {
		this.context = context;
		this.color = color;
	}

	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		if (tag.toLowerCase().equals("a")) {
			if (opening) {
				start = output.length();
			} else {
				stop = output.length();
				String content = output.subSequence(start, stop).toString();
				output.setSpan(new MySpan(context, color, content), start,
						stop, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	class MySpan extends ClickableSpan implements OnClickListener {

		String content;
		int color;
		int type;
		Context context;
		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(color);			// 设置颜色
			ds.setUnderlineText(false);// 是否显示下划线
		}
		
		public MySpan(Context context, int color, String content) {
			this.context = context;
			this.color = color;
			this.content = content;
		}
		@Override
		public void onClick(View widget) {
			// 添加点击事件
			Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
		}
	}

}
