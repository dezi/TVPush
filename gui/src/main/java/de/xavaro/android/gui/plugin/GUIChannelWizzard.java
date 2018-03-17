package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.graphics.Color;
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

    private GUIFrameLayout scrollContent;

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

    public GUIFrameLayout selectedContainer;

    public void moveDat(int keyCode)
    {
        Log.d(LOGTAG, "moveDat: keyCode=" + keyCode);
    }

    private GUITextView createChannelTextView(JSONObject channel)
    {
        GUITextView channelView = new GUITextView(getContext());
        channelView.setGravity(Gravity.VERTICAL_GRAVITY_MASK);
        channelView.setTextColor(Color.BLACK);
        channelView.setMaxLines(1);
        channelView.setPaddingDip(3);
        channelView.setFocusable(false);
        channelView.setTextSizeDip(12);
        channelView.setText(Json.getString(channel, "dial") + ": " + Json.getString(channel, "name"));
        channelView.setLayoutParams(new FrameLayout.LayoutParams(Simple.MP, Simple.MP, Gravity.CENTER));

        return channelView;
    }

    private GUIFrameLayout createContainer(int inx, int iny)
    {
        int width  = Simple.dipToPx(WIDTH / CHANNELS_LINE);
        int heigth = Simple.dipToPx(40);

        FrameLayout.LayoutParams prams = new FrameLayout.LayoutParams(width, heigth);
        prams.leftMargin = width  * inx;
        prams.topMargin  = heigth * iny;

        GUIFrameLayout container = new GUIFrameLayout(getContext())
        {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event)
            {
                Log.d(LOGTAG, "onKeyDown: conatiner event=" + event);

                if (keyCode != KeyEvent.KEYCODE_DPAD_CENTER)
                {
                    if (selectedContainer == this)
                    {
                        moveDat(keyCode);
                        return true;
                    }
                }

                return super.onKeyDown(keyCode, event);
            }
        };

        final GUIFrameLayout bgLayout = new GUIFrameLayout(getContext());
        container.addView(bgLayout);

        container.setFocusable(true);
        container.setLayoutParams(prams);
        container.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(LOGTAG, "onClick:");

                GUIFrameLayout container = (GUIFrameLayout) view;

                if (container != selectedContainer)
                {
                    bgLayout.saveBackground();
                    bgLayout.setBackgroundColor(0xffd4d4d4);
                    selectedContainer = container;
                }
                else
                {
                    bgLayout.restoreBackground();
                    selectedContainer = null;
                }
            }
        });

        scrollContent.addView(container);

        return bgLayout;
    }

    private void createChannelView()
    {
        JSONArray channels = getChannels();

        if (channels == null) return;

        int iny = 0;

        for (int inx = 0; inx < channels.length(); inx++)
        {
            JSONObject channel = Json.getObject(channels, inx);

            int left = inx % CHANNELS_LINE;
            int top = iny;


            GUITextView text = createChannelTextView(channel);

            GUIFrameLayout container = createContainer(left, top);
            container.addView(text);

            if (left == (CHANNELS_LINE-1))
            {
                iny++;
            }
        }
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate:");

        super.onCreate();

        GUIFrameLayout mainFrame = new GUIFrameLayout(getContext());
        mainFrame.setRoundedCorners(20, 0xffffffff);
        pluginFrame.addView(mainFrame);

        GUIScrollView scroll = new GUIScrollView(getContext());
        mainFrame.addView(scroll);

        scrollContent = new GUIFrameLayout(getContext());
        scroll.addView(scrollContent);

        createChannelView();
    }
}