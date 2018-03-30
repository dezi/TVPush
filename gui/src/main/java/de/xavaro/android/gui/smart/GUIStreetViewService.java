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

public class GUIStreetViewService extends WebView
{
    private static final String LOGTAG = GUIStreetViewService.class.getSimpleName();

    private final static String DUMMYHOSTNAME = "xzzylemondummy42";

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

    public void evaluate(Double lat, Double lon, int radius)
    {
        Log.d(LOGTAG, "evaluate in...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            String js = ""
                    + "console.log(\"pupsi\");\n"
                    + "blabla.getPanorama(\n"
                    + "{\n"
                    + "  location:\n"
                    + "{lat: 51.5099272, lng: -0.1349173}, radius: 100}, processSVData"
                    + ");"
                    ;

            evaluateJavascript(js, null);

            Log.d(LOGTAG, "evaluate out...");
        }
    }

    private boolean isReady;

    private class GUIWebViewCallback
    {
        private final String LOGTAG = GUIWebViewCallback.class.getSimpleName();

        @JavascriptInterface
        public void isReady()
        {
            de.xavaro.android.gui.simple.Log.d(LOGTAG, "isReady:");
            isReady = true;
        }

        @JavascriptInterface
        public void getSomething(String status, String jsonString)
        {
            de.xavaro.android.gui.simple.Log.d(LOGTAG, "getSomething: status=" + status);

            JSONObject json = Json.fromStringObject(jsonString);

            de.xavaro.android.gui.simple.Log.d(LOGTAG, "getSomething: #############" + Json.toPretty(json));
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
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
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
                String html = ""
                        + "<!DOCTYPE html>\n"
                        + "<html>\n"
                        + "<body>\n"
                        + "<script>\n"
                        + "function initMap()\n"
                        + "{\n"
                        + "  console.log(\"da bin ich....\");\n"
                        + "  blabla = new google.maps.StreetViewService();\n"
                        + "}\n"
                        + "function processSVData(data, status)\n"
                        + "{\n"
                        + "  console.log(\"da bin ich....\" + status);\n"
                        + "  GUIWebViewCallback.getSomething(status, JSON.stringify(data));\n"
                        + "}\n"
                        + "</script>\n>"
                        + "<script async defer src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyBJ1BXy83xwFwJNhJdD-imW7AfxBZsRkZs&callback=initMap\">\n"
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
