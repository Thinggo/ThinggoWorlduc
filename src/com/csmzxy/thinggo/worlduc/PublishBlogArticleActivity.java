package com.csmzxy.thinggo.worlduc;

import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;

import com.csmzxy.thinggo.worlduc.core.Category;
import com.csmzxy.thinggo.worlduc.core.FriendRequest;
import com.csmzxy.thinggo.worlduc.core.ImageLoader;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PublishBlogArticleActivity extends Activity implements UpdatePointsNotifier {

	private EditText etTitle;
	private EditText etTag;
	private EditText etContent;
	private EditText etCheckCode;
	private EditText etCategory;
	private Button btnPublish;
	private ImageView imageView;
	private ImageLoader imageLoader;
	private String checkCodeUrl = "http://www.worlduc.com/plugin/check_code.aspx?t="
			+ System.currentTimeMillis();
	private Category category;
	private boolean bSuccess = false;
	private String msg;
	private int pointTotals;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish_blog_article);
		imageLoader = new ImageLoader(this, 40);
		etTitle = (EditText) findViewById(R.id.blog_article_title);
		etTag = (EditText) findViewById(R.id.blog_article_tag);
		etContent = (EditText) findViewById(R.id.blog_article_content);
		etCheckCode = (EditText) findViewById(R.id.blog_article_input_check_code);
		etCategory = (EditText) findViewById(R.id.blog_article_column);
		etCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PublishBlogArticleActivity.this,
						BlogCategoryListActivity.class);
				startActivityForResult(intent, 0x123456);
			}
		});
		// ������ť
		btnPublish = (Button) findViewById(R.id.blog_article_btn_publish);
		btnPublish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String title = etTitle.getText().toString();
				final String tag = etTag.getText().toString();
				final String content = etContent.getText().toString();
				final String code = etCheckCode.getText().toString();
				bSuccess = true;
				if (TextUtils.isEmpty(title)) {
					etTitle.setError("���������");
					bSuccess = false;
				}
				if (TextUtils.isEmpty(tag)) {
					etTag.setError("�������ǩ");
					bSuccess = false;
				}
				if (TextUtils.isEmpty(content)) {
					etContent.setError("����������");
					bSuccess = false;
				}
				if (category == null) {
					etCategory.setError("��ѡ�����");
					bSuccess = false;
				}
				if (TextUtils.isEmpty(code)) {
					etCheckCode.setError("����������");
					bSuccess = false;
				}
				if (!bSuccess)
					return;
				
				if(pointTotals < 10){
					Toast.makeText(PublishBlogArticleActivity.this, 
							"��������10,�޷���������,��֧�ֿ�Դ������������ȡ����!", 
							Toast.LENGTH_LONG).show();
					return;
				}
				btnPublish.setEnabled(false);	
				new Thread() {

					@Override
					public void run() {
						bSuccess = false;						
						msg = "���·����ɹ���";
						try {
							bSuccess = WorlducUtils.PostArticle(
									category.getId(), code, title, tag, content);
						} catch (Exception ex) {
							msg = ex.getMessage();					
						}
						if (!bSuccess) {
							msg = "���·���ʧ�ܣ�";
						}
						System.out.println(msg);
						PublishBlogArticleActivity.this.runOnUiThread(new Runnable(){
							@Override
							public void run() {
								btnPublish.setEnabled(true);	
								if(bSuccess){
									//�����ɹ�������10����
									spendPoints(10);
									etTitle.setText("");
									imageLoader.DisplayImage(checkCodeUrl, imageView);
								}
								Toast.makeText(PublishBlogArticleActivity.this,
										msg, Toast.LENGTH_SHORT).show();
							}});
						
					}

				}.start();
			}
		});
		imageView = (ImageView) findViewById(R.id.blog_article_img_check_code);
		imageLoader.DisplayImage(checkCodeUrl, imageView);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (0x123456 != requestCode)
			return;
		Category c = (Category) data
				.getSerializableExtra(WorlducCfg.KEY_BLOG_ARTICLE_CATEGORY);
		category = c;
		etCategory.setTag(c);
		etCategory.setText(c.getName());
		etCategory.setError(null);
	}
	
	@Override
	protected void onResume() {
		// �ӷ������˻�ȡ��ǰ�û����������.
		// ���ؽ���ڻص�����getUpdatePoints(...)�д���
		AppConnect.getInstance(this).getPoints(this);
		super.onResume();
	}
	
	/**
	 * AppConnect.getPoints()������ʵ�֣�����ʵ��
	 * 
	 * @param currencyName
	 *            �����������.
	 * @param pointTotal
	 *            ����������.
	 */
	public void getUpdatePoints(String currencyName, int pointTotal) {
		pointTotals = pointTotal;
		System.out.println("AppConnect.getPoints() = "  + pointTotals);
		
	}
	/**
	 * AppConnect.getPoints() ������ʵ�֣�����ʵ��
	 * 
	 * @param error
	 *            ����ʧ�ܵĴ�����Ϣ
	 */
	public void getUpdatePointsFailed(String error) {
		System.out.println("AppConnect.getPoints() error: "  + error);
	}
	
	public boolean spendPoints(int points){
		if(pointTotals >= points){
			AppConnect.getInstance(this).spendPoints(points, this);
			return true;
		}
		return false;
	}
}
