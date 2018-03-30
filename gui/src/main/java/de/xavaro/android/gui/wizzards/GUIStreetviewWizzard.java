package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.util.Log;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLink;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewSource;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import de.xavaro.android.gui.plugin.GUIPluginTitle;
import de.xavaro.android.gui.smart.GUIStreetViewService;
import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.R;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.iot.simple.Json;

public class GUIStreetviewWizzard extends GUIPluginTitle
{
    private final static String LOGTAG = GUIStreetviewWizzard.class.getSimpleName();

    private GUIFrameLayout mapFrame;
    private GUIFrameLayout clickFrame;
    private GUIStreetViewService streetViewService;

    private StreetViewPanoramaLocation location;
    private StreetViewPanoramaCamera camera;
    private StreetViewPanorama panorama;

    private LatLng coordinates;

    private GUIFrameLayout[] nextPanoramaHints = new GUIFrameLayout[ 16 ];

    private JSONArray otherPanoramas;

    public GUIStreetviewWizzard(Context context)
    {
        super(context);

        setCoordinates(51.5099272,-0.1349173);

        setIsWizzard(true, false, Simple.isTV() ? 3 : 2, Gravity.START);

        setTitleIcon(R.drawable.wizzard_streetview_550);
        setTitleText("Streetview Wizzard");

        streetViewService = new GUIStreetViewService(context);
        streetViewService.setGUIStreetViewServiceCallback(new GUIStreetViewService.GUIStreetViewServiceCallback()
        {
            @Override
            public void onDataReceived(String status, JSONObject data)
            {
                onPanoramaDataReceived(status, data);
            }
        });

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
        mapFrame.setHighlightable(true);
        mapFrame.setFocusable(true);
        mapFrame.setToastFocus(toastFocus);
        mapFrame.setToastHighlight(toastHighlight);

        contentFrame.addView(mapFrame);

        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
        options.streetNamesEnabled(true);
        options.zoomGesturesEnabled(true);
        options.userNavigationEnabled(false);
        options.panningGesturesEnabled(true);

        StreetViewPanoramaView panoramaView = new StreetViewPanoramaView(getContext(), options);
        panoramaView.onCreate(null);

        panoramaView.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback()
        {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama)
            {
                Log.d(LOGTAG, "onStreetViewPanoramaReady:");

                panorama = streetViewPanorama;
                camera = panorama.getPanoramaCamera();

                panorama.setPosition(coordinates,  StreetViewSource.DEFAULT);

                panorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener()
                {
                    @Override
                    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation)
                    {
                        otherPanoramas = null;

                        location = streetViewPanoramaLocation;

                        streetViewService.evaluate(
                                location.position.latitude,
                                location.position.longitude,
                                200);

                        showNextPanoramas();
                    }
                });

