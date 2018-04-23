package de.xavaro.android.spr.base;

import android.support.annotation.Nullable;

import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.xavaro.android.pub.interfaces.pub.PUBSpeechListener;
import de.xavaro.android.spr.simple.Log;
import de.xavaro.android.spr.simple.Json;
import de.xavaro.android.spr.simple.Simple;

public class SPRListener implements
        RecognitionListener,
        PUBSpeechListener
{
    private static final String LOGTAG = SPRListener.class.getSimpleName();

    public static void startService(Context context)
    {
        if (SPR.instance.sprListener == null)
        {
            SPR.instance.sprListener = new SPRListener(context);
            SPR.instance.sprListener.startListening();
        }
    }

    public static void stopService()
    {
        if (SPR.instance.sprListener != null)
        {
            SPR.instance.sprListener.cancel();
            SPR.instance.sprListener.close();
            SPR.instance.sprListener = null;
        }
    }

    private Handler handler = new Handler();

    private SpeechRecognizer recognizer;
    private Intent recognizerIntent;

    private boolean lockStart;
    private boolean isEnabled;
    private boolean isOndemand;
    private boolean isBeginn;

    public static boolean isRecognitionAvailable(Context context)
    {
        final List<ResolveInfo> list = context.getPackageManager().queryIntentServices(
                new Intent(RecognitionService.SERVICE_INTERFACE), 0);

        Log.d(LOGTAG, "########" + list);

        String serviceComponent = Settings.Secure.getString(context.getContentResolver(),
                "voice_recognition_service");

        Log.d(LOGTAG, "########" + serviceComponent);

        return list != null && list.size() != 0;
    }

    public SPRListener(Context context)
    {
        isRecognitionAvailable(context);

        if (Simple.isSpeechIn())
        {
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) && ! Simple.isTV())
            {
                //
                // Intent working but not defined...
                //

                recognizerIntent.putExtra("android.speech.extra.PREFER_OFFLINE", true);
            }

            Log.d(LOGTAG, "SPRListener: create.");

            recognizer = SpeechRecognizer.createSpeechRecognizer(context);
            recognizer.setRecognitionListener(this);

            isOndemand = Simple.isTV();
        }
        else
        {
            Log.d(LOGTAG, "SPRListener: no speech available.");
        }
    }

    public void cancel()
    {
        if (recognizer != null)
        {
            recognizer.cancel();
        }
    }

    public void close()
    {
        if (recognizer != null)
        {
            recognizer.destroy();
            recognizer = null;
        }
    }

    public boolean startListening()
    {
        Log.d(LOGTAG, "startListening:");

        if (recognizer == null) return false;

        isEnabled = ! isOndemand;

        if (! lockStart)
        {
            lockStart = true;

            recognizer.startListening(recognizerIntent);
        }

        if (! isOndemand)
        {
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

        return true;
    }

    public boolean stopListening()
    {
        Log.d(LOGTAG, "stopListening:");

        if (recognizer == null) return false;

        if (! isOndemand)
        {
            Simple.turnBeepOnOff(true);
        }

        isEnabled = false;
        recognizer.stopListening();

        return true;
    }

    private final Runnable startListeningRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            startListening();
        }
    };

    public void onPleaseActivate()
    {
        Log.d(LOGTAG, "onPleaseActivate:");

        SPR.instance.onActivateRemote();
    }

    @Override
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(LOGTAG, "onReadyForSpeech:");

        isBeginn = false;

        SPR.instance.onSpeechReady();
    }

    @Override
    public void onBeginningOfSpeech()
    {
        Log.d(LOGTAG, "onBeginningOfSpeech:");

        isBeginn = true;
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
                millis = 10 * 1000;
                break;

            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                millis = 3 * 1000;

                if (Simple.isSony())
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
                millis = 10 * 1000;
                break;

            case SpeechRecognizer.ERROR_SERVER:
                message = "Error from server";
                millis = 10 * 1000;

                //
                // Fall back to network speech.
                //

                recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

                break;
        }

        Log.d(LOGTAG, "onError: error=" + error + " message=" + message);

        lockStart = false;

        if (isEnabled && ! isOndemand)
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
        if (isBeginn)
        {
            JSONObject speech = resultsToJSON(resultsBundle, true);
            if (speech != null) SPR.instance.onSpeechResults(speech);

            JSONArray results = Json.getArray(speech, "results");

            if ((results != null) && (results.length() > 0))
            {
                JSONObject result = Json.getObject(results, 0);

                Log.d(LOGTAG, "onPartialResults:"
                        + " text=" + Json.getString(result, "text")
                        + " conf=" + Json.getFloat(result, "conf")
                );
            }
        }
    }

    @Override
    public void onResults(Bundle resultsBundle)
    {
        JSONObject speech = resultsToJSON(resultsBundle, false);
        if (speech != null) SPR.instance.onSpeechResults(speech);

        JSONArray results = Json.getArray(speech, "results");

        if ((results != null) && (results.length() > 0))
        {
            JSONObject result = Json.getObject(results, 0);

            Log.d(LOGTAG, "onResults:"
                    + " text=" + Json.getString(result, "text")
                    + " conf=" + Json.getFloat(result, "conf")
            );
        }

        lockStart = false;

        if (isEnabled && ! isOndemand)
        {
            handler.post(startListeningRunnable);
        }
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
