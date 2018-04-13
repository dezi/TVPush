package de.xavaro.android.gui.base;

import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTLocation;

public class GUIUtil
{
    @Nullable
    public static String getClosestDomainForLocation(double lat, double lon, double alt)
    {
        LatLng locpos = new LatLng(lat, lon);

        JSONArray list = IOTDomain.list.getUUIDList();

        String domuuid = null;
        Double dombest = null;

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDomain domain = IOTDomain.list.getEntry(uuid);

            if (domain == null) continue;

            if ((domain.fixedLatFine == null)
                    || (domain.fixedLonFine == null)
                    || (domain.fixedAltFine == null))
            {
                continue;
            }

            LatLng dompos = new LatLng(domain.fixedLatFine, domain.fixedLonFine);

            double dist = computeDistanceBetween(locpos, dompos);

            if ((dombest == null) || (dombest > dist))
            {
                domuuid = uuid;
                dombest = dist;
            }
        }

        return domuuid;
    }

    @Nullable
    public static String getClosestLocationForDevice(double lat, double lon, double alt)
    {
        LatLng devpos = new LatLng(lat, lon);

        JSONArray list = IOTLocation.list.getUUIDList();

        String locuuid = null;
        Double locbest = null;

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTLocation location = IOTLocation.list.getEntry(uuid);

            if (location == null) continue;

            if ((location.fixedLatFine == null)
                    || (location.fixedLonFine == null)
                    || (location.fixedAltFine == null))
            {
                continue;
            }

            LatLng locpos = new LatLng(location.fixedLatFine, location.fixedLonFine);

            double dist = computeDistanceBetween(devpos, locpos);

            if ((locbest == null) || (locbest > dist))
            {
                locuuid = uuid;
                locbest = dist;
            }
        }

        return locuuid;
    }

    //region Coordinates math helper.

    public final static double EARTH_RADIUS = 6371009;

    public static double computeDistanceBetween(LatLng from, LatLng toto)
    {
        return computeAngleBetween(from, toto) * EARTH_RADIUS;
    }

    private static double computeAngleBetween(LatLng from, LatLng toto)
    {
        return distanceRadians(
                Math.toRadians(from.latitude), Math.toRadians(from.longitude),
                Math.toRadians(toto.latitude), Math.toRadians(toto.longitude));
    }

    private static double distanceRadians(double lat1, double lng1, double lat2, double lng2)
    {
        return arcHav(havDistance(lat1, lat2, lng1 - lng2));
    }

    private static double havDistance(double lat1, double lat2, double dLng)
    {
        return hav(lat1 - lat2) + hav(dLng) * Math.cos(lat1) * Math.cos(lat2);
    }

    private static double hav(double x)
    {
        double sinHalf = Math.sin(x * 0.5);
        return sinHalf * sinHalf;
    }

    private static double arcHav(double x)
    {
        return 2 * Math.asin(Math.sqrt(x));
    }

    @SuppressWarnings("SameParameterValue")
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

    public static double computeHeading(LatLng from, LatLng to)
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

    //endregion Coordinates math helper.
}
