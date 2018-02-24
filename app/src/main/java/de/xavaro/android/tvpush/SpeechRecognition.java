package de.xavaro.android.tvpush;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import de.xavaro.android.common.Simple;

public class SpeechRecognition implements RecognitionListener
{
    private static final String LOGTAG = SpeechRecognition.class.getSimpleName();

    private SpeechRecognizer recognizer;
    private Intent recognizerIntent;
    private AudioManager audioManager;
    private Context context;
    private Handler handler = new Handler();
    private boolean lockStart;

    public SpeechRecognition(Context context)
    {
        this.context = context;

        if (Simple.isIsspeech())
        {
            Log.d(LOGTAG, "SpeechRecognizer: init.");

            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 30 * 1000);

            if (! Simple.isTV())
            {
                turnBeepOn();
                turnBeepOff();
            }

            startListening();
        }
        else
        {
            Log.d(LOGTAG, "SpeechRecognizer: no speech.");
        }
    }

    private void startListening()
    {
        Log.d(LOGTAG, "startListening:");

        if (recognizer == null)
        {
            Log.d(LOGTAG, "startListening: create");

            recognizer = SpeechRecognizer.createSpeechRecognizer(context);
            recognizer.setRecognitionListener(this);
        }

        if (! lockStart)
        {
            lockStart = true;

            recognizer.startListening(recognizerIntent);
        }
    }

    private final Runnable startListeningRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            startListening();
        }
    };

    @Override
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(LOGTAG, "onReadyForSpeech:");
    }

    @Override
    public void onBeginningOfSpeech()
    {
        Log.d(LOGTAG, "onBeginningOfSpeech:");
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {
        //Log.d(LOGTAG, "onRmsChanged: rmsdB=" + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer)
    {
        Log.d(LOGTAG, "onBufferReceived: buffer=" + buffer.length);
    }

    @Override
    public void onEndOfSpeech()
    {
        Log.d(LOGTAG, "onEndOfSpeech:");
    }

    @Override
    public void onError(int error)
    {
        String message = "Unknown";
        boolean restart = true;
        long millis = 200;

        switch (error)
        {
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

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;

            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                restart = false;
                break;

            case SpeechRecognizer.ERROR_SERVER:
                message = "Error from server";
                millis = 5 * 1000;
                break;
        }

        Log.d(LOGTAG, "onError: error=" + error + " message=" + message);

        lockStart = false;

        if (restart)
        {
            handler.removeCallbacks(startListeningRunnable);
            handler.postDelayed(startListeningRunnable, millis);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults)
    {
        Log.d(LOGTAG, "onPartialResults:");

        dumpResults(partialResults);
    }

    @Override
    public void onEvent(int eventType, Bundle params)
    {
        Log.d(LOGTAG, "onEvent:");
    }

    @Override
    public void onResults(Bundle results)
    {
        Log.d(LOGTAG, "onResults: " + results);

        dumpResults(results);

        String text = getBestResult(results);

        if (text != null)
        {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

            if (text.equalsIgnoreCase("mach den ton aus"))
            {
                turnBeepOff();
            }

            if (text.equalsIgnoreCase("mach den ton an"))
            {
                turnBeepOn();
            }
        }

        lockStart = false;

        startListening();
    }

    @Nullable
    private String getBestResult(Bundle results)
    {
        ArrayList<String> text = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
        float[] conf = results.getFloatArray(android.speech.SpeechRecognizer.CONFIDENCE_SCORES);

        if ((text != null) && (text.size() > 0))
        {
            if ((conf == null) || ((conf.length > 0) && (conf[ 0 ] > 0.0f)))
            {
               return text.get(0);
            }
        }

        return null;
    }

    private void dumpResults(Bundle results)
    {
        ArrayList<String> text = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
        float[] conf = results.getFloatArray(android.speech.SpeechRecognizer.CONFIDENCE_SCORES);

        if (text != null)
        {
            for (int inx = 0; inx < text.size(); inx++)
            {
                String logline = text.get(inx);

                if (conf != null)
                {
                    int percent = Math.round(conf[inx] * 100);

                    logline += " (" + percent + "%)";
                }

                Log.d(LOGTAG, "result=" + logline);
            }
        }
    }

    private void turnBeepOff()
    {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        /*
        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
        audioManager.adjustStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_MUTE, 0);
        audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
        audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
        */
    }

    private void turnBeepOn()
    {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        /*
        audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
        audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
        audioManager.adjustStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_UNMUTE, 0);
        */
    }
}