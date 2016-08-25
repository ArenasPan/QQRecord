package com.example.qqrecord;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioRecorder implements RecordStrategy {

    private MediaRecorder recorder;
    private String fileName;
    private String fileFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/TestRecord";

    private boolean isRecording = false;

    private MediaPlayer mPlayer = null;

    @Override
    public void ready() {
        // TODO Auto-generated method stub
        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdir();
        }
//        fileName = getCurrentDate();
        fileName = "jz100Record";
        recorder = new MediaRecorder();
        recorder.setOutputFile(fileFolder + "/" + fileName + ".mp4");
        Log.i("outputFile", fileFolder + "/" + fileName + ".mp4");
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置MediaRecorder的音频源为麦克风
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 设置MediaRecorder录制的音频格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 设置MediaRecorder录制音频的编码为MP4
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置MediaRecorder录制音频的编码为amr
    }

    // 以当前时间作为文件名
    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        if (!isRecording) {
            try {
                recorder.prepare();
                recorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isRecording = true;
        }

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        if (isRecording) {
            recorder.stop();
            recorder.release();
            isRecording = false;
        }

    }

    @Override
    public void deleteOldFile() {
        // TODO Auto-generated method stub
        File file = new File(fileFolder + "/" + fileName + ".mp4");
        file.deleteOnExit();
    }

    @Override
    public double getAmplitude() {
        // TODO Auto-generated method stub
        if (!isRecording) {
            return 0;
        }
        return recorder.getMaxAmplitude();
    }

    @Override
    public String getFilePath() {
        // TODO Auto-generated method stub
        return fileFolder + "/" + fileName + ".mp4";
    }

//    @Override
//    public void playRecord() {
//        mPlayer = new MediaPlayer();
//        try {
//            //设置要播放的文件
//            mPlayer.setDataSource(getFilePath());
//            mPlayer.prepare();
//            //播放之
//            mPlayer.start();
//            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//
//                }
//            });
//        } catch (IOException e) {
//            Log.e("Record test", "prepare() failed");
//        }
//    }
//
//    @Override
//    public void playStart() {
//
//    }
//
//    @Override
//    public void playEnd() {
//
//    }

}
