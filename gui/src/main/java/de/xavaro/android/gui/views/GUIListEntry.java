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

    public GUIListEntry(Context context)
    {
        super(context);

        setFocusable(true);
        setOrientation(HORIZONTAL);
        setPaddingDip(GUIDefs.PADDING_TINY);
        setBackgroundColor(GUIDefs.COLOR_LIGHT_TRANSPARENT);

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
}

