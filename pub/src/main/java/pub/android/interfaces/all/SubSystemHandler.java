package pub.android.interfaces.all;

import org.json.JSONObject;

public interface SubSystemHandler
{
    JSONObject getSubsystemInfo();

    void startSubsystem();
    void stopSubsystem();
}
