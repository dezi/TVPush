package de.xavaro.android.gui.base;

import android.content.Context;
import android.widget.LinearLayout;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUITextView;

public class GUIPluginTitle extends GUIPlugin
{
    private final static String LOGTAG = GUIPluginTitle.class.getSimpleName();

    public GUILinearLayout splitterFrame;

    public GUIPluginTitle(Context context)
    {
        super(context);

        splitterFrame = new GUILinearLayout(context);
        splitterFrame.setOrientation(LinearLayout.VERTICAL);

        contentFrame.addView(splitterFrame);

        GUILinearLayout titleFrame = new GUILinearLayout(context);
        titleFrame.setOrientation(LinearLayout.HORIZONTAL);
        titleFrame.setSizeDip(Simple.MP, Simple.WC);
        titleFrame.setBackgroundColor(0x88008800);

        splitterFrame.addView(titleFrame);

        GUITextView titleText = new GUITextView(context);
        titleText.setText("Title:");

        titleFrame.addView(titleText);

        contentFrame = new GUIFrameLayout(context);
        contentFrame.setSizeDip(Simple.MP, Simple.MP);

        splitterFrame.addView(contentFrame);
    }
}
