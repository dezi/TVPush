package de.xavaro.android.gui.wizzards;

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

import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.gui.base.GUIPluginTitleIOT;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUI;

import de.xavaro.android.gui.simple.Simple;

import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.things.IOTDevice;

public class GUILocationWizzard extends GUIPluginTitleIOT
{
    private final static String LOGTAG = GUILocationWizzard.class.getSimpleName();

    private GUIFrameLayout mapFrame;
    private MapView mapView;
    private GoogleMap map;
    private Marker marker;

    private LatLng coordinates;
    private Float altitude;

    private int zoom;

    public GUILocationWizzard(Context context)
    {
        super(context);

        setPluginPositionDip(500, DEFAULT_TOP);

        mapFrame = new GUIFrameLayout(context)
        {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event)
            {
                return onKeyDownDoit(keyCode, event);
            }
        };

        mapFrame.setSizeDip(Simple.MP, Simple.MP);
        mapFrame.setFocusable(true);
        mapFrame.setToast("Drücken Sie " + GUIDefs.UTF_OK + " um das TV-Gerät zu positionieren");

        contentFrame.addView(mapFrame);

        mapView = new MapView(getContext());
        mapView.setLayoutParams(new FrameLayout.LayoutParams(Simple.MP, Simple.MP));
        mapView.onCreate(null);

        mapFrame.addView(mapView);
    }

    private boolean onKeyDownDoit(int keyCode, KeyEvent event)
    {
        Log.d(LOGTAG, "onKeyDown: event=" + event);

        boolean usedKey = false;

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
        {
            if (mapFrame.getHighlight())
            {
                saveLocation();
            }
            else
            {
                String help = "Bewegen mit"
                        + " "
                        + GUIDefs.UTF_MOVE
                        + " "
                        + ", zoomen mit"
                        + " "
                        + GUIDefs.UTF_ZOOMIN
                        + " "
                        + "und"
                        + " "
                        + GUIDefs.UTF_ZOOMOUT
                        ;

                GUI.instance.desktopActivity.displayToastMessage(help, 10, true);
            }

            mapFrame.setHighlight(false);
            usedKey = true;
        }
        else
        {
            if (mapFrame.getHighlight())
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
        zoom = GUIDefs.MAP_INITIAL_ZOOM;

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

    @Override
    public void setIOTObject(IOTObject iotObject)
    {
        super.setIOTObject(iotObject);

        if (iotObject instanceof IOTDevice)
        {
            IOTDevice device = (IOTDevice) iotObject;

            if (device.hasCapability("fixed"))
            {
                if ((device.fixedLatFine != null)
                        && (device.fixedLonFine != null)
                        && (device.fixedAltFine != null))
                {
                    setCoordinates(
                            device.fixedLatFine,
                            device.fixedLonFine,
                            device.fixedAltFine);
                }
                else
                {
                    if ((device.fixedLatCoarse != null)
                            && (device.fixedLonCoarse != null)
                            && (device.fixedAltCoarse != null))
                    {
                        setCoordinates(
                                device.fixedLatCoarse,
                                device.fixedLonCoarse,
                                device.fixedAltCoarse);
                    }
                }
            }
        }
    }

    private int saveLocation()
    {
        if (iotObject instanceof IOTDevice)
        {
            IOTDevice saveme = new IOTDevice(iotObject.uuid);

            saveme.fixedLatFine = coordinates.latitude;
            saveme.fixedLonFine = coordinates.longitude;
            saveme.fixedAltFine = altitude;

            return saveIOTObject(saveme);
        }

        return IOTDefs.IOT_SAVE_FAILED;
    }

    private boolean moveMap(int keyCode)
    {
        if (map == null) return false;

        boolean usedkey = false;

        Double lat = coordinates.latitude;
        Double lon = coordinates.longitude;

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        {
            lon -= GUIDefs.MAP_MOVE_STEP;
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
        {
            lat += GUIDefs.MAP_MOVE_STEP;
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            lon += GUIDefs.MAP_MOVE_STEP;
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        {
            lat -= GUIDefs.MAP_MOVE_STEP;
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