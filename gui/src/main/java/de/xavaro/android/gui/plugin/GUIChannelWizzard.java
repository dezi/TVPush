package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.gui.views.GUIScrollView;
import de.xavaro.android.gui.views.GUITextView;
import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.status.IOTMetadata;
import zz.top.utl.Json;

public class GUIChannelWizzard extends GUIPlugin
{
    private final static String LOGTAG = GUIChannelWizzard.class.getSimpleName();

    private final static int WIDTH = 600;
    private final static int CHANNELS_LINE = 4;

    private Context context;
    private GUIFrameLayout topFrame;

    public GUIChannelWizzard(Context context)
    {
        super(context);
    }

    private JSONArray getChannels()
    {
        JSONArray tvremotes = IOT.instance.getDeviceWithCapability("tvremote");

        for (int inx = 0; inx < tvremotes.length(); inx++)
        {
            String uuid = Json.getString(tvremotes, inx);
            if (uuid == null) continue;

            IOTMetadata metadata = new IOTMetadata(uuid);
            if (metadata.metadata == null) continue;

            JSONArray PUBChannels = Json.getArray(metadata.metadata, "PUBChannels");

            if (PUBChannels != null)
            {
                return PUBChannels;
            }
        }

        return null;
    }

    private void init()
    {
        context = getContext();

        GUIFrameLayout mainFrame = new GUIFrameLayout(context);
        mainFrame.setRoundedCorners(20, 0xffffffff);
        pluginFrame.addView(mainFrame);

        GUIScrollView scroll = new GUIScrollView(context);
        mainFrame.addView(scroll);

        topFrame = new GUIFrameLayout(context);
        scroll.addView(topFrame);

        createChennelView();

        setFocusable(true);

        Log.d(LOGTAG, "init: width=" + WIDTH);
    }

    private void createChennelView()
    {
        JSONArray channels = getChannels();

        if (channels == null) return;

        int topInx = 0;

        for (int inx = 0; inx < channels.length(); inx++)
        {
            JSONObject channel = Json.getObject(channels, inx);

            final String channelName = Json.getString(channel, "name");

            int width  = Simple.dipToPx(WIDTH / CHANNELS_LINE);
            int heigth = Simple.dipToPx(40);

            FrameLayout.LayoutParams prams = new FrameLayout.LayoutParams(width, heigth);
            prams.topMargin  = heigth * (((inx % CHANNELS_LINE) < (CHANNELS_LINE-1)) ? topInx : topInx++);
            prams.leftMargin = width  * (inx % CHANNELS_LINE);

            GUIFrameLayout container = new GUIFrameLayout(context);
            container.setFocusable(false);
            container.setPaddingDip(3);
            //container.setBackgroundColor(0x88000088);
            container.setLayoutParams(prams);
            topFrame.addView(container);

            final GUITextView channelView = new GUITextView(context);
            channelView.setGravity(Gravity.VERTICAL_GRAVITY_MASK);
            channelView.setMaxLines(1);
            channelView.setPaddingDip(3);
            channelView.setFocusable(true);
            channelView.setTextSizeDip(12);
            channelView.setText(Json.getString(channel, "dial") + ": " + channelName);
            channelView.setLayoutParams(new FrameLayout.LayoutParams(Simple.MP, Simple.MP, Gravity.CENTER));
            channelView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.d(LOGTAG, "onClick: channelName=" + channelName);
                    channelView.setRoundedCornersDip(0, 0xFFFFFFFF, 0xFF00A2FF);
                }
            });
            
            container.addView(channelView);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d(LOGTAG, "onKeyDown: event=" + event);

        return super.onKeyDown(keyCode, event);

        // KEYCODE_DPAD_CENTER
//        if (keyCode == KeyEvent.)
//        {
//            GUI.instance.displaySpeechRecognition(true);
//
//            return true;
//        }
//
//        Log.d(LOGTAG, "onKeyDown: event=" + event);
//
//        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate:");

        super.onCreate();

        init();
    }
}