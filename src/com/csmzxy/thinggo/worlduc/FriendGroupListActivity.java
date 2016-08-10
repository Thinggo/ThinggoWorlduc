package com.csmzxy.thinggo.worlduc;

import java.util.List;

import com.csmzxy.thinggo.worlduc.core.FriendGroup;
import com.csmzxy.thinggo.worlduc.core.TeachingGroup;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendGroupListActivity extends BaseListViewActivity<FriendGroup> {
	private ImageView btnRefresh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("好友分组列表");
		btnRefresh = (ImageView)findViewById(R.id.btn_refresh);
		btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnRefresh.setEnabled(false);
				listView.addFooterView(footer);
				new Thread(){
					@Override
					public void run() {
						List<FriendGroup> list = WorlducUtils.getFriendGroupList(true);
						if(list.size()>0){
							data.clear();
							data.addAll(list);
							FriendGroupListActivity.this.runOnUiThread(new Runnable(){
								@Override
								public void run() {
									adapter.notifyDataSetChanged();	
									btnRefresh.setEnabled(true);
									listView.removeFooterView(footer);
								}								
							});
						}
					}}.start();
			}
		});
		startLoadData();
		
	}

	@Override
	protected void getData() {
		List<FriendGroup> list = WorlducUtils.getFriendGroupList(false);
		if(list.size()>0) data.addAll(list);
		
	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent) {
		View vi = convertView;
		if(convertView==null)
            vi = inflater.inflate(R.layout.activity_friend_group_list_item, null);
        ImageView imageView = (ImageView)vi.findViewById(R.id.friend_group_img);
        TextView title = (TextView)vi.findViewById(R.id.firend_group_title);
        FriendGroup fg = data.get(position);
        title.setText(fg.getName());
		return vi;
	}

	@Override
	protected void setPageSize() {
		pager.setPageSize(Integer.MAX_VALUE);		
	}

	@Override
	protected void setActiviLayoutId() {
		this.layoutId = R.layout.activity_friend_group_list;
		
	}

	@Override
	protected void setListViewId() {
		this.listViewId = R.id.friend_group_list_view;		
	}

	@Override
	protected void doOnItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		FriendGroup fg = data.get(position);
		Intent intent = new Intent(this, FriendListActivity.class);
		intent.putExtra(WorlducCfg.KEY_FRIEND_GROUP, fg);
		startActivity(intent);
	}

}
