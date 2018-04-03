package pub.android.interfaces.all;

import org.json.JSONObject;

public interface SubSystemHandler
{
    public final static int SUBSYSTEM_MODE_MANDATORY = 0;
    public final static int SUBSYSTEM_MODE_VOLUNTARY = 1;

    public final static int SUBSYSTEM_RUN_STOPPED = 0;
    public final static int SUBSYSTEM_RUN_STARTED = 1;
    public final static int SUBSYSTEM_RUN_FAILED = 2;
    public final static int SUBSYSTEM_RUN_ZOMBIE = 3;

    JSONObject getSubsystemInfo();

    void startSubsystem();
    void stopSubsystem();

    void onSubsystemStarted(String subsystem, int state);
    void onSubsystemStopped(String subsystem, int state);
}
