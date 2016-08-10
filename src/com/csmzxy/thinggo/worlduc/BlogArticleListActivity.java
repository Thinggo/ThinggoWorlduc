package com.csmzxy.thinggo.worlduc;

import java.util.ArrayList;
import java.util.List;

import com.csmzxy.thinggo.worlduc.core.BlogArticle;
import com.csmzxy.thinggo.worlduc.core.Category;
import com.csmzxy.thinggo.worlduc.core.FriendGroup;
import com.csmzxy.thinggo.worlduc.core.SubCategory;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BlogArticleListActivity extends Activity {

	private Category category;
	private String uid;
	private List<BlogArticle> data = new ArrayList<BlogArticle>();
	private ListView listView;
	private DataAdapter adapter;
	private View footer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_article_list);
		footer = getLayoutInflater().inflate(
				R.layout.activity_word_list_footer, null);
		category = (Category) getIntent().getSerializableExtra(
				WorlducCfg.KEY_BLOG_ARTICLE_CATEGORY);
		uid = getIntent().getStringExtra(WorlducCfg.KEY_UID);
		TextView tv = (TextView) findViewById(R.id.worlduc_app_title);
		tv.setText(category.getName());
		
		//System.out.println(uid+":"+category.getName() + ":" + category.getId());
		listView = (ListView)findViewById(R.id.blog_article_list);
		adapter = new DataAdapter();
		listView.addFooterView(footer);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(BlogArticleListActivity.this, BlogArticleActivity.class);
				BlogArticle ba = data.get(position);
				intent.putExtra(WorlducCfg.KEY_BLOG_ARTICLE, ba);
				BlogArticleListActivity.this.startActivity(intent);
			}
		});
		new Thread(){

			@Override
			public void run() {
				List<SubCategory> list = WorlducUtils.getBlogSubCategories(uid, category.getId());
				for(SubCategory sc : list){
					List<BlogArticle> articles = WorlducUtils.GetBlogArticleListBySIdCId(sc);
					if(articles.size()>0)
						data.addAll(articles);
				}
				BlogArticleListActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						adapter.notifyDataSetChanged();
						if (listView.getFooterViewsCount() > 0) {
							listView.removeFooterView(footer);
						}
					}
				});				
			}		
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blog_article_list, menu);
		return true;
	}
	
	class DataAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public BlogArticle getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null){
				LayoutInflater mInflater = BlogArticleListActivity.this.getLayoutInflater();
				convertView = mInflater.inflate(
						R.layout.activity_blog_article_list_item, null);
			}	
			TextView tvId = (TextView) convertView.findViewById(R.id.tv_blog_article_id);
			tvId.setText(""+(position+1));
			TextView tv = (TextView) convertView.findViewById(R.id.tv_blog_article_name);
			BlogArticle ba = data.get(position);
			tv.setText(ba.getTitle());
			return convertView;
		}

	}

}
