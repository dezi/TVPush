package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIFrameLayout;

public class GUILocationWizzard extends GUIPlugin
{
    private final static String LOGTAG = GUILocationWizzard.class.getSimpleName();

    public final static int WIDTH = 600;
    public final static int HEIGTH = 400;

    public GUILocationWizzard(Context context)
    {
        super(context);
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate:");

        super.onCreate();

        GUIFrameLayout mainFrame = new GUIFrameLayout(getContext());
        mainFrame.setRoundedCorners(20, 0xff00ffff);
        pluginFrame.addView(mainFrame);

        final MapView map = new MapView(getContext());
        map.setLayoutParams(new FrameLayout.LayoutParams(Simple.MP, Simple.MP));
        map.setBackgroundColor(0xff00ff00);
        map.onCreate(null);
        mainFrame.addView(map);

        map.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                Log.d(LOGTAG, "onMapReady:");
                LatLng coordinates = new LatLng(53.568208, 10.140186);

                googleMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .title("Marker"));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 20));
                map.onResume();
            }
        });
    }
}