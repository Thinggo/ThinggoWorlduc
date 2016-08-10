package com.csmzxy.thinggo.worlduc;

import com.csmzxy.thinggo.worlduc.core.TeachingGroup;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 显示我加入的教研室列表
 * @author wmxing
 *
 */
public class TeachingGroupListActivity extends BaseListViewActivity<TeachingGroup> {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setTitle("我的教研室列表");
		startLoadData();
	}

	@Override
	protected void getData() {
		WorlducUtils.getTeachingGroupList(pager,false);
		data.addAll(pager.getList());
	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent) {
		View vi = convertView;
		if(convertView==null)
            vi = inflater.inflate(R.layout.activity_teaching_group_list_item, null);
        ImageView imageView = (ImageView)vi.findViewById(R.id.teaching_group_img);
        TextView title = (TextView)vi.findViewById(R.id.teaching_group_title);
        TeachingGroup tg = data.get(position);
        title.setText(tg.getName());
        imageLoader.DisplayImage(tg.getPicUrl(), imageView);
		return vi;
	}

	@Override
	protected void doOnItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		TeachingGroup tg = adapter.getItem(position);
		Intent intent = new Intent(this, TopicListActivity.class);
		intent.putExtra(WorlducCfg.KEY_GID, tg);
		startActivity(intent);
	}

	@Override
	protected void setPageSize() {
		pager.setPageSize(WorlducCfg.PAGE_SIZE_GROUP_LIST);
	}
	
	@Override
	protected void setActiviLayoutId() {
		layoutId = R.layout.activity_teaching_group_list;
	}

	@Override
	protected void setListViewId() {
		listViewId = R.id.teaching_group_list_view;
	}
	
}
