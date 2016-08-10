package com.csmzxy.thinggo.worlduc;

import com.csmzxy.thinggo.worlduc.core.LeaveWord;
import com.csmzxy.thinggo.worlduc.core.MyImageGetter;
import com.csmzxy.thinggo.worlduc.core.Person;
import com.csmzxy.thinggo.worlduc.core.TopicArticle;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TopicActivity extends BaseListViewActivity<LeaveWord> {

	private TopicArticle ta;
	private TextView tv;
	private Spanned topicContent;
	private MyImageGetter imageGetter;
	private Button btnLoad;
	private Button btnReply;
	private EditText inputText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ta = (TopicArticle) getIntent().getSerializableExtra(
				WorlducCfg.KEY_GROUP_TOPIC);
		this.setTitle(ta.getTitle());
		tv = (TextView) findViewById(R.id.tv);

		imageGetter = new MyImageGetter(tv, this, screenWidth - 10,
				TopicArticle.BASE_URL);
		listView.removeFooterView(footer);
		//加载主题内容
		new Thread(){
			@Override
			public void run() {
				String content = WorlducUtils.getTopicContent(ta.getUrl());
				topicContent = Html.fromHtml(content, imageGetter, null);
				TopicActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						tv.setText(topicContent);
						tv.setMovementMethod(null);						
					}
				});
			}					
		}.start();		
		
		btnLoad = (Button) findViewById(R.id.btn_load);
		btnLoad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnLoad.setVisibility(View.GONE);
				listView.addFooterView(footer);
				data.clear();
				startLoadData();
			}
		});
		inputText = (EditText) findViewById(R.id.input_text);
		btnReply = (Button) findViewById(R.id.btn_reply);
		btnReply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(inputText.getText())){
					inputText.setError("不能为空");
					return;
				}
				if(pointTotals <= 0){
					Toast.makeText(TopicActivity.this, 
							"积分余额不足,无法回复主题,请支持开源软件，点击广告获取积分!", 
							Toast.LENGTH_LONG).show();
					return;
				}
				inputText.setError(null);
				btnReply.setEnabled(false);
				new Thread(){
					@Override
					public void run() {
						String message = inputText.getText().toString();
						boolean bOK = WorlducUtils.replyGroupTopic(message, ta.getId());
						if (bOK) {
							spendPoints(1);
							pager.setCurPage(1);
							WorlducUtils.getLeaveWordListByTopic(pager, ta.getId());
							if (pager.getList().size() > 0) {
								data.clear();
								data.addAll(pager.getList());
								notifyDataLoaded();
								TopicActivity.this.runOnUiThread(new Runnable(){
									@Override
									public void run() {
										btnReply.setEnabled(true);
										btnLoad.setVisibility(View.GONE);
									}
								});
							}
						}
					}					
				}.start();		
			}
		});
	}

	@Override
	protected void getData() {
		WorlducUtils.getLeaveWordListByTopic(pager, ta.getId());
		if (pager.getList().size() > 0) {
			data.addAll(pager.getList());
		}
	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent) {

		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.activity_word_reply_list_item, null);

		TextView name = (TextView) vi.findViewById(R.id.reply_author); // 姓名
		TextView loc = (TextView) vi.findViewById(R.id.reply_location); // 位置
		TextView time = (TextView) vi.findViewById(R.id.reply_time); // 时间
		TextView content = (TextView) vi.findViewById(R.id.reply_text); // 内容
		ImageView head_image = (ImageView) vi.findViewById(R.id.reply_headimg); // 头像
		TextView replycnt = (TextView) vi.findViewById(R.id.reply_replycnt); // 回复次数

		LeaveWord word = data.get(position);
		Person p = word.getAuthor();
		// 设置ListView的相关值
		name.setText(p.getName());
		loc.setText(p.getLocation());
		time.setText(word.getTime());
		String txt = word.getContent();
		content.setText(Html.fromHtml(txt, new MyImageGetter(content, this,
				screenWidth, "http://group.worlduc.com/"), null));

		int n = word.getReplyCount();
		if (n > 0) {
			replycnt.setText("回复[" + n + "]次");
			replycnt.setTextColor(Color.BLUE);
		} else {
			replycnt.setText("未回复");
			replycnt.setTextColor(Color.RED);
		}
		replycnt.setText("");
		imageLoader.DisplayImage(p.getImgUrl(), head_image);
		return vi;
	}

	@Override
	protected void setPageSize() {
		pager.setPageSize(WorlducCfg.PAGE_SIZE_REPLY_WORD_LIST);
	}

	@Override
	protected void setActiviLayoutId() {
		layoutId = R.layout.activity_topic;

	}

	@Override
	protected void setListViewId() {
		listViewId = R.id.topic_reply_list_view;

	}

}
