package zz.top.p2p.camera;

import android.support.annotation.Nullable;

import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.util.Iterator;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

import zz.top.utl.Json;

public class P2PRestApi
{
    private static final String LOGTAG = P2PRestApi.class.getSimpleName();

    public static void getPostThreaded(String what,
                                       JSONObject params,
                                       boolean asPost,
                                       RestApiResultListener callback)
    {
        getPostThreaded(what, params, asPost, true, callback);
    }

    public static void getPostThreaded(final String what,
                                       final JSONObject params,
                                       final boolean asPost,
                                       final boolean callbackOnUIThread,
                                       final RestApiResultListener callback)
    {
        final Handler handler = callbackOnUIThread ? new Handler() : null;

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final JSONObject result = getPost(what, params, asPost);

                    if (callback != null)
                    {
                        if (callbackOnUIThread)
                        {
                            handler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    callback.OnRestApiResult(what, params, result);
                                }
                            });
                        }
                        else
                        {
                            callback.OnRestApiResult(what, params, result);
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Nullable
    public static JSONObject getPost(String what, JSONObject params, boolean asPost)
    {
        try
        {
            String src = what;
            String dat = getQueryDataString(params);

            Log.d(LOGTAG, "getPost: src=" + src);
            Log.d(LOGTAG, "getPost: dat=" + dat);

            URL url = new URL(src + (asPost ? "" : ("?" + dat)));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (asPost)
            {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
            }
            else
            {
                connection.setRequestMethod("GET");
                connection.setDoOutput(false);
            }

            connection.setConnectTimeout(4000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.connect();

            if (asPost)
            {
                OutputStream os = connection.getOutputStream();
                os.write(dat.getBytes("UTF-8"));
                os.flush();
                os.close();
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                //
                // File cannot be loaded.
                //

                Log.d(LOGTAG, "getContentFromServer: ERR=" + connection.getResponseCode());

                return null;
            }

            //
            // Fetch file.
            //

            int length = connection.getContentLength();
            InputStream input = connection.getInputStream();
            byte[] buffer;
            int total = 0;

            if (length > 0)
            {
                buffer = new byte[ length ];

                for (int xfer; total < length; total += xfer)
                {
                    xfer = input.read(buffer, total, length - total);
                }
            }
            else
            {
                byte[] chunk = new byte[ 32 * 1024 ];

                buffer = new byte[ 0 ];

                for (int xfer; ; total += xfer)
                {
                    xfer = input.read(chunk, 0, chunk.length);
                    if (xfer <= 0) break;

                    byte[] temp = new byte[ buffer.length + xfer ];
                    System.arraycopy(buffer, 0, temp, 0, buffer.length);
                    System.arraycopy(chunk, 0, temp, buffer.length, xfer);
                    buffer = temp;
                }
            }

            input.close();

            String result = new String(buffer);

            Log.d(LOGTAG, "getPost result=" + result);

            return Json.fromStringObject(result);
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return null;
    }

    public static String getQueryDataString(JSONObject params)
    {
        String queryData = "";

        Iterator<String> keysIterator = params.keys();

        while (keysIterator.hasNext())
        {
            String key = keysIterator.next();
            Object raw = Json.get(params, key);

            if (raw == null) continue;

            if (queryData.length() > 0) queryData += "&";

            try
            {
                queryData = queryData
                        + URLEncoder.encode(key, "UTF-8")
                        + "="
                        + URLEncoder.encode(raw.toString(), "UTF-8");

            }
            catch (Exception ex)
            {
                Log.d(LOGTAG, ex.toString());
            }
        }

        return queryData;
    }

    public interface RestApiResultListener
    {
        void OnRestApiResult(String what, JSONObject params, JSONObject result);
    }
}
