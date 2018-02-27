package de.xavaro.android.tvpush;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.xavaro.android.common.Simple;

public class MainActivity extends AppCompatActivity
{
    private final static String LOGTAG = MainActivity.class.getSimpleName();
    private final IntentFilter intentFilter = new IntentFilter();

    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    WifiReceiver receiver;

    private SpeechRecognition recognition;
    private TextView voiceButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        this.setFinishOnTouchOutside(true);

        //if (getSupportActionBar() != null) getSupportActionBar().hide();

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.alpha = 1.0f;    // lower than one makes it more transparent
        params.dimAmount = 0.0f;  // set it higher if you want to dim behind the window

        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.format = PixelFormat.TRANSLUCENT;

        getWindow().setAttributes(params);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (height > width)
        {
            getWindow().setLayout((int) (width * 0.4), (int) (height * 0.4));
        }
        else
        {
            getWindow().setLayout((int) (width * 0.4), (int) (height * 0.4));
        }

        Log.d(LOGTAG, "onCreate: stringFromJNI=" + stringFromJNI());

        FrameLayout topframe = new FrameLayout(this);
        topframe.setBackgroundColor(0x88880000);
        setContentView(topframe);

        TextView heloButton = new TextView(this);
        heloButton.setText("HELO");
        heloButton.setTextSize(36);
        heloButton.setTextColor(Color.WHITE);
        heloButton.setPadding(50, 50, 50, 50);

        heloButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                RegistrationService.requestHello(view.getContext());
            }
        });

        topframe.addView(heloButton, new FrameLayout.LayoutParams(Simple.WC, Simple.WC, Gravity.TOP + Gravity.START));

        voiceButton = new TextView(this);
        voiceButton.setText("VOICE ON");
        voiceButton.setTextSize(36);
        voiceButton.setTextColor(Color.WHITE);
        voiceButton.setPadding(50, 50, 50, 50);

        voiceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                speechClick();
            }
        });

        topframe.addView(voiceButton, new FrameLayout.LayoutParams(Simple.WC, Simple.WC, Gravity.BOTTOM + Gravity.END));
    }

    private void speechClick()
    {
        if (recognition == null)
        {
            recognition = new SpeechRecognition(this);
        }

        String text = voiceButton.getText().toString();

        if (text.equals("VOICE ON"))
        {
            voiceButton.setText("VOICE OFF");
            recognition.startListening();
        }
        else
        {
            voiceButton.setText("VOICE ON");
            recognition.stopListening();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        receiver = new WifiReceiver();

        registerReceiver(receiver, intentFilter);

        /*
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess()
            {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank. Code for peer discovery goes in the
                // onReceive method, detailed below.
            }

            @Override
            public void onFailure(int reasonCode)
            {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
            }
        });
        */
    }

    @Override
    public void onPause()
    {
        super.onPause();

        unregisterReceiver(receiver);
    }

    public native String stringFromJNI();
}
