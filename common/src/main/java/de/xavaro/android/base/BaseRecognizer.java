package de.xavaro.android.base;

import android.support.annotation.Nullable;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import de.xavaro.android.simple.Simple;

public class BaseRecognizer implements RecognitionListener
{
    private static final String LOGTAG = BaseRecognizer.class.getSimpleName();

    private Handler handler = new Handler();

    private SpeechRecognizer recognizer;
    private Intent recognizerIntent;
    private Context context;

    private boolean lockStart;
    private boolean isEnabled;

    public BaseRecognizer(Context context)
    {
        this.context = context;

        if (Simple.isSpeech())
        {
            Log.d(LOGTAG, "SpeechRecognizer: init.");

            Simple.turnBeepOnOff(context, true);

            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        }
        else
        {
            Log.d(LOGTAG, "SpeechRecognizer: no speech.");
        }
    }

    public void destroy()
    {
        Log.d(LOGTAG, "destroy:");

        if (recognizer != null)
        {
            recognizer.destroy();
            recognizer = null;
        }
    }

    public void startListening()
    {
        Log.d(LOGTAG, "startListening:");

        handler.removeCallbacks(stopListeningRunnable);

        isEnabled = true;

        if (recognizer == null)
        {
            Log.d(LOGTAG, "startListening: create");

            recognizer = SpeechRecognizer.createSpeechRecognizer(context);
            recognizer.setRecognitionListener(this);
        }

        if (!lockStart)
        {
            lockStart = true;

            recognizer.startListening(recognizerIntent);
        }

        //
        // Turn off beep delayed, so the first
        // beep is delivered to user.
        //

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Simple.turnBeepOnOff(context, false);
            }
        }, 1000);
    }

    public void stopListening()
    {
        Simple.turnBeepOnOff(context, true);

        isEnabled = false;

        recognizer.stopListening();
    }

    private final Runnable startListeningRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            startListening();
        }
    };

    private final Runnable stopListeningRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            stopListening();
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

                if (Simple.getDeviceFullName().startsWith("SONY"))
                {
                    onPleaseActivate();
                }

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

        if (restart && isEnabled)
        {
            handler.removeCallbacks(startListeningRunnable);
            handler.postDelayed(startListeningRunnable, millis);
        }
    }

    public void onPleaseActivate()
    {
        Log.d(LOGTAG, "onPleaseActivate:");
    }

    @Override
    public void onEvent(int eventType, Bundle params)
    {
        Log.d(LOGTAG, "onEvent:");
    }

    @Override
    public void onPartialResults(Bundle partialResults)
    {
        Log.d(LOGTAG, "onPartialResults: result=" + getBestResult(partialResults));

        //
        // Sometimes the recognition hangs here forever.
        //

        handler.removeCallbacks(stopListeningRunnable);
        handler.postDelayed(stopListeningRunnable, 10 * 1000);
    }

    @Override
    public void onResults(Bundle results)
    {
        Log.d(LOGTAG, "onResults: result=" + getBestResult(results));

        lockStart = false;

        if (isEnabled) startListening();
    }

    @Nullable
    public String getBestResult(Bundle results)
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

    public void dumpResults(Bundle results)
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
}