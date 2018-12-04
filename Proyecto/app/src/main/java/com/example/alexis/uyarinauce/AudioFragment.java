package com.example.alexis.uyarinauce;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFragment extends Fragment {

    private Button btnHablar, btnEscuchar;

    static final int bufferSize = 200000;
    final short[] buffer = new short[bufferSize];
    short[] readBuffer = new short[bufferSize];
    private AudioRecord arec = null;
    private Boolean isRecording;
    private Boolean eco=false;
    boolean ns =false;
    AcousticEchoCanceler aec;
    AudioTrack atrack;
    AudioManager localAudioManager;

    /*private String file= null;
    private MediaRecorder grabar;
    MediaPlayer mediaPlayer = new MediaPlayer();*/
    public AudioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_audio,container,false);
        btnHablar = (Button) vista.findViewById(R.id.btnHablar);
        btnEscuchar = (Button) vista.findViewById(R.id.btnEscuchar);

        btnHablar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reproducir();
            }
        });

        btnEscuchar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording = true){
                    arec.stop();
                    atrack.stop();
                }else {
                    Toast.makeText(getContext(),"PRESIONE HABLAR ...",Toast.LENGTH_LONG).show();
                }
            }
        });

        return vista;
    }

    public void reproducir(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRecording = true;
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                int buffersize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
                arec = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffersize);
               

                atrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffersize, AudioTrack.MODE_STREAM);
                atrack.setPlaybackRate(44100);
                byte[] buffer = new byte[buffersize];
                arec.startRecording();
                atrack.play();
                Context context = getContext();
                localAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                //  localAudioManager.setSpeakerphoneOn(true);
                while(isRecording) {
                    arec.read(buffer, 0, buffersize);
                    atrack.write(buffer, 0, buffer.length);
                }
            }
        }).start();
    }

}
