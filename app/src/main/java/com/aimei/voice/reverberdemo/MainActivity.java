package com.aimei.voice.reverberdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.BassBoost;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 2020-3-20
 * xingchun
 */
public class MainActivity extends AppCompatActivity {

    private Button recordOrStop;

    private TextureView textureview;

    private Song currentSong;
    private List<Song> songList;

    private ScrollView ll_scroll_view;

    private MyVisualizerView my_visualizer_view;

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
            android.Manifest.permission.RECORD_AUDIO};

    private static int REQUEST_PERMISSION_CODE = 3;

    private MediaPlayer mediaplayer;
    private int meidaPlayerSatus;
    private static final int INIT = 1;
    private static final int PREPANING = 2;
    private static final int PREPARED = 3;
    private static final int STARTERD = 4;
    private static final int PAUSED = 5;
    private static final int STOPPED = 6;
    private static final int COMPLETED = 7;
    private static final int RELEASE = 8;
    private Uri uri;
    private BubbleSeekBar seekBar1;
    private BubbleSeekBar seekBar2;
    private BubbleSeekBar seekBar3;
    private BubbleSeekBar seekBar4;
    private BubbleSeekBar seekBar5;
    private BubbleSeekBar seekBar6;
    private BubbleSeekBar seekBar7;
    private BubbleSeekBar seekBar8;
    private BubbleSeekBar seekBar9;
    private Spinner spinner_sound_field;
    private LinearLayout ll_layout;
    private MyVisualizerView myVisualizerView;
    private List<Short> reverbValues = new ArrayList();
    private List<String> reverbNames = new ArrayList();
    private EnvironmentalReverb environmentalReverb;
    private Equalizer equalizer;
    private PresetReverb presetReverb;
    private BassBoost bassBoost;
    private Surface surface;
    private int seek = -1;


    private int sampleRate = 14400;
    private int record_channels = AudioFormat.CHANNEL_IN_MONO;
    private int nframes = AudioFormat.ENCODING_PCM_16BIT;
    private int audioSource = MediaRecorder.AudioSource.MIC;
    private int play_channels = AudioFormat.CHANNEL_OUT_MONO;
    private int streamType = android.media.AudioManager.STREAM_MUSIC;
    private int creationMode = AudioTrack.MODE_STREAM;

    private boolean hasInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openPermission(this);
        initView();
        initSongs();
        initVolume();
        inityingui();
        initRecordButton();
        initMediaPlayer(currentSong.getRes());
        initSpinner();
        initReverberUI();
    }

    private void initView() {
        seekBar9 = findViewById(R.id.seekbar9);
        textureview = findViewById(R.id.textureview);
        ll_layout = findViewById(R.id.ll_layout);
        ll_scroll_view = findViewById(R.id.ll_scroll_view);
        ll_scroll_view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                seekBar1.correctOffsetWhenContainerOnScrolling();
                seekBar2.correctOffsetWhenContainerOnScrolling();
                seekBar3.correctOffsetWhenContainerOnScrolling();
                seekBar4.correctOffsetWhenContainerOnScrolling();
                seekBar5.correctOffsetWhenContainerOnScrolling();
                seekBar6.correctOffsetWhenContainerOnScrolling();
                seekBar7.correctOffsetWhenContainerOnScrolling();
                seekBar8.correctOffsetWhenContainerOnScrolling();
                seekBar9.correctOffsetWhenContainerOnScrolling();
                for (int i = 0; i < ll_layout.getChildCount(); i++) {
                    if (ll_layout.getChildAt(i) instanceof LinearLayout) {
                        LinearLayout linearLayout = (LinearLayout) ll_layout.getChildAt(i);
                        for (int j = 0; j < linearLayout.getChildCount(); j++) {
                            if (linearLayout.getChildAt(j) instanceof BubbleSeekBar) {
                                ((BubbleSeekBar) linearLayout.getChildAt(j)).correctOffsetWhenContainerOnScrolling();
                            }
                        }

                    }
                }


            }
        });
    }

    private void initVolume() {
        final SeekBar seekBar_music_left = findViewById(R.id.music_bg_left);
        final SeekBar seekBar_music_right = findViewById(R.id.music_bg_right);


        seekBar_music_left.setMin(0);
        seekBar_music_left.setMax(100);
        seekBar_music_left.setProgress(50);

        seekBar_music_right.setMin(0);
        seekBar_music_right.setMax(100);
        seekBar_music_right.setProgress(50);
        seekBar_music_left.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaplayer.setVolume(progress / 100.0f, seekBar_music_right.getProgress() / 100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_music_right.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaplayer.setVolume(seekBar_music_left.getProgress() / 100.0f, progress / 100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void inityingui() {
        findViewById(R.id.but_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = mediaplayer.getSelectedTrack(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO);
                Log.w("春哥", "音轨 i=" + i);
                if (i == 1) {
                    mediaplayer.selectTrack(2);
                } else {
                    mediaplayer.selectTrack(1);
                }

            }
        });
    }

    private void initRecordButton() {
        recordOrStop = findViewById(R.id.but_1);
        recordOrStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("录制".equals(recordOrStop.getText())) {
                    try {
                        if (!hasInit) {
                            AudioRecordManager.getInstance().init(sampleRate, record_channels, nframes, audioSource);
                            environmentalReverb = AudioTrackManager.getInstance()
                                    .initAndroidAudio(sampleRate, play_channels, streamType, nframes, creationMode)
                                    .getEnvironmentalReverb();
                            setInitValue(environmentalReverb);
                            setupEqualizer(AudioTrackManager.getInstance().getTrack());
                            setupBassBoost(AudioTrackManager.getInstance().getTrack());
                            initSpinnerSoundField(AudioTrackManager.getInstance().getTrack());
//                        initVisualizerView(AudioTrackManager.getInstance().getTrack());
                            hasInit = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    recordOrStop.setText("录制中");
                    AudioRecordManager.getInstance().startRecord(MainActivity.this);
                    AudioTrackManager.getInstance().play();
                } else if ("录制中".equals(recordOrStop.getText())) {
                    recordOrStop.setText("录制");
                    AudioRecordManager.getInstance().stop();
                    AudioTrackManager.getInstance().stop();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        play();
        super.onResume();
    }

    public void openPermission(Activity obj) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
                if (ActivityCompat.checkSelfPermission(obj,
                        PERMISSIONS_STORAGE[i]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(obj, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                }
            }
        }
    }

    public void setupEqualizer(AudioTrack audioTrack) {
        ll_layout.removeAllViews();
        equalizer = new Equalizer(0, audioTrack.getAudioSessionId());
        equalizer.setEnabled(true);
        TextView eqTitle = new TextView(this);
        eqTitle.setText("均衡器：");
        ll_layout.addView(eqTitle);
        //获取均衡器控制器支持最小值和最大值
        final short minEQLevel = equalizer.getBandLevelRange()[0];
        short maxEQLevel = equalizer.getBandLevelRange()[1];

        //获取均衡器控制器支持的所有频率
        short brands = equalizer.getNumberOfBands();
        for (short i = 0; i < brands; i++) {
            TextView eqTextView = new TextView(this);
            eqTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            eqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            eqTextView.setText((equalizer.getCenterFreq(i) / 1000) + "Hz");
            ll_layout.addView(eqTextView);

            LinearLayout tmpLayout = new LinearLayout(this);
            tmpLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            minDbTextView.setText((minEQLevel / 100) + "dB");

            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxEQLevel / 100) + "dB");

            BubbleSeekBar bar = new BubbleSeekBar(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            bar.setLayoutParams(layoutParams);
            bar.getConfigBuilder().max(maxEQLevel - minEQLevel).build();
            bar.setProgress(equalizer.getBandLevel(i));
            final short brand = i;
            bar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                    // TODO Auto-generated method stub
                    equalizer.setBandLevel(brand, (short) (progress + minEQLevel));
                }

                @Override
                public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

                }

                @Override
                public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

                }
            });

            //使用水平排列组件的LinearLayout盛装三个组件
            tmpLayout.addView(minDbTextView);
            tmpLayout.addView(bar);
            tmpLayout.addView(maxDbTextView);
            ll_layout.addView(tmpLayout);
        }
    }

    private void initVisualizerView(AudioTrack audioTrack) {
        myVisualizerView = findViewById(R.id.my_visualizer_view);
        Visualizer visualizer = new Visualizer(audioTrack.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {

            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
                                              int samplingRate) {
                myVisualizerView.updateVisualizer(waveform);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft,
                                         int samplingRate) {
                // TODO Auto-generated method stub

            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
        visualizer.setEnabled(true);
    }

    private void initSongs() {
        songList = new ArrayList<>();
        songList.add(new Song("成都", R.raw.chengdu));
        songList.add(new Song("突然的自我", R.raw.turandeziwo));
        songList.add(new Song("小芳", R.raw.xiaofang));
        currentSong = songList.get(0);
    }

    private void initSpinner() {

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter(MainActivity.this, R.layout.item_spinner_song, songList));
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSong = songList.get(position);
                nextPlay(currentSong.getRes());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    private void initSpinnerSoundField(AudioTrack audioTrack) {
        spinner_sound_field = findViewById(R.id.spinner_sound_field);

        presetReverb = new PresetReverb(0, audioTrack.getAudioSessionId());
        presetReverb.setEnabled(true);


        for (short i = 0; i < equalizer.getNumberOfPresets(); i++) {
            reverbValues.add(i);
            reverbNames.add(equalizer.getPresetName(i));
        }


        spinner_sound_field.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, reverbNames));
        spinner_sound_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                try {
                    presetReverb.setPreset(reverbValues.get(position));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, reverbNames.get(position) + "音场无效", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    public void setupBassBoost(AudioTrack audioTrack) {
        bassBoost = new BassBoost(0, audioTrack.getAudioSessionId());
        bassBoost.setEnabled(true);
        seekBar9.getConfigBuilder().min(0).max(1000).build();
        seekBar9.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                bassBoost.setStrength((short) progress);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
    }

    private void setInitValue(EnvironmentalReverb environmentalReverb) {
        seekBar1.setProgress(environmentalReverb.getDecayHFRatio());
        seekBar1.setTooltipText(environmentalReverb.getDecayHFRatio() + "");
        seekBar2.setProgress(environmentalReverb.getDecayTime());
        seekBar3.setProgress(environmentalReverb.getDensity());
        seekBar4.setProgress(environmentalReverb.getDiffusion());
        seekBar5.setProgress(environmentalReverb.getReflectionsDelay());
        seekBar6.setProgress(environmentalReverb.getReverbLevel());
        seekBar7.setProgress(environmentalReverb.getRoomLevel());
        seekBar8.setProgress(environmentalReverb.getRoomHFLevel());

    }

    private void initReverberUI() {
        //设置高频衰减时间(5khz)与低频衰减时间的比值
        //频率
        seekBar1 = findViewById(R.id.seekbar1);
        seekBar1.getConfigBuilder().min(100).max(2000).build();
        seekBar1.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if (environmentalReverb != null) {
                    environmentalReverb.setDecayHFRatio((short) progress);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        //设置混响电平衰减60 dB所需的时间。
        seekBar2 = findViewById(R.id.seekbar2);
        seekBar2.getConfigBuilder().min(100).max(20000).build();
        seekBar2.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if (environmentalReverb != null) {
                    environmentalReverb.setDecayTime((short) progress);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        //控制后期混响衰减的模态密度
        seekBar3 = findViewById(R.id.seekbar3);
        seekBar3.getConfigBuilder().min(0).max(1000).build();
        seekBar3.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if (environmentalReverb != null) {
                    environmentalReverb.setDensity((short) progress);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        //设置后期混响衰减中的回声密度
        seekBar4 = findViewById(R.id.seekbar4);
        seekBar4.getConfigBuilder().min(0).max(1000).build();
        seekBar4.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if (environmentalReverb != null) {
                    environmentalReverb.setDiffusion((short) progress);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        //设置早期反射的延迟时间
        seekBar5 = findViewById(R.id.seekbar5);
        seekBar5.getConfigBuilder().min(0).max(300).build();
        seekBar5.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if (environmentalReverb != null) {
                    environmentalReverb.setReflectionsDelay((short) progress);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        //设置后期混响的音量
        seekBar6 = findViewById(R.id.seekbar6);
        seekBar6.getConfigBuilder().min(-9000).max(2000).build();
        seekBar6.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if (environmentalReverb != null) {
                    environmentalReverb.setReverbLevel((short) progress);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        //设置环境混响效果的主音量级别
        seekBar7 = findViewById(R.id.seekbar7);
        seekBar7.getConfigBuilder().min(-9000).max(0).build();
        seekBar7.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if (environmentalReverb != null) {
                    environmentalReverb.setRoomLevel((short) progress);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        //将音量级别设置为相对于低频扬声器的音量级别为5 kHz整体混响效果
        seekBar8 = findViewById(R.id.seekbar8);
        seekBar8.getConfigBuilder().min(-9000).max(0).build();
        seekBar8.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if (environmentalReverb != null) {
                    environmentalReverb.setRoomHFLevel((short) progress);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
    }

    private String getUri(int res) {
        String mVideoUrl = "android.resource://" + getPackageName() + "/" + res;
        return mVideoUrl;
    }

    private void initMediaPlayer(int res) {
        String mVideoUrl = getUri(res);
        meidaPlayerSatus = INIT;
        textureview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                surface = new Surface(surfaceTexture);
                mediaplayer.setSurface(surface);
                play();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (mediaplayer != null && mediaplayer.isPlaying()) {
                    seek = mediaplayer.getCurrentPosition();
                    meidaPlayerSatus = PAUSED;
                    mediaplayer.pause();
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        if (uri == null)
            uri = Uri.parse(mVideoUrl);
        try {
            mediaplayer = new MediaPlayer();
            initPlay(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPlay(Uri uri) {
        try {
            mediaplayer.setDataSource(this, uri);
            mediaplayer.setLooping(true);
            mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    meidaPlayerSatus = PREPARED;
                    play();
                }
            });
            mediaplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    if (what == -38) {
                        mp.reset();
                        initMediaPlayer(currentSong.getRes());
                        return true;
                    }
                    return false;
                }
            });
            mediaplayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {

                    return false;
                }
            });
            mediaplayer.prepareAsync();
            meidaPlayerSatus = PREPANING;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextPlay(int res) {
        uri = Uri.parse(getUri(res));
        if (mediaplayer != null && mediaplayer.isPlaying()) {
            try {
                mediaplayer.pause();
                mediaplayer.reset();
                initPlay(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void play() {
        try {
            if (meidaPlayerSatus == PREPARED || meidaPlayerSatus == PAUSED) {
                if (seek != -1) {
                    mediaplayer.seekTo(seek);
                }
                if (mediaplayer != null)
                    mediaplayer.start();
                meidaPlayerSatus = STARTERD;
            } else if (meidaPlayerSatus == STOPPED) {
                mediaplayer.prepareAsync();
                meidaPlayerSatus = PREPANING;
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onPause() {
        if (mediaplayer != null && mediaplayer.isPlaying()) {
            seek = mediaplayer.getCurrentPosition();
            meidaPlayerSatus = PAUSED;
            mediaplayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mediaplayer != null) {
            meidaPlayerSatus = STOPPED;
            mediaplayer.stop();
        }
        recordOrStop.setText("录制");
        AudioRecordManager.getInstance().stop();
        AudioTrackManager.getInstance().stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mediaplayer != null) {
            mediaplayer.release();
            mediaplayer = null;
        }
        if (environmentalReverb != null)
            environmentalReverb.release();
        if (equalizer != null)
            equalizer.release();
        if (bassBoost != null)
            bassBoost.release();
        AudioRecordManager.getInstance().release();
        AudioTrackManager.getInstance().release();
        super.onDestroy();
    }


}
