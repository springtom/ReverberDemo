package com.aimei.voice.reverberdemo;

import android.media.AudioTrack;
import android.media.audiofx.EnvironmentalReverb;
import android.util.Log;

public class AudioTrackManager {
    private static AudioTrackManager instance;

    public static AudioTrackManager getInstance() {
        if (instance == null) {
            synchronized (AudioTrackManager.class) {
                if (instance == null)
                    instance = new AudioTrackManager();
            }
        }
        return instance;
    }

    public AudioTrack getTrack() {
        return track;
    }

    private AudioTrack track;

    public AudioTrackManager initAndroidAudio(int sampleRete, int channels, int streamType, int nframes, int creationMode) throws Exception {
        if (track != null)
            return this;
        int minBufferSize = AudioTrack.getMinBufferSize(
                sampleRete, channels,
                nframes);
        if (minBufferSize < 0) {
            throw new Exception("Failed to get minimum buffer size: "
                    + Integer.toString(minBufferSize));
        }

        track = new AudioTrack(streamType,
                sampleRete, channels,
                nframes, minBufferSize,
                creationMode);
        return this;
    }


    public PCMData getPCMData() {
        if (AudioRecordManager.getInstance().getPcmList() != null && AudioRecordManager.getInstance().getPcmList().size() > 0)
            return AudioRecordManager.getInstance().getPcmList().remove(0);
        return null;
    }

    private boolean stop = false;

    public void play() {
        stop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop) {
                    PCMData pcmData;
                    if ((pcmData = getPCMData()) != null) {
                        track.write(pcmData.pcmData, 0, pcmData.size);
                        track.setStereoVolume(0.7f, 0.7f);
                        track.play();
                        Log.w("春哥", "播放数据 pcmData.time" + pcmData.time);
                    } else {
//                        try {
//                            Thread.sleep(20);
//                        } catch (Exception e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                        continue;
                    }
                }
                track.stop();

            }
        }).start();
    }

    public void stop() {
        stop = true;
    }

    public void release() {
        if (track != null) {
            track = null;
        }
    }

    EnvironmentalReverb environmentalReverb;

    public EnvironmentalReverb getEnvironmentalReverb() {
        if (environmentalReverb != null)
            return environmentalReverb;
        environmentalReverb = new EnvironmentalReverb(0, track.getAudioSessionId());
        environmentalReverb.setEnabled(true);
        return environmentalReverb;
    }


}
