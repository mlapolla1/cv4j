/**
 * 
 */
package com.cv4j.app.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cv4j.app.R;


/**
 * @author Tony Shen
 *
 */
public class DoubleClickExitUtils {

	private final Activity mActivity;

	private Handler mHandler;
	private boolean isOnKeyBacking = false;
	private Toast mBackToast = null;

	public DoubleClickExitUtils(Activity activity) {
		mActivity = activity;
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	/**
	 * Activity onKeyDown事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		final Runnable onBackTimeRunnable = new Runnable() {
		
			@Override
			public void run() {
				isOnKeyBacking = false;
				if(mBackToast != null){
					mBackToast.cancel();
				}
			}
		};
		
		if(keyCode != KeyEvent.KEYCODE_BACK) {
			return false;
		}
		
		if(isOnKeyBacking) {
			mHandler.removeCallbacks(onBackTimeRunnable);
			if(mBackToast != null){
				mBackToast.cancel();
			}

			mActivity.finish();
			android.os.Process.killProcess(android.os.Process.myPid());
			return true;
		} else {
			isOnKeyBacking = true;
			final int param = 2000;

			if(mBackToast == null) {
				mBackToast = Toast.makeText(mActivity, R.string.finish_by_back_again, Toast.LENGTH_LONG);
			}

			mBackToast.show();
			mHandler.postDelayed(onBackTimeRunnable, param);
			return true;
		}
	}
}
