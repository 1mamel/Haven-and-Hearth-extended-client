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

import com.sun.opengl.util.Screenshot;

import javax.media.opengl.GLException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class RootWidget extends ConsoleHost {
    public static final Resource defcurs = Resource.load("gfx/hud/curs/arw");
    @SuppressWarnings({"UnusedDeclaration"})
    Logout logout = null;
    Profile gprof;
    @SuppressWarnings({"UnusedDeclaration"})
    boolean afk = false;
    public static boolean screenshot = false;
    public static boolean names_ready = false;

    public RootWidget(final UI ui, final Coord sz) {
        super(ui, new Coord(0, 0), sz);
        setfocusctl(true);
        cursor = defcurs;
    }

    public boolean globtype(final char key, final KeyEvent ev) {
        if (!super.globtype(key, ev)) {
            final int code = ev.getKeyCode();
            final boolean ctrl = ev.isControlDown();
            final boolean alt = ev.isAltDown();

            if (Config.profile && key == '`') {
                new Profwnd(ui.slen, ui.mainview.prof, "MV prof");
            } else if (Config.profile && key == '~') {
                new Profwnd(ui.slen, gprof, "Glob prof");
            } else if (Config.profile && key == '!') {
                new Profwnd(ui.slen, ui.mainview.mask.prof, "ILM prof");
            } else if (code == KeyEvent.VK_N && ctrl) {
                CustomConfig.toggleNightvision();
            } else if (code == KeyEvent.VK_X && ctrl) {
                CustomConfig.toggleXray();
            } else if (code == KeyEvent.VK_H && ctrl) {
                CustomConfig.toggleHideObjects();
            } else if (code == KeyEvent.VK_Y && ctrl) {
                CustomConfig.toggleRender();
            } else if (code == KeyEvent.VK_Q && alt) {
                UI.speedget.get().wdgmsg("set", 0);
            } else if (code == KeyEvent.VK_W && alt) {
                UI.speedget.get().wdgmsg("set", 1);
            } else if (code == KeyEvent.VK_E && alt) {
                UI.speedget.get().wdgmsg("set", 2);
            } else if (code == KeyEvent.VK_R && alt) {
                UI.speedget.get().wdgmsg("set", 3);
            } else if (code == KeyEvent.VK_G && ctrl) {
                CustomConfig.toggleMapGrid();
            } else if (key == 2 & ctrl) { // CTRL - B
                BuddyWnd.instance.toggle();
            } else if (key == 20 & ctrl) {  // CTRL - T
                CharWnd.instance.get().toggle();
            } else if (code == KeyEvent.VK_HOME) {
                ui.mainview.resetcam();
            } else if (code == KeyEvent.VK_END) {
                screenshot = true;
            } else if (code == KeyEvent.VK_COLON) {
                entercmd();
            } else if (key == '`' || key == '~') {
                CustomConfig.toggleConsole();
            } else if (key != 0) {
//                System.err.println("gk" + (int) key + " ctrl:" + ctrl + " alt:" + alt);
                if (key != 20 && key != 2) {
                    wdgmsg("gk", (int) key);
                }
            }
        }
        return true;
    }

    public void draw(final GOut g) {
        if (screenshot && Config.sshot_noui) {
            visible = false;
        }
        super.draw(g);
        drawcmd(g, new Coord(20, 580));
        if (screenshot && (!Config.sshot_nonames || names_ready)) {
            visible = true;
            screenshot = false;
            try {
                final Coord s = CustomConfig.getWindowSize();
                final String stamp = Utils.sessdate(System.currentTimeMillis());
                final String ext = Config.sshot_compress ? ".jpg" : ".png";
                final File f = new File("screenshots/SS_" + stamp + ext);
                f.mkdirs();
                Screenshot.writeToFile(f, s.x, s.y);
            } catch (GLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//	if(!afk && (System.currentTimeMillis() - ui.lastevent > 300000)) {
//	    afk = true;
//	    Widget slen = findchild(SlenHud.class);
//	    if(slen != null)
//		slen.wdgmsg("afk");
//	} else if(afk && (System.currentTimeMillis() - ui.lastevent < 300000)) {
//	    afk = false;
//	}
    }

    public void error(final String msg) {
    }

    @Override
    public void destroy() {
        UI.console = null;
        super.destroy();
    }
}
