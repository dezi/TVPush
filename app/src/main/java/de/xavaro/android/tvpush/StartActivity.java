package de.xavaro.android.tvpush;

import android.widget.TextView;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import de.xavaro.android.common.ActBase;

public class StartActivity extends ActBase
{
    private final static String LOGTAG = StartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate:");

        super.onCreate(savedInstanceState);

        TextView dummy = new TextView(this);
        dummy.setTextSize(36);
        dummy.setTextColor(Color.WHITE);
        dummy.setText("Hallo");

        topframe.addView(dummy);
    }
}
