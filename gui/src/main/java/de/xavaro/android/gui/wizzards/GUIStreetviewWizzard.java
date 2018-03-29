package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.util.Log;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLink;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.plugin.GUIPluginTitle;
import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.gui.simple.Simple;

public class GUIStreetviewWizzard extends GUIPluginTitle
{
    private final static String LOGTAG = GUIStreetviewWizzard.class.getSimpleName();

    private GUIFrameLayout mapFrame;
    private GUIFrameLayout clickFrame;

    private StreetViewPanoramaView panoramaView;
    private StreetViewPanoramaLocation location;
    private StreetViewPanoramaCamera camera;
    private StreetViewPanorama panorama;

    private LatLng coordinates;

    private int zoom;

    public GUIStreetviewWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false, Simple.isTV() ? 3 : 2, Gravity.START);

        setTitleIcon(R.drawable.ping_440);
        setTitleText("Streetview Wizzard");

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
                }
                else
                {
                    clickFrame.setVisibility(VISIBLE);
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
        mapFrame.setHighlightable(false);
        mapFrame.setFocusable(true);
        mapFrame.setToastFocus(toastFocus);
        mapFrame.setToastHighlight(toastHighlight);

        contentFrame.addView(mapFrame);

        clickFrame = new GUIFrameLayout(context);

        mapFrame.addView(clickFrame);

        clickFrame.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });

        coordinates = new LatLng(51.5099269,-0.1349173);

        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
        options.position(coordinates);
        options.zoomGesturesEnabled(true);
        options.panningGesturesEnabled(true);
        options.userNavigationEnabled(true);
        options.streetNamesEnabled(true);

        panoramaView = new StreetViewPanoramaView(getContext(), options);
        panoramaView.onCreate(null);

        panoramaView.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback()
        {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama)
            {
                Log.d(LOGTAG, "onStreetViewPanoramaReady:");

                panorama = streetViewPanorama;
                location = panorama.getLocation();
                camera = panorama.getPanoramaCamera();

                mapFrame.setHighlight(true);
            }
        });

        mapFrame.addView(panoramaView);
    }

    private boolean onKeyDownDoit(int keyCode, KeyEvent event)
    {
        //Log.d(LOGTAG, "onKeyDown: event=" + event);

        boolean usedKey = false;

        if (mapFrame.getHighlight())
        {
            usedKey = moveCamera(keyCode);
        }

        return usedKey;
    }

    public void setCoordinates(Double lat, Double lon, Double alt)
    {
        coordinates = new LatLng(lat, lon);

        zoom = GUIDefs.MAP_INITIAL_ZOOM;
    }

    private boolean moveCamera(int keyCode)
    {
        boolean usedkey = false;

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
        {
            Log.d(LOGTAG, "moveCamera: center zoom=" + camera.zoom);

            coordinates = computeOffset(coordinates, 10, camera.bearing);

            panorama.setPosition(coordinates);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        {
            float bearing = camera.bearing - 10;

            Log.d(LOGTAG, "moveCamera: bearing=" + bearing);

            camera = new StreetViewPanoramaCamera.Builder(camera)
                    .bearing(bearing)
                    .build();

            panorama.animateTo(camera, 250);

            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            float bearing = camera.bearing + 10;

            Log.d(LOGTAG, "moveCamera: bearing=" + bearing);

            camera = new StreetViewPanoramaCamera.Builder(camera)
                    .bearing(bearing)
                    .build();

            panorama.animateTo(camera, 250);

            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
        {
            float tilt = camera.tilt + 10;
            tilt = (tilt > 90) ? 90 : tilt;

            Log.d(LOGTAG, "moveCamera: tilt=" + tilt);

            camera = new StreetViewPanoramaCamera.Builder(camera)
                    .tilt(tilt)
                    .build();

            panorama.animateTo(camera, 250);

            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        {
            float tilt = camera.tilt - 10;
            tilt = (tilt < -90) ? -90 : tilt;

            Log.d(LOGTAG, "moveCamera: tilt=" + tilt);

            camera = new StreetViewPanoramaCamera.Builder(camera)
                    .tilt(tilt)
                    .build();

            panorama.animateTo(camera, 250);

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

        return usedkey;
    }

    static final double EARTH_RADIUS = 6371009;

    public static LatLng computeOffset(LatLng from, double distance, double heading)
    {
        distance /= EARTH_RADIUS;
        heading = Math.toRadians(heading);
        double fromLat = Math.toRadians(from.latitude);
        double fromLng = Math.toRadians(from.longitude);
        double cosDistance = Math.cos(distance);
        double sinDistance = Math.sin(distance);
        double sinFromLat = Math.sin(fromLat);
        double cosFromLat = Math.cos(fromLat);
        double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * Math.cos(heading);
        double dLng = Math.atan2(
                sinDistance * cosFromLat * Math.sin(heading),
                cosDistance - sinFromLat * sinLat);

        return new LatLng(Math.toDegrees(Math.asin(sinLat)), Math.toDegrees(fromLng + dLng));
    }
}