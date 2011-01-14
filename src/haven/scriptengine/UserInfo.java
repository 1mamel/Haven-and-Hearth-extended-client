package haven.scriptengine;

import haven.IMeter;
import haven.Speedget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 19:15
 */
public class UserInfo {

    public int getEnergy() {
        return energy;
    }

    public int getAuthority() {
        return authorityNow;
    }

    public int getAuthorityMax() {
        return authorityMax;
    }

    public int getHPHard() {
        return hpHard;
    }

    public int getHPSoft() {
        return hpSoft;
    }

    public int getHPCurrent() {
        return hpNow;
    }

    public int getHappy() {
        return happy;
    }

    public int getHappyTowards() {
        return happyTowards;
    }

    public int getHungry() {
        return hungry;
    }

    public int getSpeed() {
        return speedCurrent;
    }

    public int getMaxSpeed() {
        return speedMax;
    }

    public boolean setSpeed(int speed) {
        return speedGet != null && speedGet.changeSpeed(speed);
    }

    public static void iMeterGenerated(IMeter meter, String resName) {
        ourInstance.newMeter(meter, resName);
    }

    public static void meterUpdated(IMeter meter, String tooltip) {
        ourInstance.meterUp(meter, tooltip);
    }

    private void meterUp(IMeter meter, String tooltip) {
        try {
            if (meter == hpMeter) updateHp(tooltip);
            if (meter == energyMeter) updateEnergy(tooltip);
            if (meter == happyMeter) updateHappy(tooltip);
            if (meter == hungryMeter) updateHungry(tooltip);
            if (meter == authorityMeter) updateAuthority(tooltip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newMeter(IMeter meter, String resName) {
        if (resName.equals("gfx/hud/meter/hp")) //HP meter
            hpMeter = meter;
        else if (resName.equals("gfx/hud/meter/nrj")) // Energy Meter
            energyMeter = meter;
        else if (resName.equals("gfx/hud/meter/hngr")) // Hungry Meter
            hungryMeter = meter;
        else if (resName.equals("gfx/hud/meter/happy")) // Happyness Meter
            happyMeter = meter;
        else if (resName.equals("gfx/hud/meter/auth")) // Authority Meter
            authorityMeter = meter;
        else
            System.err.println("Unexpected IMeter with imagename=" + resName);
    }

    private void updateAuthority(String tooltip) {
        Matcher makeMatch = intsOnly.matcher(tooltip);
        makeMatch.find();
        authorityNow = Integer.parseInt(makeMatch.group());
        makeMatch.find();
        authorityMax = Integer.parseInt(makeMatch.group());
    }

    private void updateHungry(String tooltip) {
        Matcher makeMatch = intsOnly.matcher(tooltip);
        makeMatch.find();
        makeMatch.find();
        hungry = Integer.parseInt(makeMatch.group());
    }

    private void updateHappy(String tooltip) {
        Matcher makeMatch = intsOnly.matcher(tooltip);
        makeMatch.find();
        happy = Integer.parseInt(makeMatch.group());
        happyTowards = (makeMatch.find()) ? Integer.parseInt(makeMatch.group()) : 0;
        if (tooltip.startsWith("Neutral")) {
            happyTowards = happy;
            happy = 0;
        }
        if (tooltip.startsWith("Un")) happy *= -1;
    }

    private void updateEnergy(String tooltip) {
        Matcher makeMatch = intsOnly.matcher(tooltip);
        makeMatch.find();
        energy = Integer.parseInt(makeMatch.group());
    }


    public void updateHp(String tooltip) {
        Matcher makeMatch = intsOnly.matcher(tooltip);
        makeMatch.find();
        hpNow = Integer.parseInt(makeMatch.group());
        makeMatch.find();
        hpSoft = Integer.parseInt(makeMatch.group());
        makeMatch.find();
        hpHard = Integer.parseInt(makeMatch.group());
    }

    private IMeter hpMeter;
    private IMeter energyMeter;
    private IMeter happyMeter;
    private IMeter hungryMeter;
    private IMeter authorityMeter;

    private int energy = -1;
    private int hpNow = -1;
    private int hpSoft = -1;
    private int hpHard = -1;
    private int hungry = -1;
    private int authorityNow = -1;
    private int authorityMax = -1;
    private int happy = -1;
    private int happyTowards = -1;

    private int progress = -1; // [0-100] or -1 if not in progress

    private Speedget speedGet;
    private int speedCurrent = -1; // [0-3]
    private int speedMax = -1; // [0-3]

    static Pattern intsOnly = Pattern.compile("[-]?\\d+");

    private static final UserInfo ourInstance = new UserInfo();

    public static UserInfo getInstance() {
        return ourInstance;
    }

    public static void updateProgress(int p) {
        getInstance().progress = p;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isInProgress() {
        return progress > 0;
    }

    public static void updateSpeed(int cur, int max, Speedget sg) {
        getInstance().speedCurrent = cur;
        getInstance().speedMax = max;
        getInstance().speedGet = sg;
    }
}
