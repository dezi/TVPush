package de.xavaro.android.gui.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.smart.GUIDesktopActivity;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.R;

import pub.android.interfaces.ext.OnSpeechHandler;
import pub.android.interfaces.pub.PUBCamera;
import pub.android.interfaces.pub.PUBSmartBulb;
import pub.android.interfaces.pub.PUBSmartPlug;
import pub.android.interfaces.gui.DesktopHandler;
import pub.android.interfaces.gui.OnSubsystemRequest;
import pub.android.interfaces.gui.OnCameraHandlerRequest;
import pub.android.interfaces.gui.OnSmartBulbHandlerRequest;
import pub.android.interfaces.gui.OnSmartPlugHandlerRequest;
import pub.android.interfaces.all.SubSystemHandler;

public class GUI implements
        SubSystemHandler,
        DesktopHandler,
        OnSpeechHandler,
        OnSubsystemRequest,
        OnCameraHandlerRequest,
        OnSmartPlugHandlerRequest,
        OnSmartBulbHandlerRequest
{
    private static final String LOGTAG = GUI.class.getSimpleName();

    public static GUI instance;

    public GUISubSystems subSystems;
    public GUIApplication application;
    public GUIDesktopActivity desktopActivity;

    public GUI(Application appcontext)
    {
        application = (GUIApplication) appcontext;
        subSystems = new GUISubSystems();
    }

    @Override
    public void setInstance()
    {
        GUI.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "gui");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_MANDATORY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_gui_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_gui_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_gui_420));

        return info;
    }

    @Override
    public JSONObject getSubsystemSettings()
    {
        return getSubsystemInfo();
    }

    @Override
    public void startSubsystem(String subsystem)
    {
        onSubsystemStarted("gui", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        onSubsystemStopped("gui", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    @Override
    public int getSubsystemState(String subsystem)
    {
        Log.d(LOGTAG, "getSubsystemState: STUB! subsystem=" + subsystem);
        return SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED;
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        Log.d(LOGTAG, "onSubsystemStarted: STUB! state=" + state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        Log.d(LOGTAG, "onSubsystemStopped: STUB! state=" + state);
    }

    @Override
    public JSONObject onGetSubsystemSettings(String subsystem)
    {
        Log.d(LOGTAG, "onGetSubsystemSettings: STUB! subsystem=" + subsystem);

        return null;
    }

    @Override
    public void onStartSubsystemRequest(String drv)
    {
        Log.d(LOGTAG, "onStartSubsystemRequest: STUB!");
    }

    @Override
    public void onStopSubsystemRequest(String drv)
    {
        Log.d(LOGTAG, "onStopSubsystemRequest: STUB!");
    }

    @Override
    public PUBCamera onCameraHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        Log.d(LOGTAG, "onCameraHandlerRequest: STUB!");

        return null;
    }

    @Override
    public PUBSmartPlug onSmartPlugHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        Log.d(LOGTAG, "OnSmartPlugHandlerRequest: STUB!");

        return null;
    }

    @Override
    public PUBSmartBulb onSmartBulbHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        Log.d(LOGTAG, "onSmartBulbHandlerRequest: STUB!");

        return null;
    }

    @Override
    public void displayCamera(boolean show, String uuid)
    {
        Log.d(LOGTAG, "displayCamera: uuid=" + uuid);

        GUI.instance.desktopActivity.displayCamera(show, uuid);
    }

    @Override
    public void displaySpeechRecognition(boolean show)
    {
        Log.d(LOGTAG, "displaySpeechRecognition: show=" + show);

        GUI.instance.desktopActivity.displaySpeechRecognition(show);
    }

    @Override
    public void displayWizzard(boolean show, String wizzard)
    {
        Log.d(LOGTAG, "displayWizzard: show=" + show + " wizzard=" + wizzard);

        GUI.instance.desktopActivity.displayWizzard(show, wizzard);
    }

    @Override
    public void displayStreetView(boolean show, String address)
    {
        Log.d(LOGTAG, "displayStreetView: show=" + show + " address=" + address);

        GUI.instance.desktopActivity.displayStreetView(show, address);
    }

    @Override
    public void displayPinCodeMessage(int timeout)
    {
        GUI.instance.desktopActivity.displayPinCodeMessage(timeout);
    }

    @Override
    public void displayToastMessage(String message, int seconds, boolean emphasis)
    {
        Log.d(LOGTAG, "displayToastMessage: message=" + message);

        GUI.instance.desktopActivity.displayToastMessage(message, seconds, emphasis);
    }

    @Override
    public void onActivateRemote()
    {
        GUI.instance.desktopActivity.onActivateRemote();
    }

    @Override
    public void onSpeechReady()
    {
        GUI.instance.desktopActivity.onSpeechReady();
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        GUI.instance.desktopActivity.onSpeechResults(speech);
    }
}
