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

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RemoteUI implements UI.Receiver {
    final Session sess;
    UI ui;

    protected static final Logger LOG = Logger.getLogger(RemoteUI.class);

    public RemoteUI(final Session sess) {
        this.sess = sess;
        Widget.initbardas();
    }

    public void rcvmsg(final int id, @NotNull final String name, final Object... args) {
        final Message msg = new Message(Message.RMSG_WDGMSG);
        msg.adduint16(id);
        msg.addstring(name);
        msg.addlist(args);
        sess.queuemsg(msg);
    }

    public void run(@NotNull final UI ui) throws InterruptedException {
        this.ui = ui;
        CustomConfig.ui = ui;
        ui.setreceiver(this);
        while (sess.alive()) {
            Message msg;
            while ((msg = sess.getuimsg()) != null) {
                if (msg.type == Message.RMSG_NEWWDG) {
                    final int id = msg.uint16(); // New widget Id
                    String type = msg.string(); // New widget Type
                    Coord c = msg.coord(); // New widget coordinates
                    final int parent = msg.uint16(); //Parent Id for new widget
                    final Object[] args = msg.list(); // Arguments for widget creator (WidgetFactory)

                    // UI fixes START
                    if (type.equals("cnt")) { // Central welcome widget
                        args[0] = CustomConfig.getWindowSize();
                    } else if (type.equals("img") && args.length >= 1 && (args[0] instanceof String)) {
                        final String arg0 = (String) args[0];
                        if (arg0.startsWith("gfx/hud/prog/")) { // Hourglass (progress bar) at center of screen and change widget type
                            c = CustomConfig.getWindowCenter();
                            type = "progressbar";
                            Progress.class.getClass();
                        }
                        if (arg0.equals("gfx/ccscr"))
                            c = CustomConfig.getWindowCenter().add(-400, -300);
                        if (arg0.equals("gfx/logo2"))
                            c = CustomConfig.getWindowCenter().add(-415, -300);
                    } else if (type.equals("charlist") && args.length >= 1) {
                        c = CustomConfig.getWindowCenter().add(-380, -50);
                    } else if (type.equals("ibtn") && args.length >= 2) { // New Player Button
                        if (args[0].equals("gfx/hud/buttons/ncu") && args[1].equals("gfx/hud/buttons/ncd")) {
                            c = CustomConfig.getWindowCenter().add(86, 214);
                        }
                    } else if (type.equals("wnd") && c.x == 400 && c.y == 200) {
                        LOG.info("Strange window name=" + args[1].toString());
                        c = CustomConfig.getWindowCenter().add(0, -100);
                    }
                    // UI fixes END
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Creating Widget id=" + id + " parentId=" + parent + " type='" + type + "' in coord " + c.toString() + "\n\twith args: " + Arrays.toString(args));
                    }
                    ui.newwidget(id, type, c, parent, args);

                } else if (msg.type == Message.RMSG_WDGMSG) {
                    final int id = msg.uint16();
                    final String type = msg.string();
                    final Object[] args = msg.list();
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Message (type='" + type + "') for widget (id=" + id + ')' + "\n\tcontains: " + Arrays.toString(args));
                    }
                    ui.uimsg(id, type, args);

                } else if (msg.type == Message.RMSG_DSTWDG) {
                    final int id = msg.uint16();
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Deleting widget id=" + id);
                    }
                    ui.destroy(id);
                }
            }
            //noinspection SynchronizeOnNonFinalField
            synchronized (sess) {
                sess.wait();
            }

        }
    }
}
