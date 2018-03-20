package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;
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

import de.xavaro.android.iot.base.IOT;
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

        setPluginPositionDip(400, DEFAULT_TOP);

        final String toast1 = "Drücken Sie " + GUIDefs.UTF_OK + " um das TV-Gerät zu positionieren";

        final String toast2 = "Bewegen mit" + " " + GUIDefs.UTF_MOVE + " "
                + ", zoomen mit" + " " + GUIDefs.UTF_ZOOMIN
                + " " + "und" + " " + GUIDefs.UTF_ZOOMOUT;


        mapFrame = new GUIFrameLayout(context)
        {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event)
            {
                return onKeyDownDoit(keyCode, event) || super.onKeyDown(keyCode, event);
            }

            @Override
            public void onHighlightStarted(View view)
            {
                GUI.instance.desktopActivity.displayToastMessage(toast2, 10, false);
            }

            @Override
            public void onHighlightFinished(View view)
            {
                saveLocation();
            }
        };

        mapFrame.setSizeDip(Simple.MP, Simple.MP);
        mapFrame.setHighlightable(true);
        mapFrame.setFocusable(true);
        mapFrame.setToast(toast1);

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

        if (mapFrame.getHighlight())
        {
            usedKey = moveMap(keyCode);
        }

        return usedKey;
    }

    public void setCoordinates(Double lat, Double lon, Float alt)
    {
        coordinates = new LatLng(lat, lon);
        altitude = alt;

        zoom = GUIDefs.MAP_INITIAL_ZOOM;

        mapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                Log.d(LOGTAG, "onMapReady:");

                map = googleMap;

                if (marker == null)
                {
                    marker = map.addMarker(new MarkerOptions().position(coordinates));
                }
                else
                {
                    marker.setPosition(coordinates);
                }

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoom));

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

                    return;
                }

                if ((device.fixedLatCoarse != null)
                        && (device.fixedLonCoarse != null)
                        && (device.fixedAltCoarse != null))
                {
                    setCoordinates(
                            device.fixedLatCoarse,
                            device.fixedLonCoarse,
                            device.fixedAltCoarse);

                    return;
                }
            }

            //
            // Fallback to running device.
            //

            if ((IOT.device != null) && IOT.device.hasCapability("fixed"))
            {
                if ((IOT.device.fixedLatFine != null)
                        && (IOT.device.fixedLonFine != null)
                        && (IOT.device.fixedAltFine != null))
                {
                    setCoordinates(
                            IOT.device.fixedLatFine,
                            IOT.device.fixedLonFine,
                            IOT.device.fixedAltFine);

                    return;
                }

                if ((IOT.device.fixedLatCoarse != null)
                        && (IOT.device.fixedLonCoarse != null)
                        && (IOT.device.fixedAltCoarse != null))
                {
                    setCoordinates(
                            IOT.device.fixedLatCoarse,
                            IOT.device.fixedLonCoarse,
                            IOT.device.fixedAltCoarse);

                    return;
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