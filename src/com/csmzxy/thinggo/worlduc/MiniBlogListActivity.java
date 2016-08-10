package com.csmzxy.thinggo.worlduc;

import com.csmzxy.thinggo.worlduc.core.MiniBlog;
import com.csmzxy.thinggo.worlduc.core.NoticeMessage;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MiniBlogListActivity extends BaseListViewActivity<MiniBlog> {

	private Button btnPublish;
	private EditText inputText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("我的微博列表");
		btnPublish = (Button) findViewById(R.id.btn_reply);
		btnPublish.setText("发微博");
		inputText = (EditText) findViewById(R.id.input_text);
		inputText.setHint("请输入要发布的微博内容");
		btnPublish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pointTotals < 10) {
					Toast.makeText(MiniBlogListActivity.this,
							"积分余额不足10,无法发布微博,请支持开源软件，点击广告获取积分!",
							Toast.LENGTH_LONG).show();
					return;
				}
				final String txt = inputText.getText().toString();
				if (TextUtils.isEmpty(txt))
					return;
				
				btnPublish.setEnabled(false);

				new Thread() {
					@Override
					public void run() {
						final boolean bOk = WorlducUtils.publishMinoBlog(txt,
								"", "0");

						MiniBlogListActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								btnPublish.setEnabled(true);
								if (bOk) {
									spendPoints(10);
									Toast.makeText(MiniBlogListActivity.this,
											"发布成功", Toast.LENGTH_SHORT).show();
								}
							}
						});
					}

				}.start();

			}
		});

		startLoadData();
	}

	@Override
	protected void getData() {
		WorlducUtils.getMiniBlogList(pager);
		if (pager.getList().size() > 0)
			data.addAll(pager.getList());

	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.activity_mini_blog_list_item, null);
		TextView time = (TextView) vi.findViewById(R.id.mini_blog_time); // 时间
		TextView content = (TextView) vi.findViewById(R.id.mini_blog_text); // 内容
		TextView category = (TextView) vi.findViewById(R.id.mini_blog_category); // 分类
		TextView mood = (TextView) vi.findViewById(R.id.mini_blog_mood); // 心情

		MiniBlog mb = data.get(position);
		time.setText(mb.getTime());
		content.setText(Html.fromHtml(mb.getContent()));
		category.setText(mb.getCategory());
		mood.setText(mb.getMood());
		return vi;
	}

	@Override
	protected void setPageSize() {
		pager.setPageSize(WorlducCfg.PAGE_SIZE_MINI_BLOG_LIST);
	}

	@Override
	protected void setActiviLayoutId() {
		this.layoutId = R.layout.activity_mini_blog_list;
	}

	@Override
	protected void setListViewId() {
		this.listViewId = R.id.weibo_list_view;
	}

}
