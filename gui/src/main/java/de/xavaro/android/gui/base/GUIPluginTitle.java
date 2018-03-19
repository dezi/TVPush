package de.xavaro.android.gui.base;

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
import de.xavaro.android.gui.R;

public class GUIPluginTitle extends GUIPlugin
{
    private final static String LOGTAG = GUIPluginTitle.class.getSimpleName();

    public GUIPluginTitle(Context context)
    {
        super(context);

        GUILinearLayout splitterFrame = new GUILinearLayout(context);
        splitterFrame.setOrientation(LinearLayout.VERTICAL);
        splitterFrame.setPaddingDip(10, 0, 10, 10);
        splitterFrame.setRoundedCorners(20, 0x88888888, Color.WHITE);

        contentFrame.addView(splitterFrame);

        GUILinearLayout titleFrame = new GUILinearLayout(context);
        titleFrame.setOrientation(LinearLayout.HORIZONTAL);
        titleFrame.setSizeDip(Simple.MP, Simple.WC);

        splitterFrame.addView(titleFrame);

        GUIImageView titleIcon = new GUIImageView(context);
        titleIcon.setImageResource(R.drawable.device_tv_100);
        titleIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        titleIcon.setSizeDip(50, 50);
        titleIcon.setPaddingDip(10);

        titleFrame.addView(titleIcon);

        GUILinearLayout titleCenter = new GUILinearLayout(context);
        titleCenter.setOrientation(LinearLayout.HORIZONTAL);
        titleCenter.setGravity(Gravity.START + Gravity.CENTER_VERTICAL);
        titleCenter.setSizeDip(Simple.MP, Simple.MP);

        titleFrame.addView(titleCenter);

        GUITextView titleText = new GUITextView(context);
        titleText.setText("Dezi's Dominator-XL");
        titleText.setPaddingDip(5);
        titleText.setTextSizeDip(16);

        titleCenter.addView(titleText);

        GUIEditText titleEdit = new GUIEditText(context);
        titleEdit.setHint("Nicknamen hier eintragen");
        titleEdit.setSizeDip(Simple.MP, Simple.WC);
        titleEdit.setPaddingDip(20, 5, 20, 5);
        titleEdit.setTextSizeDip(16);
        titleEdit.setFocusable(true);

        titleCenter.addView(titleEdit);

        contentFrame = new GUIFrameLayout(context);
        contentFrame.setSizeDip(Simple.MP, Simple.MP);

        splitterFrame.addView(contentFrame);
    }
}