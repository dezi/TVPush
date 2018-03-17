package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.util.Log;
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
        setRoundedCorners(20, 0xffffffff);

        GUIScrollView scroll = new GUIScrollView(context);
        scroll.setFocusable(false);
        pluginFrame.addView(scroll);

        topFrame = new GUIFrameLayout(context);
        topFrame.setFocusable(false);
        scroll.addView(topFrame);

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

            int width  = Simple.dipToPx(WIDTH / 3);
            int heigth = Simple.dipToPx(60);

            FrameLayout.LayoutParams prams = new FrameLayout.LayoutParams(width, heigth);
            prams.topMargin  = heigth * (((inx % 3) < 2) ? topInx : topInx++);
            prams.leftMargin = width  * (inx % 3);

            final GUITextView channelView = new GUITextView(context);
            channelView.setFocusable(true);
            channelView.setTextSizeDip(20);
            channelView.setText(Json.getString(channel, "dial") + ": " + channelName);
            channelView.setBackgroundColor(0x88000088);
            channelView.setLayoutParams(prams);
            channelView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.d(LOGTAG, "onClick: channelName=" + channelName);
                }
            });

            topFrame.addView(channelView);
        }
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate:");

        super.onCreate();

        init();
        createChennelView();
    }
}