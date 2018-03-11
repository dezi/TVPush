package de.xavaro.android.tvpush;

import android.os.Bundle;
import android.util.Log;

import de.xavaro.android.gui.base.GUIActivity;

import de.xavaro.android.gui.plugin.GUISpeechRecogniton;
import de.xavaro.android.gui.plugin.GUIVideoSurface;
import de.xavaro.android.gui.simple.Simple;

import pub.android.interfaces.cam.Camera;
import zz.top.cam.Cameras;

public class SpeechRecognitionActivity extends GUIActivity
{
    private final static String LOGTAG = SpeechRecognitionActivity.class.getSimpleName();

    private GUISpeechRecogniton speechRecognition;

    private GUIVideoSurface videoSurface1;
    private GUIVideoSurface videoSurface2;

    private Camera camera1;
    private Camera camera2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        speechRecognition = new GUISpeechRecogniton(this);

        topframe.addView(speechRecognition);

        setWindowHeightDip(600);

        videoSurface1 = new GUIVideoSurface(this);
        videoSurface2 = new GUIVideoSurface(this);

        videoSurface1.setPosition( 10,10);
        videoSurface2.setPosition(400,10);

        topframe.addView(videoSurface1);
        topframe.addView(videoSurface2);

        Simple.getHandler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                String name1 = "Orwell Oben";

                camera1 = Cameras.createCameraByName(name1);

                if (camera1 == null)
                {
                    Log.d(LOGTAG, "createCameraByName not fund name=" + name1);
                }
                else
                {
                    camera1.connectCamera();

                    camera1.registerSurface(videoSurface1.getGLSVideoView());

                    camera1.setResolution(Camera.RESOLUTION_720P);

                    camera1.startRealtimeVideo();
                    camera1.startFaceDetection(true);

                    //camera2.startRealtimeAudio();
                }

                String name2 = "Orwell Büro";

                camera2 = Cameras.createCameraByName(name2);

                if (camera2 == null)
                {
                    Log.d(LOGTAG, "createCameraByName not fund name=" + name2);
                }
                else
                {
                    camera2.connectCamera();

                    camera2.registerSurface(videoSurface2.getGLSVideoView());

                    camera2.setResolution(Camera.RESOLUTION_720P);

                    camera2.startRealtimeVideo();
                    camera2.startFaceDetection(true);

                    //camera2.startRealtimeAudio();
                }
            }

        }, 2000);

    }
}
