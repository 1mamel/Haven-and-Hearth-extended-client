/**
 * @(#)GameOptions.java
 *
 *
 * @author
 * @version 1.00 2009/10/23
 */
package haven;

import java.util.regex.Pattern;

@SuppressWarnings({"StringContatenationInLoop"})
public class GameOptions extends Window {

    private static final Pattern whitespaceSplitter = Pattern.compile(" ");

    static {
        Widget.addtype("gopts", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                if (args.length < 2)
                    return (new Window(c, (Coord) args[0], parent, null));
                else
                    return (new Window(c, (Coord) args[0], parent, (String) args[1]));
            }
        });
    }

    final Label sfxVol;
    final Label musicVol;
    final Label serverLabel;
    final Label chnlLabel;
    final Label defIRCNickLabel;
    final Label altIRCNickLabel;
    final TextEntry serverAddress;
    final TextEntry channelList;
    final TextEntry defNick;
    final TextEntry altNick;
    final FillBox sfxVolBar;
    final FillBox musicVolBar;
    final CheckBox musicToggle;
    final CheckBox soundToggle;
    final CheckBox ircToggle;
    //    Listbox channelListbox;
    Button okBtn;
    Button cancelBtn;

    @SuppressWarnings({"NonConstantStringShouldBeStringBuffer"})
    public GameOptions(Widget parent) {
        super(CustomConfig.windowSize.div(2).add(-200, -200), Coord.z.add(200, 200), parent, "Game Options", true);

        //	SFX volume
        sfxVol = new Label(new Coord(0, 0), this, "SFX Vol:");
        sfxVolBar = new FillBox(Coord.z.add(sfxVol.sz.x + 5, 0), Coord.z.add(120, 20), CustomConfig.sfxVol, this);

        //	Music volume bar
        musicVol = new Label(new Coord(0, 30), this, "Music Vol:");
        musicVolBar = new FillBox(Coord.z.add(sfxVol.sz.x + 5, 30), Coord.z.add(120, 20), CustomConfig.musicVol, this);

        //	Server entry
        serverLabel = new Label(new Coord(0, 60), this, "Server:");
        serverAddress = new TextEntry(Coord.z.add(sfxVol.sz.x + 5, 60), Coord.z.add(120, 15),
                this, CustomConfig.ircServerAddress);
        serverAddress.badchars = " ";

        //	Channel list entry
        chnlLabel = new Label(new Coord(0, 80), this, "Channels:");
        StringBuilder builder = new StringBuilder();
        for (Listbox.Option channel : CustomConfig.ircChannelList) {
            String name = channel.name.trim();
            String disp = channel.disp.trim();
            if (!name.isEmpty()) {
                builder.append(name).append(' ');
            }
            if (!disp.isEmpty()) {
                builder.append(disp).append(' ');
            }
        }
        channelList = new TextEntry(Coord.z.add(sfxVol.sz.x + 5, 80), Coord.z.add(120, 15),
                this, builder.toString().trim());

        //	Nickname entries
        defIRCNickLabel = new Label(new Coord(0, 100), this, "IRC Nick:");
        defNick = new TextEntry(Coord.z.add(sfxVol.sz.x + 5, 100), Coord.z.add(120, 15),
                this, CustomConfig.ircDefNick);
        defNick.badchars = "~@#$%^& ";

        altIRCNickLabel = new Label(new Coord(0, 120), this, "Alt Nick:");
        altNick = new TextEntry(Coord.z.add(sfxVol.sz.x + 5, 120), Coord.z.add(120, 15),
                this, CustomConfig.ircAltNick);
        altNick.badchars = defNick.badchars;

        //	Sound toggle
        soundToggle = new CheckBox(Coord.z.add(0, 140), this, "Sound On/Off");
        soundToggle.a = CustomConfig.isSoundOn;

        //	Music toggle
        musicToggle = new CheckBox(Coord.z.add(soundToggle.sz.x, 140), this, "Music On/Off");
        musicToggle.a = CustomConfig.isMusicOn;

        //	IRC toggle
        ircToggle = new CheckBox(Coord.z.add(0, 160), this, "IRC On/Off");
        ircToggle.a = CustomConfig.isIRCOn;

        //	Ok button
        //	okBtn = new Button(new Coord(50, 190), 50, this, "Ok");

        //	Cancel button
        //	cancelBtn = new Button(okBtn.c.add(okBtn.sz.x+10,0), okBtn.sz.x, this, "Cancel");

        ui.bind(sfxVolBar, CustomConfig.wdgtID++);
        ui.bind(musicVolBar, CustomConfig.wdgtID++);
        ui.bind(musicToggle, CustomConfig.wdgtID++);
        ui.bind(soundToggle, CustomConfig.wdgtID++);
        ui.bind(okBtn, CustomConfig.wdgtID++);
        ui.bind(cancelBtn, CustomConfig.wdgtID++);
        pack();
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == cbtn) {
            toggle();
            return;
        } else if (sender == sfxVolBar && msg.equals("change")) {
            CustomConfig.sfxVol = args[0] != null ? (Integer) args[0] : CustomConfig.sfxVol;
            return;
        } else if (sender == musicVolBar && msg.equals("change")) {
            CustomConfig.musicVol = args[0] != null ? (Integer) args[0] : CustomConfig.musicVol;
            return;
        } else if (sender == musicToggle && msg.equals("ch")) {
            CustomConfig.isMusicOn = args[0] != null ? (Boolean) args[0] : CustomConfig.isMusicOn;
            return;
        } else if (sender == soundToggle && msg.equals("ch")) {
            CustomConfig.isSoundOn = args[0] != null ? (Boolean) args[0] : CustomConfig.isSoundOn;
            return;
        } else if (sender == ircToggle && msg.equals("ch")) {
            CustomConfig.isIRCOn = args[0] != null ? (Boolean) args[0] : CustomConfig.isIRCOn;
            return;
        } else if (sender == okBtn && msg.equals("activate")) {
            return;
        } else if (sender == cancelBtn && msg.equals("activate")) {
            return;
        }
        super.wdgmsg(sender, msg, args);
    }

    @SuppressWarnings({"NonConstantStringShouldBeStringBuffer"})
    public boolean toggle() {
        Listbox.Option channel = null;
        CustomConfig.ircServerAddress = serverAddress.text;
        CustomConfig.ircDefNick = defNick.text;
        CustomConfig.ircAltNick = altNick.text;
        if (this.visible) {
            String channelData[] = whitespaceSplitter.split(channelList.text);
            CustomConfig.ircChannelList.clear();
            for (int i = 0; i < channelData.length; i++) {
                channelData[i] = channelData[i].trim();
                if (channelData[i].length() > 0) {
                    if (channelData[i].startsWith("#")) {
                        if (channel != null) {
                            CustomConfig.ircChannelList.add(channel);
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
                        CustomConfig.ircChannelList.add(channel);
                        channel = null;
                    }
                }
            }
            if (channel != null) {
                CustomConfig.ircChannelList.add(channel);
                //noinspection UnusedAssignment
                channel = null;
            }
        } else {
            StringBuilder builder = new StringBuilder();
            for (Listbox.Option chan : CustomConfig.ircChannelList) {
                String name = chan.name.trim();
                String disp = chan.disp.trim();
                if (!name.isEmpty()) {
                    builder.append(name).append(' ');
                }
                if (!disp.isEmpty()) {
                    builder.append(disp).append(' ');
                }
            }
            channelList.settext(builder.toString().trim());
        }
        if (CustomConfig.isSaveable) CustomConfigProcessor.saveSettings();
        return super.toggle();
    }
}
