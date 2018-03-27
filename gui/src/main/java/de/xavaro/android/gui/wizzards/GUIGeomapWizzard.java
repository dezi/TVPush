package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.Gravity;
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

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.gui.base.GUIPluginTitleIOT;
import de.xavaro.android.gui.base.GUIDefs;

import de.xavaro.android.gui.simple.Simple;

import de.xavaro.android.gui.views.GUIImageView;
import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.things.IOTDevice;

public class GUIGeomapWizzard extends GUIPluginTitleIOT
{
    private final static String LOGTAG = GUIGeomapWizzard.class.getSimpleName();

    private GUIFrameLayout mapFrame;
    private GUIFrameLayout clickFrame;
    private GUIImageView crosshair;
    private MapView mapView;
    private GoogleMap map;
    private Marker marker;

    private LatLng coordinates;
    private Double altitude;

    private int zoom;

    public GUIGeomapWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, true, 2, Gravity.RIGHT);

        mapFrame = new GUIFrameLayout(context)
        {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event)
            {
                return onKeyDownDoit(keyCode, event) || super.onKeyDown(keyCode, event);
            }

            @Override
            public void onHighlightChanged(View view, boolean highlight)
            {
                if (highlight)
                {
                    clickFrame.setVisibility(GONE);
                    crosshair.setVisibility(VISIBLE);
                    marker.setVisible(false);

                    Log.d(LOGTAG, "onHighlightChanged: old position"
                            + " lat=" + map.getCameraPosition().target.latitude
                            + " lon=" + map.getCameraPosition().target.longitude
                    );
                }
                else
                {
                    crosshair.setVisibility(GONE);
                    clickFrame.setVisibility(VISIBLE);
                    marker.setVisible(true);

                    Log.d(LOGTAG, "onHighlightChanged: new position"
                        + " lat=" + map.getCameraPosition().target.latitude
                        + " lon=" + map.getCameraPosition().target.longitude
                    );

                    setCoordinates(
                            map.getCameraPosition().target.latitude,
                            map.getCameraPosition().target.longitude,
                            altitude);

                    saveLocation();
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b)
            {
                super.onLayout(changed, l, t, r, b);

                if (crosshair.getVisibility() == GONE)
                {
                    int width = (r - l);
                    int height = (b - t);

                    int nettoWidth = width - getPaddingLeft() - getPaddingRight();
                    int nettoHeight = height - getPaddingTop() - getPaddingLeft();

                    Log.d(LOGTAG, "onLayout: nettoWidth=" + nettoWidth + " nettoHeight=" + nettoHeight);

                    crosshair.setMarginLeftDip(Simple.pxToDip(nettoWidth / 2) - (GUIDefs.CROSSHAIR_SIZE / 2));
                    crosshair.setMarginTopDip(Simple.pxToDip(nettoHeight / 2) - (GUIDefs.CROSSHAIR_SIZE / 2));
                }
            }
        };

        final String toastFocus = ""
                + "Drücken Sie "
                + GUIDefs.UTF_OK
                + " um das Gerät zu positionieren";

        final String toastHighlight = ""
                + "Bewegen mit" + " " + GUIDefs.UTF_MOVE + " "
                + ", zoomen mit" + " " + GUIDefs.UTF_ZOOMIN
                + " und" + " " + GUIDefs.UTF_ZOOMOUT;

        mapFrame.setSizeDip(Simple.MP, Simple.MP);
        mapFrame.setHighlightable(true);
        mapFrame.setFocusable(true);
        mapFrame.setToastFocus(toastFocus);
        mapFrame.setToastHighlight(toastHighlight);

        contentFrame.addView(mapFrame);

        mapView = new MapView(getContext());
        mapView.setLayoutParams(new FrameLayout.LayoutParams(Simple.MP, Simple.MP));
        mapView.onCreate(null);

        mapFrame.addView(mapView);

        crosshair = new GUIImageView(getContext());
        crosshair.setImageResource(R.drawable.crosshair_500);
        crosshair.setSizeDip(GUIDefs.CROSSHAIR_SIZE,GUIDefs.CROSSHAIR_SIZE);
        crosshair.setVisibility(GONE);

        mapFrame.addView(crosshair);

        clickFrame = new GUIFrameLayout(context);

        mapFrame.addView(clickFrame);

        clickFrame.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(LOGTAG, "onClick: clickFrame.");

                mapFrame.setHighlight(true);
            }
        });

        crosshair.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(LOGTAG, "onClick: crosshair.");

                mapFrame.setHighlight(false);
            }
        });
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

    public void setCoordinates(Double lat, Double lon, Double alt)
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
        if (coordinates != null)
        {
            if (iotObject instanceof IOTDevice)
            {
                IOTDevice saveme = new IOTDevice(iotObject.uuid);

                saveme.fixedLatFine = coordinates.latitude;
                saveme.fixedLonFine = coordinates.longitude;
                saveme.fixedAltFine = altitude;

                return saveIOTObject(saveme);
            }
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