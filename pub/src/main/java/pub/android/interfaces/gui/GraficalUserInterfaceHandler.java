package pub.android.interfaces.gui;

import org.json.JSONObject;

import pub.android.interfaces.cam.Camera;

public interface GraficalUserInterfaceHandler
{
    Camera onRequestCameraByUUID(String uuid);

    void onSpeechResults(JSONObject speech);

    void displayCamera(boolean show, String uuid);
    void displaySpeechRecognition(boolean show);
}
