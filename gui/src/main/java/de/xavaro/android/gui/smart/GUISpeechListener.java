package de.xavaro.android.gui.smart;

import android.support.annotation.Nullable;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;

public class GUISpeechListener implements RecognitionListener
{
    private static final String LOGTAG = GUISpeechListener.class.getSimpleName();

    private Handler handler = new Handler();

    private GUISpeechCallback callback;
    private SpeechRecognizer recognizer;
    private Intent recognizerIntent;

    private boolean lockStart;
    private boolean isEnabled;

    public GUISpeechListener(Context context)
    {
        if (Simple.isSpeechIn())
        {
            Log.d(LOGTAG, "SpeechRecognizer: init.");

            Simple.turnBeepOnOff(true);

            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            Log.d(LOGTAG, "startListening: create");

            recognizer = SpeechRecognizer.createSpeechRecognizer(context);
            recognizer.setRecognitionListener(this);
        }
        else
        {
            Log.d(LOGTAG, "SpeechRecognizer: no speech.");
        }
    }

    public void setCallback(GUISpeechCallback callback)
    {
        this.callback = callback;
    }

    public void startListening()
    {
        Log.d(LOGTAG, "startListening:");

        handler.removeCallbacks(restartListeningRunnable);

        isEnabled = true;

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
                Simple.turnBeepOnOff(false);
            }
        }, 1000);
    }

    public void stopListening()
    {
        Log.d(LOGTAG, "stopListening:");

        Simple.turnBeepOnOff(true);

        isEnabled = false;

        if (recognizer != null)
        {
            recognizer.stopListening();
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

    private final Runnable restartListeningRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            stopListening();

            handler.postDelayed(startListeningRunnable, 250);
        }
    };

    public void onPleaseActivate()
    {
        Log.d(LOGTAG, "onPleaseActivate:");

        if (callback != null) callback.onActivateRemote();
    }

    @Override
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(LOGTAG, "onReadyForSpeech:");

        if (callback != null) callback.onSpeechReady();
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

                millis = 3 * 1000;
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

    @Override
    public void onEvent(int eventType, Bundle params)
    {
        Log.d(LOGTAG, "onEvent:");
    }

    @Override
    public void onPartialResults(Bundle resultsBundle)
    {
        JSONObject jresults = resultsToJSON(resultsBundle, true);
        if ((jresults != null) && (callback != null)) callback.onSpeechResults(jresults);

        //
        // Sometimes the recognition hangs here forever.
        //

        handler.removeCallbacks(restartListeningRunnable);
        handler.postDelayed(restartListeningRunnable, 10 * 1000);
    }

    @Override
    public void onResults(Bundle resultsBundle)
    {
        JSONObject speech = resultsToJSON(resultsBundle, false);

        JSONArray results = Json.getArray(speech, "results");
        if ((results == null) || (results.length() == 0)) return;

        JSONObject result = Json.getObject(results, 0);

        String text = Json.getString(result, "text");
        float conf = Json.getFloat(result, "conf");

        Log.d(LOGTAG, "onSpeechResults: conf=" + conf + " text=" + text);

        if ((speech != null) && (callback != null)) callback.onSpeechResults(speech);

        lockStart = false;

        if (isEnabled) startListening();
    }

    @Nullable
    public JSONObject resultsToJSON(Bundle results, boolean partial)
    {
        JSONObject jobject = new JSONObject();
        JSONArray jarray = new JSONArray();

        Json.put(jobject, "partial", partial);
        Json.put(jobject, "results", jarray);

        ArrayList<String> text = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
        float[] conf = results.getFloatArray(android.speech.SpeechRecognizer.CONFIDENCE_SCORES);

        if (text != null)
        {
            for (int inx = 0; inx < text.size(); inx++)
            {
                String words = text.get(inx);

                if (! words.isEmpty())
                {
                    JSONObject one = new JSONObject();
                    Json.put(jarray, one);

                    Json.put(one, "text", words);
                    Json.put(one, "conf", (conf != null) ? conf[inx] : -1f);
                }
            }
        }

        return (jarray.length() > 0) ? jobject : null;
    }
}
