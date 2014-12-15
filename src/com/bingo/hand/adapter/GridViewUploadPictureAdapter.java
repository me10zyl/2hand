package com.bingo.hand.adapter;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bingo.hand.R;


/**
 * MVC Model 数据模型 persons,View 视图 lv,Controller 控制器 myAdpter
 * 
 * @author ZyL
 * 
 */
public class GridViewUploadPictureAdapter extends BaseAdapter {

	private List<Bitmap> arr;
	private Context context;
	private LayoutInflater inflater;
	public GridViewUploadPictureAdapter(Context context,
			List<Bitmap> arr) {
		super();
		this.context = context;
		this.arr = arr;
		inflater = LayoutInflater.from(context);
	}

	/**
	 * 返回条目的总数
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr.size();
	}


	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return arr.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * Get a View that displays the data at the specified position in the data
	 * set 展现一个数据集在某一个位置的视图 有新的view要显示才会调用getView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = inflater.inflate(R.layout.item_gridview_uploadpic, null);
		ImageView img = (ImageView)v.findViewById(R.id.iv_uploadpicture);
		img.setImageBitmap(arr.get(position));
		return v;
	}

}
