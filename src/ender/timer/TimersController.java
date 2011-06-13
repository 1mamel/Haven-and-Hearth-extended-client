package ender.timer;

import haven.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TimersController {
    private static final Properties options;
    private static final java.util.Timer timer;
    public static final Collection<Timer> timers;

    static {
        options = new Properties();
        timers = new ConcurrentLinkedQueue<Timer>();
        timer = new java.util.Timer(true);
        timer.schedule(new CheckTask(), 0, 1000);
    }

    static class CheckTask extends TimerTask {
        @Override
        public void run() {
            for (Timer timer : timers) {
                if ((timer.isWorking()) && (timer.update())) {
                    timer.stop();
                }
            }
        }
    }

    static void add(Timer t) {
        timers.add(t);
    }

    static void remove(Timer t) {
        timers.remove(t);
        TimersController.save();
    }

    public static void load() {
        synchronized (options) {
            try {
                options.load(new FileInputStream("timers.conf"));
                timers.clear();
                for (Object key : options.keySet()) {
                    String str = key.toString();
                    if (str.indexOf("Name") > 0) {
                        continue;
                    }
                    String tmp[] = Utils.commaPattern.split(options.getProperty(str));
                    try {
                        int start = Integer.parseInt(tmp[0]);
                        int time = Integer.parseInt(tmp[1]);
                        String name = options.getProperty(str + "Name");
                        new Timer(start, time, name);
                    } catch (Exception e) {
                    }
                }
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    }

    public static void save() {
        int i = 0;
        synchronized (options) {
            options.clear();
            synchronized (timers) {
                for (Timer timer : timers) {
                    options.setProperty("Timer" + i, String.format("%d,%d", timer.getStart(), timer.getTime()));
                    options.setProperty("Timer" + i + "Name", timer.getName());
                    i++;
                }
            }
            try {
                options.store(new FileOutputStream("timers.conf"), "Timers config");
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    }
}
