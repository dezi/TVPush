package pub.android.interfaces.gui;

import org.json.JSONObject;

public interface DesktopHandler
{
    void onSpeechResults(JSONObject speech);

    void displayCamera(boolean show, String uuid);
    void displaySpeechRecognition(boolean show);
    void displayPinCodeMessage(int timeout);
    void displayToastMessage(String message, int seconds, boolean emphasis);
}
