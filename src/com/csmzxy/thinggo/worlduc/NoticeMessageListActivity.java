package com.csmzxy.thinggo.worlduc;

import org.xml.sax.XMLReader;

import com.csmzxy.thinggo.worlduc.core.BlogArticle;
import com.csmzxy.thinggo.worlduc.core.NoticeMessage;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class NoticeMessageListActivity extends
		BaseListViewActivity<NoticeMessage> {
	static Context ctx=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		// setContentView(R.layout.activity_notice_message_list);
		setTitle("通知消息");
		startLoadData();
	}

	@Override
	protected void getData() {
		WorlducUtils.getNoticeMessageList(pager);
		if (pager.getList().size() > 0)
			data.addAll(pager.getList());

	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.activity_notice_message_list_item,
					null);
		TextView time = (TextView) vi.findViewById(R.id.message_time); // 通知消息
		TextView content = (TextView) vi.findViewById(R.id.message_text); // 通知消息
		NoticeMessage msg = data.get(position);
		time.setText(msg.getTime());
		//content.setMovementMethod(LinkMovementMethod.getInstance());
		content.setText(Html.fromHtml(msg.getHtml()));
		dealHyperLink(content);
		//content.setText(Html.fromHtml(msg.getHtml(),null,new MyTagHandler(this, Color.RED)));
		return vi;
	}

	@Override
	protected void setPageSize() {
		pager.setPageSize(WorlducCfg.PAGE_SIZE_NOTICE_MESSAGE_LIST);
	}

	@Override
	protected void setActiviLayoutId() {
		this.layoutId = R.layout.activity_notice_message_list;
	}

	@Override
	protected void setListViewId() {
		this.listViewId = R.id.notice_message_list_view;

	}
	
	private void dealHyperLink(TextView tv){
		tv.setMovementMethod(LinkMovementMethod.getInstance());   
        CharSequence text = tv.getText();   
        if (text instanceof Spannable) {   
            int end = text.length();   
            Spannable sp = (Spannable) tv.getText();   
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);   
            SpannableStringBuilder style = new SpannableStringBuilder(text);   
            style.clearSpans();// should clear old spans   
            //循环把链接发过去             
            for (URLSpan url : urls) {   
            	MyURLSpan myURLSpan = new MyURLSpan(url.getURL());   
                style.setSpan(myURLSpan, sp.getSpanStart(url),   
                        sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);   
            }   
            tv.setText(style);   
        }   
	}
	
	private static class MyURLSpan extends ClickableSpan {   		   
        private String mUrl;    
        MyURLSpan(String url) {   
            mUrl = url.toLowerCase();   
        }     
        @Override   
        public void onClick(View widget) {   
        	if(mUrl.contains("blog2012.aspx")){
        		Intent intent = new Intent(ctx, BlogArticleActivity.class);
        		BlogArticle ba = new BlogArticle("", mUrl);
        		intent.putExtra(WorlducCfg.KEY_BLOG_ARTICLE, ba);
        		ctx.startActivity(intent);
        	}else if(mUrl.contains("index.aspx")){
        		
        	}else if(mUrl.contains("leavewordlist.aspx")){
        		Intent intent = new Intent(ctx, WordListActivity.class);
        		ctx.startActivity(intent);
        	} else if(mUrl.contains("reply.aspx")){
        		Intent intent = new Intent(ctx, WordReplyListActivity.class);
        		intent.putExtra(WorlducCfg.KEY_WORD_URL, "http://www.worlduc.com"+mUrl);
        		ctx.startActivity(intent);
        	}
        	System.out.println(mUrl);              
        }   
    }   
}
