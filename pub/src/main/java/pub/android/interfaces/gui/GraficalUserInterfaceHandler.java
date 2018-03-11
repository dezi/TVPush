package pub.android.interfaces.gui;

import org.json.JSONObject;

import pub.android.interfaces.cam.Camera;

public interface GraficalUserInterfaceHandler
{
    Camera onRequestCameraByUUID(String uuid);

    void onSpeechResults(JSONObject speech);

    void displaySpeechRecognition(boolean show);
    void displayCamera(String uuid);
}
