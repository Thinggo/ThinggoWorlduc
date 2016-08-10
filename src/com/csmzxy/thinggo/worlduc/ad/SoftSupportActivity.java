package com.csmzxy.thinggo.worlduc.ad;

import com.csmzxy.thinggo.worlduc.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.waps.AdInfo;
import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;

public class SoftSupportActivity extends Activity implements
		View.OnClickListener, UpdatePointsNotifier {

	private int pointsTotals = 0;
	private String showAd;
	private TextView pointsTextView;
	private TextView tvWelcome;

	private String displayPointsText;

	final Handler mHandler = new Handler();

	// 抽屉广告布局
	private View slidingDrawerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soft_support);
		TextView tv = (TextView) findViewById(R.id.worlduc_app_title);
		tv.setText("支持作者，点击广告获取积分");

		Button offersButton = (Button) findViewById(R.id.OffersButton);
		Button gameOffersButton = (Button) findViewById(R.id.gameOffersButton);
		Button appOffersButton = (Button) findViewById(R.id.appOffersButton);
		Button moreAppsButton = (Button) findViewById(R.id.moreAppsButton);
		Button spendButton = (Button) findViewById(R.id.spendButton);
		Button feedbackButton = (Button) findViewById(R.id.feedbackButton);
		Button awardButton = (Button) findViewById(R.id.awardButton);
		Button diyAdButton = (Button) findViewById(R.id.diyAdButton);
		Button diyAdListButton = (Button) findViewById(R.id.diyAdListButton);
		Button popAdButton = (Button) findViewById(R.id.popAdButton);
		Button ownAppDetailButton = (Button) findViewById(R.id.ownAppDetailButton);
		Button funAdButton = (Button) findViewById(R.id.funAdButton);

		offersButton.setOnClickListener(this);
		gameOffersButton.setOnClickListener(this);
		appOffersButton.setOnClickListener(this);
		moreAppsButton.setOnClickListener(this);
		spendButton.setOnClickListener(this);
		feedbackButton.setOnClickListener(this);
		awardButton.setOnClickListener(this);
		diyAdButton.setOnClickListener(this);
		diyAdListButton.setOnClickListener(this);
		popAdButton.setOnClickListener(this);
		ownAppDetailButton.setOnClickListener(this);
		funAdButton.setOnClickListener(this);

		pointsTextView = (TextView) findViewById(R.id.PointsTextView);
		tvWelcome = (TextView) findViewById(R.id.SDKVersionView);

		// 带有默认参数值的在线配置，使用此方法，程序第一次启动使用的是"defaultValue"，之后再启动则是使用的服务器端返回的参数值
		showAd = AppConnect.getInstance(this).getConfig("showAd",
				"defaultValue");
		
		tvWelcome.setText("感谢您使用本软件，如果您觉得软件不错，并且心情也好，请您点一下广告支持作者，谢谢！：：) "
				+ showAd);

		// 互动广告调用方式
		LinearLayout layout = (LinearLayout) this
				.findViewById(R.id.AdLinearLayout);
		AppConnect.getInstance(this).showBannerAd(this, layout);

		// 迷你广告调用方式
		// AppConnect.getInstance(this).setAdBackColor(Color.argb(50, 120, 240,
		// 120));//设置迷你广告背景颜色
		// AppConnect.getInstance(this).setAdForeColor(Color.YELLOW);//设置迷你广告文字颜色
		LinearLayout miniLayout = (LinearLayout) findViewById(R.id.miniAdLinearLayout);
		AppConnect.getInstance(this).showMiniAd(this, miniLayout, 10);// 10秒刷新一次

		slidingDrawerView = SlideWall.getInstance().getView(this);
		if (slidingDrawerView != null) {
			this.addContentView(slidingDrawerView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
	}

	public void onClick(View v) {
		if (v instanceof Button) {
			int id = ((Button) v).getId();

			switch (id) {
			case R.id.OffersButton:
				// 显示推荐列表（综合）
				AppConnect.getInstance(this).showOffers(this);
				break;
			case R.id.popAdButton:
				// 显示插屏广告
				// 判断插屏广告是否已初始化完成，用于确定是否能成功调用插屏广告
				// boolean hasPopAd =
				// AppConnect.getInstance(this).hasPopAd(this);
				// if(hasPopAd){
				AppConnect.getInstance(this).showPopAd(this);
				// }
				break;
			case R.id.appOffersButton:
				// 显示推荐列表（软件）
				AppConnect.getInstance(this).showAppOffers(this);
				break;
			case R.id.gameOffersButton:
				// 显示推荐列表（游戏）
				AppConnect.getInstance(this).showGameOffers(this);
				break;
			case R.id.diyAdListButton:
				// 获取全部自定义广告数据
				try {
					Intent appWallIntent = new Intent(this, AppWall.class);
					this.startActivity(appWallIntent);
				} catch (Exception ex) {
				}
				break;
			case R.id.diyAdButton:
				// 获取一条自定义广告数据
				AdInfo adInfo = AppConnect
						.getInstance(SoftSupportActivity.this).getAdInfo();
				AppDetail.getInstanct().showAdDetail(SoftSupportActivity.this,
						adInfo);
				break;
			case R.id.spendButton:
				// 消费虚拟货币.
				// AppConnect.getInstance(this).spendPoints(10, this);
				break;
			case R.id.awardButton:
				// 奖励虚拟货币
				// AppConnect.getInstance(this).awardPoints(10, this);
				break;
			case R.id.moreAppsButton:
				// 显示自家应用列表
				AppConnect.getInstance(this).showMore(this);
				break;
			case R.id.ownAppDetailButton:
				// 根据指定的应用app_id展示其详情
				AppConnect.getInstance(this).showMore(this,
						"2478717e9f828ada95a7ded613c1e75c");
				break;
			case R.id.funAdButton:
				// 调用功能广告接口（使用浏览器接口）
				String uriStr = "http://www.baidu.com";
				AppConnect.getInstance(this).showBrowser(this, uriStr);
				break;
			case R.id.feedbackButton:
				// 用户反馈
				AppConnect.getInstance(this).showFeedback(this);
				break;
			}
		}
	}

	// 建议加入onConfigurationChanged回调方法
	// 注:如果当前Activity没有设置android:configChanges属性,或者是固定横屏或竖屏模式,则不需要加入
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// 横竖屏状态切换时,关闭处于打开状态中的退屏广告
		QuitPopAd.getInstance().close();
		// 使用抽屉式应用墙,横竖屏状态切换时,重新加载抽屉,保证ListView重新加载,保证ListView中Item的布局匹配当前屏幕状态
		if (slidingDrawerView != null) {
			// 先remove掉slidingDrawerView
			((ViewGroup) slidingDrawerView.getParent())
					.removeView(slidingDrawerView);
			slidingDrawerView = null;
			// 重新获取抽屉样式布局,此时ListView重新设置了Adapter
			slidingDrawerView = SlideWall.getInstance().getView(this);
			if (slidingDrawerView != null) {
				this.addContentView(slidingDrawerView, new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			}
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		// 从服务器端获取当前用户的虚拟货币.
		// 返回结果在回调函数getUpdatePoints(...)中处理
		AppConnect.getInstance(this).getPoints(this);
		super.onResume();
	}

	// 创建一个线程
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (pointsTextView != null) {
				pointsTextView.setText(displayPointsText);
			}
		}
	};

	/**
	 * AppConnect.getPoints()方法的实现，必须实现
	 * 
	 * @param currencyName
	 *            虚拟货币名称.
	 * @param pointTotal
	 *            虚拟货币余额.
	 */
	public void getUpdatePoints(String currencyName, int pointTotal) {
		pointsTotals = pointTotal;
		if(pointTotal == 0){
			if("OK".endsWith(showAd)){
				AppConnect.getInstance(this).awardPoints(100, this);
			}
		}
		displayPointsText = "您的" + currencyName + ": " + pointTotal;
		mHandler.post(mUpdateResults);
	}

	/**
	 * AppConnect.getPoints() 方法的实现，必须实现
	 * 
	 * @param error
	 *            请求失败的错误信息
	 */
	public void getUpdatePointsFailed(String error) {
		displayPointsText = error;
		mHandler.post(mUpdateResults);
	}
}