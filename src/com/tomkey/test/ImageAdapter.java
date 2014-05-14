package com.tomkey.test;

import com.tomkey.scaleablescrollview.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageAdapter extends BaseAdapter {
	private final LinearLayout.LayoutParams LAYOUT_TALL = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
			LayoutParams.MATCH_PARENT);
	private final LinearLayout.LayoutParams LAYOUT_WIDE = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.MATCH_PARENT);
	private LayoutInflater mInflater;
	private Context context;
	Options opts = new Options();

	private int[] resIds = new int[] { R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4 };

	public ImageAdapter(Context context) {
		this.context = context;
		this.mInflater = ((LayoutInflater) context.getSystemService("layout_inflater"));
		opts.inJustDecodeBounds = true;
	}

	public int getCount() {
		return Integer.MAX_VALUE;
	}

	public Object getItem(int position) {
		return resIds[position % resIds.length];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.gallery_image, parent, false);
			holder = new Holder();
			holder.item = ((ImageView) convertView.findViewById(R.id.gallery_item_image_view));
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		BitmapFactory.decodeResource(context.getResources(), resIds[position % resIds.length], opts);
		if (opts.outWidth <= opts.outHeight) {
			holder.item.setLayoutParams(LAYOUT_TALL);
		} else {
			holder.item.setLayoutParams(LAYOUT_WIDE);
		}
		holder.item.setImageResource(resIds[position % resIds.length]);
		return convertView;
	}

	private class Holder {
		public ImageView item;

		private Holder() {
		}
	}

}
