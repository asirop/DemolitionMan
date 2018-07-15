package com.santriop.demolitionman;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.speech.tts.*;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, RecognitionListener {
    private static final int REQUEST_RECORD_PERMISSION = 100;
    TextToSpeech amendeTTS = null;
    SpeechRecognizer listeningVulgarity = null;
    RecognizerIntent phrase = null;
    Switch toggle = null;
    TextView testlisten =  null;
    Intent recognizerIntent = null;
    Locale language = null;
    String amende = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggle = (Switch) findViewById(R.id.listen);
        testlisten = (TextView) findViewById(R.id.textTest);
        // Speaker
        amendeTTS = new TextToSpeech(this, this);
        // Listener
//        Intent recognition = new Intent(this, VoiceRecognitionActivity.class);
//        startActivity(recognition, savedInstanceState);
            // 2
        listeningVulgarity = SpeechRecognizer.createSpeechRecognizer(this);
        listeningVulgarity.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.FRENCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        // Language
        RadioGroup lang = (RadioGroup) findViewById(R.id.lang);
        int langId = lang.getCheckedRadioButtonId();
        View v = (View) findViewById(langId);
        onLangChange(v);
    }
    public void onLangChange(View view){
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.langEN:
                language = Locale.UK;
                amende = "You are fined 1 credit for a violation of the verbal morality statue";
                    break;
            case R.id.langFR:
                language = Locale.FRENCH;
                amende = "Vous avez une amende d'un crédit pour infraction au code de moralité du langage";
                    break;
        }
        if (amendeTTS != null)
            amendeTTS.setLanguage(language);
    }
    public void speak(View view){
        TextView test = (TextView) findViewById(R.id.textTest);
        test.setText("blll");
        saySomething(amende, 1);
    }
    public void listenning(View view){
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    testlisten.setText("listening");
                    // 2
                    ActivityCompat.requestPermissions(MainActivity.this,
                                                       new String[]{Manifest.permission.RECORD_AUDIO},
                                                       REQUEST_RECORD_PERMISSION);
                    //listeningVulgarity.setRecognitionListener(phrase);
                    //listeningVulgarity.startListening(phrase);
                } else {
                    //testlisten.setText("not listening");
                    // 2
                    listeningVulgarity.stopListening();
                }
            }
        });
    }

    private void saySomething(String text, int qmode) {
        if (qmode == 1)
            amendeTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        else
            amendeTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (amendeTTS != null) {
                int result = amendeTTS.setLanguage(language);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language is not supported", Toast.LENGTH_LONG).show();
                } else {
                    //saySomething("TTS is ready", 0);
                }
            }
        } else {
            Toast.makeText(this, "TTS initialization failed",
                    Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onDestroy() {
        if (amendeTTS != null) {
            amendeTTS.stop();
            amendeTTS.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listeningVulgarity.startListening(recognizerIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    // Override RecognitionListener
    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        testlisten.setText(errorMessage);

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

        testlisten.setText(text);

    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