                panorama.setOnStreetViewPanoramaCameraChangeListener(new StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener()
                {
                    @Override
                    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera)
                    {
                        camera = streetViewPanoramaCamera;

                        showNextPanoramas();
                    }
                });

                panorama.setOnStreetViewPanoramaClickListener(new StreetViewPanorama.OnStreetViewPanoramaClickListener()
                {
                    @Override
                    public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation streetViewPanoramaOrientation)
                    {
                        Log.d(LOGTAG, "onStreetViewPanoramaClick: orient=" + streetViewPanoramaOrientation);
                        camera = panorama.getPanoramaCamera();
                        Log.d(LOGTAG, "onStreetViewPanoramaClick: camera=" + camera);

                        coordinates = computeOffset(location.position, 20, streetViewPanoramaOrientation.bearing);
                        panorama.setPosition(coordinates,  StreetViewSource.DEFAULT);

                    }
                });

                if (Simple.isTV()) mapFrame.setHighlight(true);
            }
        });

        mapFrame.addView(panoramaView);

        clickFrame = new GUIFrameLayout(context);
        clickFrame.setSizeDip(Simple.MP, Simple.MP);

        mapFrame.addView(clickFrame);

        /*
        clickFrame.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(LOGTAG, "onClick: ....");

            }
        });
        */

        for (int inx = 0; inx < nextPanoramaHints.length; inx++)
        {
            nextPanoramaHints[ inx ] = new GUIFrameLayout(getContext());
            nextPanoramaHints[ inx ].setSizeDip(50,50);
            nextPanoramaHints[ inx ].setVisibility(GONE);

            nextPanoramaHints[ inx ].setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String panoid = (String) view.getTag();
                    panorama.setPosition(panoid);

                    Log.d(LOGTAG, "onClick: panoid=" + panoid);
                }
            });

            mapFrame.addView(nextPanoramaHints[ inx ]);
        }
    }

    public void setCoordinates(Double lat, Double lon)
    {
        coordinates = new LatLng(lat, lon);
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

    private void showNextPanoramas()
    {
        int viewsused = 0;

        ArrayList<String> dups = new ArrayList<>();

        if ((location != null) && (location.links != null))
        {
            dups.add(location.panoId);

            //
            // Show defined navigation panoramas as green.
            //

            for (int inx = 0; (inx < location.links.length) && (viewsused < nextPanoramaHints.length); inx++)
            {
                StreetViewPanoramaLink link = location.links[inx];

                dups.add(link.panoId);

                StreetViewPanoramaOrientation orient = new StreetViewPanoramaOrientation(camera.tilt, link.bearing);
                Point point = panorama.orientationToPoint(orient);

                Log.d(LOGTAG, "showNextPanoramas: links panoid=" + link.panoId + " point=" + point);

                if (point == null) continue;

                MarginLayoutParams lp = (MarginLayoutParams) nextPanoramaHints[viewsused].getLayoutParams();
                lp.leftMargin = point.x;
                lp.topMargin = point.y;
                nextPanoramaHints[viewsused].setLayoutParams(lp);

                nextPanoramaHints[viewsused].setVisibility(VISIBLE);
                nextPanoramaHints[viewsused].setBackgroundColor(0x8800ff00);
                nextPanoramaHints[viewsused].setTag(link.panoId);

                viewsused++;
            }
        }

        if (otherPanoramas != null)
        {
            //
            // Show retrieved other panoramas as yellow.
            //

            for (int inx = 0; inx < otherPanoramas.length(); inx++)
            {
                JSONObject otherPanorama = Json.getObject(otherPanoramas, inx);
                if (otherPanorama == null) continue;

                String panoid = Json.getString(otherPanorama, "panoid");

                if (panoid == null) continue;
                if (dups.contains(panoid)) continue;

                float heading = Json.getFloat(otherPanorama, "heading");
                StreetViewPanoramaOrientation orient = new StreetViewPanoramaOrientation(camera.tilt, heading);
                Point point = panorama.orientationToPoint(orient);

                Log.d(LOGTAG, "showNextPanoramas: other panoid=" + panoid + " point=" + point);

                if (point == null) continue;

                MarginLayoutParams lp = (MarginLayoutParams) nextPanoramaHints[viewsused].getLayoutParams();
                lp.leftMargin = point.x;
                lp.topMargin = point.y;
                nextPanoramaHints[viewsused].setLayoutParams(lp);

                nextPanoramaHints[viewsused].setVisibility(VISIBLE);
                nextPanoramaHints[viewsused].setBackgroundColor(0x88ffff00);
                nextPanoramaHints[viewsused].setTag(panoid);

                viewsused++;
            }
        }

        //
        // Render superflous hints invisible.
        //

        while (viewsused < nextPanoramaHints.length)
        {
            nextPanoramaHints[ viewsused++ ].setVisibility(GONE);
        }
    }

    private boolean moveCamera(int keyCode)
    {
        boolean okkey = false;
        boolean movekey = false;

        float zoom = camera.zoom;
        float tilt = camera.tilt;
        float bearing = camera.bearing;

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
        {
            Log.d(LOGTAG, "moveCamera: center zoom=" + camera.zoom);

            coordinates = computeOffset(coordinates, 10, camera.bearing);
            panorama.setPosition(coordinates);

            okkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        {
            bearing += 10;
            movekey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            bearing -= 10;
            movekey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
        {
            tilt += 10;
            if (tilt > 90) tilt =  90;
            movekey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        {
            tilt -= 10;
            if (tilt < -90) tilt = -90;
            movekey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND)
        {
            zoom -= 0.5f;
            if (zoom < 0) zoom = 0;
            movekey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
        {
            zoom += 0.5f;
            if (zoom > 5) zoom = 5;
            movekey = true;
        }

        if (movekey)
        {
            Log.d(LOGTAG, "moveCamera: zoom=" + zoom + " tilt=" + tilt + " bearing=" + bearing);

            camera = new StreetViewPanoramaCamera.Builder().zoom(zoom).tilt(tilt).bearing(bearing).build();

            panorama.animateTo(camera, 250);

        }

        return movekey || okkey;
    }

    private void onPanoramaDataReceived(String status, JSONObject data)
    {
        Log.d(LOGTAG, "onPanoramaDataReceived: status=" + status + " data=" + Json.toPretty(data));

        if ((data == null) || ! status.equals("OK")) return;

        JSONObject locationJson = Json.getObject(data, "location");
        String description = Json.getString(locationJson, "description");

        setTitleInfo(description);

        JSONObject follows = Json.getObject(data, "f");

        if (follows != null)
        {
            otherPanoramas = new JSONArray();

            Iterator<String> panoids = follows.keys();

            while (panoids.hasNext())
            {
                String panoid = panoids.next();
                JSONObject latlon = Json.getObject(follows, panoid);
                if (latlon == null) continue;

                Double lat = Json.getDouble(latlon, "lat");
                Double lon = Json.getDouble(latlon, "lng");
                LatLng panopos = new LatLng(lat, lon);

                Double heading = computeHeading(location.position, panopos);

                Json.put(latlon, "panoid", panoid);
                Json.put(latlon, "heading", heading);

                Json.put(otherPanoramas, latlon);

                Log.d(LOGTAG, "onPanoramaDataReceived: pano=" + latlon.toString());
            }
        }
    }

    private final static double EARTH_RADIUS = 6371009;

    private static LatLng computeOffset(LatLng from, double distance, double heading)
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

    private static double computeHeading(LatLng from, LatLng to)
    {
        double fromLat = Math.toRadians(from.latitude);
        double fromLng = Math.toRadians(from.longitude);

        double toLat = Math.toRadians(to.latitude);
        double toLng = Math.toRadians(to.longitude);

        double dLng = toLng - fromLng;

        double heading = Math.atan2(
                Math.sin(dLng) * Math.cos(toLat),
                Math.cos(fromLat) * Math.sin(toLat) - Math.sin(fromLat) * Math.cos(toLat) * Math.cos(dLng));

        return wrap(Math.toDegrees(heading), -180, 180);
    }

    private static double wrap(double n, double min, double max)
    {
        return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
    }

    private static double mod(double x, double m)
    {
        return ((x % m) + m) % m;
    }
}