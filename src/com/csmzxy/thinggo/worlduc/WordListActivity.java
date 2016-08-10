package com.csmzxy.thinggo.worlduc;

import com.csmzxy.thinggo.worlduc.core.LeaveWord;
import com.csmzxy.thinggo.worlduc.core.MyImageGetter;
import com.csmzxy.thinggo.worlduc.core.Person;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

public class WordListActivity extends BaseListViewActivity<LeaveWord> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("�ҵ����԰�");
		startLoadData();
	}
	

	@Override
	protected void getData() {
		WorlducUtils.getLeaveWordList(pager);
		data.addAll(pager.getList());
	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent) {
		View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_word_list_item, null);

        TextView name = (TextView)vi.findViewById(R.id.author); 	// ����
        TextView loc = (TextView)vi.findViewById(R.id.location); 	// λ��
        TextView time = (TextView)vi.findViewById(R.id.time); 		// ʱ��
        TextView content  = (TextView)vi.findViewById(R.id.text);	// ����
        ImageView head_image=(ImageView)vi.findViewById(R.id.headimg); // ͷ��
        TextView replycnt = (TextView)vi.findViewById(R.id.replycnt);	//�ظ�����
        
        LeaveWord word = data.get(position);
        Person p = word.getAuthor();
        // ����ListView�����ֵ
        name.setText(p.getName());
        loc.setText(p.getLocation());
        time.setText(word.getTime());
        String txt = word.getContent();    
        content.setText(Html.fromHtml(txt, 
        		new MyImageGetter(content, this, screenWidth, WorlducCfg.BASE_URL_LEAVE_WORD), null));
        int n = word.getReplyCount();
        if(n>0){
        	replycnt.setText("�ظ�["+ n +"]��");
        }
        else{
        	replycnt.setText("δ�ظ�");
        }
        
        imageLoader.DisplayImage(p.getImgUrl(), head_image);
        return vi;
	}

	@Override
	protected void doOnItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		Intent intent = new Intent(WordListActivity.this, WordReplyListActivity.class);
		LeaveWord word =adapter.getItem(position);
		
		intent.putExtra(WorlducCfg.KEY_WORD, word);
		startActivity(intent);
	}

	@Override
	protected void setPageSize() {
		pager.setPageSize(WorlducCfg.PAGE_SIZE_WORD_LIST);
	}

	@Override
	protected void setActiviLayoutId() {
		layoutId  = R.layout.activity_word_list;
	}

	@Override
	protected void setListViewId() {
		listViewId = R.id.word_list_view;
	}
}
