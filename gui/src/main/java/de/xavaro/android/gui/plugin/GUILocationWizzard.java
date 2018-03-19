package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.widget.FrameLayout;
import android.view.KeyEvent;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.iot.base.IOTObject;

public class GUILocationWizzard extends GUIPlugin
{
    private final static String LOGTAG = GUILocationWizzard.class.getSimpleName();

    public final static int DEFAULT_WIDTH = 500;
    public final static int DEFAULT_HEIGTH = 300;

    public final static int INITIAL_ZOOM = 20;
    public final static double MOVE_STEP = 0.000002;

    private IOTObject iotObject;

    private GoogleMap map;
    private Marker marker;
    private MapView mapView;
    private LatLng coordinates;

    private boolean haveHighlight;

    public GUILocationWizzard(Context context)
    {
        super(context);
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate:");

        super.onCreate();

        GUIFrameLayout mainFrame = new GUIFrameLayout(getContext())
        {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event)
            {
                Log.d(LOGTAG, "onKeyDown: mainFrame haveHighlight=" + haveHighlight);

                boolean usedKey = false;

                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
                {
                    haveHighlight = !haveHighlight;
                    setHighlight(haveHighlight);
                    usedKey = true;
                }
                else
                {
                    if (haveHighlight)
                    {
                        usedKey = moveMap(keyCode);
                    }
                }

                return usedKey;
            }
        };

        mainFrame.setFocusable(true);
        mainFrame.setRoundedCorners(20, 0xff00ffff);

        pluginFrame.addView(mainFrame);

        GUIFrameLayout moveFrame = new GUIFrameLayout(getContext());
        moveFrame.setFocusable(false);

        mainFrame.addView(moveFrame);

        mapView = new MapView(getContext());
        mapView.setLayoutParams(new FrameLayout.LayoutParams(Simple.MP, Simple.MP));
        mapView.setBackgroundColor(0xff00ff00);
        mapView.onCreate(null);

        moveFrame.addView(mapView);
    }

    public void setCoordinates(Double lat, Double lon, Float alt)
    {
        coordinates = new LatLng(lat, lon);

        mapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                Log.d(LOGTAG, "onMapReady:");

                map = googleMap;

                marker = googleMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .title("Marker"));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 20));
                mapView.onResume();
            }
        });
    }

    public void setIOTObject(IOTObject iotObject)
    {
        this.iotObject = iotObject;
    }

    private boolean moveMap(int keyCode)
    {
        if (map == null) return false;

        boolean usedkey = false;

        Double lat = coordinates.latitude;
        Double lon = coordinates.longitude;

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        {
            lon -= MOVE_STEP;
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
        {
            lat += MOVE_STEP;
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            lon += MOVE_STEP;
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        {
            lat -= MOVE_STEP;
            usedkey = true;
        }

        if (usedkey)
        {
            coordinates = new LatLng(lat, lon);
            marker.setPosition(coordinates);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 20));
        }

        return usedkey;
    }
}