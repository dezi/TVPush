package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.simple.Simple;
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

        pluginFrame.setBackgroundColor(0x88880000);
        setRoundedCorners(20, 0x88008800);

        GUITextView bla = new GUITextView(context);
        bla.setText("Hallo Welt");
        bla.setBackgroundColor(0x88000088);
        bla.setLayoutParams(new FrameLayout.LayoutParams(Simple.WC, Simple.WC, Gravity.CENTER));

        pluginFrame.addView(bla);

        JSONObject channelsForDevices = new JSONObject();

        JSONArray tvremotes = IOT.instance.getDeviceWithCapability("tvremote");
        Log.d(LOGTAG, "onCreate: tvremotes=" + Json.toPretty(tvremotes));

        for (int inx = 0; inx < tvremotes.length(); inx++)
        {
            String uuid = Json.getString(tvremotes, inx);
            Log.d(LOGTAG, "onCreate: uuid=" + uuid);

            if (uuid == null) continue;

            IOTMetadata metadata = new IOTMetadata(uuid);
            Log.d(LOGTAG, "onCreate: metadata.metadata=" + metadata.metadata);
            if (metadata.metadata == null) continue;

            JSONArray PUBChannels = Json.getArray(metadata.metadata, "PUBChannels");
            Log.d(LOGTAG, "onCreate: PUBChannels=" + Json.toPretty(PUBChannels));
            if (PUBChannels == null) continue;

            Json.put(channelsForDevices, uuid, PUBChannels);
        }

        Log.d(LOGTAG, "onCreate: " + Json.toPretty(channelsForDevices));
    }
}