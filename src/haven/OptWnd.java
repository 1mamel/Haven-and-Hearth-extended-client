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

import java.awt.font.TextAttribute;
import java.util.*;

public class OptWnd extends Window {
    public static final RichText.Foundry foundry = new RichText.Foundry(TextAttribute.FAMILY, "SansSerif", TextAttribute.SIZE, 10);
    private static final String[][] CHECKBOXES_LIST = {{"Walls", "gfx/arch/walls"},
            {"Gates", "gfx/arch/gates"},
            {"Wooden Houses", "gfx/arch/cabin"},
            {"Stone Mansions", "gfx/arch/inn"},
            {"Plants", "gfx/terobjs/plants"},
            {"Trees", "gfx/terobjs/trees"},
            {"Stones", "gfx/terobjs/bumlings"},
            {"Flavor objects", "flavobjs"},
            {"Bushes", "gfx/tiles/wald"},
            {"Thicket", "gfx/tiles/dwald"}};
    private final Tabs body;
    private String curcam;
    private final Map<String, CamInfo> caminfomap = new HashMap<String, CamInfo>();
    private final Map<String, String> camname2type = new HashMap<String, String>();
    private final Map<String, String[]> camargs = new HashMap<String, String[]>();
    private final Comparator<String> camcomp = new Comparator<String>() {
        public int compare(String a, String b) {
            if (a.startsWith("The ")) a = a.substring(4);
            if (b.startsWith("The ")) b = b.substring(4);
            return (a.compareTo(b));
        }
    };

    private static class CamInfo {
        final String name;
        final String desc;
        final Tabs.Tab args;

        private CamInfo(final String name, final String desc, final Tabs.Tab args) {
            this.name = name;
            this.desc = desc;
            this.args = args;
        }
    }

    //// IRC
    final Label serverLabel;
    final Label chnlLabel;
    final Label defIRCNickLabel;
    final Label altIRCNickLabel;
    final TextEntry serverAddress;
    final TextEntry channelList;
    final TextEntry defNick;
    final TextEntry altNick;
    final CheckBox ircToggle;

