package de.xavaro.android.gui.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;

public class GUIListEntry extends GUILinearLayout
{
    public String idtag;
    public boolean isinuse;

    public GUIFrameLayout levelView;
    public GUIIconViewIOT iconView;
    public GUITextView headerViev;
    public GUITextView infoView;

    public GUIListEntry(Context context)
    {
        super(context);

        isinuse = true;

        setFocusable(true);
        setOrientation(HORIZONTAL);
        setPaddingDip(GUIDefs.PADDING_TINY);
        setBackgroundColor(GUIDefs.COLOR_LIGHT_TRANSPARENT);

        levelView = new GUIFrameLayout(context);
        levelView.setVisibility(GONE);
        addView(levelView);

        iconView = new GUIIconViewIOT(context);
        addView(iconView);

        GUILinearLayout entryCenter = new GUILinearLayout(context);
        entryCenter.setGravity(Gravity.START + Gravity.CENTER_VERTICAL);
        entryCenter.setSizeDip(Simple.MP, Simple.MP, 1.0f);

        addView(entryCenter);

        GUILinearLayout entryBox = new GUILinearLayout(context);
        entryBox.setOrientation(VERTICAL);
        entryBox.setSizeDip(Simple.MP, Simple.WC);

        entryCenter.addView(entryBox);

        headerViev = new GUITextView(context);
        headerViev.setTextSizeDip(GUIDefs.FONTSIZE_HEADERS);
        headerViev.setSingleLine(true);
        headerViev.setEllipsize(TextUtils.TruncateAt.END);

        entryBox.addView(headerViev);

        infoView = new GUITextView(context);
        infoView.setTextSizeDip(GUIDefs.FONTSIZE_INFOS);
        infoView.setSingleLine(true);
        infoView.setEllipsize(TextUtils.TruncateAt.END);

        entryBox.addView(infoView);
    }

    public void setLevel(int level)
    {
        if (level == 0)
        {
            levelView.setVisibility(GONE);
        }
        else
        {
            levelView.setVisibility(VISIBLE);
            levelView.setSizeDip(GUIDefs.PADDING_XLARGE, Simple.MP);
        }
    }
}

