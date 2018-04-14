package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.iot.base.IOT;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.plugin.GUIPluginTitle;
import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.gui.views.GUIScrollView;
import de.xavaro.android.gui.views.GUITextView;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.simple.Json;

import de.xavaro.android.iot.status.IOTMetadata;

public class GUIChannelWizzard extends GUIPluginTitle
{
    private final static String LOGTAG = GUIChannelWizzard.class.getSimpleName();

    private int MENU_WIDTH = 150;

    private int CHANNEL_COLS = 5;
    private int CHANNEL_WIDTH;
    private int CHANNEL_HEIGHT;

    private ArrayList<GUIFrameLayout> channelPosi;
    private HashMap<GUIFrameLayout, GUITextView> containerText;

    private GUIFrameLayout scrollContent;
    private GUIScrollView scrollView;

    private GUIFrameLayout selectedContainer;

    public GUIChannelWizzard(Context context)
    {
        super(context);

        setWizzard(true, false, 3, Gravity.LEFT);

        setTitleIcon(R.drawable.magic_hand_440);
        setNameText("Channel Wizzard");

//        contentFrame.setBackgroundColor(Color.BLUE);
        contentFrame.setBackgroundColor(Color.WHITE);
        GUILinearLayout menuContainer = createMenu();
        contentFrame.addView(menuContainer);

        GUIFrameLayout scrollDeFuck = new GUIFrameLayout(getContext());
//        scrollDeFuck.setBackgroundColor(Color.RED);
        menuContainer.addView(scrollDeFuck);

        scrollView = new GUIScrollView(getContext());
        scrollDeFuck.addView(scrollView);

        scrollContent = new GUIFrameLayout(getContext());
//        scrollContent.setBackgroundColor(Color.WHITE);
        scrollView.addView(scrollContent);

        Log.d(LOGTAG, "GUIChannelWizzard: width=" + getPluginWidthDip() + " height=" + getPluginHeightDip());
        Log.d(LOGTAG, "GUIChannelWizzard: width=" + getPluginWidth() + " height=" + getPluginHeight());
        Log.d(LOGTAG, "GUIChannelWizzard: width=" + getPluginNettoWidth() + " height=" + getPluginNettoHeight());

        CHANNEL_WIDTH = (getPluginNettoWidth() - scrollView.getScrollBarSize() - Simple.dipToPx(MENU_WIDTH)) / CHANNEL_COLS;
        CHANNEL_HEIGHT = Simple.dipToPx(40);
    }

    // ship with containers
    private HashMap<GUIFrameLayout, GUITextView> buttonShip = new HashMap<>();

    private GUIFrameLayout createMenuButton(String title, boolean addToShip)
    {
        GUIFrameLayout container = new GUIFrameLayout(getContext());
        container.setMarginLeftDip(5);
        container.setMarginRightDip(5);
        container.setFocusable(true);
        container.setSizeDip(Simple.MP, Simple.WC);

        GUITextView button = new GUITextView(getContext());
        button.setPaddingDip(5);
        button.setText(title);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(0xff5C6370);
        button.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        button.saveBackground();
        container.addView(button);

        if (addToShip)
        {
            buttonShip.put(container, button);
        }

        return container;
    }

