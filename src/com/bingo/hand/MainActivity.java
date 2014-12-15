/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bingo.hand;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bingo.hand.fragment.LeftFragment;
import com.bingo.hand.fragment.MainFragment;
import com.bingo.hand.keyboard.KeyboardLayout;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {
	SlidingMenu menu;
	LeftFragment leftFragment;
	KeyboardLayout keyboradLayout;
	boolean isKeyboardPopup;

	FragmentTransaction t;
	LinearLayout ll_yaomai, ll_zhuye, ll_wode, ll_shichang, ll_xiaoxi;
	TextView tv_title;
	private long firstTime;


	public FragmentTransaction getT() {
		return t;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isKeyboardPopup) {
				return super.onKeyUp(keyCode, event);// 软键盘已弹出
			}
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 1000) {// 如果两次按键时间间隔大于1000毫秒，则不退出
				Toast.makeText(MainActivity.this, "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				firstTime = secondTime;// 更新firstTime
				return true;
			} else {
				System.exit(0);// 否则退出程序
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.menu_frame);
		keyboradLayout = (KeyboardLayout) findViewById(R.id.kl_main);
		init();
	}

	private void init() {
		t = this.getSupportFragmentManager().beginTransaction();
		menu = getSlidingMenu();
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shade_bg);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		leftFragment = new LeftFragment();
		t.replace(R.id.ll_main, new MainFragment());
		t.replace(R.id.fl_menu_frame, new LeftFragment());
		t.commit();
	}

	public void switchContent(final Fragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// ft.setCustomAnimations(android.R.animator.fade_in,
		// android.R.animator.fade_out);
		ft.replace(R.id.ll_main, fragment).commit();
		getSlidingMenu().showContent();
	}

	public void menuClick(View v) {
		if (getSlidingMenu().isMenuShowing())
			getSlidingMenu().showContent();
		else
			getSlidingMenu().showMenu();
	}
}
