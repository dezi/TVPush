package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIFrameLayout;

public class GUILocationWizzard extends GUIPlugin
{
    private final static String LOGTAG = GUILocationWizzard.class.getSimpleName();

    public final static int WIDTH = 300;
    public final static int HEIGTH = 150;

    private Marker marker;
    private GoogleMap map;
    private LatLng coordinates = new LatLng(53.568208, 10.140186);

    public GUILocationWizzard(Context context)
    {
        super(context);
    }

    private boolean takeFoucus = false;

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
                Log.d(LOGTAG, "onKeyDown: mainFrame takeFoucus=" + takeFoucus);

                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
                {
                    takeFoucus = !takeFoucus;
                }
                else
                {
                    moveMap(keyCode);
                }

                return takeFoucus;
            }
        };

        mainFrame.setFocusable(true);
        mainFrame.setRoundedCorners(20, 0xff00ffff);
        pluginFrame.addView(mainFrame);

        GUIFrameLayout moveFrame = new GUIFrameLayout(getContext());
        moveFrame.setFocusable(false);
        mainFrame.addView(moveFrame);

        final MapView mapView = new MapView(getContext());
        mapView.setLayoutParams(new FrameLayout.LayoutParams(Simple.MP, Simple.MP));
        mapView.setBackgroundColor(0xff00ff00);
        mapView.onCreate(null);
        moveFrame.addView(mapView);

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


    private void moveMap(int keyCode)
    {
        if (map == null) return;

        Log.d(LOGTAG, "moveMap:");

        Double moveParm = 0.000001;

        Double lat = coordinates.latitude;
        Double lon = coordinates.longitude;

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        {
            lon -= moveParm;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
        {
            lat += moveParm;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            lon += moveParm;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        {
            lat -= moveParm;
        }

        coordinates = new LatLng(lat, lon);

        marker.setPosition(coordinates);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 20));

//        Location targetLocation = new Location(LocationManager.GPS_PROVIDER);//provider name is unnecessary
//        targetLocation.setLatitude(lat);//your coords of course
//        targetLocation.setLongitude(lon);
//
//        Log.d(LOGTAG, "moveMap: hasAltitude=" + targetLocation.hasAltitude());
//        Log.d(LOGTAG, "moveMap: altitude=" + targetLocation.getAltitude());
    }
}