    private final OnClickListener modeListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            for (Map.Entry<GUIFrameLayout, GUITextView> entry : buttonShip.entrySet())
            {
                if (entry.getKey() == view)
                {
                    entry.getValue().saveBackground();
                    entry.getValue().setBackgroundColor(0xff818896);
                }
                else
                {
                    entry.getValue().restoreBackground();
                }
            }
        }
    };

    private GUILinearLayout createMenu()
    {
        GUILinearLayout menuContainer = new GUILinearLayout(getContext());
        menuContainer.setPaddingDip(0);
//        menuContainer.setMarginRightDip(0);
//        menuContainer.setBackgroundColor(Color.RED);
        menuContainer.setOrientation(LinearLayout.HORIZONTAL);
        menuContainer.setGravity(Gravity.END);

        GUILinearLayout menu = new GUILinearLayout(getContext());
        menu.setBackgroundColor(0xff282C34);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setSizeDip(MENU_WIDTH, Simple.MP);
        menu.setGravity(Gravity.CENTER);
        menuContainer.addView(menu);

        GUIFrameLayout tvButton = createMenuButton("Tv", true);
        menu.addView(tvButton);
        tvButton.setOnClickListener(modeListener);

        GUIFrameLayout radioButton = createMenuButton("Radio", true);
        menu.addView(radioButton);
        radioButton.setOnClickListener(modeListener);

//        GUIFrameLayout allButton = createMenuButton("All", true);
//        menu.addView(allButton);
//        allButton.setOnClickListener(modeListener);
//        allButton.callOnClick();

        tvButton.callOnClick();

        GUIFrameLayout saveButton = createMenuButton("Save", false);
        saveButton.setMarginTopDip(20);
        menu.addView(saveButton);

        return menuContainer;
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        createChannelView();
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        nukeChannelView();
    }

    private JSONArray getChannels()
    {
        JSONArray tvremotes = IOT.instance.getDevicesWithCapability("tvremote");

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

    private void updateDailPos(GUITextView textView, int posi)
    {
        String dialTxt = (posi + 1) + "";

        if (posi < 100) dialTxt = "0" + dialTxt;
        if (posi <  10) dialTxt = "0" + dialTxt;

        JSONObject channelInfo = (JSONObject) textView.getTag();
        String name = dialTxt + ": " + Json.getString(channelInfo, "name");

        textView.setText(name);
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
        channelView.setLayoutParams(new FrameLayout.LayoutParams(Simple.MP, Simple.MP, Gravity.CENTER));
        channelView.setTag(channel);

        return channelView;
    }

    private void moveToPosi(GUIFrameLayout layout, int posi)
    {
        Integer[] posixy = getPosition(posi);

        FrameLayout.LayoutParams prams = (FrameLayout.LayoutParams) layout.getLayoutParams();
        prams.leftMargin = CHANNEL_WIDTH  * posixy[ 0 ];
        prams.topMargin  = CHANNEL_HEIGHT * posixy[ 1 ];
        layout.setLayoutParams(prams);

        GUITextView txtView = containerText.get(layout);
        updateDailPos(txtView, posi);
    }

    private int key2Posi(int posi, int keyCode)
    {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)  posi -= 1;
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)    posi -= CHANNEL_COLS;
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) posi += 1;
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)  posi += CHANNEL_COLS;

        return posi;
    }

    private Integer[] getPosition(int posi)
    {
        return new Integer[]{
                (posi % CHANNEL_COLS),
                (posi / CHANNEL_COLS)
        };
    }

    public void moveDat(int keyCode)
    {
        int start = channelPosi.lastIndexOf(selectedContainer);
        int end = key2Posi(start, keyCode);

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

        channelPosi.set(end, selectedContainer);

        int containerTop    = CHANNEL_HEIGHT * (end / CHANNEL_COLS);
        int containerBottom = containerTop + CHANNEL_HEIGHT;

        int scrollTop       = scrollView.getScrollY();
        int scrollBottom    = scrollView.getBottom() + scrollTop;

        if (containerTop < scrollTop)
        {
            scrollView.smoothScrollBy(0, -CHANNEL_HEIGHT);
        }

        if (containerBottom > scrollBottom)
        {
            scrollView.smoothScrollBy(0, CHANNEL_HEIGHT);
        }

        invalidate();
    }

    private void createContainer(JSONObject channel, int posi)
    {
        Log.d(LOGTAG, "createContainer: posi=" + posi + " channel=" + Json.toPretty(channel));

        final GUIFrameLayout bgFrame = new GUIFrameLayout(getContext());

        Integer[] initPosi = getPosition(posi);

        FrameLayout.LayoutParams prams = new FrameLayout.LayoutParams(CHANNEL_WIDTH, CHANNEL_HEIGHT);
        prams.leftMargin = CHANNEL_WIDTH  * initPosi[ 0 ];
        prams.topMargin  = CHANNEL_HEIGHT * initPosi[ 1 ];

        GUIFrameLayout container = new GUIFrameLayout(getContext())
        {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event)
            {
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
                    bgFrame.saveBackground();
                    bgFrame.setBackgroundColor(0xffd4d4d4);
                    selectedContainer = container;
                }
                else
                {
                    bgFrame.restoreBackground();
                    selectedContainer = null;
                }
            }
        });

        GUITextView text = createChannelTextView(channel);
        updateDailPos(text, posi);
        bgFrame.addView(text);

        container.addView(bgFrame);

        scrollContent.addView(container);
        channelPosi.add(container);
        containerText.put(container, text);

        if (posi == 0)
        {
            container.requestFocus();
        }
    }

    private String mode = "tv";
//    private String mode = "radio";

    private void createChannelView()
    {
        scrollContent.removeAllViews();
        channelPosi = new ArrayList<>();
        containerText = new HashMap<>();

        JSONArray channels = getChannels();
        if (channels == null) return;

        int posi = 0;

        for (int inx = 0; inx < channels.length(); inx++)
        {
            JSONObject channel = Json.getObject(channels, inx);

            String type = Json.getString(channel, "type");
            if ((type == null) || !type.equalsIgnoreCase(mode))
            {
                continue;
            }

            createContainer(channel, posi);
            posi++;
        }
    }

    private void nukeChannelView()
    {
        channelPosi = null;
        containerText = null;
        scrollContent.removeAllViews();
    }
}