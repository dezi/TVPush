package pub.android.interfaces.all;

import org.json.JSONObject;

public interface SubSystemHandler
{
    int SUBSYSTEM_MODE_VOLUNTARY = 0;
    int SUBSYSTEM_MODE_MANDATORY = 1;
    int SUBSYSTEM_MODE_IMPOSSIBLE = 2;
    int SUBSYSTEM_MODE_DEFAULTACT = 3;

    int SUBSYSTEM_STATE_DEACTIVATED = 0;
    int SUBSYSTEM_STATE_ACTIVATED = 1;

    int SUBSYSTEM_RUN_STOPPED = 0;
    int SUBSYSTEM_RUN_STARTED = 1;
    int SUBSYSTEM_RUN_FAILED = 2;
    int SUBSYSTEM_RUN_ZOMBIE = 3;

    String SUBSYSTEM_TYPE_SERVICE = "service";
    String SUBSYSTEM_TYPE_FEATURE = "feature";

    void setInstance();

    JSONObject getSubsystemInfo();
    JSONObject getSubsystemSettings();

    int getSubsystemState(String subsystem);
    void setSubsystemState(String subsystem, int state);

    void startSubsystem(String subsystem);
    void stopSubsystem(String subsystem);

    void onSubsystemStarted(String subsystem, int runstate);
    void onSubsystemStopped(String subsystem, int runstate);
}
