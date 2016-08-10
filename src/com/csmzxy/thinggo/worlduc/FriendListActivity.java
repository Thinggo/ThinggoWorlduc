package com.csmzxy.thinggo.worlduc;

import com.csmzxy.thinggo.worlduc.core.FriendGroup;
import com.csmzxy.thinggo.worlduc.core.FriendRequest;
import com.csmzxy.thinggo.worlduc.core.Person;
import com.csmzxy.thinggo.worlduc.core.TeachingGroup;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendListActivity extends BaseListViewActivity<Person> {

	private FriendGroup friendGroup;
	private EditText txt_content;
	private Button btnLeaveWord;
	private String errorMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_friend_list);
		friendGroup = (FriendGroup) getIntent().getSerializableExtra(
				WorlducCfg.KEY_FRIEND_GROUP);
		this.setTitle(friendGroup.getName());

		txt_content = (EditText) findViewById(R.id.txt_content);
		this.setButtonViewVisible(View.VISIBLE,"查看文章");
		((CheckBox) findViewById(R.id.btn_check_all))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						for (int i = 0; i < data.size(); i++) {
							data.get(i).setChecked(isChecked);
						}
						adapter.notifyDataSetChanged();
					}
				});
		btnLeaveWord = (Button) findViewById(R.id.btn_leave_word);
		btnLeaveWord.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String txt = txt_content.getText().toString();
				if (TextUtils.isEmpty(txt)) {
					txt_content.setError("请输入要留言的内容！");
					return;
				}
				v.setEnabled(false);

				txt_content.setError(null);

				int cnt = 0;
				for (Person fr : data) {
					if (fr.isChecked())
						cnt++;
				}
				if ((cnt > 10 && pointTotals < 10) || cnt > pointTotals) {
					Toast.makeText(FriendListActivity.this,
							"积分余额不足, 请支持开源软件，点击广告获取积分!", Toast.LENGTH_LONG)
							.show();
					return;
				}
				new Thread() {
					@Override
					public void run() {
						errorMsg = "";
						int count = 0;
						for (int i = 0; i < data.size(); i++) {
							if (!data.get(i).isChecked())
								continue;
							boolean ok = WorlducUtils.publishLeaveWord(txt,
									data.get(i).getUid());
							if (!ok) {
								errorMsg += "给【" + data.get(i).getName()
										+ "】留言失败！";
							} else {
								count++;
								Log.i(WorlducCfg.TAG, "给好友"
										+ data.get(i).getName() + ok);
							}
						}
						spendPoints(count > 10 ? 10 : count);
						FriendListActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								btnLeaveWord.setEnabled(true);
								txt_content.setText("");
								if (errorMsg.length() > 0) {
									Toast.makeText(FriendListActivity.this,
											errorMsg, Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(FriendListActivity.this,
											"留言成功！", Toast.LENGTH_LONG).show();
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
		WorlducUtils.getFriendListByGroup(pager, friendGroup.getId(), false);
		if (pager.getList().size() > 0)
			data.addAll(pager.getList());
	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.activity_friend_list_item, null);
		ImageView imageView = (ImageView) vi.findViewById(R.id.friend_list_img);
		TextView title = (TextView) vi.findViewById(R.id.firend_list_title);
		CheckBox cb = (CheckBox) vi.findViewById(R.id.friend_list_cb);
		Person p = data.get(position);
		cb.setChecked(p.isChecked());
		title.setText(p.getName());
		imageLoader.DisplayImage(p.getImgUrl(), imageView);
		return vi;
	}

	@Override
	protected void setPageSize() {
		pager.setPageSize(WorlducCfg.PAGE_SIZE_FRIEND_LIST);

	}

	@Override
	protected void setActiviLayoutId() {
		this.layoutId = R.layout.activity_friend_list;
	}

	@Override
	protected void setListViewId() {
		this.listViewId = R.id.friend_list_view;

	}

	@Override
	protected void doOnItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		CheckBox cb = (CheckBox) view.findViewById(R.id.friend_list_cb);
		cb.setChecked(!cb.isChecked());
		data.get(position).setChecked(cb.isChecked());
	}

	@Override
	protected void onButtonViewClick(View v) {
		int k = -1;
		for(int i=0;i<data.size();i++){
			if(data.get(i).isChecked()){
				k = i;
				break;
			}
		}
		if(k<0){
			Toast.makeText(FriendListActivity.this,
					"请选择一个好友进行查看！", Toast.LENGTH_SHORT).show();
			return;
		}
		Person p = data.get(k);
		Intent intent = new Intent(this,FriendBlogCategoryActivity.class);
		intent.putExtra(WorlducCfg.KEY_FRIEND_PERSON, p);
		startActivity(intent);
	}
}
