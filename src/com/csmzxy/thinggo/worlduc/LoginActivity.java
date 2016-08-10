package com.csmzxy.thinggo.worlduc;

import cn.waps.AppConnect;

import com.csmzxy.thinggo.worlduc.core.AESUtils;
import com.csmzxy.thinggo.worlduc.core.WorlducUtils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private String mEmail="";
	private String mPassword="";

	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	private TextView mLoginStatusMessageView;
	private CheckBox cb_cache;

	private View mLoginFormView;
	private View mLoginStatusView;

	private UserLoginTask mAuthTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// 广告用
		AppConnect.getInstance(this);
		LinearLayout container = (LinearLayout) findViewById(R.id.AdLinearLayout);
		AppConnect.getInstance(this).showBannerAd(this, container);

		cb_cache = (CheckBox)findViewById(R.id.cb_cache);
		TextView tv = (TextView) findViewById(R.id.worlduc_app_title);
		tv.setText("世界大学城登录");
		SharedPreferences sharedPreferences = getSharedPreferences(
				WorlducCfg.KEY_LOGIN_USER, Context.MODE_PRIVATE);
		try {
			mEmail = sharedPreferences.getString(WorlducCfg.KEY_NAME, "");
			String pwd = sharedPreferences.getString(WorlducCfg.KEY_PWD, "");
			if (pwd.length() > 0) {
				mPassword = AESUtils.decrypt(WorlducCfg.TAG, pwd);
			} else {
				mPassword = "";
			}
		} catch (Exception ex) {
			Log.e(WorlducCfg.TAG, "--Login sharedPreferences--");
			ex.printStackTrace();
			mPassword = "";
		}
		//mEmail = "430000098950@worlduc.com";
		//mPassword = "w2t58worlduc";

		mEmailEditText = (EditText) findViewById(R.id.email);
		if (mEmail.length() > 0)
			mEmailEditText.setText(mEmail);
		mPasswordEditText = (EditText) findViewById(R.id.password);
		if (mPassword.length() > 0)
			mPasswordEditText.setText(mPassword);
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		findViewById(R.id.sign_in_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!WorlducUtils.isNetConnected(LoginActivity.this)) {
							Toast.makeText(LoginActivity.this, "无法链接到网络",
									Toast.LENGTH_SHORT).show();
							return;
						}
					    WorlducCfg.IsClearCache = 	cb_cache.isChecked();
						login();

					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private void login() {
		if (mAuthTask != null) {
			return;
		}
		mEmailEditText.setError(null);
		mPasswordEditText.setError(null);

		mEmail = mEmailEditText.getText().toString();
		mPassword = mPasswordEditText.getText().toString();

		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mEmail)) {
			mEmailEditText.setError(getString(R.string.error_field_required));
			focusView = mEmailEditText;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailEditText.setError(getString(R.string.error_invalid_email));
			focusView = mEmailEditText;
			cancel = true;
		}
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordEditText
					.setError(getString(R.string.error_field_required));
			focusView = mPasswordEditText;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	private void showProgress(final boolean show) {
		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPostExecute(Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				try {
					SharedPreferences sharedPreferences = getSharedPreferences(
							WorlducCfg.KEY_LOGIN_USER, Context.MODE_PRIVATE);
					Editor editor = sharedPreferences.edit();// 获取编辑器
					editor.putString(WorlducCfg.KEY_NAME, mEmail);
					String pwd = AESUtils.encrypt(WorlducCfg.TAG, mPassword);
					editor.putString(WorlducCfg.KEY_PWD, pwd);
					editor.commit(); // 提交修改
				} catch (Exception ex) {
					Log.e(WorlducCfg.TAG, "--editor.commit--");
					ex.printStackTrace();
				}
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				mPasswordEditText
						.setError(getString(R.string.error_incorrect_password));
				mPasswordEditText.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return WorlducUtils.login(mEmail, mPassword);
		}
	}
}
