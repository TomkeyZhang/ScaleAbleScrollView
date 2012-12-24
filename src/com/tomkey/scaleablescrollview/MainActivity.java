package com.tomkey.scaleablescrollview;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Gallery;

import com.tomkey.scaleablescrollview.widget.ScaleAbleController;

public class MainActivity extends Activity {
	Gallery gallery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		gallery = (Gallery) findViewById(R.id.images_gallery);
		gallery.setAdapter(new ImageAdapter(this));
		gallery.setSelection(10);
		new ScaleAbleController(this, R.id.scroll, R.id.images_gallery, R.id.txt_tv).init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
