/*
 * 有用
 */
package com.bingo.hand.fragment;

import com.bingo.hand.MainActivity;
import com.bingo.hand.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class LeftFragment extends Fragment implements OnClickListener {
	LinearLayout ll_yaomai, ll_zhuye, ll_wode, ll_shichang, ll_xiaoxi,ll_shezhi,ll_fankui;
	TextView tv_title,tv_title_function;
	private MainActivity mainActivity;
	MainFragment mainFragment;
	PublishFragment publishFragment;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_left, null);
		ll_yaomai = (LinearLayout) view.findViewById(R.id.ll_yaomai);
		ll_zhuye = (LinearLayout) view.findViewById(R.id.ll_zhuye);
		ll_wode = (LinearLayout) view.findViewById(R.id.ll_wode);
		ll_shichang = (LinearLayout) view.findViewById(R.id.ll_shichang);
		ll_xiaoxi = (LinearLayout) view.findViewById(R.id.ll_xiaoxi);
		ll_shezhi = (LinearLayout) view.findViewById(R.id.ll_shezhi);
		ll_fankui = (LinearLayout) view.findViewById(R.id.ll_fankui);
		tv_title_function = (TextView) getActivity().findViewById(R.id.tv_title_function);
		ll_yaomai.setOnClickListener(this);
		ll_zhuye.setOnClickListener(this);
		ll_wode.setOnClickListener(this);
		ll_shichang.setOnClickListener(this);
		ll_xiaoxi.setOnClickListener(this);
		ll_fankui.setOnClickListener(this);
		ll_shezhi.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mainFragment = new MainFragment();
		publishFragment = new PublishFragment();
	}

	@Override
	public void onClick(View v) {
		mainActivity = (MainActivity) getActivity();
		tv_title = (TextView) mainActivity.findViewById(R.id.tv_title);
		if (v.getId() == R.id.ll_yaomai) {
			tv_title.setText("发布交易信息");
			tv_title_function.setText("");
			mainActivity.switchContent(publishFragment);
		} else if (v.getId() == R.id.ll_zhuye) {
			tv_title.setText("小葫芦");
			tv_title_function.setText("");
			mainActivity.switchContent(mainFragment);
		} else if (v.getId() == R.id.ll_wode) {
			tv_title.setText("个人信息");
			tv_title_function.setText("");
//			mainActivity.switchContent(personFragment);
		} else if (v.getId() == R.id.ll_shichang) {
			tv_title.setText("市场");
			tv_title_function.setText("");
//			mainActivity.switchContent(marketFragment);
		} else if(v.getId() == R.id.ll_xiaoxi)
		{
			tv_title.setText("消息");
			tv_title_function.setText("");
//			mainActivity.switchContent(messageFragment);
		}else if(v.getId() == R.id.ll_shezhi){
			tv_title.setText("设置");
			tv_title_function.setText("");
//			mainActivity.switchContent(settingFragment);
		}
		// mainActivity.showLeft();
	}
}
