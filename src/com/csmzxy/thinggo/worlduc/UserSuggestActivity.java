package com.csmzxy.thinggo.worlduc;

import cn.waps.AppConnect;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class UserSuggestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_suggest);
		this.setVisible(false);
		
	}
	@Override
	protected void onResume() {
		//”√ªß∑¥¿°
		AppConnect.getInstance(this).showFeedback(this);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_suggest, menu);
		return true;
	}

}
