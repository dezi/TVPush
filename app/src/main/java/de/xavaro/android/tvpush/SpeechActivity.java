package de.xavaro.android.tvpush;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.xavaro.android.common.ActBase;
import de.xavaro.android.common.Simple;

public class SpeechActivity extends ActBase
{
    private final static String LOGTAG = SpeechActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        RelativeLayout bottom = new RelativeLayout(this);
        bottom.setGravity(Gravity.BOTTOM);
        Simple.setSizeDip(bottom, Simple.MP, Simple.MP);

        topframe.addView(bottom);

        RelativeLayout center = new RelativeLayout(this);
        center.setGravity(Gravity.CENTER_VERTICAL + Gravity.CENTER_HORIZONTAL);
        Simple.setSizeDip(center, Simple.MP, Simple.WC);
        Simple.setMarginDip(center, Simple.PADDING_XLARGE, Simple.PADDING_NORMAL, Simple.PADDING_XLARGE, Simple.PADDING_NORMAL);
        Simple.setRoundedCorners(center, Simple.ROUNDED_MEDIUM, 0x11ffffff);

        bottom.addView(center);

        TextView speech = new TextView(this);
        speech.setTextColor(Color.WHITE);
        Simple.setTextSizeDip(speech, 36);
        Simple.setPaddingDip(speech, Simple.PADDING_SMALL);

        center.addView(speech);

        speech.setText("Test test test");
    }
}
