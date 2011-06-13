package haven.scriptengine;

import haven.CustomConfig;
import haven.CustomConfigProcessor;
import haven.CustomConsole;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 23:33
 */
@SuppressWarnings({"UnusedDeclaration", "MethodMayBeStatic"})
public class ConfigProvider {

    private void setNightvision(boolean nightvision) {
        CustomConfig.hasNightVision = nightvision;
    }

    public void setNightvision(String arg0) {
        arg0 = arg0.toUpperCase();
        if (arg0.equals("ON") || arg0.equals("TRUE"))
            CustomConfig.hasNightVision = true;
        else if (arg0.equals("OFF") || arg0.equals("FALSE"))
            CustomConfig.hasNightVision = false;
        else throw new RuntimeException("setNightvision(ON|OFF) " + arg0);
    }

    private void setIrc(boolean irc) {
        setIrc(String.valueOf(irc));
    }

    public void setIrc(String arg0) {
        arg0 = arg0.toUpperCase();
        if (arg0.equals("ON") || arg0.equals("TRUE"))
            CustomConfig.isIRCOn = true;
        else if (arg0.equals("OFF") || arg0.equals("FALSE"))
            CustomConfig.isIRCOn = false;
        else throw new RuntimeException("setIrc(ON|OFF)");
    }

    public void setDebugMsgs(String arg0) {
        arg0 = arg0.toUpperCase();
        if (arg0.equals("ON") || arg0.equals("TRUE"))
            CustomConfig.setDebugLogging(true);
        else if (arg0.equals("OFF") || arg0.equals("FALSE"))
            CustomConfig.setDebugLogging(false);
        else throw new RuntimeException("setDebugMsgs(ON|OFF)");
    }

    public void setScreenSize(int width, int height) {
        try {
            CustomConfig.setWindowSize(Math.max(width, 800), Math.max(height, 600));
            CustomConfigProcessor.saveSettings();
            CustomConsole.append("Client must be restarted for new settings to take effect.", Color.RED.darker());
        } catch (NumberFormatException e) {
            throw new RuntimeException("setScreenSize(int width, int height);");
        }
    }

    public void setSound(int sound) {
        setSound(String.valueOf(sound));
    }

    public void setSound(String arg0) {
        arg0 = arg0.toUpperCase();
        int vol;
        try {
            if (arg0.equals("ON") || arg0.equals("TRUE")) {
                CustomConfig.isSoundOn = true;
            } else if (arg0.equals("OFF") || arg0.equals("FALSE")) {
                CustomConfig.isSoundOn = false;
            } else if ((vol = Integer.parseInt(arg0)) >= 0 && vol <= 100) {
                CustomConfig.sfxVol = vol;
            } else throw new RuntimeException("setSound([ON|OFF]|[0-100])");
        } catch (NumberFormatException e) {
            throw new RuntimeException("setSound([ON|OFF]|[0-100])");
        }
    }

    public void setMusic(String arg0) {
        arg0 = arg0.toUpperCase();
        int vol;
        try {
            if (arg0.equals("ON") || arg0.equals("TRUE")) {
                CustomConfig.isMusicOn = true;
            } else if (arg0.equals("OFF") || arg0.equals("FALSE")) {
                CustomConfig.isMusicOn = false;
            } else if ((vol = Integer.parseInt(arg0)) >= 0 && vol <= 100) {
                CustomConfig.musicVol = vol;
            } else throw new RuntimeException("setMusic([ON|OFF]|[0-100])");
        } catch (NumberFormatException e) {
            throw new RuntimeException("setMusic([ON|OFF]|[0-100])");
        }
    }

    public void save() {
        CustomConfigProcessor.saveSettings();
    }

    public void forcesave() {
        CustomConfig.isSaveable = true;
        CustomConfigProcessor.saveSettings();
    }

    public boolean getNightvision() {
        return CustomConfig.hasNightVision;
    }

    public boolean getIrc() {
        return CustomConfig.isIRCOn;
    }

    public int getScreenWidth() {
        return CustomConfig.getWindowWidth();
    }

    public int getScreenHeight() {
        return CustomConfig.getWindowHeight();
    }

    public int getScreenCenterX() {
        return CustomConfig.getWindowCenter().x();
    }

    public int getScreenXenterY() {
        return CustomConfig.getWindowCenter().y();
    }

    public boolean isSoundOn() {
        return CustomConfig.isSoundOn;
    }

    public int getSoundVolume() {
        return CustomConfig.sfxVol;
    }

    public boolean isMusicOn() {
        return CustomConfig.isMusicOn;
    }

    public int getMusicVolume() {
        return CustomConfig.musicVol;
    }

    public boolean isDebugLogging() {
        return CustomConfig.isDebugLogging();
    }

    private static ConfigProvider ourInstance = new ConfigProvider();

    public static ConfigProvider getInstance() {
        return ourInstance;
    }

    private ConfigProvider() {
    }
}
