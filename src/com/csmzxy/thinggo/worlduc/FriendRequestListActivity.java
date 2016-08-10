package com.csmzxy.thinggo.worlduc;

import java.util.ArrayList;
import java.util.List;

import cn.waps.AppConnect;

import com.csmzxy.thinggo.worlduc.core.FriendGroup;
import com.csmzxy.thinggo.worlduc.core.FriendRequest;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FriendRequestListActivity extends
		BaseListViewActivity<FriendRequest> {
	private List<FriendGroup> listFriendGroup;
	private Spinner spinnerFriendGroup;
	private ArrayAdapter<FriendGroup> frAdapter;
	private Button btnAgreeAll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("好友请求列表");
		spinnerFriendGroup = (Spinner) findViewById(R.id.sp_friend_group);
		listFriendGroup = new ArrayList<FriendGroup>();
		
		btnAgreeAll = (Button) findViewById(R.id.btn_agree_all);
		btnAgreeAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (spinnerFriendGroup.getSelectedItemPosition() < 0) {
					Toast.makeText(FriendRequestListActivity.this, "请选择一个分组",
							Toast.LENGTH_LONG).show();
					return;
				}
				final FriendGroup fg = (FriendGroup) spinnerFriendGroup
						.getSelectedItem();

				int cnt  =0;
				for (FriendRequest fr : data) {
					if (fr.isChecked()) cnt++;
				}
				if(cnt >0 && pointTotals < 10){
					Toast.makeText(FriendRequestListActivity.this, 
							"积分余额不足10,无法一次同意多个好友请求,请支持开源软件，点击广告获取积分!", 
							Toast.LENGTH_LONG).show();
					return;
				}
				btnAgreeAll.setEnabled(false);
				new Thread() {
					@Override
					public void run() {
						List<FriendRequest> delList = new ArrayList<FriendRequest>();
						for (FriendRequest fr : data) {
							if (!fr.isChecked())
								continue;
							boolean bok = WorlducUtils.acceptFriendRequest(fr,
									fg.getId());
							if (bok) {
								delList.add(fr);
							}
						}
						data.removeAll(delList);
						FriendRequestListActivity.this
								.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										btnAgreeAll.setEnabled(true);
										adapter.notifyDataSetChanged();
									}
								});
					}

				}.start();

			}
		});

		((CheckBox) findViewById(R.id.btn_check_all))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {						
						for (FriendRequest fr : data) {
							fr.setChecked(isChecked);
						}
						adapter.notifyDataSetChanged();
					}
				});

		// 加载好友分组数据
		new Thread() {
			@Override
			public void run() {
				List<FriendGroup> fgs = WorlducUtils.getFriendGroupList(false);
				if (fgs.size() > 0) {
					listFriendGroup.addAll(fgs);
					int maxLength = 0;
					for(FriendGroup fr : listFriendGroup){
						if(fr.getName().length()>maxLength)
							maxLength = fr.getName().length();
					}
					String str = "未分组";
					for(int i=0;i<maxLength - str.length();i++)
						str = str + " ";
					listFriendGroup.get(0).setName(str);
				}
				FriendRequestListActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						frAdapter = new ArrayAdapter<FriendGroup>(FriendRequestListActivity.this,
								android.R.layout.simple_spinner_item, listFriendGroup);
						spinnerFriendGroup.setAdapter(frAdapter);
						
						frAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					}
				});
			}

		}.start();
		startLoadData();
	}

	@Override
	protected void getData() {
		WorlducUtils.getFriendRequestList(pager);
		if (pager.getList().size() > 0)
			data.addAll(pager.getList());
	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.activity_friend_request_list_item,
					null);

		TextView name = (TextView) vi.findViewById(R.id.friend_name); // 姓名
		TextView loc = (TextView) vi.findViewById(R.id.friend_location); // 位置
		TextView time = (TextView) vi.findViewById(R.id.request_time); // 时间
		TextView content = (TextView) vi.findViewById(R.id.friend_remark); // 附言
		ImageView head_image = (ImageView) vi.findViewById(R.id.friend_headimg); // 头像
		CheckBox cb = (CheckBox) vi.findViewById(R.id.friend_cb);
		final FriendRequest fr = data.get(position);
		cb.setChecked(fr.isChecked());
		// 设置ListView的相关值
		name.setText(fr.getName());
		loc.setText(fr.getLocation());
		time.setText(fr.getTime());
		content.setText("附言:" + fr.getRemark());
		if (position % 2 == 0)
			vi.setBackgroundColor(0xff0);
		imageLoader.DisplayImage(fr.getImgUrl(), head_image);
		return vi;
	}

	@Override
	protected void doOnItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		FriendRequest fr = data.get(position);
		fr.setChecked(!fr.isChecked());
		CheckBox cb = (CheckBox) view.findViewById(R.id.friend_cb);
		cb.setChecked(fr.isChecked());
	}

	@Override
	protected void setPageSize() {

		pager.setPageSize(WorlducCfg.PAGE_SIZE_FRIENT_REQUEST_LIST);
	}

	@Override
	protected void setActiviLayoutId() {
		this.layoutId = R.layout.activity_friend_request_list;
	}

	@Override
	protected void setListViewId() {
		this.listViewId = R.id.friend_request_list_view;

	}

}
