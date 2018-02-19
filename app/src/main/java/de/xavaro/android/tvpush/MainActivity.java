package de.xavaro.android.tvpush;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private final static String LOGTAG = MainActivity.class.getSimpleName();

    static
    {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(LOGTAG, "onCreate: stringFromJNI=" + stringFromJNI());
    }

    public native String stringFromJNI();
}
