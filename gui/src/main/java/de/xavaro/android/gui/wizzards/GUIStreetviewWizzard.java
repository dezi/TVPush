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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
    private GUIStreetViewService webView;

    private StreetViewPanoramaView panoramaView;
    private StreetViewPanoramaLocation location;
    private StreetViewPanoramaCamera camera;
    private StreetViewPanorama panorama;

    private LatLng coordinates;

    private Map<String, JSONObject> panoramaMetas = new HashMap<>();
    private GUIFrameLayout[] exitDoors = new GUIFrameLayout[ 4 ];

    public GUIStreetviewWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false, Simple.isTV() ? 3 : 2, Gravity.START);

        setTitleIcon(R.drawable.wizzard_streetview_550);
        setTitleText("Streetview Wizzard");

        webView = new GUIStreetViewService(context);
        webView.setGUIStreetViewServiceCallback(new GUIStreetViewService.GUIStreetViewServiceCallback()
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

        setCoordinates(51.5099272,-0.1349173);

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

                        webView.evaluate(location.position.latitude, location.position.longitude, 100);

                        registerLocation();

                        showNextPanoramas();
                    }
                });

                panorama.setOnStreetViewPanoramaCameraChangeListener(new StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener()
                {
                    @Override
                    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera)
                    {
                        camera = streetViewPanoramaCamera;
                        Log.d(LOGTAG, "onStreetViewPanoramaCameraChange: camera=" + camera);

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

        for (int inx = 0; inx < exitDoors.length; inx++)
        {
            exitDoors[ inx ] = new GUIFrameLayout(getContext());
            exitDoors[ inx ].setSizeDip(50,50);
            exitDoors[ inx ].setBackgroundColor(0x8800ff00);
            exitDoors[ inx ].setVisibility(GONE);

            exitDoors[ inx ].setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String panoid = (String) view.getTag();
                    panorama.setPosition(panoid);

                    Log.d(LOGTAG, "onClick: panoid=" + panoid);
                }
            });

            mapFrame.addView(exitDoors[ inx ]);
        }
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

    public void setCoordinates(Double lat, Double lon)
    {
        coordinates = new LatLng(lat, lon);
    }

    private void registerLocation()
    {
        Log.d(LOGTAG, "registerLocation: location=" + location);

        getPanoramaMetaAsync(location.panoId);

        if ((location != null) && (location.links != null))
        {
            for (StreetViewPanoramaLink link : location.links)
            {
                getPanoramaMetaAsync(link.panoId);
            }
        }
    }

    private void showNextPanoramas()
    {
        if ((location != null) && (location.links != null))
        {
            for (int inx = 0;  inx < location.links.length; inx++)
            {
                StreetViewPanoramaLink link = location.links[ inx ];

                StreetViewPanoramaOrientation orient = new StreetViewPanoramaOrientation(camera.tilt, link.bearing);
                Point point = panorama.orientationToPoint(orient);

                Log.d(LOGTAG, "showNextPanoramas: panoid=" + link.panoId + " point=" + point);

                if (inx < exitDoors.length)
                {
                    if (point == null)
                    {
                        exitDoors[inx].setVisibility(GONE);
                    }
                    else
                    {
                        MarginLayoutParams lp = (MarginLayoutParams) exitDoors[inx].getLayoutParams();
                        lp.leftMargin = point.x;
                        lp.topMargin = point.y;
                        exitDoors[inx].setLayoutParams(lp);

                        exitDoors[inx].setVisibility(VISIBLE);
                        exitDoors[inx].setTag(link.panoId);
                    }
                }
            }

            for (int inx = location.links.length; inx < exitDoors.length; inx++)
            {
                exitDoors[ inx ].setVisibility(GONE);
            }
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

    private void getPanoramaMetaAsync(final String panoid)
    {
        if (! panoramaMetas.containsKey(panoid))
        {
            Thread runner = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    getPanoramaMeta(panoid);
                }
            });

            runner.start();
        }
    }

    private void getPanoramaMeta(String panoid)
    {
        String url = "https://maps.googleapis.com/maps/api/streetview/metadata"
                + "?pano=" + panoid + "&key=AIzaSyBJ1BXy83xwFwJNhJdD-imW7AfxBZsRkZs";

        Log.d(LOGTAG, "getPanoramaMeta: url=" + url);

        try
        {
            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();

            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");

            InputStream stream = connection.getInputStream();

            if (stream != null)
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder respStr = new StringBuilder();

                String line;

                while ((line = bufferedReader.readLine()) != null)
                {
                    respStr.append(line);
                    respStr.append("\n");
                }

                stream.close();

                Log.d(LOGTAG, "getPanoramaMeta: json=" + respStr);

                panoramaMetas.put(panoid, Json.fromStringObject(respStr.toString()));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void onPanoramaDataReceived(String status, JSONObject data)
    {
        Log.d(LOGTAG, "onPanoramaDataReceived: status=" + status + " data=" + Json.toPretty(data));

        if ((data == null) || ! status.equals("OK")) return;

        JSONObject location = Json.getObject(data, "location");
        String description = Json.getString(location, "description");

        setTitleEdit(description);
    }
}