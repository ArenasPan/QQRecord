package com.example.qqrecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Administrator on 2016/8/10.
 */
public class RecordLayout extends LinearLayout {

    private ImageView ivLeftVoice;
    private ImageView ivRightVoice;
    private TextView tvRecordTime;
    private Button btnRecord;
    private MyRoundProcess mProgressBar;


    private static final int MIN_RECORD_TIME = 1; // 最短录音时间，单位秒
    public static final int RECORD_OFF = 0; // 不在录音
    public static final int RECORD_ON = 1; // 正在录音
    public static final int RECORD_OVER = 2; //录音完毕
    public static final int RECORD_PLAYING = 3; //正在播放录音

//    private Dialog mRecordDialog;
    private RecordStrategy mAudioRecorder;
    private Thread mRecordThread;
    private RecordListener listener;

    private int recordState = 0; // 录音状态
    private float recodeTime = 0.0f; // 录音时长，如果录音时间太短则录音失败
    private double voiceValue = 0.0; // 录音的音量值
    private boolean isCanceled = false; // 是否取消录音
    private float downY;

    private TextView dialogTextView;
    private ImageView dialogImg;
    private Context mContext;

    private MediaPlayer mPlayer = null;
    private float playTime = 0.0f;
    private PlayThread playThread;

    // 录音线程
    private Runnable recordThread = new Runnable() {

        @Override
        public void run() {
            recodeTime = 0.0f;
            while (recordState == RECORD_ON) {
                {
                    try {
                        Thread.sleep(100);
                        recodeTime += 0.1;
                        // 获取音量，更新dialog
                        if (!isCanceled) {
                            voiceValue = mAudioRecorder.getAmplitude();
                            recordHandler.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            setDialogImage();
            switch (msg.what) {
                //更新录音状态
                case 1:
                    updateRecordStatus();
                    break;
                //更新播放状态
                case 2:
                    updatePlayStatus();
                    break;
            }

        }
    };

    public RecordLayout(Context context) {
        super(context);
        init(context);
    }

    public RecordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecordLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_record, this, true);
        ivLeftVoice = (ImageView) findViewById(R.id.iv_left_voice);
        ivRightVoice = (ImageView) findViewById(R.id.iv_right_voice);
        tvRecordTime = (TextView) findViewById(R.id.tv_record_time);
        btnRecord = (Button) findViewById(R.id.btn_record);
        mProgressBar = (MyRoundProcess) findViewById(R.id.progress_bar_round);
        mProgressBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recordState == RECORD_PLAYING) {
                    stopPlayRecord();
                }
            }
        });
        btnRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (recordState) {
                    case RECORD_OFF:
                        startRecord();
                        break;
                    case RECORD_ON:
                        stopRecord();
                        break;
                    case RECORD_OVER:
                        playRecord();
                        break;
                    case RECORD_PLAYING:
                        stopPlayRecord();
                        break;
                }
            }
        });
    }

    public void setAudioRecord(RecordStrategy record) {
        this.mAudioRecorder = record;
    }

    public void setRecordListener(RecordListener listener) {
        this.listener = listener;
    }

    // 开启录音计时线程
    private void callRecordTimeThread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }

    private synchronized void updateRecordStatus() {
        int time = (int) recodeTime;
        tvRecordTime.setVisibility(VISIBLE);
        tvRecordTime.setText(String.valueOf(time));
    }

    public interface RecordListener {
        public void recordEnd(String filePath);
    }

    private void handleUp() {

    }

    public void startRecord() {
//        this.setBackgroundResource(R.drawable.record_cancel);
        btnRecord.setBackgroundResource(R.drawable.record_cancel);
//        showVoiceDialog(0);
        if (mAudioRecorder != null) {
            mAudioRecorder.ready();
            recordState = RECORD_ON;
            mAudioRecorder.start();
            callRecordTimeThread();
        }
    }

    public void stopRecord() {
        if (recordState == RECORD_ON) {
            recordState = RECORD_OVER;
//            this.setBackgroundResource(R.mipmap.ic_launcher);
            btnRecord.setBackgroundResource(R.mipmap.ic_launcher);
            mAudioRecorder.stop();
            mRecordThread.interrupt();
            voiceValue = 0.0;
            if (isCanceled) {
                mAudioRecorder.deleteOldFile();
            } else {
                if (recodeTime < MIN_RECORD_TIME) {
                    showWarnToast("时间太短  录音失败");
                    mAudioRecorder.deleteOldFile();
                    listener.recordEnd("");
                } else {
                    if (listener != null) {
                        listener.recordEnd(mAudioRecorder.getFilePath());
                    }
                }
            }
            isCanceled = false;
            btnRecord.setText("点击播放");
        }
    }

    private void playRecord() {
        mPlayer = new MediaPlayer();
        try {
            btnRecord.setVisibility(GONE);
            mProgressBar.setVisibility(VISIBLE);
//            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //设置要播放的文件
            mPlayer.setDataSource(mAudioRecorder.getFilePath());
            mPlayer.prepare();
            //播放之
            mPlayer.start();
            btnRecord.setText("正在播放");
            recordState = RECORD_PLAYING;
            playThread = new PlayThread();
            playThread.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    recordState = RECORD_OVER;
                    mPlayer.release();
                    int time = (int) recodeTime;
                    btnRecord.setVisibility(VISIBLE);
                    mProgressBar.setVisibility(GONE);
                    mProgressBar.setProgress(0f);
                    tvRecordTime.setVisibility(VISIBLE);
                    tvRecordTime.setText(String.valueOf(time));
                    playTime = 0;
                    btnRecord.setText("点击播放");
                }
            });
        } catch (IOException e) {
            Log.e("PlayRecord", "prepare() failed");
        }
    }

    public int getStatus() {
        return recordState;
    }

    // 录音时间太短时Toast显示
    private void showWarnToast(String toastText) {
        Toast toast = new Toast(mContext);
        View warnView = LayoutInflater.from(mContext).inflate(
                R.layout.toast_warn, null);
        toast.setView(warnView);
        toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public class PlayThread extends Thread{
        @Override
        public void run() {
            while (recordState == RECORD_PLAYING) {
                playTime += 0.1;
                recordHandler.sendEmptyMessage(2);
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void updatePlayStatus() {
        int time = (int) playTime;
        tvRecordTime.setText(String.valueOf(time));
        float progress = playTime / recodeTime;
//        Log.e("progress", String.valueOf(progress*100));
        mProgressBar.setProgress(progress*100);
    }

    public void stopPlayRecord() {
        recordState = RECORD_OVER;
        mPlayer.stop();
        mPlayer.release();
        playThread.interrupt();
        int time = (int) recodeTime;
        tvRecordTime.setVisibility(VISIBLE);
        tvRecordTime.setText(String.valueOf(time));
        playTime = 0.0f;
        btnRecord.setText("点击播放");
        btnRecord.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mProgressBar.setProgress(0f);
    }

}
