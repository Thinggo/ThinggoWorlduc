package com.csmzxy.thinggo.worlduc;

import com.csmzxy.thinggo.worlduc.core.TeachingGroup;
import com.csmzxy.thinggo.worlduc.core.TopicArticle;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * 
 * 显示群组中的主题列表
 * @author wmxing
 *
 */
public class TopicListActivity extends BaseListViewActivity<TopicArticle>{

	private TeachingGroup group;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		group = (TeachingGroup)getIntent().getSerializableExtra(WorlducCfg.KEY_GID);
		setTitle(group.getName());
		startLoadData();
	}

	
	@Override
	protected void getData() {
		WorlducUtils.getTopicListByGroup(group.getId(), pager, false);
		data.addAll(pager.getList());
	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent) {
		View vi = convertView;
		if(vi==null){
			vi = inflater.inflate(R.layout.activity_topic_list_item, null);
		}
		TextView title = (TextView)vi.findViewById(R.id.topic_title);
		TextView content = (TextView)vi.findViewById(R.id.topic_content);
		TextView time = (TextView)vi.findViewById(R.id.topic_time);
		TextView author = (TextView)vi.findViewById(R.id.topic_author);
		TopicArticle ta = data.get(position);
		title.setText(ta.getTitle());
		content.setText(ta.getContent());
		time.setText(ta.getTime());
		author.setText(ta.getAuthor().getName());
		return vi;
	}

	@Override
	protected void doOnItemClick(AdapterView parent, View view, int position, long id) {
		TopicArticle ta  = adapter.getItem(position);
		Intent intent = new Intent(this, TopicActivity.class);
		intent.putExtra(WorlducCfg.KEY_GROUP_TOPIC, ta);
		startActivity(intent);
	}

	@Override
	protected void setPageSize() {
		pager.setPageSize(WorlducCfg.PAGE_SIZE_TOPIC_LIST);
	}

	@Override
	protected void setActiviLayoutId() {
		layoutId = R.layout.activity_topic_list;
	}
	
	@Override
	protected void setListViewId() {
		listViewId = R.id.topic_list_view;
	}
}
