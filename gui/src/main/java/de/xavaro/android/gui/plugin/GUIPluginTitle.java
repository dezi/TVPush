package de.xavaro.android.gui.plugin;

import android.widget.LinearLayout;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.util.Log;

import de.xavaro.android.gui.views.GUIIconView;
import de.xavaro.android.gui.views.GUIIconViewIOT;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.gui.views.GUIEditText;
import de.xavaro.android.gui.views.GUIRelativeLayout;
import de.xavaro.android.gui.views.GUITextView;

import de.xavaro.android.gui.base.GUIDefs;

import de.xavaro.android.gui.simple.Simple;

public class GUIPluginTitle extends GUIPlugin
{
    private final static String LOGTAG = GUIPluginTitle.class.getSimpleName();

    public GUIIconViewIOT titleIcon;
    public GUITextView nameText;
    public GUIEditText nameEdit;
    public GUITextView nickInfo;
    public GUIEditText nickEdit;
    public GUIIconView actionIcon;

    public String objectTag;

    private int titleIconResid;

    public GUIPluginTitle(Context context)
    {
        super(context);

        this.setPaddingDip(GUIDefs.PADDING_SMALL, GUIDefs.PADDING_ZERO, GUIDefs.PADDING_SMALL, GUIDefs.PADDING_SMALL);

        GUILinearLayout splitterFrame = new GUILinearLayout(context);
        splitterFrame.setOrientation(LinearLayout.VERTICAL);

        contentFrame.addView(splitterFrame);

        GUILinearLayout titleFrame = new GUILinearLayout(context);
        titleFrame.setOrientation(LinearLayout.HORIZONTAL);
        titleFrame.setSizeDip(Simple.MP, Simple.WC);

        splitterFrame.addView(titleFrame);

        titleIcon = new GUIIconViewIOT(context);
        titleFrame.addView(titleIcon);

        GUILinearLayout titleCenter = new GUILinearLayout(context);
        titleCenter.setOrientation(LinearLayout.HORIZONTAL);
        titleCenter.setGravity(Gravity.START + Gravity.CENTER_VERTICAL);
        titleCenter.setSizeDip(Simple.WC, Simple.MP, 1.0f);

        titleFrame.addView(titleCenter);

        nameText = new GUITextView(context);
        nameText.setPaddingDip(GUIDefs.PADDING_SMALL);
        nameText.setTextSizeDip(GUIDefs.FONTSIZE_HEADERS);

        titleCenter.addView(nameText);

        nameEdit = new GUIEditText(context)
        {
            @Override
            public void onHighlightChanged(View view, boolean highlight)
            {
                if (! highlight) onNameEditFinished(view);
            }
        };

        nameEdit.setSizeDip(Simple.MP, Simple.WC, 0.5f);
        nameEdit.setTextSizeDip(GUIDefs.FONTSIZE_HEADERS);
        nameEdit.setPaddingDip(GUIDefs.PADDING_SMALL);
        nameEdit.setHighlightable(false);
        nameEdit.setFocusable(false);
        nameEdit.setVisibility(GONE);

        titleCenter.addView(nameEdit);

        nickInfo = new GUITextView(context);
        nickInfo.setSizeDip(Simple.MP, Simple.WC);
        nickInfo.setPaddingDip(GUIDefs.PADDING_SMALL);
        nickInfo.setTextSizeDip(GUIDefs.FONTSIZE_HEADERS);
        nickInfo.setGravity(Gravity.END);
        nickInfo.setVisibility(GONE);

        titleCenter.addView(nickInfo);

        nickEdit = new GUIEditText(context)
        {
            @Override
            public void onHighlightChanged(View view, boolean highlight)
            {
                if (! highlight) onNickEditFinished(view);
            }
        };

        nickEdit.setSizeDip(Simple.MP, Simple.WC, 0.5f);
        nickEdit.setTextSizeDip(GUIDefs.FONTSIZE_HEADERS);
        nickEdit.setPaddingDip(GUIDefs.PADDING_SMALL);
        nickEdit.setHighlightable(false);
        nickEdit.setFocusable(false);
        nickEdit.setVisibility(GONE);

        titleCenter.addView(nickEdit);

        int addiconpadd = GUIDefs.PADDING_SMALL;
        int addiconsize = GUIDefs.ICON_SIZE - (addiconpadd * 2);

        GUIRelativeLayout actionIconPad = new GUIRelativeLayout(context);
        actionIconPad.setPaddingDip(addiconpadd);

        titleFrame.addView(actionIconPad);

        actionIcon = new GUIIconView(context);
        actionIcon.setSizeDip(addiconsize, addiconsize);
        actionIcon.setVisibility(GONE);

        actionIcon.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onActionIconClicked();
            }
        });

        actionIconPad.addView(actionIcon);

        contentFrame = new GUIFrameLayout(context);
        contentFrame.setSizeDip(Simple.MP, Simple.MP);

        splitterFrame.addView(contentFrame);
    }

    public void setObjectTag(String objectTag)
    {
        this.objectTag = objectTag;
    }

    public void setTitleIcon(int resid)
    {
        titleIconResid = resid;

        titleIcon.setImageResource(resid);
    }

    public void setTitleIcon(String base64)
    {
        titleIcon.setImageResource(base64);
    }

    public int getTitleIconResid()
    {
        return titleIconResid;
    }

    public void setNameText(String text)
    {
        nameText.setText(text);
    }

    public String getNameText()
    {
        return nameText.getText().toString();
    }

    public void setNickInfo(String text)
    {
        nickInfo.setText(text);
        nickInfo.setVisibility(VISIBLE);

        nickEdit.setVisibility(GONE);
    }

    public void setNameEdit(String text, String hint, String toast)
    {
        nameEdit.setText(text);
        nameEdit.setHint(hint);
        nameEdit.setFocusable(true);
        nameEdit.setToastFocus(toast);
        nameEdit.setHighlightable(true);
        nameEdit.setVisibility(VISIBLE);

        nameText.setVisibility(GONE);
    }

    public void setNickEdit(String text, String hint, String toast)
    {
        if ((text != null) && text.equals(nameText.getText().toString()))
        {
            //
            // Unset bogus self repeating stuff.
            //

            text = "";
        }

        nickEdit.setText(text);
        nickEdit.setHint(hint);
        nickEdit.setFocusable(true);
        nickEdit.setToastFocus(toast);
        nickEdit.setHighlightable(true);
        nickEdit.setVisibility(VISIBLE);

        nickInfo.setVisibility(GONE);
    }

    public void setActionIconVisible(int resid, boolean visible)
    {
        actionIcon.setImageResource(resid);
        actionIcon.setVisibility(visible? VISIBLE : GONE);
        actionIcon.setFocusable(visible);
    }

    public void onNameEditFinished(View view)
    {
        Log.d(LOGTAG, "onNameEditFinished: STUB!");
    }

    public void onNickEditFinished(View view)
    {
        Log.d(LOGTAG, "onNickEditFinished: STUB!");
    }

    public void onActionIconClicked()
    {
        Log.d(LOGTAG, "onAddClicked: STUB!");
    }
}