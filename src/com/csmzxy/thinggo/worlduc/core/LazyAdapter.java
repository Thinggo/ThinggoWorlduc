package com.csmzxy.thinggo.worlduc.core;
import java.util.List;

import com.csmzxy.thinggo.worlduc.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private List<LeaveWord> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; //��������ͼƬ����
    
    public LazyAdapter(Activity a, List<LeaveWord> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext(),70);
    }

    public int getCount() {
        return data.size();
    }

    public LeaveWord getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
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
        content.setText(txt);
        int n = word.getReplyCount();
        if(n>0){
        	replycnt.setText("�ظ�["+ n +"]��");
        	replycnt.setTextColor(Color.BLUE);
        }
        else{
        	replycnt.setText("δ�ظ�");
        	replycnt.setTextColor(Color.RED);
        }
        
        imageLoader.DisplayImage(p.getImgUrl(), head_image);
        return vi;
	}
    
    public void addAll(List<LeaveWord> list){
    	data.addAll(list);
    	//this.notifyDataSetChanged();
    }
}
