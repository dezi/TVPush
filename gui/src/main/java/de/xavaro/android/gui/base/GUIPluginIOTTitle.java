package de.xavaro.android.gui.base;

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
import de.xavaro.android.gui.R;
import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.things.IOTDevice;

public class GUIPluginIOTTitle extends GUIPluginIOT
{
    private final static String LOGTAG = GUIPluginIOTTitle.class.getSimpleName();

    private GUITextView titleText;
    private GUIEditText titleEdit;

    public GUIPluginIOTTitle(Context context)
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

        GUIImageView titleIcon = new GUIImageView(context);
        titleIcon.setImageResource(R.drawable.device_tv_100);
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
        titleEdit.setFocusable(true);
        titleEdit.setHighlightable(true);

        titleCenter.addView(titleEdit);

        contentFrame = new GUIFrameLayout(context);
        contentFrame.setSizeDip(Simple.MP, Simple.MP);

        splitterFrame.addView(contentFrame);
    }

    @Override
    public void setIOTObject(IOTObject iotObject)
    {
        super.setIOTObject(iotObject);

        if (iotObject instanceof IOTDevice)
        {
            String hint = "Bitte Nicknamen hier eintragen";

            String toast = "Sprechen Sie jetzt die Nicknamen ein"
                    + " oder drücken Sie "
                    + GUIDefs.UTF_OK
                    + " zum Bearbeiten";

            setTitleText(((IOTDevice) iotObject).name);
            setTitleEdit(((IOTDevice) iotObject).nick, hint, toast);
        }
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
    }

    private int saveNick(String newNick)
    {
        if (iotObject instanceof IOTDevice)
        {
            IOTDevice saveme = new IOTDevice(iotObject.uuid);

            saveme.nick = newNick;

            return saveIOTObject(saveme);
        }

        return IOTDefs.IOT_SAVE_FAILED;
    }

    public void onTitleEditFinished(View view)
    {
        saveNick(((GUIEditText) view).getText().toString());
    }
}