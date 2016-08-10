package com.csmzxy.thinggo.worlduc;

import com.csmzxy.thinggo.worlduc.core.BlogArticle;
import com.csmzxy.thinggo.worlduc.core.LeaveWord;
import com.csmzxy.thinggo.worlduc.core.MyImageGetter;
import com.csmzxy.thinggo.worlduc.core.Person;
import com.csmzxy.thinggo.worlduc.core.TopicArticle;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BlogArticleActivity extends BaseListViewActivity<LeaveWord> {

	private BlogArticle ba;
	private TextView tv;
	private Spanned blogContent;
	private MyImageGetter imageGetter;
	private Button btnLoad;
	private Button btnReply;
	private EditText inputText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_blog_article);
		ba = (BlogArticle) getIntent().getSerializableExtra(
				WorlducCfg.KEY_BLOG_ARTICLE);
		this.setTitle(ba.getTitle());
		tv = (TextView) findViewById(R.id.tv_blog_content);

		imageGetter = new MyImageGetter(tv, this, screenWidth - 10,
				BlogArticle.BASE_URL);
		listView.removeFooterView(footer);
		//������������
		new Thread(){
			@Override
			public void run() {
				String content = WorlducUtils.getBlogArticleContent(ba.getUrl());
				blogContent = Html.fromHtml(content, imageGetter, null);
				BlogArticleActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						tv.setText(blogContent);
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
					inputText.setError("����Ϊ��");
					return;
				}
				if(pointTotals <= 0){
					Toast.makeText(BlogArticleActivity.this, 
							"��������,�޷��ظ�,��֧�ֿ�Դ������������ȡ����!", 
							Toast.LENGTH_LONG).show();
					return;
				}
				inputText.setError(null);
				btnReply.setEnabled(false);
				new Thread(){
					@Override
					public void run() {
						String message = inputText.getText().toString();
						boolean bOK = WorlducUtils.replyBlogArticle(message, ba.getId());
						if (bOK) {
							pager.setCurPage(1);
							WorlducUtils.getLeaveWordListByBlogArticle(pager, ba.getId(), ba.getTid());
							if (pager.getList().size() > 0) {
								data.clear();
								data.addAll(pager.getList());
								notifyDataLoaded();
								BlogArticleActivity.this.runOnUiThread(new Runnable(){
									@Override
									public void run() {
										btnReply.setEnabled(true);
										btnLoad.setVisibility(View.GONE);
										inputText.setText("");
										spendPoints(1);										
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void getData() {
		WorlducUtils.getLeaveWordListByBlogArticle(pager, ba.getId(), ba.getTid());
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

		//TextView name = (TextView) vi.findViewById(R.id.reply_author); // ����
		//TextView loc = (TextView) vi.findViewById(R.id.reply_location); // λ��
		//TextView time = (TextView) vi.findViewById(R.id.reply_time); // ʱ��
		TextView content = (TextView) vi.findViewById(R.id.reply_text); // ����
		ImageView head_image = (ImageView) vi.findViewById(R.id.reply_headimg); // ͷ��
		TextView replycnt = (TextView) vi.findViewById(R.id.reply_replycnt); // �ظ�����

		LeaveWord word = data.get(position);
		Person p = word.getAuthor();
		// ����ListView�����ֵ
		//name.setText(p.getName());
		//loc.setText(p.getLocation());
		//time.setText(word.getTime());
		String txt = word.getContent();
		content.setText(Html.fromHtml(txt, new MyImageGetter(content, this,
				screenWidth, "http://www.worlduc.com/"), null));

		int n = word.getReplyCount();
		if (n > 0) {
			replycnt.setText("�ظ�[" + n + "]��");
			replycnt.setTextColor(Color.BLUE);
		} else {
			replycnt.setText("δ�ظ�");
			replycnt.setTextColor(Color.RED);
		}
		replycnt.setText("");
		imageLoader.DisplayImage(p.getImgUrl(), head_image);
		return vi;
	}

	@Override
	protected void setPageSize() {
		pager.setPageSize(WorlducCfg.PAGE_SIZE_WORD_LIST);
		
	}

	@Override
	protected void setActiviLayoutId() {
		this.layoutId = R.layout.activity_blog_article;
		
	}

	@Override
	protected void setListViewId() {
		this.listViewId = R.id.blog_reply_list_view;
	}

}
