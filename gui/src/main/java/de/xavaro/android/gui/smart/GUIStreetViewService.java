package de.xavaro.android.gui.smart;

import android.support.annotation.RequiresApi;
import android.webkit.JavascriptInterface;
import android.annotation.SuppressLint;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import android.content.Context;
import android.os.Build;
import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;

public class GUIStreetViewService extends WebView
{
    private static final String LOGTAG = GUIStreetViewService.class.getSimpleName();

    private final static String DUMMYHOSTNAME = "xzzydummy42";

    private boolean serviceIsReady;

    @SuppressLint("SetJavaScriptEnabled")
    public GUIStreetViewService(Context context)
    {
        super(context);

        WebSettings webSettings = getSettings();

        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        setWebViewClient(new GUIWebViewClient());

        addJavascriptInterface(new GUIWebViewCallback(), "GUIWebViewCallback");

        loadUrl("http://" + DUMMYHOSTNAME + "/");
    }

    public void evaluate(final Double lat, final Double lon, final int radius)
    {
        if (!serviceIsReady)
        {
            Simple.getHandler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (!serviceIsReady)
                    {
                        Simple.getHandler().postDelayed(this, 500);
                    }
                    else
                    {
                        evaluateReal(lat, lon, radius);
                    }
                }

            }, 500);
        }
        else
        {
            evaluateReal(lat, lon, radius);
        }
    }

    public void evaluateReal(Double lat, Double lon, int radius)
    {
        Log.d(LOGTAG, "evaluate in...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            String js = ""
                    + "console.log(\"pupsi\");\n"
                    + "GUIStreetViewService.getPanorama(\n"
                    + "  {\n"
                    + "    location:\n"
                    + "      {\n"
                    + "        lat: " + lat + ",\n"
                    + "        lng: " + lon + "\n"
                    + "      },\n"
                    + "      radius: " + radius + "\n"
                    + "  },\n"
                    + "  processSVData\n"
                    + ");\n"
                    ;

            evaluateJavascript(js, null);

            Log.d(LOGTAG, "evaluate out...");
        }
    }

    @SuppressWarnings("unused")
    private class GUIWebViewCallback
    {
        private final String LOGTAG = GUIWebViewCallback.class.getSimpleName();

        @JavascriptInterface
        public void serviceIsReady()
        {
            de.xavaro.android.gui.simple.Log.d(LOGTAG, "serviceIsReady:");

            serviceIsReady = true;
        }

        @JavascriptInterface
        public void gotPanoramaData(final String status, String jsonString)
        {
            final JSONObject jsonData = Json.fromStringObject(jsonString);

            if (callback != null)
            {
                Simple.getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        callback.onDataReceived(status, jsonData);
                    }
                });
            }
        }
    }

    private class GUIWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return false;
        }

        @Override
        @SuppressWarnings("deprecation")
        public WebResourceResponse shouldInterceptRequest(WebView view, String url)
        {
            Log.d(LOGTAG, "shouldInterceptRequest: old=" + url);
            return null;
        }

        @Override
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
        {
            Uri uri = request.getUrl();

            Log.d(LOGTAG, "shouldInterceptRequest: uri=" + uri.toString());

            if (! uri.getHost().equals(DUMMYHOSTNAME)) return null;

            try
            {
                String apikey = Simple.getManifestMetaData("com.google.android.geo.API_KEY");

                String html = ""
                        + "<!DOCTYPE html>\n"
                        + "<html>\n"
                        + "<body>\n"
                        + "<script>\n"
                        + "function initMap()\n"
                        + "{\n"
                        + "  GUIStreetViewService = new google.maps.StreetViewService();\n"
                        + "  GUIWebViewCallback.serviceIsReady();\n"
                        + "}\n"
                        + "function processSVData(data, status)\n"
                        + "{\n"
                        + "  GUIWebViewCallback.gotPanoramaData(status, JSON.stringify(data));\n"
                        + "}\n"
                        + "</script>\n>"
                        + "<script async defer\n"
                        + "  src=\"https://maps.googleapis.com/maps/api/js?key=" + apikey + "&callback=initMap\"\n"
                        + "  >\n"
                        + "</script>\n"
                        + "</body>\n"
                        + "</html>\n"
                        ;

                ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());

                return new WebResourceResponse("text/html", "UTF-8", is);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return null;
        }
    }

    private GUIStreetViewServiceCallback callback;

    public void setGUIStreetViewServiceCallback(GUIStreetViewServiceCallback callback)
    {
        this.callback = callback;
    }

    public interface GUIStreetViewServiceCallback
    {
        void onDataReceived(String status, JSONObject data);
    }
}
