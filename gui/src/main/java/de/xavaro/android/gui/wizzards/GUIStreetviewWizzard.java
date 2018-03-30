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

@SuppressWarnings("WeakerAccess")
public class GUIStreetviewWizzard extends GUIPluginTitle
{
    private final static String LOGTAG = GUIStreetviewWizzard.class.getSimpleName();

    private GUIStreetViewService streetViewService;
    private GUIFrameLayout[] nextPanoramaHints;
    private GUIFrameLayout mapFrame;

    private StreetViewPanoramaView panoramaView;
    private StreetViewPanoramaLocation location;
    private StreetViewPanoramaLink[] lastlinks;
    private StreetViewPanoramaCamera camera;
    private StreetViewPanorama panorama;

    private JSONArray otherPanoramas;

    private LatLng coordinates;

    public GUIStreetviewWizzard(Context context)
    {
        super(context);

        setCoordinates(51.5099272,-0.1349173);

        setIsWizzard(true, false, Simple.isTV() ? 3 : 2, Gravity.START);

        setTitleIcon(R.drawable.wizzard_streetview_550);
        setTitleText("Streetview Wizzard");

        mapFrame = new GUIFrameLayout(context)
        {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event)
            {
                return onKeyDownDoit(keyCode, event) || super.onKeyDown(keyCode, event);
            }
        };

        final String toastFocus = ""
                + "Drücken Sie "
                + GUIDefs.UTF_OK
                + " um die Ansicht zur bearbeiten";

        final String toastHighlight = ""
                + "Kamera: " + GUIDefs.UTF_MOVE + " "
                + ", "
                + "Zoom: " + GUIDefs.UTF_ZOOMIN + GUIDefs.UTF_ZOOMOUT
                + ", "
                + "Jump: " + GUIDefs.UTF_OK
                + ", "
                + "Exit: " + GUIDefs.UTF_BACK
                ;

        mapFrame.setSizeDip(Simple.MP, Simple.MP);
        mapFrame.setHighlightable(true);
        mapFrame.setFocusable(true);
        mapFrame.setToastFocus(toastFocus);
        mapFrame.setToastHighlight(toastHighlight);

