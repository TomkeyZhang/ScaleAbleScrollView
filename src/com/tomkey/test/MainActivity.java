package com.tomkey.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Gallery;

import com.anjuke.library.uicomponent.scaleablescrollview.ScaleAbleController;
import com.anjuke.library.uicomponent.scaleablescrollview.ScaleAbleController.ScaleListener;
import com.tomkey.scaleablescrollview.R;

public class MainActivity extends Activity implements ScaleListener {
	Gallery gallery;
    ScaleAbleController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		gallery = (Gallery) findViewById(R.id.images_gallery);
		gallery.setAdapter(new ImageAdapter(this));
		gallery.setSelection(10);
        controller = new ScaleAbleController(this, R.id.scroll, R.id.images_gallery, R.id.txt_tv, true);
        controller.setScaleListener(this);
        controller.init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

    @Override
    public void onReachStart() {
        Log.d("zqt", "onReachStart");
    }

    @Override
    public void onReachEnd() {
        Log.d("zqt", "onReachEnd");
    }

    @Override
    public void onScale(int height) {
        Log.d("zqt", "onScale height=" + height);
    }

}
