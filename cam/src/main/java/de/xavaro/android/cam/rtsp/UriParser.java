package de.xavaro.android.cam.rtsp;

import static de.xavaro.android.cam.session.SessionBuilder.AUDIO_AAC;
import static de.xavaro.android.cam.session.SessionBuilder.AUDIO_NONE;
import static de.xavaro.android.cam.session.SessionBuilder.AUDIO_AMRNB;
import static de.xavaro.android.cam.session.SessionBuilder.VIDEO_H263;
import static de.xavaro.android.cam.session.SessionBuilder.VIDEO_H264;
import static de.xavaro.android.cam.session.SessionBuilder.VIDEO_NONE;

import android.hardware.Camera.CameraInfo;
import android.content.ContentValues;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URI;
import java.util.Set;

import de.xavaro.android.cam.session.SessionBuilder;
import de.xavaro.android.cam.session.Session;
import de.xavaro.android.cam.streams.AudioQuality;
import de.xavaro.android.cam.streams.VideoQuality;

public class UriParser
{
    //
    // Configures a Session according to the given URI.
    //
    // Here are some examples of URIs that can be used to configure a Session:
    //
    // rtsp://xxx.xxx.xxx.xxx:8086?h264&flash=on
    // rtsp://xxx.xxx.xxx.xxx:8086?h263&camera=front&flash=on
    // rtsp://xxx.xxx.xxx.xxx:8086?h264=200-20-320-240
    // rtsp://xxx.xxx.xxx.xxx:8086?aac
    //

    public static Session parse(String uri)
    {
        SessionBuilder builder = SessionBuilder.getInstance().clone();

        ContentValues params = new ContentValues();
        String query = URI.create(uri).getQuery();

        if (query != null)
        {
            String[] queryParams = query.split("&");

            for (String param : queryParams)
            {
                String[] keyval = param.split("=");

                try
                {
                    if (keyval.length == 2)
                    {
                        params.put(URLDecoder.decode(keyval[0], "UTF-8"),
                                URLDecoder.decode(keyval[1], "UTF-8"));
                    }
                    else
                    {
                        params.put(URLDecoder.decode(keyval[0], "UTF-8"),
                                (String) null);
                    }
                }
                catch (Exception ignore)
                {
                }
            }
        }

        builder.setAudioEncoder(AUDIO_NONE);
        builder.setVideoEncoder(VIDEO_NONE);

        Set<String> paramKeys = params.keySet();
        for (String paramName : paramKeys)
        {
            String paramValue = params.getAsString(paramName);

            if (paramName.equalsIgnoreCase("flash"))
            {
                builder.setFlashEnabled(paramValue.equalsIgnoreCase("on"));

                continue;
            }

            if (paramName.equalsIgnoreCase("camera"))
            {
                if (paramValue.equalsIgnoreCase("back"))
                {
                    builder.setCamera(CameraInfo.CAMERA_FACING_BACK);
                }

                if (paramValue.equalsIgnoreCase("front"))
                {
                    builder.setCamera(CameraInfo.CAMERA_FACING_FRONT);
                }

                continue;
            }

            if (paramName.equalsIgnoreCase("multicast"))
            {
                if (paramValue != null)
                {
                    try
                    {
                        InetAddress addr = InetAddress.getByName(paramValue);

                        if (addr.isMulticastAddress())
                        {
                            builder.setDestination(paramValue);
                        }
                    }
                    catch (UnknownHostException ignore)
                    {
                    }
                }
                else
                {
                    builder.setDestination("228.5.6.7");
                }

                continue;
            }

            if (paramName.equalsIgnoreCase("unicast"))
            {
                if (paramValue != null)
                {
                    builder.setDestination(paramValue);
                }

                continue;
            }

            if (paramName.equalsIgnoreCase("ttl"))
            {
                if (paramValue != null)
                {
                    int ttl = Integer.parseInt(paramValue);
                    if (ttl >= 0) builder.setTimeToLive(ttl);
                }

                continue;
            }

            if (paramName.equalsIgnoreCase("h264"))
            {
                VideoQuality quality = VideoQuality.parseQuality(paramValue);
                builder.setVideoQuality(quality).setVideoEncoder(VIDEO_H264);

                continue;
            }

            if (paramName.equalsIgnoreCase("h263"))
            {
                VideoQuality quality = VideoQuality.parseQuality(paramValue);
                builder.setVideoQuality(quality).setVideoEncoder(VIDEO_H263);

                continue;
            }


            if (paramName.equalsIgnoreCase("amrnb") || paramName.equalsIgnoreCase("amr"))
            {
                AudioQuality quality = AudioQuality.parseQuality(paramValue);
                builder.setAudioQuality(quality).setAudioEncoder(AUDIO_AMRNB);

                continue;
            }

            if (paramName.equalsIgnoreCase("aac"))
            {
                AudioQuality quality = AudioQuality.parseQuality(paramValue);
                builder.setAudioQuality(quality).setAudioEncoder(AUDIO_AAC);
            }
        }

        if ((builder.getVideoEncoder() == VIDEO_NONE)
                && (builder.getAudioEncoder() == AUDIO_NONE))
        {
            SessionBuilder old = SessionBuilder.getInstance();
            builder.setVideoEncoder(old.getVideoEncoder());
            builder.setAudioEncoder(old.getAudioEncoder());
        }

        return builder.build();
    }
}
