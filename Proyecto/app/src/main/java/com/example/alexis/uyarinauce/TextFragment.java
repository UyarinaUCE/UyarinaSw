package com.example.alexis.uyarinauce;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends Fragment {

    private static final int VOICE_RECOGNITION=100;
    ImageButton button;
    TextView txtTexto;

    View view;

    public TextFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_text,container,false);
        button = (ImageButton) vista.findViewById(R.id.button);
        txtTexto = (TextView) vista.findViewById(R.id.txtTexto);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakRecognition();
            }
        });
        return vista;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case VOICE_RECOGNITION:{
                if (resultCode == RESULT_OK && null!=data ){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtTexto.setText(result.get(0));
                }
                break;
            }

        }

    }

    public void speakRecognition(){
        Intent speak = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speak.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speak.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try{
            startActivityForResult(speak,VOICE_RECOGNITION);
        }catch (ActivityNotFoundException anfe){

        }
    }
}
