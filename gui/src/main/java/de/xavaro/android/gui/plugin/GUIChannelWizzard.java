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

import java.util.ArrayList;
import java.util.HashMap;

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

    private static final int CHANNEL_WIDTH = Simple.dipToPx(WIDTH / CHANNELS_LINE);
    private static final int CHANNEL_HEIGTH = Simple.dipToPx(40);

    private HashMap<GUIFrameLayout, GUITextView> containerText;

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

    public GUIFrameLayout selectedContainer;

    private void moveToPosi(GUIFrameLayout layout, int posi)
    {
        Integer[] posixy = getPosition(posi);

        FrameLayout.LayoutParams prams = (FrameLayout.LayoutParams) layout.getLayoutParams();
        prams.leftMargin = CHANNEL_WIDTH  * posixy[ 0 ];
        prams.topMargin  = CHANNEL_HEIGTH * posixy[ 1 ];
        layout.setLayoutParams(prams);

        String newTxt = (posi + 1) + "";

        if (posi < 100) newTxt = "0" + newTxt;
        if (posi < 10)  newTxt = "0" + newTxt;

        GUITextView txtView = containerText.get(layout);
        String text = (String) txtView.getText();

        txtView.setText(newTxt + ": " + text.substring(5));
    }

    private int key2Posi(int posi, int keyCode)
    {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)  posi -= 1;
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)    posi -= CHANNELS_LINE;
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) posi += 1;
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)  posi += CHANNELS_LINE;

        return posi;
    }

//    private int getPosition(Integer[] posi)
//    {
//        return (posi[ 0 ] + CHANNELS_LINE * posi[ 1 ]);
//    }

    private Integer[] getPosition(int posi)
    {
        return new Integer[]{
                (posi % CHANNELS_LINE),
                (posi / CHANNELS_LINE)
        };
    }

    public void moveDat(int keyCode)
    {
        int start = channelPosi.lastIndexOf(selectedContainer);
        int end = key2Posi(start, keyCode);

//        Integer[] oldPosi = getPosition(start);
//        Integer[] newPosi = getPosition(end);
//
//        Log.d(LOGTAG, "moveDat: start=" + start);
//        Log.d(LOGTAG, "moveDat: end=" + end);
//        Log.d(LOGTAG, "moveDat: oldPosi=" + oldPosi[ 0 ] + "/" + oldPosi[ 1 ]);
//        Log.d(LOGTAG, "moveDat: newPosi=" + newPosi[ 0 ] + "/" + newPosi[ 1 ]);

        if ((end < 0) || (end >= channelPosi.size())) return;

        moveToPosi(selectedContainer, end);

        // --->
        for (int inx = start + 1; inx <= end; inx++)
        {
            GUIFrameLayout moveObj = channelPosi.get(inx);
            moveToPosi(moveObj, inx - 1);
            channelPosi.set(inx - 1, moveObj);
        }

        // <---
        for (int inx = start - 1; inx >= end; inx--)
        {
            GUIFrameLayout moveObj = channelPosi.get(inx);
            moveToPosi(moveObj, inx + 1);
            channelPosi.set(inx + 1, moveObj);
        }

        invalidate();

        channelPosi.set(end, selectedContainer);
    }

    private void createContainer(JSONObject channel, int posi)
    {
        final GUIFrameLayout bgLayout = new GUIFrameLayout(getContext());

        Integer[] initPosi = getPosition(posi);

        FrameLayout.LayoutParams prams = new FrameLayout.LayoutParams(CHANNEL_WIDTH, CHANNEL_HEIGTH);
        prams.leftMargin = CHANNEL_WIDTH  * initPosi[ 0 ];
        prams.topMargin  = CHANNEL_HEIGTH * initPosi[ 1 ];

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

        GUITextView text = createChannelTextView(channel);
        bgLayout.addView(text);

        container.addView(bgLayout);
        scrollContent.addView(container);

        channelPosi.add(container);

        containerText.put(container, text);
    }

    private ArrayList<GUIFrameLayout> channelPosi;

    private void createChannelView()
    {
        JSONArray channels = getChannels();

        if (channels == null) return;

        channelPosi = new ArrayList<>();

        for (int inx = 0; inx < channels.length(); inx++)
        {
            JSONObject channel = Json.getObject(channels, inx);

            String type = Json.getString(channel, "type");
            if ((type == null) || !type.equalsIgnoreCase("tv")) continue;

            createContainer(channel, inx);
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

        containerText = new HashMap<>();

        createChannelView();
    }
}