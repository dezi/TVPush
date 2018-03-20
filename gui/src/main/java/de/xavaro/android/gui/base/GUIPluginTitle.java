package de.xavaro.android.gui.base;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import de.xavaro.android.gui.views.GUIEditText;
import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.gui.views.GUIImageView;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUITextView;
import de.xavaro.android.gui.simple.Simple;

public class GUIPluginTitle extends GUIPlugin
{
    private final static String LOGTAG = GUIPluginTitle.class.getSimpleName();

    private GUITextView titleText;
    private GUIEditText titleEdit;
    private GUIImageView titleIcon;

    public GUIPluginTitle(Context context)
    {
        super(context);

        GUILinearLayout splitterFrame = new GUILinearLayout(context);
        splitterFrame.setOrientation(LinearLayout.VERTICAL);
        splitterFrame.setRoundedCorners(20, 0x88888888, Color.WHITE);
        splitterFrame.setPaddingDip(GUIDefs.PADDING_SMALL, GUIDefs.PADDING_ZERO, GUIDefs.PADDING_SMALL, GUIDefs.PADDING_SMALL);

        contentFrame.addView(splitterFrame);

        GUILinearLayout titleFrame = new GUILinearLayout(context);
        titleFrame.setOrientation(LinearLayout.HORIZONTAL);
        titleFrame.setSizeDip(Simple.MP, Simple.WC);

        splitterFrame.addView(titleFrame);

        titleIcon = new GUIImageView(context);
        titleIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        titleIcon.setSizeDip(50, 50);
        titleIcon.setPaddingDip(GUIDefs.PADDING_SMALL);

        titleFrame.addView(titleIcon);

        GUILinearLayout titleCenter = new GUILinearLayout(context);
        titleCenter.setOrientation(LinearLayout.HORIZONTAL);
        titleCenter.setGravity(Gravity.START + Gravity.CENTER_VERTICAL);
        titleCenter.setSizeDip(Simple.MP, Simple.MP);

        titleFrame.addView(titleCenter);

        titleText = new GUITextView(context);
        titleText.setPaddingDip(GUIDefs.PADDING_SMALL);
        titleText.setTextSizeDip(GUIDefs.FONTSIZE_HEADERS);

        titleCenter.addView(titleText);

        titleEdit = new GUIEditText(context)
        {
            @Override
            public void onHighlightFinished(View view)
            {
                onTitleEditFinished(view);
            }
        };

        titleEdit.setSizeDip(Simple.MP, Simple.WC);
        titleEdit.setPaddingDip(GUIDefs.PADDING_SMALL);
        titleEdit.setTextSizeDip(GUIDefs.FONTSIZE_HEADERS);
        titleEdit.setHighlightable(false);
        titleEdit.setFocusable(false);
        titleEdit.setVisibility(GONE);

        titleCenter.addView(titleEdit);

        contentFrame = new GUIFrameLayout(context);
        contentFrame.setSizeDip(Simple.MP, Simple.MP);

        splitterFrame.addView(contentFrame);
    }

    public void setTitleIcon(int resid)
    {
        titleIcon.setImageResource(resid);
    }

    public void setTitleText(String text)
    {
        titleText.setText(text);
    }

    public void setTitleEdit(String text, String hint, String toast)
    {
        if ((text != null) && text.equals(titleText.getText()))
        {
            //
            // Unset bogus self repeating stuff.
            //

            text = "";
        }

        titleEdit.setText(text);
        titleEdit.setHint(hint);
        titleEdit.setToast(toast);
        titleEdit.setFocusable(true);
        titleEdit.setHighlightable(true);
        titleEdit.setVisibility(VISIBLE);
    }

    public void onTitleEditFinished(View view)
    {
        Log.d(LOGTAG, "onTitleEditFinished: STUB!");
    }
}