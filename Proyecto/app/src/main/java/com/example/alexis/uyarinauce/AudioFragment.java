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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
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

        btnHablar.setEnabled(true);
        btnEscuchar.setEnabled(false);
        btnHablar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnHablar.setEnabled(false);
                btnEscuchar.setEnabled(true);
                reproducir();
            }
        });

        btnEscuchar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording == true){
                    btnHablar.setEnabled(true);
                    btnEscuchar.setEnabled(false);
                    System.out.println("fiiiiiiiin");
                    isRecording=false;
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
                if(ns){
                    System.out.println("holllllllaaaaaaa");
                    localAudioManager.setParameters("noise_suppression=off");

                    ns=!ns;
                }
                isRecording = true;
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                //CHANNEL_IN_STEREO
                int buffersize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
                arec = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, buffersize);

                //STREAM_VOICE_CALL
                atrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, buffersize, AudioTrack.MODE_STREAM);
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
