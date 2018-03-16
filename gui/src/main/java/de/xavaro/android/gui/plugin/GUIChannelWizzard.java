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

    private Context context;

    public GUIChannelWizzard(Context context)
    {
        super(context);
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate:");

        context = getContext();

        super.onCreate();

        //pluginFrame.setBackgroundColor(0x88880000);
        setRoundedCorners(20, 0xffffffff);

        JSONArray tvremotes = IOT.instance.getDeviceWithCapability("tvremote");

        JSONArray channels = null;

        for (int inx = 0; inx < tvremotes.length(); inx++)
        {
            String uuid = Json.getString(tvremotes, inx);
            if (uuid == null) continue;

            IOTMetadata metadata = new IOTMetadata(uuid);
            if (metadata.metadata == null) continue;

            JSONArray PUBChannels = Json.getArray(metadata.metadata, "PUBChannels");
            if (PUBChannels == null) continue;

            channels = PUBChannels;
            break;
        }

        Log.d(LOGTAG, "onCreate: channels=" + Json.toPretty(channels));

        if (channels == null) return;

        GUIScrollView scroll = new GUIScrollView(context);
        scroll.setFocusable(false);
        pluginFrame.addView(scroll);

        GUIFrameLayout layout = new GUIFrameLayout(context);
        //layout.setBackgroundColor(0xffff0000);
        layout.setFocusable(false);
        scroll.addView(layout);

        for (int inx = 0; inx < channels.length(); inx++)
        {
            JSONObject channel = Json.getObject(channels, inx);

            final String channelName = Json.getString(channel, "name");

            FrameLayout.LayoutParams prams = new FrameLayout.LayoutParams(
                    Simple.dipToPx(300),
                    Simple.dipToPx(60));

            prams.topMargin = Simple.dipToPx(65) * inx;
            prams.leftMargin = Simple.dipToPx(100);

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

            layout.addView(channelView);
        }
    }
}