package com.example.alexis.uyarinauce;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SoundFragment extends Fragment {
    private TextView volumenDerecho;
    private TextView volumenIzquierdo;
    SeekBar seekBarDerecho;
    SeekBar seekBarIzquierdo;

    AudioManager audioManager;
    SoundPool pool;
    SoundPool.Builder sBuilder;
    Button record;
    Button parar;
    Thread hilo;


    static final int bufferSize = 200000;
    final short[] buffer = new short[bufferSize];
    short[] readBuffer = new short[bufferSize];
    private AudioRecord arec1 = null;
    private Boolean isRecording1;
    private Boolean eco1 = false;
    boolean ns = false;
    AcousticEchoCanceler aec1;
    AudioTrack atrack1;
    AudioManager localAudioManager1;
    public SoundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_sound, container, false);

        volumenDerecho = (TextView) vista.findViewById(R.id.txt_volumen_derecho);
        volumenIzquierdo = (TextView) vista.findViewById(R.id.txt_volumen_izquierdo);
        seekBarDerecho = (SeekBar) vista.findViewById(R.id.seekBar_derecho);

        seekBarIzquierdo = (SeekBar) vista.findViewById(R.id.seekBar_izquierdo);

        record = (Button) vista.findViewById(R.id.btn_record_audio);
        parar = (Button) vista.findViewById(R.id.btn_parar_record);
        Context context = getContext();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        record.setEnabled(true);
        parar.setEnabled(false);
        setearVolumenDerecho();
        setearVolumenIzquierdo();
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record.setEnabled(false);
                parar.setEnabled(true);
                reproducir();
            }
        });

        parar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording1 == true){
                    record.setEnabled(true);
                    parar.setEnabled(false);
                    isRecording1=false;
                    arec1.stop();
                    atrack1.stop();
                }else {
                    Toast.makeText(getContext(),"PRESIONE HABLAR ...",Toast.LENGTH_LONG).show();
                }
            }
        });
        return vista;
    }

    private void setearVolumenDerecho() {
       // seekBarDerecho.setMax(AudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBarDerecho.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumenDerecho.setText("0");
        seekBarDerecho.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volumenDerecho.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void setearVolumenIzquierdo() {
       // audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBarIzquierdo.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumenIzquierdo.setText("0");
        seekBarIzquierdo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volumenIzquierdo.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }



    // manejo del seekBar para el control de volumen
    private void controlVolumen(final AudioTrack track) {

    }
    /*
    private void initComponentes() {
        volumenDerecho = (TextView) findViewById(R.id.txt_volumen_derecho);
        volumenIzquierdo = (TextView) findViewById(R.id.txt_volumen_izquierdo);
        seekBarDerecho = (SeekBar) findViewById(R.id.seekBar_derecho);
        seekBarIzquierdo = (SeekBar) findViewById(R.id.seekBar_izquierdo);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        record = (Button) findViewById(R.id.btn_record_audio);
        parar = (Button) findViewById(R.id.btn_parar_record);

    }
    */
    //manejo del audio record para realizar el streaming  al microfono


    /******************************************************************SONIDO**************************************************************************/
    //Reproducir
    public void reproducir() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                isRecording1 = true;
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                int buffersize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
                arec1 = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffersize);


                //AudioFormat.CHANNEL_CONFIGURATION_MONO

                //atrack1 = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffersize, AudioTrack.MODE_STREAM);
                atrack1 = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffersize, AudioTrack.MODE_STREAM);
                atrack1.setPlaybackRate(44100);
                byte[] buffer = new byte[buffersize];
                arec1.startRecording();
                atrack1.play();
                Context context = getContext();
                localAudioManager1 = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                //  localAudioManager.setSpeakerphoneOn(true);
                while (isRecording1) {
                    arec1.read(buffer, 0, buffersize);
                    // atrack.setStereoVolume(0,1);
                    int volumen1=Integer.parseInt(volumenDerecho.getText().toString());
                    float porcentaje1=(volumen1*100)/15;
                    float porcentajeDerecho=porcentaje1/100;
                    System.out.println(volumen1);

                    int volumen2=Integer.parseInt(volumenIzquierdo.getText().toString());
                    float porcentaje2=(volumen2*100)/15;
                    float porcentajeIzquiedo=porcentaje2/100;

                    System.out.println(volumen2);
                    atrack1.setStereoVolume(porcentajeIzquiedo,porcentajeDerecho);
                    atrack1.write(buffer, 0, buffer.length);
                }
            }
        }).start();
    }
}
