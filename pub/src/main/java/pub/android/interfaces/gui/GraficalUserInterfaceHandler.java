package pub.android.interfaces.gui;

import pub.android.interfaces.cam.Camera;

public interface GraficalUserInterfaceHandler
{
    Camera onRequestCameraByUUID(String uuid);

    void displaySpeechRecognition(boolean show);
    void displayCamera(String uuid);
}
