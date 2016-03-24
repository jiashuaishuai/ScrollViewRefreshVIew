package com.example.jiashuaishuai.myapplicationrefreshscrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private DropDownScrollView my_scroll;
    private ImageView img;
    private RelativeLayout rel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        my_scroll = (DropDownScrollView) findViewById(R.id.my_scroll);
        img = (ImageView) findViewById(R.id.img);
        rel = (RelativeLayout) findViewById(R.id.rly);


        my_scroll.setBackgroundImgAndStretchLayout(img, rel);
        my_scroll.setDropDownScrollViewPullDownListener(new DropDownScrollView.DropDownScrollViewPullDownListener() {
            @Override
            public void ScrollViewPullDown() {
                Toast.makeText(MainActivity.this, "刷新喽", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
