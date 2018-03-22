package pub.android.interfaces.gui;

import org.json.JSONObject;

import pub.android.interfaces.drv.Camera;

public interface DesktopHandler
{
    Camera onRequestCameraByUUID(String uuid);

    void onSpeechResults(JSONObject speech);

    void displayCamera(boolean show, String uuid);
    void displaySpeechRecognition(boolean show);
    void displayPinCodeMessage(int timeout);
    void displayToastMessage(String message, int seconds, boolean emphasis);
}
