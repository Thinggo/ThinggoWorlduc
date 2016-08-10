package com.csmzxy.thinggo.worlduc;

import java.util.ArrayList;
import java.util.List;

import com.csmzxy.thinggo.worlduc.core.Category;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class BlogCategoryListActivity extends Activity {
	private ExpandableListView blogCategoryList;
	private LayoutInflater mInflater;
	private List<Category> categories;
	private MyExpandableListAdapter adapter;
	private boolean isDataLoaded;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_category_list);
		blogCategoryList = (ExpandableListView) findViewById(R.id.blog_category_list);
		this.mInflater = LayoutInflater.from(this);
		categories = new ArrayList<Category>();
		this.adapter = new MyExpandableListAdapter();
		TextView tvTitle = (TextView)findViewById(R.id.worlduc_app_title);
		tvTitle.setText("文章栏目列表");
		final View footer = getLayoutInflater().inflate(
				R.layout.activity_word_list_footer, null);
		
		new Thread(){
			@Override
			public void run() {
				if(isDataLoaded) return;
				
				List<Category> list = WorlducUtils.getBlogCategories();
				if(list.size()>0){
					categories.clear();
					categories.addAll(list);
					BlogCategoryListActivity.this.runOnUiThread(new Runnable(){
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
							blogCategoryList.removeFooterView(footer);
							isDataLoaded = true;
						}});
				}
			}			
		}.start();
		
		blogCategoryList.addFooterView(footer);
		blogCategoryList.setAdapter(adapter);
		blogCategoryList.setOnChildClickListener(new OnChildClickListener() {		
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Category c = categories.get(groupPosition).getSubCategories().get(childPosition);				
				Log.i(WorlducCfg.TAG, "childClick:" + c.getName());
				Intent intent = getIntent();
				intent.putExtra(WorlducCfg.KEY_BLOG_ARTICLE_CATEGORY, c);
				setResult(0x123456, intent);
				finish();
				return true;
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    switch (keyCode) {
	        case KeyEvent.KEYCODE_BACK:
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	/** 扩展baseExpandableAdapter用于展示二级列表使用 */
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		
		@Override
		public int getGroupCount() {
			return categories.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return categories.get(groupPosition).getSubCategories().size();
		}

		@Override
		public Category getGroup(int groupPosition) {
			return categories.get(groupPosition);
		}

		@Override
		public Category getChild(int groupPosition, int childPosition) {
			List<Category> subs = categories.get(groupPosition)
					.getSubCategories();
			return subs == null ? null : subs.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return groupPosition * 100 + childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			String name = categories.get(groupPosition).getName();
			return getGenericView(convertView, name);
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			List<Category> subs = categories.get(groupPosition)
					.getSubCategories();
			String name = subs.get(childPosition).getName();
			return getGenericView(convertView, name);
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		/**
		 * 获得展示的VIEW
		 * 
		 * @return
		 */
		public View getGenericView(View vi, String name) {
			if (vi == null)
				vi = mInflater.inflate(
						R.layout.activity_blog_category_list_item, null);
			TextView tv = (TextView) vi.findViewById(R.id.tv_category_name);
			tv.setText(name);
			return vi;
		}
	}
}
