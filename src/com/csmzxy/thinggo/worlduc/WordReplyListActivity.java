package com.csmzxy.thinggo.worlduc;

import cn.waps.AppConnect;

import com.csmzxy.thinggo.worlduc.core.LeaveWord;
import com.csmzxy.thinggo.worlduc.core.MyImageGetter;
import com.csmzxy.thinggo.worlduc.core.Person;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.os.Message;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 留言回复列表
 * @author wmxing
 *
 */
public class WordReplyListActivity extends BaseListViewActivity<LeaveWord>{

	private EditText inputText;
	private Button btnReply;
	private LeaveWord word;
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("留言回复");		
		inputText = (EditText)findViewById(R.id.input_text);
		btnReply = (Button)findViewById(R.id.btn_reply);
		btnReply.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(pointTotals<=0){
					Toast.makeText(WordReplyListActivity.this, 
							"积分余额不足,无法回复流言,请支持开源软件，点击广告获取积分!", 
							Toast.LENGTH_LONG).show();
					return;
				}
				if(TextUtils.isEmpty(inputText.getText())){
					inputText.setError("留言内容不能为空");
					return;
				}
				inputText.setError(null);
					
				v.setEnabled(false);
				new Thread(){
					@Override
					public void run() {
						String message = inputText.getText().toString();
						LeaveWord word = data.get(0);
						word = WorlducUtils.replyLeaveWord(message, word.getId());		
						if(word!=null){
							spendPoints(1);
							data.add(word);
							notifyDataLoaded();
							WordReplyListActivity.this.runOnUiThread(new Runnable(){
								@Override
								public void run() {
									btnReply.setEnabled(true);
									inputText.setText("");
									Toast.makeText(WordReplyListActivity.this, "留言回复成功", Toast.LENGTH_LONG).show();									
								}							
							});
						}
					}					
				}.start();			
			}
		});		
		
		word = (LeaveWord)getIntent().getSerializableExtra(WorlducCfg.KEY_WORD);
		url = getIntent().getStringExtra(WorlducCfg.KEY_WORD_URL);
		System.out.println("--" + url);
		startLoadData();
	}
	

	@Override
	protected void getData() {
		if(word!=null)
			WorlducUtils.getReplyWordListBy(word.getId(), word.getAuthor().getUid(), pager);
		else if(url!=null)
			WorlducUtils.getReplyWordListBy(url, pager);
		else return;
		LeaveWord word = pager.getList().get(0);
		data.add(word);
		if(word.getReplyCount()>0)
			data.addAll(word.getReplyList());
	}
	
	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent) {
		View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_word_reply_list_item, null);

        TextView name = (TextView)vi.findViewById(R.id.reply_author); 	// 姓名
        TextView loc = (TextView)vi.findViewById(R.id.reply_location); 	// 位置
        TextView time = (TextView)vi.findViewById(R.id.reply_time); 		// 时间
        TextView content  = (TextView)vi.findViewById(R.id.reply_text);	// 内容
        ImageView head_image=(ImageView)vi.findViewById(R.id.reply_headimg); // 头像
        TextView replycnt = (TextView)vi.findViewById(R.id.reply_replycnt);	//回复次数
        
        LeaveWord word = data.get(position);
        Person p = word.getAuthor();
        // 设置ListView的相关值
        name.setText(p.getName());
        loc.setText(p.getLocation());
        time.setText(word.getTime());
        String txt = word.getContent();   
        
        content.setText(Html.fromHtml(txt, 
        		new MyImageGetter(content, this, screenWidth, WorlducCfg.BASE_URL_LEAVE_WORD), null));
        int n = word.getReplyCount();
        if(n>0){
        	replycnt.setText("留言人");
        }
        else{
        	replycnt.setText("回复人");
        }
        
        imageLoader.DisplayImage(p.getImgUrl(), head_image);
        return vi;
	}

	@Override
	protected void doOnItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		
	}



	@Override
	protected void setPageSize() {
		pager.setPageSize(WorlducCfg.PAGE_SIZE_REPLY_WORD_LIST);
	}



	@Override
	protected void setActiviLayoutId() {
		layoutId  = R.layout.activity_word_reply_list;
	}



	@Override
	protected void setListViewId() {
		listViewId = R.id.word_list_reply_view;
	}

}
