package de.xavaro.android.iot.proxim;

import android.support.annotation.Nullable;

import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.net.URL;

import de.xavaro.android.iot.simple.Log;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.base.IOT;

public class IOTPosition implements LocationListener
{
    private static final String LOGTAG = IOTPosition.class.getSimpleName();

    private boolean gpsIsEnabled;
    private boolean networkIsEnabled;

    private final JSONObject lastLocations = new JSONObject();

    public static void startService(Context appcontext)
    {
        if ((IOT.instance != null) && (IOT.instance.proximLocationListener == null))
        {
            IOT.instance.proximLocationListener = new IOTPosition();
            IOT.instance.proximLocationListener.start(appcontext);
        }
    }

    public static void stopService()
    {
        if ((IOT.instance != null) && (IOT.instance.proximLocationListener != null))
        {
            IOT.instance.proximLocationListener.stop();
            IOT.instance.proximLocationListener = null;
        }
    }

    private void start(Context appcontext)
    {
        if (Simple.checkLocationPermission(appcontext))
        {
            LocationManager locationManager = Simple.getLocationManager();

            gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location last = null;

            if (networkIsEnabled)
            {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        5000l, 1f,
                        IOT.instance.proximLocationListener);

                Log.d(LOGTAG, "startService: NETWORK_PROVIDER installed.");

                Location netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (netLoc != null)
                {
                    last = netLoc;

                    Log.d(LOGTAG, "startService: last NET location"
                            + " lat=" + last.getLatitude()
                            + " lon=" + last.getLongitude()
                            + " alt=" + last.getAltitude()
                            + " acc=" + last.getAccuracy()
                            + " age=" + Simple.getAgeInSeconds(last.getTime())
                    );
                }
            }

            if (gpsIsEnabled)
            {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000l, 1f,
                        IOT.instance.proximLocationListener);

                Log.d(LOGTAG, "startService: GPS_PROVIDER installed.");

                Location gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (gpsLoc != null)
                {
                    last = gpsLoc;

                    Log.d(LOGTAG, "startService: last GPS location"
                            + " lat=" + last.getLatitude()
                            + " lon=" + last.getLongitude()
                            + " alt=" + last.getAltitude()
                            + " acc=" + last.getAccuracy()
                            + " age=" + Simple.getAgeInSeconds(last.getTime())
                    );
                }
            }

