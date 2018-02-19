package de.xavaro.android.tvpush;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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

        FrameLayout topframe = new FrameLayout(this);
        setContentView(topframe);

        TextView button = new TextView(this);
        button.setText("HELO");
        button.setPadding(50, 50, 50 ,50 );

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                RegistrationService.requestHello();
            }
        });

        topframe.addView(button);
    }

    public native String stringFromJNI();
}
