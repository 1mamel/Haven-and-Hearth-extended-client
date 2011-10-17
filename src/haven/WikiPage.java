package haven;

import haven.RichText.Foundry;
import org.jetbrains.annotations.NotNull;
import wikilib.Request;
import wikilib.RequestCallback;
import wikilib.WikiLib;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class WikiPage extends HWindow {
    private static final Foundry fnd = new Foundry(TextAttribute.FOREGROUND, Color.BLACK, TextAttribute.SIZE, 12);
    private static final Color busycolor = new Color(255, 255, 255, 128);
    private static final Pattern WIKIREPLACER = Pattern.compile("/wiki/");
    private static final AtomicReference<WikiLib> fetcher = new AtomicReference<WikiLib>();

    private final RichTextBox content;
    private final RequestCallback callback;
    private final AtomicBoolean busy = new AtomicBoolean(false);


    public WikiPage(final Widget parent, final String request) {
        super(parent, request, true);
        content = new RichTextBox(Coord.z, sz, this, "", fnd);
        content.bg = new Color(255, 255, 255, 128);
        content.registerclicks = true;

        final HWindow wnd = this;
        callback = new RequestCallback() {
            public void run(final Request req) {
                synchronized (content) {
                    content.settext(req.getResult());
                    if (req.getTitle() != null) {
                        title = req.getTitle();
                        ui.wiki.updurgency(wnd, 0);
                    }
                    busy.set(false);
                }
            }
        };

        open(request);
        if (cbtn != null) {
            cbtn.raise();
        }
    }

    public void setsz(final Coord s) {
        super.setsz(s);
        content.setsz(sz);
    }

    public void draw(final GOut g) {
        super.draw(g);
        if (busy.get()) {
            g.chcolor(busycolor);
            g.frect(Coord.z, sz);
            g.chcolor();
        }
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        if (busy.get()) {
            return;
        }
        if (sender == content) {
            final String request = (String) args[0];
            if ((Integer) args[1] == 1) {
                open(request);
            } else {
                new WikiPage(parent, request);
            }
        } else if (sender == cbtn) {
            ui.destroy(this);
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    private void open(String request) {
        final Request req = new Request(request, callback);
        if (request.contains("/wiki/")) {
            request = WIKIREPLACER.matcher(request).replaceAll("");
            req.initPage(request);
        }
        busy.set(true);
        search(req);
    }

    private static void search(@NotNull final Request request) {
        if (fetcher.get() == null) {
            fetcher.compareAndSet(null, new WikiLib());
        }
        fetcher.get().search(request);
    }

}
