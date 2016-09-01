package com.example.qqrecord;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivShowRecord;
    private RecordCircleView ivStartRecord;

    private LinearLayout layoutRecord;
    private RelativeLayout allLayout;

    private MediaPlayer mMediaPlayer;
    private int duration;


    private boolean isPlaying = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        ivShowRecord = (ImageView) findViewById(R.id.iv_video);
        ivStartRecord = (RecordCircleView) findViewById(R.id.record_icon);
        layoutRecord = (LinearLayout) findViewById(R.id.layout_record);
        allLayout = (RelativeLayout) findViewById(R.id.all_layout);
        allLayout.setOnClickListener(this);
        ivShowRecord.setOnClickListener(this);
        ivStartRecord.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_video:
                if (layoutRecord.getVisibility() == View.GONE) {
                    layoutRecord.setVisibility(View.VISIBLE);
                } else {
                    layoutRecord.setVisibility(View.GONE);
                }
                break;
            case R.id.record_icon:
                Intent intent = new Intent(this, RecordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.all_layout:
                if (layoutRecord.getVisibility() == View.VISIBLE) {
                    layoutRecord.setVisibility(View.GONE);
                }
                break;
        }
    }
}
