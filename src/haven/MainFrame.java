/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import haven.resources.Resource;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Collection;
import java.util.LinkedList;

public class MainFrame extends Frame implements Runnable, FSMan {
    final HavenPanel panel;
    final ThreadGroup g;
    DisplayMode fsmode = null, prefs = null;
    final Dimension insetsSize;

//    CustomConfig config;

    static {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    DisplayMode findmode(Dimension size) {
        GraphicsDevice dev = getGraphicsConfiguration().getDevice();
        if (!dev.isFullScreenSupported())
            return (null);
        DisplayMode b = null;
        for (DisplayMode m : dev.getDisplayModes()) {
            int d = m.getBitDepth();
            if ((m.getWidth() == size.width) && (m.getHeight() == size.height) && ((d == 24) || (d == 32) || (d == DisplayMode.BIT_DEPTH_MULTI))) {
                if ((b == null) || (d > b.getBitDepth()) || ((d == b.getBitDepth()) && (m.getRefreshRate() > b.getRefreshRate())))
                    b = m;
            }
        }
        return (b);
    }

    public void setfs() {
        GraphicsDevice dev = getGraphicsConfiguration().getDevice();
        if (prefs != null)
            return;
        prefs = dev.getDisplayMode();
        try {
            setVisible(false);
            dispose();
            setUndecorated(true);
            setVisible(true);
            dev.setFullScreenWindow(this);
            dev.setDisplayMode(fsmode);

        } catch (Exception e) {
            throw (new RuntimeException(e));
        }
    }

    public void setwnd() {
        GraphicsDevice dev = getGraphicsConfiguration().getDevice();
        if (prefs == null)
            return;
        try {
            dev.setDisplayMode(prefs);
            dev.setFullScreenWindow(null);
            setVisible(false);
            dispose();
            setUndecorated(false);
            setVisible(true);
        } catch (Exception e) {
            throw (new RuntimeException(e));
        }
        prefs = null;
    }

    public boolean hasfs() {
        return (prefs != null);
    }

    public void togglefs() {
        if (prefs == null)
            setfs();
        else
            setwnd();
    }

    private void seticon() {
        Image icon = null;
        try {
            InputStream data = MainFrame.class.getResourceAsStream("icon.png");
            icon = javax.imageio.ImageIO.read(data);
            data.close();
        } catch (IOException e) {
            CustomConfig.logger.warn("Cannot set window image", e);
        }
        setIconImage(icon);
    }

    private MainFrame(final Coord sizeC, ThreadGroup tg) {
        super("Haven and Hearth (modified by VladP53 with some code from Ender, Gilbertus, Pacho clients)");
        ourInstance = this;
        this.g = tg;

        final Dimension size = sizeC.toDimension();
        panel = new HavenPanel(size);
        fsmode = findmode(size);

        setResizable(true);

        add(panel);
        pack();

        Insets insets = getInsets();
        insetsSize = new Dimension(insets.left + insets.right, insets.top + insets.bottom);
        Dimension minimalSizeWithInsets = new Dimension(800 + insetsSize.width, 600 + insetsSize.height);
        setMinimumSize(minimalSizeWithInsets);
        setSize(new Dimension(size.width + insetsSize.width, size.height + insetsSize.height));

        panel.requestFocus();
        seticon();
        setVisible(true);
        panel.init();
        setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
    }

    public void run() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (CustomConfig.isSaveable) CustomConfigProcessor.saveSettings();
                g.interrupt();
            }
        });
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                CustomConfig.updateWindowSize(getWidth() - insetsSize.width, getHeight() - insetsSize.height);
            }
        });
        Thread ui = new HackThread(panel, "Haven UI thread");
        panel.setfsm(this);
        ui.start();
        try {
            while (true) {
                Bootstrap bill = new Bootstrap();
                if (Config.defserv != null)
                    bill.setaddr(Config.defserv);
                if ((Config.authuser != null) && (Config.authck != null)) {
                    bill.setinitcookie(Config.authuser, Config.authck);
                    Config.authck = null;
                }
                Session sess = bill.run(panel);
                RemoteUI rui = new RemoteUI(sess);
                rui.run(panel.newui(sess));
            }
        } catch (InterruptedException ignored) {
        } finally {
            ui.interrupt();
            dispose();
        }
    }

    public static void setupres() {
        Resource.addcache(ResCache.global);
        Resource.addurl(Config.resurl);

        if (ResCache.global != null) {
            try {
                Resource.loadlist(ResCache.global.fetch("tmp/allused"), -10);
            } catch (IOException e) {
                CustomConfig.logger.error("Failed to load resources from tmp/allused", e);
            }
        }
        if (!Config.nopreload) {
            try {
                InputStream pls = Resource.class.getResourceAsStream("res-preload");
                if (pls != null) {
                    Resource.loadlist(pls, -5);
                }
            } catch (IOException e) {
                CustomConfig.logger.error("Failed to load res-preload", e);
            }
            try {
                InputStream pls = Resource.class.getResourceAsStream("res-bgload");
                if (pls != null) {
                    Resource.loadlist(pls, -10);
                }
            } catch (IOException e) {
                CustomConfig.logger.error("Failed to load res-bgload", e);
            }
        }
    }

    static {
        WebBrowser.self = JnlpBrowser.create();
    }

    private static void javabughack() throws InterruptedException {
        /* Work around a stupid deadlock bug in AWT. */
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    java.io.PrintStream bitbucket = new java.io.PrintStream(new java.io.ByteArrayOutputStream());
                    bitbucket.print(LoginScreen.textf);
                    bitbucket.print(LoginScreen.textfs);
                }
            });
        } catch (java.lang.reflect.InvocationTargetException e) {
            /* Oh, how I love Swing! */
            throw (new Error(e));
        }
        /* Work around another deadl bug in Sun's JNLP client. */
        javax.imageio.spi.IIORegistry.getDefaultInstance();
    }

    private static void main2(String[] args) {
        Config.cmdline(args);
        ThreadGroup threadGroup = HackThread.tg();
        setupres();
        MainFrame mainFrame = new MainFrame(CustomConfig.getWindowSize(), threadGroup);
        //noinspection UnusedParameters
        CustomConfig.isSaveable = true;
        if (Config.fullscreen)
            mainFrame.setfs();
        if (threadGroup instanceof haven.error.ErrorHandler) {
            final haven.error.ErrorHandler hg = (haven.error.ErrorHandler) threadGroup;
            hg.sethandler(new haven.error.ErrorGui(null) {
                public void errorsent() {
                    hg.interrupt();
                }
            });
        }
        mainFrame.run();
        dumplist(Resource.loadwaited, Config.loadwaited);
        dumplist(Resource.cached(), Config.allused);
        if (ResCache.global != null) {
            try {
                Collection<Resource> used = new LinkedList<Resource>();
                for (Resource res : Resource.cached()) {
                    if (res.getPriority() >= 0)
                        used.add(res);
                }
                Writer w = new OutputStreamWriter(ResCache.global.store("tmp/allused"), "UTF-8");
                try {
                    Resource.dumplist(used, w);
                } finally {
                    w.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static void main(final String[] args) {
        /* Set up the error handler as early as humanly possible. */
        ThreadGroup g = new ThreadGroup("Haven client");
        String ed;
        if (!(ed = Utils.getprop("haven.errorurl", "")).isEmpty()) {
            try {
                final haven.error.ErrorHandler hg = new haven.error.ErrorHandler(new java.net.URL(ed));
                hg.sethandler(new haven.error.ErrorGui(null) {
                    public void errorsent() {
                        hg.interrupt();
                    }
                });
                g = hg;
            } catch (java.net.MalformedURLException ignored) {
            }
        }
        Thread main = new HackThread(g, new Runnable() {
            public void run() {
                try {
                    javabughack();
                } catch (InterruptedException e) {
                    return;
                }
                main2(args);
            }
        }, "Haven main thread");
        main.start();
        try {
            main.join();
        } catch (InterruptedException e) {
            g.interrupt();
            return;
        }
        System.exit(0);
    }

    private static void dumplist(Collection<Resource> list, String fn) {
        if (fn == null) return;
        try {
            Writer w = new OutputStreamWriter(new FileOutputStream(fn), "UTF-8");
            try {
                Resource.dumplist(list, w);
            } finally {
                w.close();
            }
        } catch (IOException e) {
            throw (new RuntimeException(e));
        }
    }

    private static MainFrame ourInstance;

    public static void setWindowSize(Dimension dimension) {
        if (ourInstance == null) return;
        ourInstance.setSize(dimension);
    }
}
