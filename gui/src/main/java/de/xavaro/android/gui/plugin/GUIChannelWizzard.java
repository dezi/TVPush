package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUILinearLayout;
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

        GUILinearLayout layout = new GUILinearLayout(context);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);
        pluginFrame.addView(layout);

        for (int inx = 0; inx < channels.length(); inx++)
        {
            JSONObject channel = Json.getObject(channels, inx);

            final String channelName = Json.getString(channel, "name");

            final GUITextView channelView = new GUITextView(context);
            channelView.setFocusable(true);
            channelView.setTextSizeDip(20);
            channelView.setText(channelName);
            channelView.setBackgroundColor(0x88000088);
            channelView.setPaddingDip(0, 10, 0, 0);
            channelView.setLayoutParams(new FrameLayout.LayoutParams(Simple.WC, Simple.WC, Gravity.CENTER));
            channelView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.d(LOGTAG, "onClick: channelName=" + channelName);
                }
            });
            
            channelView.setOnFocusChangeListener(new OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View view, boolean b)
                {
                    Log.d(LOGTAG, "onFocusChange: channelName=" + channelName + " b=" + b);
                    channelView.setBackgroundColor(0x88008800);
                }
            });

            layout.addView(channelView);
        }
    }
}