        contentFrame.addView(mapFrame);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        connectStreetView();
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        releaseStreetView();
    }

    private void connectStreetView()
    {
        streetViewService = new GUIStreetViewService(getContext());
        streetViewService.setGUIStreetViewServiceCallback(new GUIStreetViewService.GUIStreetViewServiceCallback()
        {
            @Override
            public void onDataReceived(String status, JSONObject data)
            {
                onPanoramaDataReceived(status, data);
            }
        });

        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
        options.streetNamesEnabled(true);
        options.zoomGesturesEnabled(true);
        options.userNavigationEnabled(false);
        options.panningGesturesEnabled(true);

        panoramaView = new StreetViewPanoramaView(getContext(), options);
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

                        location = streetViewPanoramaLocation;
                        coordinates = location.position;

                        otherPanoramas = null;

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
                        camera = panorama.getPanoramaCamera();
                        coordinates = computeOffset(location.position, 20, streetViewPanoramaOrientation.bearing);
                        panorama.setPosition(coordinates,  StreetViewSource.DEFAULT);
                    }
                });

                mapFrame.requestFocus();
            }
        });

        mapFrame.addView(panoramaView);

        nextPanoramaHints = new GUIFrameLayout[ 16 ];

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

    private void releaseStreetView()
    {
        mapFrame.removeAllViews();

        panoramaView.onDestroy();
        panoramaView = null;

        nextPanoramaHints = null;
        streetViewService = null;
        otherPanoramas = null;
        lastlinks = null;
        location = null;
        camera = null;
    }

    public void setCoordinates(Double lat, Double lon)
    {
        coordinates = new LatLng(lat, lon);
    }

    @Override
    public boolean onBackPressed()
    {
        Log.d(LOGTAG, "onBackPressed:");

        if (mapFrame.getHighlight())
        {
            mapFrame.setHighlight(false);

            return true;
        }

        return super.onBackPressed();
    }

    @SuppressWarnings("unused")
    private boolean onKeyDownDoit(int keyCode, KeyEvent event)
    {
        boolean usedKey = false;

        if (mapFrame.getHighlight())
        {
            usedKey = moveCamera(keyCode);
        }

        return usedKey;
    }

    private void showNextPanoramas()
    {
        int hintsUsed = 0;

        ArrayList<String> dups = new ArrayList<>();

        if (location != null)
        {
            dups.add(location.panoId);
        }

        if ((location != null) && (location.links != null) && (location.links.length > 0))
        {
            lastlinks = location.links;

            //
            // Show defined navigation panoramas as green.
            //

            for (int inx = 0; (inx < location.links.length) && (hintsUsed < nextPanoramaHints.length); inx++)
            {
                StreetViewPanoramaLink link = location.links[inx];

                if (dups.contains(link.panoId)) continue;
                dups.add(link.panoId);

                StreetViewPanoramaOrientation orient = new StreetViewPanoramaOrientation(camera.tilt, link.bearing);
                
                hintsUsed = setupHint(link.panoId, hintsUsed, orient, true);
            }
        }
        else
        {
            //
            // We are trapped inside the current panorama.
            // Retain the last good panorama links.
            //

            if (lastlinks != null)
            {
                for (int inx = 0; (inx < lastlinks.length) && (hintsUsed < nextPanoramaHints.length); inx++)
                {
                    StreetViewPanoramaLink link = lastlinks[inx];

                    if (dups.contains(link.panoId)) continue;
                    dups.add(link.panoId);

                    StreetViewPanoramaOrientation orient = new StreetViewPanoramaOrientation(camera.tilt, link.bearing);

                    hintsUsed = setupHint(link.panoId, hintsUsed, orient, true);
                }
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

                float bearing = Json.getFloat(otherPanorama, "bearing");
                StreetViewPanoramaOrientation orient = new StreetViewPanoramaOrientation(camera.tilt, bearing);

                hintsUsed = setupHint(panoid, hintsUsed, orient, false);
            }
        }

        //
        // Render superfluous hints invisible.
        //

        while (hintsUsed < nextPanoramaHints.length)
        {
            nextPanoramaHints[ hintsUsed++ ].setVisibility(GONE);
        }
    }

    private int setupHint(String panoid, int hintsUsed, StreetViewPanoramaOrientation orient, boolean main)
    {
        Point point = panorama.orientationToPoint(orient);
        Log.d(LOGTAG, "showNextPanoramas: other panoid=" + panoid + " point=" + point);
        if (point == null) return hintsUsed;

        MarginLayoutParams lp = (MarginLayoutParams) nextPanoramaHints[hintsUsed].getLayoutParams();
        lp.leftMargin = point.x;
        lp.topMargin = point.y;
        nextPanoramaHints[hintsUsed].setLayoutParams(lp);

        nextPanoramaHints[hintsUsed].setVisibility(VISIBLE);
        nextPanoramaHints[hintsUsed].setBackgroundColor(main ? 0x8800ff00 : 0x88ffff00);
        nextPanoramaHints[hintsUsed].setTag(panoid);

        return ++hintsUsed;
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
            Log.d(LOGTAG, "moveCamera: DPAD_CENTER");

            String bestPanoid = null;
            double bestDist = -1;

            int width = mapFrame.getWidth();
            int height = mapFrame.getHeight();

            for (GUIFrameLayout hint : nextPanoramaHints)
            {
                if (hint.getVisibility() == GONE) continue;

                MarginLayoutParams lp = (MarginLayoutParams) hint.getLayoutParams();

                double dist = Math.pow(width - lp.leftMargin, 2) + Math.pow((height - lp.topMargin), 2);

                Log.d(LOGTAG, "moveCamera: ok left=" + lp.leftMargin + " top=" + lp.topMargin + " dist=" + dist);

                if ((bestDist < 0) || (dist < bestDist))
                {
                    bestPanoid = (String) hint.getTag();
                    bestDist = dist;
                }
            }

            if (bestPanoid != null)
            {
                panorama.setPosition(bestPanoid);
            }

            okkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        {
            bearing -= 20;
            movekey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            bearing += 20;
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

            panorama.animateTo(camera, 200);

        }

        return movekey || okkey;
    }

    private void onPanoramaDataReceived(String status, JSONObject data)
    {
        //Log.d(LOGTAG, "onPanoramaDataReceived: status=" + status + " data=" + Json.toPretty(data));

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

                Double bearing = computeHeading(location.position, panopos) + 180;

                Json.put(latlon, "panoid", panoid);
                Json.put(latlon, "bearing", bearing);

                Json.put(otherPanoramas, latlon);

                Log.d(LOGTAG, "onPanoramaDataReceived: pano=" + latlon.toString());
            }
        }

        showNextPanoramas();
    }

    private final static double EARTH_RADIUS = 6371009;

    @SuppressWarnings("SameParameterValue")
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

    @SuppressWarnings("SameParameterValue")
    private static double wrap(double n, double min, double max)
    {
        return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
    }

    private static double mod(double x, double m)
    {
        return ((x % m) + m) % m;
    }
}