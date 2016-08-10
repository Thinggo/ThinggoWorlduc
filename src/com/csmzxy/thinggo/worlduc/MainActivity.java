package com.csmzxy.thinggo.worlduc;

import cn.waps.AppConnect;

import com.csmzxy.thinggo.worlduc.ad.QuitPopAd;
import com.csmzxy.thinggo.worlduc.ad.SlideWall;
import com.csmzxy.thinggo.worlduc.ad.SlideWallDrawer;
import com.csmzxy.thinggo.worlduc.ad.SoftSupportActivity;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.Bundle;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.TranslateAnimation;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements
		OnCheckedChangeListener {

	private TabHost tabHost;
	private TabHost.TabSpec tabSpec;
	private RadioGroup radioGroup;
	private RelativeLayout bottomLayout;
	private ImageView img;
	private int startLeft;
	private HorizontalScrollView mHorizontalScrollView;

	private static String[] tabNames = { "noticeMessage", 
			"friendRequest", "replyWord", 
			"myGroup", "writeWord", 
			"writeArticle", "writeWeibo",
			"friendArticle","supportAuthor" };
	private static int[] tabIds = { R.id.radio_notice_message,
			R.id.radio_friend_request, R.id.radio_reply_word,
			R.id.radio_mygroup, R.id.radio_write_word, 
			R.id.radio_write_article, R.id.radio_write_weibo, 
			R.id.radio_friend_article, R.id.radio_support_author };
	
	private static Class[] tabClass = { NoticeMessageListActivity.class,
			FriendRequestListActivity.class, WordListActivity.class,
			TeachingGroupListActivity.class, FriendGroupListActivity.class, 
			PublishBlogArticleActivity.class, MiniBlogListActivity.class,  
			FriendBlogCategoryActivity.class, SoftSupportActivity.class };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		WorlducUtils.createFileCache(this);

		// 预加载自定义广告内容（仅在使用了自定义广告、抽屉广告或迷你广告的情况，才需要添加）
		AppConnect.getInstance(this).initAdInfo();

		// 预加载插屏广告内容（仅在使用到插屏广告的情况，才需要添加）
		AppConnect.getInstance(this).initPopAd(this);

		// 预加载功能广告内容（仅在使用到功能广告的情况，才需要添加）
		AppConnect.getInstance(this).initFunAd(this);

		mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		bottomLayout = (RelativeLayout) findViewById(R.id.layout_bottom);


		tabHost = getTabHost();
		for (int i = 0; i < tabNames.length; i++) {
			tabHost.addTab(tabHost.newTabSpec(tabNames[i])
					.setIndicator(tabNames[i])
					.setContent(new Intent(this, tabClass[i])));
		}

		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		radioGroup.setOnCheckedChangeListener(this);

		img = new ImageView(this);
		img.setImageResource(R.drawable.tab_front_bg);

		bottomLayout.addView(img);
		startLeft = 0;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private int getTabIndex(int tabId) {
		for (int i = 0; i < tabIds.length; i++) {
			if (tabIds[i] == tabId)
				return i;
		}
		return -1;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int i = getTabIndex(checkedId);
		if (i < 0)
			return;
		tabHost.setCurrentTabByTag(tabNames[i]);
		moveFrontBg(img, startLeft, img.getWidth() * i, 0, 0);
		startLeft = img.getWidth() * i;
	}

	public void moveFrontBg(View v, int startX, int toX, int startY, int toY) {
		TranslateAnimation anim = new TranslateAnimation(startX, toX, startY,
				toY);
		anim.setDuration(200);
		anim.setFillAfter(true);
		v.startAnimation(anim);
		// Log.i(WorlducCfg.TAG, "startX="+startX + ",w="+v.getWidth());
		mHorizontalScrollView.smoothScrollTo(toX - v.getWidth() * 2, 0);

	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (SlideWall.getInstance().slideWallDrawer != null
					&& SlideWall.getInstance().slideWallDrawer.isOpened()) {
				// 如果抽屉式应用墙展示中，则关闭抽屉
				SlideWall.getInstance().closeSlidingDrawer();
				System.out.println("----key---");
			} else {
				// 调用退屏广告
				QuitPopAd.getInstance().show(this);
				System.out.println("----show---");
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 用于监听插屏广告的显示与关闭
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Dialog dialog = AppConnect.getInstance(this).getPopAdDialog();
		if (dialog != null) {
			if (dialog.isShowing()) {
				// 插屏广告正在显示
			}
			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// 监听插屏广告关闭事件
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		AppConnect.getInstance(this).close();
		super.onDestroy();
	}
}
