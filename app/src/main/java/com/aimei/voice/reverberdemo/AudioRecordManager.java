package com.aimei.voice.reverberdemo;

import android.content.Context;
import android.media.AudioRecord;
import android.util.Log;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AudioRecordManager {
    private static AudioRecordManager instance;

    public List<PCMData> getPcmList() {
        return pcmList;
    }

    private List<PCMData> pcmList;
    private boolean isStop = false;

    public static AudioRecordManager getInstance() {
        if (instance == null) {
            synchronized (AudioRecordManager.class) {
                if (instance == null)
                    instance = new AudioRecordManager();
            }
        }
        return instance;
    }

    private AudioRecord audioRecord;
    private int packagesize;
    private short[] tempBuffer;

    public AudioRecordManager init(int sampleRate, int channels, int nframes, int audioSource) {
        if (audioRecord != null)
            return this;
        pcmList = Collections.synchronizedList(new LinkedList<PCMData>());

        int bufferSize = AudioRecord.getMinBufferSize(
                sampleRate, channels,
                nframes);
        packagesize = 160;
        tempBuffer = new short[packagesize];
        audioRecord = new AudioRecord(audioSource,
                sampleRate, channels,
                nframes, bufferSize);
        return this;
    }


    public void startRecord(Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isStop = false;
                audioRecord.startRecording();
                long startTime = System.currentTimeMillis();
                long endTime = startTime;
                while (!isStop) {
                    int bufferRead = audioRecord
                            .read(tempBuffer, 0, packagesize);
                    if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                        throw new IllegalStateException(
                                "read() returned AudioRecord.ERROR_INVALID_OPERATION");
                    } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
                        throw new IllegalStateException(
                                "read() returned AudioRecord.ERROR_BAD_VALUE");
                    } else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                        throw new IllegalStateException(
                                "read() returned AudioRecord.ERROR_INVALID_OPERATION");
                    }
                    PCMData pcmData = new PCMData();
                    pcmData.size = bufferRead;
                    System.arraycopy(tempBuffer, 0, pcmData.pcmData, 0, bufferRead);
                    pcmData.time = (int) (endTime - startTime);
                    endTime = System.currentTimeMillis();
                    pcmList.add(pcmData);
                    Log.w("春哥", "录制数据 pcmData.time" + pcmData.time);
                }
                audioRecord.stop();
            }
        }).start();
    }


    public void stop() {
        isStop = true;
    }

    public void release() {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

}


