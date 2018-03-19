package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.widget.FrameLayout;
import android.view.KeyEvent;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.simple.Simple;

import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

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
    private Float altitude;
    private int zoom;

    private boolean haveHighlight;

    public GUILocationWizzard(Context context)
    {
        super(context);

        contentFrame.setFocusable(true);

        mapView = new MapView(getContext());
        mapView.setLayoutParams(new FrameLayout.LayoutParams(Simple.MP, Simple.MP));
        mapView.setBackgroundColor(0xff00ff00);
        mapView.onCreate(null);

        contentFrame.addView(mapView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d(LOGTAG, "onKeyDown: event=" + event);

        boolean usedKey = false;

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
        {
            if (haveHighlight)
            {
                if (saveIOTObject())
                {
                    String mess = "Gespeichert";
                    GUI.instance.desktopActivity.displayToastMessage(mess, 2, true);
                }
                else
                {
                    String mess = "Speichern fehlgeschlagen";
                    GUI.instance.desktopActivity.displayToastMessage(mess, 10, true);
                }
            }
            else
            {
                String help = "Bewegen mit"
                        + " "
                        + GUIDefs.UTF_LEFT + GUIDefs.UTF_RIGHT
                        + GUIDefs.UTF_UP + GUIDefs.UTF_DOWN
                        + " "
                        + ", zoomen mit"
                        + " "
                        + GUIDefs.UTF_REWIND
                        + " "
                        + "und"
                        + " "
                        + GUIDefs.UTF_FAST_FORWARD
                        ;

                GUI.instance.desktopActivity.displayToastMessage(help, 10, true);
            }

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

    public void setCoordinates(Double lat, Double lon, Float alt)
    {
        altitude = alt;
        coordinates = new LatLng(lat, lon);
        zoom = INITIAL_ZOOM;

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

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoom));
                mapView.onResume();
            }
        });
    }

    public void setIOTObject(IOTObject iotObject)
    {
        this.iotObject = iotObject;
    }

    private boolean saveIOTObject()
    {
        if (iotObject instanceof IOTDevice)
        {
            IOTDevice saveme = new IOTDevice(iotObject.uuid);

            saveme.fixedLatFine = coordinates.latitude;
            saveme.fixedLonFine = coordinates.longitude;
            saveme.fixedAltFine = altitude;

            return IOTDevices.addEntry(saveme, true);
        }

        return false;
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

        if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND)
        {
            if (zoom > 15) zoom -= 1;
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
        {
            if (zoom < 20) zoom += 1;
            usedkey = true;
        }

        if (usedkey)
        {
            coordinates = new LatLng(lat, lon);
            marker.setPosition(coordinates);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoom));
        }

        return usedkey;
    }
}