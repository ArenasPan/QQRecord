package com.example.qqrecord;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.Date;

public class RecordActivity extends Activity implements View.OnClickListener {
    private LinearLayout bottomLayout;
    private View bottomLine;
    private Button btnCancel;
    private Button btnSend;
//    private RecordButton ivStop;

    private RecordLayout btnRecord;

    private static final int START_RECORD = 1;

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1001;

    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_RECORD:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }else{
            //
            initView();
        }



        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(this, true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(R.color.colorPrimary);

    }

    private void initView() {
        btnCancel = (Button) findViewById(R.id.cancel);
        btnSend = (Button) findViewById(R.id.send);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        bottomLine = findViewById(R.id.bottom_line);
        btnRecord = (RecordLayout) findViewById(R.id.record_layout);
        btnRecord.setAudioRecord(new AudioRecorder());
        btnRecord.startRecord();
        btnRecord.setRecordListener(new RecordLayout.RecordListener() {
            @Override
            public void recordEnd(String filePath) {
                if (TextUtils.isEmpty(filePath)) {
                    finish();
                } else {
                    bottomLayout.setVisibility(View.VISIBLE);
                    bottomLine.setVisibility(View.VISIBLE);
                }
            }
        });
//        ivStop = (RecordButton) findViewById(R.id.btn_record);
//        ivStop.setAudioRecord(new AudioRecorder());
//        ivStop.setRecordListener(new RecordButton.RecordListener() {
//            @Override
//            public void recordEnd(String filePath) {
//                Toast.makeText(RecordActivity.this,filePath,Toast.LENGTH_SHORT).show();
//            }
//        });
//        ivStop.startRecord();
//        mHandler.sendEmptyMessageDelayed(START_RECORD, 100);
        bottomLayout.setVisibility(View.INVISIBLE);
        bottomLine.setVisibility(View.INVISIBLE);

        btnCancel.setOnClickListener(this);
        btnSend.setOnClickListener(this);
//        ivStop.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.send:
                Toast.makeText(this,"上传",Toast.LENGTH_SHORT).show();
                break;
//            case R.id.btn_record:
//                if (ivStop.getStatus() == RecordButton.RECORD_ON) {
//                    ivStop.stopRecord();
//                    ivStop.setBackgroundResource(R.mipmap.delete_img);
//                } else if (ivStop.getStatus() == RecordButton.RECORD_OVER) {
//                    Toast.makeText(this,"播放",Toast.LENGTH_SHORT).show();
//                }
//                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initView();
            } else
            {
                // Permission Denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        clearRecord();
    }

    private void clearRecord(){
        delAllFile(Environment.getExternalStorageDirectory()
                .getPath() + "/TestRecord");
    }

    /**
     * 删除指定文件夹下的所有文件
     *
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                long lastModeify = temp.lastModified();
                long current = new Date().getTime();
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                flag = true;
            }
        }
        return flag;
    }
}