    @SuppressWarnings({"UnusedAssignment"})
    public OptWnd(final Coord c, final Widget parent) {
        super(c, new Coord(400, 440), parent, "Options");
        UI.optionsWindow.set(this);
        body = new Tabs(Coord.z, new Coord(400, 430), this) {
            public void changed(final Tab from, final Tab to) {
                if (to != null) Utils.setpref("optwndtab", to.btn.getText());
                if (from != null) from.btn.c.setY(0);
                if (to != null) to.btn.c.setY(-2);
            }
        };
        Widget tab;

        { /* GENERAL TAB */
            tab = body.new Tab(new Coord(0, 0), 60, "General");

            new Button(new Coord(10, 40), 125, tab, "Quit") {
                public void click() {
                    new MessageBox(MessageBox.DC, MessageBox.DS, ui.root, "Confirmation", "Are you sure want to exit?", MessageBox.BUTTONS_YESNO, new Callback<Integer>() {
                        @Override
                        public void result(final Integer result) {
                            if (MessageBox.BUTTON_YES == result) {
                                HackThread.tg().interrupt();
                            }
                        }
                    });
                }
            };
            new Button(new Coord(10, 70), 125, tab, "Log out") {
                public void click() {
                    new MessageBox(MessageBox.DC, MessageBox.DS, ui.root, "Confirmation", "Are you sure want to log out?", MessageBox.BUTTON_YES | MessageBox.BUTTON_NO, new Callback<Integer>() {
                        @Override
                        public void result(final Integer result) {
                            if (MessageBox.BUTTON_YES == result) {
                                ui.sess.close();
                            }
                        }
                    });
                }
            };
            new Button(new Coord(10, 100), 125, tab, "Toggle fullscreen") {
                public void click() {
                    if (ui.fsm != null) {
                        if (ui.fsm.hasfs()) ui.fsm.setwnd();
                        else ui.fsm.setfs();
                    }
                }
            };

            new CheckBox(new Coord(10, 130), tab, "Use new minimap (restart required)") {
                public void changed(final boolean val) {
                    CustomConfig.setNew_minimap(val);
                    CustomConfig.save();
                }

                {
                    a = CustomConfig.isNew_minimap();
                }
            };

            new CheckBox(new Coord(10, 165), tab, "Use new chat (restart required)") {
                public void changed(final boolean val) {
                    CustomConfig.setNew_chat(val);
                    CustomConfig.save();
                }

                {
                    a = CustomConfig.isNew_chat();
                }
            };

            (new CheckBox(new Coord(10, 200), tab, "Add timestamp in chat") {
                public void changed(final boolean val) {
                    CustomConfig.setAddChatTimestamp(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isAddChatTimestamp();

            (new CheckBox(new Coord(10, 235), tab, "Show dowsing direcion") {
                public void changed(final boolean val) {
                    CustomConfig.setShowDirection(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isShowDirection();

            (new CheckBox(new Coord(10, 270), tab, "Always show heartling names") {
                public void changed(final boolean val) {
                    CustomConfig.setShowNames(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isShowNames();

            (new CheckBox(new Coord(10, 305), tab, "Always show other kin names") {
                public void changed(final boolean val) {
                    CustomConfig.setShowOtherNames(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isShowOtherNames();

            (new CheckBox(new Coord(10, 340), tab, "Show smileys in chat") {
                public void changed(final boolean val) {
                    CustomConfig.setUse_smileys(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isUse_smileys();

            (new CheckBox(new Coord(10, 375), tab, "Show item quality") {
                public void changed(final boolean val) {
                    CustomConfig.setShowq(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isShowq();

            (new CheckBox(new Coord(220, 130), tab, "Fast menu") {
                public void changed(final boolean val) {
                    CustomConfig.setFastFlowerAnim(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isFastFlowerAnim();

            (new CheckBox(new Coord(220, 165), tab, "Compress screenshots") {
                public void changed(final boolean val) {
                    CustomConfig.setSshot_compress(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isSshot_compress();

            (new CheckBox(new Coord(220, 200), tab, "Exclude UI from screenshot") {
                public void changed(final boolean val) {
                    CustomConfig.setSshot_noui(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isSshot_noui();

            (new CheckBox(new Coord(220, 235), tab, "Exclude names from screenshot") {
                public void changed(final boolean val) {
                    CustomConfig.setSshot_nonames(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isSshot_nonames();

            (new CheckBox(new Coord(220, 270), tab, "Use optimized claim higlighting") {
                public void changed(final boolean val) {
                    CustomConfig.setNewclaim(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isNewclaim();

//        (new CheckBox(new Coord(220, 305), tab, "Show digit toolbar") {
//        public void changed(boolean val) {
//            ui.mnu.digitbar.visible = val;
//            Config.setWindowOpt(ui.mnu.digitbar.name, val);
//        }
//        }).a = ui.mnu.digitbar.visible;

//        (new CheckBox(new Coord(220, 340), tab, "Show F-button toolbar") {
//        public void changed(boolean val) {
//            ui.mnu.functionbar.visible = val;
//            Config.setWindowOpt(ui.mnu.functionbar.name, val);
//        }
//        }).a = ui.mnu.functionbar.visible;

//        (new CheckBox(new Coord(220, 375), tab, "Show numpad toolbar") {
//        public void changed(boolean val) {
//            ui.mnu.numpadbar.visible = val;
//            Config.setWindowOpt(ui.mnu.numpadbar.name, val);
//        }
//        }).a = ui.mnu.numpadbar.visible;

            final Widget editbox = new Frame(new Coord(310, 30), new Coord(90, 100), tab);
            new Label(new Coord(20, 10), editbox, "Edit mode:");
            final RadioGroup editmode = new RadioGroup(editbox) {
                public void changed(final int btn, final String lbl) {
                    Utils.setpref("editmode", lbl.toLowerCase());
                }
            };
            editmode.add("Emacs", new Coord(10, 25));
            editmode.add("PC", new Coord(10, 50));
            if (Utils.getpref("editmode", "pc").equals("emacs")) editmode.check("Emacs");
            else editmode.check("PC");
        }

        { /* CAMERA TAB */
            curcam = Utils.getpref("defcam", "border");
            tab = body.new Tab(new Coord(70, 0), 60, "Camera");

            new Label(new Coord(10, 40), tab, "Camera type:");
            final RichTextBox caminfo = new RichTextBox(new Coord(180, 70), new Coord(210, 180), tab, "", foundry);
            caminfo.bg = new java.awt.Color(0, 0, 0, 64);
            final String dragcam = "\n\n$col[225,200,100,255]{You can drag and recenter with the middle mouse button.}";
            final String fscam = "\n\n$col[225,200,100,255]{Should be used in full-screen mode.}";
            addinfo("orig", "The Original", "The camera centers where you left-click.", null);
            addinfo("predict", "The Predictor", "The camera tries to predict where your character is heading - à la Super Mario World - and moves ahead of your character. Works unlike a charm." + dragcam, null);
            addinfo("border", "Freestyle", "You can move around freely within the larger area of the window; the camera only moves along to ensure the character does not reach the edge of the window. Boom chakalak!" + dragcam, null);
            addinfo("fixed", "The Fixator", "The camera is fixed, relative to your character." + dragcam, null);
            addinfo("kingsquest", "King's Quest", "The camera is static until your character comes close enough to the edge of the screen, at which point the camera snaps around the edge.", null);
            addinfo("cake", "Pan-O-Rama", "The camera centers at the point between your character and the mouse cursor. It's pantastic!", null);

            final Tabs cambox = new Tabs(new Coord(100, 60), new Coord(300, 200), tab);
            Tabs.Tab ctab;
            /* clicktgt arg */
            ctab = cambox.new Tab();
            new Label(new Coord(45, 10), ctab, "Fast");
            new Label(new Coord(45, 180), ctab, "Slow");
            new Scrollbar(new Coord(60, 20), 160, ctab, 0, 20) {
                {
                    val = Integer.parseInt(Utils.getpref("clicktgtarg1", "10"));
                    setcamargs("clicktgt", calcarg());
                }

                public boolean mouseup(final Coord c, final int button) {
                    if (super.mouseup(c, button)) {
                        setcamargs(curcam, calcarg());
                        setcamera(curcam);
                        Utils.setpref("clicktgtarg1", String.valueOf(val));
                        return (true);
                    }
                    return (false);
                }

                private String calcarg() {
                    return (String.valueOf(Math.cbrt(Math.cbrt(val / 24.0))));
                }
            };
            addinfo("clicktgt", "The Target Seeker", "The camera recenters smoothly where you left-click." + dragcam, ctab);
            /* fixedcake arg */
            ctab = cambox.new Tab();
            new Label(new Coord(45, 10), ctab, "Fast");
            new Label(new Coord(45, 180), ctab, "Slow");
            new Scrollbar(new Coord(60, 20), 160, ctab, 0, 20) {
                {
                    val = Integer.parseInt(Utils.getpref("fixedcakearg1", "10"));
                    setcamargs("fixedcake", calcarg());
                }

                public boolean mouseup(final Coord c, final int button) {
                    if (super.mouseup(c, button)) {
                        setcamargs(curcam, calcarg());
                        setcamera(curcam);
                        Utils.setpref("fixedcakearg1", String.valueOf(val));
                        return (true);
                    }
                    return (false);
                }

                private String calcarg() {
                    return (String.valueOf(Math.pow(1 - (val / 20.0), 2)));
                }
            };
            addinfo("fixedcake", "The Borderizer", "The camera is fixed, relative to your character unless you touch one of the screen's edges with the mouse, in which case the camera peeks in that direction." + dragcam + fscam, ctab);

            final RadioGroup cameras = new RadioGroup(tab) {
                public void changed(final int btn, String lbl) {
                    if (camname2type.containsKey(lbl))
                        lbl = camname2type.get(lbl);
                    if (!lbl.equals(curcam)) {
                        if (camargs.containsKey(lbl))
                            setcamargs(lbl, camargs.get(lbl));
                        setcamera(lbl);
                    }
                    final CamInfo inf = caminfomap.get(lbl);
                    if (inf == null) {
                        cambox.showtab(null);
                        caminfo.settext("");
                    } else {
                        cambox.showtab(inf.args);
                        caminfo.settext(String.format("$size[12]{%s}\n\n$col[200,175,150,255]{%s}", inf.name, inf.desc));
                    }
                }
            };
            final List<String> clist = new ArrayList<String>();
            for (final String camtype : MapView.camtypes.keySet())
                clist.add(caminfomap.containsKey(camtype) ? caminfomap.get(camtype).name : camtype);
            Collections.sort(clist, camcomp);
            int y = 25;
            for (final String camname : clist)
                cameras.add(camname, new Coord(10, y += 25));
            cameras.check(caminfomap.containsKey(curcam) ? caminfomap.get(curcam).name : curcam);
            (new CheckBox(new Coord(50, 270), tab, "Allow zooming with mouse wheel") {
                public void changed(final boolean val) {
                    CustomConfig.setZoom(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isZoom();
            (new CheckBox(new Coord(50, 300), tab, "Disable camera borders") {
                public void changed(final boolean val) {
                    CustomConfig.setNoborders(val);
                    CustomConfig.save();
                }
            }).a = CustomConfig.isNoborders();
        }
        { /* AUDIO TAB */
            tab = body.new Tab(new Coord(140, 0), 60, "Audio");

            new Label(new Coord(35, 40), tab, "Volume");
            new Label(new Coord(10, 280), tab, "SFX");
            new Frame(new Coord(15, 65), new Coord(20, 206), tab);
            final Label sfxvol = new Label(new Coord(40, 69 + (int) ((100 - CustomConfig.getSfxVol()) * 1.86)), tab, String.valueOf(CustomConfig.getSfxVol()) + " %");
            new Scrollbar(new Coord(30, 70), 196, tab, 0, 100) {
                {
                    val = 100 - CustomConfig.getSfxVol();
                }

                public void changed() {
                    CustomConfig.setSfxVol(100 - val);
                    sfxvol.c.setY(69 + (int) ((val) * 1.86));
                    sfxvol.settext(String.valueOf(100 - val) + " %");
                }

                public boolean mousewheel(final Coord c, final int amount) {
                    val = Utils.clip(val + amount, min, max);
                    changed();
                    return (true);
                }
            };
            new Label(new Coord(70, 280), tab, "Music");
            final Label musVol = new Label(new Coord(95, 69 + (int) ((100 - CustomConfig.getMusicVol()) * 1.86)), tab, String.valueOf(CustomConfig.getMusicVol()) + " %");
            new Frame(new Coord(75, 65), new Coord(20, 206), tab);
            new Scrollbar(new Coord(90, 70), 196, tab, 0, 100) {
                {
                    val = 100 - CustomConfig.getMusicVol();
                }

                public void changed() {
                    CustomConfig.setMusicVol(100 - val);
                    musVol.c.setY(69 + (int) ((val) * 1.86));
                    musVol.settext(String.valueOf(100 - val) + " %");
                }

                public boolean mousewheel(final Coord c, final int amount) {
                    val = Utils.clip(val + amount, min, max);
                    changed();
                    return true;
                }
            };
            new CheckBox(new Coord(120, 40), tab, "Sound enabled") {
                public void changed(final boolean val) {
                    CustomConfig.setSoundOn(val);
                }

                {
                    a = CustomConfig.isSoundOn();
                }
            };
            new CheckBox(new Coord(120, 70), tab, "Music enabled") {
                public void changed(final boolean val) {
                    CustomConfig.setMusicOn(val);
                }

                {
                    a = CustomConfig.isMusicOn();
                }
            };
        }

        { /*IRC TAB */
            tab = body.new Tab(new Coord(210, 0), 60, "IRC");

            // Labels (1st column)
            final int firstCellXOffset = 10;
            serverLabel = new Label(new Coord(firstCellXOffset, 40), tab, "Server:");
            chnlLabel = new Label(new Coord(firstCellXOffset, 60), tab, "Channels:");
            defIRCNickLabel = new Label(new Coord(firstCellXOffset, 80), tab, "IRC Nick:");
            altIRCNickLabel = new Label(new Coord(firstCellXOffset, 100), tab, "Alt Nick:");

            // TextEntries (2nd column)
            final int secondCellXOffset = 15 + Math.max(serverLabel.sz.x, Math.max(chnlLabel.sz.x, Math.max(defIRCNickLabel.sz.x, altIRCNickLabel.sz.x)));
            final Coord textFieldSize = new Coord(180, 15);

            // Server entry
            serverAddress = new TextEntry(new Coord(secondCellXOffset, 40), textFieldSize, tab, CustomConfig.getIrcServerAddress());
            serverAddress.badchars = " ";

            // Channel list entry
            final StringBuilder builder = new StringBuilder();
            for (final Listbox.Option channel : CustomConfig.getIrcChannelList()) {
                final String name = channel.name.trim();
                final String disp = channel.disp.trim();
                if (!name.isEmpty()) {
                    builder.append(name).append(' ');
                }
                if (!disp.isEmpty()) {
                    builder.append(disp).append(' ');
                }
            }
            channelList = new TextEntry(new Coord(secondCellXOffset, 60), textFieldSize,
                    tab, builder.toString().trim());

            final String someBadChars = "~@#$%^& ";

            // Nickname entries
            defNick = new TextEntry(new Coord(secondCellXOffset, 80), textFieldSize,
                    tab, CustomConfig.getIrcDefNick()) {
                {
                    badchars = someBadChars;
                }
            };

            altNick = new TextEntry(new Coord(secondCellXOffset, 100), textFieldSize,
                    tab, CustomConfig.getIrcAltNick()) {
                {
                    badchars = someBadChars;
                }
            };

            // IRC toggle
            ircToggle = new CheckBox(new Coord((firstCellXOffset + secondCellXOffset) / 2, 130), tab, "IRC On/Off") {
                public void changed(final boolean val) {
                    CustomConfig.setIRCOn(val);
                }

                {
                    a = CustomConfig.isIRCOn();
                }
            };

        }

        { /* HIDE OBJECTS TAB */
            tab = body.new Tab(new Coord(280, 0), 80, "Hide Objects");

            int y = 0;
            for (final String[] checkbox : CHECKBOXES_LIST) {
                //noinspection ObjectAllocationInLoop
                new CheckBox(new Coord(10, y += 30), tab, checkbox[0]) {

                    public void changed(final boolean val) {
                        if (val) {
                            CustomConfig.getHidingObjects().add(checkbox[1]);
                        } else {
                            CustomConfig.getHidingObjects().remove(checkbox[1]);
                        }
                        CustomConfig.save();
                    }

                    {
                        a = CustomConfig.getHidingObjects().contains(checkbox[1]);
                    }
                };
            }
            y = 0;
            new CheckBox(new Coord(150, y += 30), tab, "Hiding enabled") {
                public void changed(final boolean val) {
                    CustomConfig.setHideObjects(val);
                    CustomConfig.save();
                }

                {
                    a = CustomConfig.isHideObjects();
                }
            };
            new CheckBox(new Coord(150, y += 30), tab, "XRay enabled") {
                public void changed(final boolean val) {
                    CustomConfig.setXray(val);
                    CustomConfig.save();
                }

                {
                    a = CustomConfig.isXray();
                }
            };
            new CheckBox(new Coord(150, y += 30), tab, "NightVision enabled") {
                public void changed(final boolean val) {
                    CustomConfig.setHasNightVision(val);
                }

                {
                    a = CustomConfig.isNightVision();
                }
            };
        }

        { /* TRANSLATE OPTIONS TAB */
            tab = body.new Tab(new Coord(300, 0), 80, "Translation");
            (new CheckBox(new Coord(10, 30), tab, "Turn on") {
                public void changed(final boolean val) {
                    Config.translator.turn(val);
                }
            }).a = Config.translator.isWorking();

            new Label(new Coord(150, 35), tab, "Target Language:");

            final RadioGroup langs = new RadioGroup(tab) {
                public void changed(final int btn, final String lbl) {
                    Config.translator.useLanguage(lbl);
                }
            };
            langs.add("en", new Coord(150, 45));
            langs.add("ru", new Coord(150, 70));
            langs.check(Config.translator.getLanguage());

            new Label(new Coord(25, 125), tab, "Google API Key:");
            final TextEntry te = new TextEntry(new Coord(25, 150), new Coord(300, 20), tab, Config.translator.getKey());
            new Button(new Coord(330, 150), 50, tab, "set") {
                public void click() {
                    Config.translator.useKey(te.text);
                    CustomConfig.setGoogleTranslateApiKey(te.text);
                }
            };

            new Label(new Coord(100, 190), tab, "Powered by Google Translate");
        }

        new Frame(new Coord(-10, 20), new Coord(420, 330), this);
        final String last = Utils.getpref("optwndtab", "");
        for (final Tabs.Tab t : body.tabs) {
            if (t.btn.getText().equals(last))
                body.showtab(t);
        }
    }

    void saveSome() {
        Listbox.Option channel = null;
        CustomConfig.setIrcServerAddress(serverAddress.text);
        CustomConfig.setIrcDefNick(defNick.text);
        CustomConfig.setIrcAltNick(altNick.text);
        final String[] channelData = Utils.whitespacePattern.split(channelList.text);
        List<Listbox.Option> ircChannelList = CustomConfig.getIrcChannelList();
        ircChannelList.clear();
        for (int i = 0; i < channelData.length; i++) {
            channelData[i] = channelData[i].trim();
            if (channelData[i].length() > 0) {
                if (channelData[i].startsWith("#")) {
                    if (channel != null) {
                        ircChannelList.add(channel);
//noinspection UnusedAssignment
                        channel = null;
                    }
                    channel = new Listbox.Option(channelData[i], "");
                    continue;

                } else {
                    if (channel != null)
                        channel.disp = (channel.disp + ' ' + channelData[i]).trim();
                }
                if (channel != null) {
                    ircChannelList.add(channel);
                    channel = null;
                }
            }
        }
        if (channel != null) {
            ircChannelList.add(channel);
//noinspection UnusedAssignment
            channel = null;
        }
//        if (CustomConfig.isSaveable()) {
            CustomConfig.save();
//        }
    }

    private void setcamera(final String camtype) {
        curcam = camtype;
        Utils.setpref("defcam", curcam);
        String[] args = camargs.get(curcam);
        if (args == null) args = new String[0];

        final MapView mv = ui.mainview;
        if (mv != null) {
            if (curcam.equals("clicktgt")) mv.cam = new MapView.OrigCam2(args);
            else if (curcam.equals("fixedcake")) mv.cam = new MapView.FixedCakeCam(args);
            else {
                try {
                    mv.cam = MapView.camtypes.get(curcam).newInstance();
                } catch (InstantiationException ignored) {
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }

    private void setcamargs(final String camtype, final String... args) {
        camargs.put(camtype, args);
        if (args.length > 0 && curcam.equals(camtype))
            Utils.setprefb("camargs", Utils.serialize(args));
    }

    private static int getsfxvol() {
        return ((int) (100 - Double.parseDouble(Utils.getpref("sfxvol", "1.0")) * 100));
    }

    private void addinfo(final String camtype, final String title, final String text, final Tabs.Tab args) {
        caminfomap.put(camtype, new CamInfo(title, text, args));
        camname2type.put(title, camtype);
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        if (checkIsCloseButton(sender) || (sender == foldButton))
            super.wdgmsg(sender, msg, args);
    }

    public static class Frame extends Widget {
        private final IBox box;

        public Frame(final Coord c, final Coord sz, final Widget parent) {
            super(c, sz, parent);
            box = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
        }

        public void draw(final GOut og) {
            super.draw(og);
            final GOut g = og.reclip(Coord.z, sz);
            g.chcolor(150, 200, 125, 255);
            box.draw(g, Coord.z, sz);
        }
    }

    @Override
    public void destroy() {
        UI.optionsWindow.compareAndSet(this, null);
        super.destroy();
    }
}
