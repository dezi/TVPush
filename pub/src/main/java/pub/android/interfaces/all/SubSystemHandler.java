package pub.android.interfaces.all;

import org.json.JSONObject;

public interface SubSystemHandler
{
    int SUBSYSTEM_MODE_VOLUNTARY = 0;
    int SUBSYSTEM_MODE_MANDATORY = 1;

    int SUBSYSTEM_RUN_STOPPED = 0;
    int SUBSYSTEM_RUN_STARTED = 1;
    int SUBSYSTEM_RUN_FAILED = 2;
    int SUBSYSTEM_RUN_ZOMBIE = 3;

    JSONObject getSubsystemInfo();

    void startSubsystem();
    void stopSubsystem();

    void onSubsystemStarted(String subsystem, int state);
    void onSubsystemStopped(String subsystem, int state);
}
