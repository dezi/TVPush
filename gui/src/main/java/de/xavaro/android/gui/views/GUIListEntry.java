package de.xavaro.android.gui.views;

import android.content.Context;
import android.view.Gravity;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;

public class GUIListEntry extends GUILinearLayout
{
    public GUIIconView iconView;
    public GUITextView headerViev;
    public GUITextView infoView;
    public GUIRelativeLayout bulletView;

    public GUIListEntry(Context context)
    {
        super(context);

        setFocusable(true);
        setOrientation(HORIZONTAL);
        setPaddingDip(GUIDefs.PADDING_TINY);
        setBackgroundColor(GUIDefs.COLOR_LIGHT_TRANSPARENT);

        GUIRelativeLayout statusBox = new GUIRelativeLayout(context);
        statusBox.setGravity(Gravity.CENTER);
        statusBox.setSizeDip(Simple.WC, Simple.MP);
        statusBox.setPaddingDip(GUIDefs.PADDING_TINY);

        addView(statusBox);

        bulletView = new GUIRelativeLayout(context);
        bulletView.setSizeDip(GUIDefs.PADDING_MEDIUM,GUIDefs.PADDING_MEDIUM);

        statusBox.addView(bulletView);

        iconView = new GUIIconView(context);
        addView(iconView);

        GUIRelativeLayout entryCenter = new GUIRelativeLayout(context);
        entryCenter.setGravity(Gravity.START + Gravity.CENTER_VERTICAL);
        entryCenter.setSizeDip(Simple.MP, Simple.MP);

        addView(entryCenter);

        GUILinearLayout entryBox = new GUILinearLayout(context);
        entryBox.setOrientation(VERTICAL);
        entryBox.setSizeDip(Simple.MP, Simple.WC);

        entryCenter.addView(entryBox);

        headerViev = new GUITextView(context);
        headerViev.setTextSizeDip(GUIDefs.FONTSIZE_HEADERS);

        entryBox.addView(headerViev);

        infoView = new GUITextView(context);
        infoView.setTextSizeDip(GUIDefs.FONTSIZE_INFOS);

        entryBox.addView(infoView);
    }

    public void setStatusColor(int color)
    {
        bulletView.setRoundedCornersDip(GUIDefs.PADDING_MEDIUM / 2, color);
    }
}