            if (last == null)
            {
                Log.e(LOGTAG, "startService: no last location.");
            }
        }
        else
        {
            Log.e(LOGTAG, "startService: no permission!");
        }
    }

    private void stop()
    {
        if (networkIsEnabled || gpsIsEnabled)
        {
            LocationManager locationManager = Simple.getLocationManager();
            locationManager.removeUpdates(IOT.instance.proximLocationListener);
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location == null) return;

        if (location.getAltitude() == 0.0)
        {
            Double altitude = getAltitude(location.getLatitude(), location.getLongitude());
            if (altitude != null) location.setAltitude(altitude);
        }

        if (IOT.device != null)
        {
            if (IOT.device.hasCapability("fixed"))
            {
                IOTDevice device = new IOTDevice(IOT.device.uuid);
                device.fixedLatCoarse = location.getLatitude();
                device.fixedLonCoarse = location.getLongitude();
                device.fixedAltCoarse = location.getAltitude();

                IOTDevice.list.addEntry(device, false, true);
            }

            IOTStatus status = new IOTStatus(IOT.device.uuid);
            status.positionLatCoarse = location.getLatitude();
            status.positionLonCoarse = location.getLongitude();
            status.positionAltCoarse = location.getAltitude();

            IOTStatus.list.addEntry(status, false, true);

            if (IOT.instance.proximServer != null)
            {
                IOT.instance.proximServer.advertiseGPSCoarse();
            }

            JSONObject locmeasurement = new JSONObject();
            Json.put(locmeasurement, "akey", IOT.device.uuid);
            Json.put(locmeasurement, "prov", location.getProvider());
            Json.put(locmeasurement, "time", System.currentTimeMillis());
            Json.put(locmeasurement, "mode", "coarse");
            Json.put(locmeasurement, "lat", location.getLatitude());
            Json.put(locmeasurement, "lon", location.getLongitude());
            Json.put(locmeasurement, "alt", location.getAltitude());

            JSONObject measurement = new JSONObject();
            Json.put(measurement, "LOCMeasurement", locmeasurement);

            addLocationMeasurement(measurement);
        }

        Log.d(LOGTAG, "onLocationChanged:"
                + " lat=" + location.getLatitude()
                + " lon=" + location.getLongitude()
                + " alt=" + location.getAltitude()
                + " age=" + Simple.getAgeInSeconds(location.getTime())
                + " acc=" + location.getAccuracy()
                + " pro=" + location.getProvider()
        );
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d(LOGTAG, "onProviderDisabled: provider=" + provider);
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.d(LOGTAG, "onProviderEnabled: provider=" + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.d(LOGTAG, "onStatusChanged: provider=" + provider + " status=" + status + " extras=" + extras);
    }

    @Nullable
    public static Double getAltitude(Double lat, Double lon)
    {
        Double altitude = null;

        String url = "https://maps.googleapis.com/maps/api/elevation/json"
                + "?locations=" + String.valueOf(lat) + "," + String.valueOf(lon)
                + "&sensor=true" + "&key=AIzaSyBJ1BXy83xwFwJNhJdD-imW7AfxBZsRkZs";

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

                JSONObject respJson = Json.fromStringObject(respStr.toString());
                JSONArray results = Json.getArray(respJson, "results");

                if ((results != null) && (results.length() > 0))
                {
                    JSONObject entry = Json.getObject(results, 0);
                    if (entry != null)
                    {
                        altitude = Json.getDouble(entry, "elevation");
                    }
                }

                if (altitude == null)
                {
                    Log.e(LOGTAG, "getAltitude: url=" + url);
                    Log.e(LOGTAG, "getAltitude: respStr=" + respStr);
                }
                else
                {
                    Log.d(LOGTAG, "getAltitude: lat=" + lat + " lon=" + lon + " alt=" + altitude);
                }
            }
        }
        catch (IOException exc)
        {
            exc.printStackTrace();
        }

        return altitude;
    }

    public void addLocationMeasurement(JSONObject measurement)
    {
        //Log.d(LOGTAG, "addLocationMeasurement: measurement=" + Json.toPretty(measurement));

        if (Json.has(measurement, "LOCMeasurement"))
        {
            measurement = Json.getObject(measurement, "LOCMeasurement");
        }

        String akey = Json.getString(measurement, "akey");
        String pkey = Json.getString(measurement, "prov");

        if ((akey == null) || (pkey == null)) return;

        String lkey = akey + ":" + pkey;

        Json.put(lastLocations, lkey, measurement);

        Iterator<String> keys = lastLocations.keys();

        Log.d(LOGTAG, "addLocationMeasurement: ------------");

        while (keys.hasNext())
        {
            lkey = keys.next();

            measurement = Json.getObject(lastLocations, lkey);
            String prov = Json.getString(measurement, "prov");
            String mode = Json.getString(measurement, "mode");
            if ((prov == null) || (mode == null)) continue;

            long time = Json.getLong(measurement, "time");

            int txpo = 100 + Json.getInt(measurement, "txpo");
            int rssi = 100 + Json.getInt(measurement, "rssi");

            txpo = (txpo < 0) ? 0 : (txpo >= 100) ? 100 : txpo;
            rssi = (rssi < 0) ? 0 : (rssi >= 100) ? 100: rssi;

            double lat = Json.getDouble(measurement, "lat");
            double lon = Json.getDouble(measurement, "lon");
            double alt = Json.getDouble(measurement, "alt");

            int ages = (int) ((System.currentTimeMillis() - time) / 1000);

            int dist = (txpo - rssi) * 10;
            dist = (dist < 0) ? 0 : (dist > 1000) ? 1000 : dist;

            float fact = 1f - (dist / 1000f);
            fact = fact * (prov.equals("network") ? 0.5f : (mode.equals("coarse") ? 0.75f : 1.0f));

            //
            // Dezi's display asperger.
            //

            txpo = (txpo >= 100) ? 99 : txpo;
            rssi = (rssi >= 100) ? 99: rssi;
            dist = (dist >= 1000) ? 999: dist;
            fact = Math.round(fact * 100f) / 100f;

            Log.d(LOGTAG, "addLocationMeasurement:"
                    + " ages=" + Simple.padLeft(ages, 3)
                    + " txpo=" + Simple.padLeft(txpo, 2)
                    + " rssi=" + Simple.padLeft(rssi, 2)
                    + " dist=" + Simple.padLeft(dist, 3)
                    + " fact=" + Simple.padRight(fact, 4)
                    + " prov=" + Simple.padRight(prov, 9)
                    + " lkey=" + lkey
            );
        }
    }
}
