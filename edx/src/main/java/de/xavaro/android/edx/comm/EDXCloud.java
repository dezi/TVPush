package de.xavaro.android.edx.comm;

import de.xavaro.android.edx.simple.Log;

public class EDXCloud
{
    private static final String LOGTAG = EDXCloud.class.getSimpleName();

    public static void updateSettings()
    {
        String body = ""
            + "{\"pushType\":\"gcm\","
            + "\"localeIdentifier\":\"de-DE\","
            + "\"appVersion\":\"1.0.1\","
            + "\"deviceType\":\"android\","
            + "\"appIdentifier\":\"com.edimax.edismart\","
            + "\"installationId\":\"faae759a-7358-4d6c-aa48-20d62ec7f356\","
            + "\"parseVersion\":\"1.15.8\","
            + "\"appName\":\"EdiSmart\","
            + "\"GCMSenderId\":\"id:869997638701\","
            + "\"timeZone\":\"Europe/Berlin\"}";

        String url = "https://pg-app-c9c8cz82iexbxxdeebtxheb03v6hvs.scalabl.cloud/1/classes/_Installation";

        String result = EDXUtil.getPost(url,body, null, null, null);

        Log.d(LOGTAG, "updateSettings: result=" + result);
    }
}
