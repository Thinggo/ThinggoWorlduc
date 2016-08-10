package com.csmzxy.thinggo.worlduc;

import java.util.ArrayList;
import java.util.List;

import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;

import com.csmzxy.thinggo.worlduc.core.ImageLoader;
import com.csmzxy.thinggo.worlduc.core.PagerModel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public abstract class BaseListViewActivity<T> extends Activity implements
		OnScrollListener, UpdatePointsNotifier {

	protected List<T> data;
	protected View footer;
	protected ListView listView;
	protected boolean dataLoaded;
	protected PagerModel<T> pager;
	protected DataLoadHandler handler;
	protected DataAdapter adapter;
	protected int imageSize = 70;
	protected LayoutInflater inflater;
	protected ImageLoader imageLoader; // ��������ͼƬ����
	protected String TAG = WorlducCfg.TAG;
	protected int listViewId; // ListView�Ŀؼ�ID
	protected int layoutId; // ��ǰActivity�Ĳ����ļ�ID
	protected int screenWidth;
	protected static int pointTotals;	//����
	protected Button btnView;
	
	public BaseListViewActivity() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = new ArrayList<T>();
		pager = new PagerModel<T>(1);
		handler = new DataLoadHandler();
		adapter = new DataAdapter();
		setPageSize();
		setActiviLayoutId();
		setListViewId();
		setContentView(layoutId);
		// ����DisplayMetrics ����
		DisplayMetrics dm = new DisplayMetrics();
		// ȡ�ô�������
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		footer = getLayoutInflater().inflate(
				R.layout.activity_word_list_footer, null);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(this, imageSize);

		listView = (ListView) findViewById(listViewId);
		listView.setOnScrollListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				doOnItemClick(parent, view, position, id);
			}
		});
		listView.addFooterView(footer);
		listView.setAdapter(adapter);
		
		btnView = (Button)findViewById(R.id.btn_view);
		btnView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonViewClick(v);
			}
			
		});
	}
	
	protected void setButtonViewVisible(int visibility,String title){
		btnView.setVisibility(visibility);
		btnView.setText(title);
	}
	protected void onButtonViewClick(View v) {
				
	}

	protected void startLoadData() {
		new DataLoadThread(1).start();
	}

	protected void setTitle(String title) {
		TextView tv = (TextView) findViewById(R.id.worlduc_app_title);
		tv.setText(title);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int curPage = totalItemCount % pager.getPageSize() == 0 ? totalItemCount
				/ pager.getPageSize()
				: (totalItemCount / pager.getPageSize() + 1);
		if (totalItemCount > 0
				&& firstVisibleItem + visibleItemCount == totalItemCount
				&& dataLoaded && curPage + 1 <= pager.getPageCount()) {
			dataLoaded = false;
			listView.addFooterView(footer);
			new DataLoadThread(curPage + 1).start();
		}

	}

	class DataLoadThread extends Thread {

		public DataLoadThread(int iPage) {
			pager.setCurPage(iPage);
		}

		@Override
		public void run() {
			getData();
			notifyDataLoaded();
		}
	}

	protected void notifyDataLoaded() {
		Message msg = new Message();
		msg.what = WorlducCfg.HANDLER_LOADED;
		handler.sendMessage(msg);
	}

	class DataLoadHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WorlducCfg.HANDLER_LOADED) {
				adapter.notifyDataSetChanged();
				if (listView.getFooterViewsCount() > 0) {
					listView.removeFooterView(footer);
				}
				dataLoaded = true;
			}
			handleOtherMessage(msg);
		}
	}

	protected void handleOtherMessage(Message msg) {

	}

	class DataAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public T getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getListItemView(position, convertView, parent);
		}

	}

	/**
	 * ���������б�,��д
	 */
	protected abstract void getData();

	/**
	 * �Զ����ListViewItem��ͼ����������д
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	protected abstract View getListItemView(int position, View convertView,
			ViewGroup parent);

	/**
	 * �����б��еĵ���¼�
	 * 
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	protected void doOnItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		return;
	}

	/**
	 * �����б���ʾ�ķ�ҳ��С pager.setPageSize(int)
	 */
	protected abstract void setPageSize();

	/**
	 * ��������Activity�Ĳ����ļ�Id, layoutId
	 */
	protected abstract void setActiviLayoutId();

	/**
	 * ���õ�ǰListView�Ŀؼ�Id
	 */
	protected abstract void setListViewId();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		// �ӷ������˻�ȡ��ǰ�û����������.
		// ���ؽ���ڻص�����getUpdatePoints(...)�д���
		AppConnect.getInstance(this).getPoints(this);
		super.onResume();
	}
	
	/**
	 * AppConnect.getPoints()������ʵ�֣�����ʵ��
	 * 
	 * @param currencyName
	 *            �����������.
	 * @param pointTotal
	 *            ����������.
	 */
	public void getUpdatePoints(String currencyName, int pointTotal) {
		pointTotals = pointTotal;
		System.out.println("AppConnect.getPoints() = "  + pointTotals);
		
	}
	/**
	 * AppConnect.getPoints() ������ʵ�֣�����ʵ��
	 * 
	 * @param error
	 *            ����ʧ�ܵĴ�����Ϣ
	 */
	public void getUpdatePointsFailed(String error) {
		System.out.println("AppConnect.getPoints() error: "  + error);
	}
	
	public boolean spendPoints(int points){
		if(pointTotals >= points){
			AppConnect.getInstance(this).spendPoints(points, this);
			return true;
		}
		return false;
	}

}
