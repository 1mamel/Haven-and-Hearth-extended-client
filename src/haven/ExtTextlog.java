package haven;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class ExtTextlog extends Widget implements ClipboardOwner {

    static final int MAX_LINES = 250;
    static final Text.Foundry fnd = new Text.Foundry(new Font("SansSerif", Font.PLAIN, 9), Color.BLACK);
    @SuppressWarnings({"UnusedDeclaration"})
    static Color transparent = new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), Color.TRANSLUCENT);
    static final Color alphaBlue = new Color(Color.BLUE.darker().getRed(), Color.BLUE.darker().getGreen(), Color.BLUE.darker().getBlue(), 100);
    static final Tex texpap = Resource.loadtex("gfx/hud/texpap");
    private final List<GLLine> lines;
    private int nextLineLoc = -11;
    private final int lineHeight = 11;
    private final BufferedImage visibleCharacters = new BufferedImage(sz.x(), sz.y(), BufferedImage.TYPE_INT_ARGB);
    private BufferedImage drawnCharacters = new BufferedImage(sz.x(), lineHeight * MAX_LINES, BufferedImage.TYPE_INT_ARGB);
    private final Scrollbar scrollBar;
    private boolean scrollLocked;
    private boolean dragging = false;
    @SuppressWarnings({"UnusedDeclaration"})
    private boolean ctrlPressed = false;
    private final Rectangle selectedArea = new Rectangle(0, 0, 0, 0);
    private String selectedText = "";


    static {
        Widget.addtype("extlog", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                return (new ExtTextlog(c, (Coord) args[0], parent));
            }
        });
    }

    public void draw(GOut g) {
        Coord dc = new Coord();
        for (dc.setY(0); dc.y() < sz.y(); dc.setY(dc.y() + texpap.sz().y())) {
            for (dc.setX(0); dc.x() < sz.x(); dc.setX(dc.x() + texpap.sz().x())) {
                g.image(texpap, dc);
            }
        }
        if (scrollBar != null)
            g.image(visibleCharacters, Coord.z);
        g.chcolor(alphaBlue);
        if (selectedArea != null)
            synchronized (selectedArea) {
                g.frect(new Coord(selectedArea.x, selectedArea.y), new Coord(selectedArea.width, selectedArea.height));
            }
        g.chcolor();
        super.draw(g);
    }

    public ExtTextlog(Coord c, Coord sz, Widget parent) {
        super(c, sz, parent);
        lines = new LinkedList<GLLine>();
        setcanfocus(true);
        scrollBar = new Scrollbar(Coord.z.add(sz.x(), 5), sz.y() - 10, this, 0, 0);
    }

    @SuppressWarnings({"UnusedAssignment"})
    public synchronized void append(String text, Color col) {
        if (text == null || text.length() == 0) text = ".";

        //	Splits at obvious line breaks
        if (text.contains("\n")) {
            String[] lines = Utils.eoLinePattern.split(text.trim());
            for (String line : lines) {
                append(line, col);
            }
            return;
        }

        GLLine tGLLine;
        String[] words = Utils.whitespacePattern.split(text.trim());

        Graphics g = drawnCharacters.getGraphics();

        //	Max amount of lines reached, trimming the top
        if (drawnCharacters.getHeight() < nextLineLoc + lineHeight) {
            BufferedImage tDrawnCharacters = drawnCharacters;
            drawnCharacters = new BufferedImage(drawnCharacters.getWidth(),
                    drawnCharacters.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            g = drawnCharacters.getGraphics();
            tDrawnCharacters = tDrawnCharacters.getSubimage(0, lineHeight, tDrawnCharacters.getWidth(), tDrawnCharacters.getHeight() - lineHeight);
            if (!lines.isEmpty()) lines.remove(0);
            g.drawImage(tDrawnCharacters, 0, 0, null);
            nextLineLoc -= lineHeight;
            scrollLocked = true;
        }

        //	Set foundry color
        fnd.defcol = col;

        //	Handle the scrollbar
        if (nextLineLoc + lineHeight > sz.y() && !scrollLocked) {
            scrollBar.max++;
        }
        if (scrollBar.val + 1 == scrollBar.max) {
            scrollBar.val = scrollBar.max;
        }

        //	Splits lines at the width of the widget
        StringBuilder line = new StringBuilder();
        boolean hasExtraLines = false;
        for (int i = 0, lineWidth = 0, wordchars = 0;
             i < words.length;
             i++, lineWidth += fnd.strsize(line.toString().trim()).x()) {
            if (lineWidth >= sz.x() - 5) {
                text = text.substring(wordchars - 1);
                lineWidth = 0;
                wordchars = 0;
                hasExtraLines = true;
                break;
            }
            line.append(words[i]).append(' ');
            wordchars += words[i].length();
        }
        tGLLine = new GLLine(line.toString(), col, nextLineLoc);
        nextLineLoc += lineHeight;
        System.out.println(tGLLine.width);
        g.drawImage(tGLLine.img, 0, nextLineLoc, null);
        updateDrawData();
        if (hasExtraLines)
            append(text, col);
    }

    public synchronized void append(String line) {
        append(line, Color.BLACK);
    }

    public void uimsg(String msg, Object... args) {
        if (msg.equals("apnd")) {
            append((String) args[0]);
        }
    }

    public void updateDrawData() {
        BufferedImage background = ((TexI) texpap).back;
        Graphics g = visibleCharacters.createGraphics();
        g.clearRect(0, 0, sz.x(), sz.y());
        g.drawImage(background, 0, 0, sz.x(), sz.y(), null);
        if (scrollBar != null)
            g.drawImage(drawnCharacters, 0, 0 - (lineHeight * (scrollBar.val)), null);
    }

    public boolean mousewheel(Coord c, int amount) {
        if (scrollBar.mousewheel(c, amount)) {
            updateDrawData();
            return true;
        }
        return super.mousewheel(c, amount);
    }

    public boolean mousedown(Coord c, int button) {
        ui.grabmouse(this);
        if (c.x() >= sz.x() - 5 && c.x() <= sz.x() && scrollBar.mousedown(c, button)) {
            updateDrawData();
            return true;
        }
        if (button == 1 && c.x() >= 0 && c.x() < sz.x() - 5 && c.y() >= 0 && c.y() < sz.y()) {
            parent.setfocus(this);
            dragging = true;
            synchronized (selectedArea) {
                selectedArea.setBounds(c.x(), c.y(), 0, 0);
            }
            return true;
        }
        return (super.mousedown(c, button));
    }

    public void mousemove(Coord c) {
        if (c.x() >= sz.x() - 5 && c.x() <= sz.x()) {
            scrollBar.mousemove(c);
            updateDrawData();
            return;
        }
        if (dragging && c.x() >= 0 && c.x() < sz.x() - 5 && c.y() > 0 && c.y() < sz.y()) {
            synchronized (selectedArea) {
                selectedArea.setSize(c.x() - selectedArea.getLocation().x, c.y() - selectedArea.getLocation().y);
            }
        }
    }

    public boolean mouseup(Coord c, int button) {
        ui.ungrabmouse();
        if (c.x() >= sz.x() - 5 && c.x() <= sz.x() && scrollBar.mouseup(c, button)) {
            updateDrawData();
            return true;
        }
        if (button == 1 && c.x() >= 0 && c.x() < sz.x() - 5 && dragging) {
            dragging = false;
            if (selectedArea.width == 0 && selectedArea.height == 0) {
                return checkLink();
            }
            if (selectedArea.width < 0) {
                selectedArea.x += selectedArea.width;
                selectedArea.width = Math.abs(selectedArea.width);
            }
            if (selectedArea.height < 0) {
                selectedArea.y += selectedArea.height;
                selectedArea.height = Math.abs(selectedArea.height);
            }
            showSelected();
            synchronized (selectedArea) {
                selectedArea.setBounds(0, 0, 0, 0);
            }
            return true;
        }
        return (super.mouseup(c, button));
    }

    public void lostfocus() {
        selectedArea.setBounds(0, 0, 0, 0);
        showSelected();
    }

    public boolean checkLink() {
//        Rectangle charArea;

        selectedArea.width = 2;
        selectedArea.height = 2;
        synchronized (lines) {
            /*  	for(GLLine tGLLine : characters)
               {
                   charArea = new Rectangle(tGLChar.location.x, tGLChar.location.y-(lineHeight*scrollBar.val),
                                        tGLLine.size().x, tGLChar.size().y);
                   if(selectedArea.intersects(charArea) && tGLChar.isLink())
                   {
                       selectedArea.setBounds(0,0,0,0);
                       return tGLChar.activate();
                   }
               }*/
        }
        selectedArea.setBounds(0, 0, 0, 0);
        return false;
    }

    public void showSelected() {
//        Rectangle charArea;
//        Graphics g = drawnCharacters.getGraphics();
        selectedText = "";

        synchronized (lines) {
            /*    	for(GLCharacter tGLChar : characterMap)
               {
                   charArea = new Rectangle(tGLChar.location.x, tGLChar.location.y-(lineHeight*scrollBar.val),
                                        tGLChar.size().x, tGLChar.size().y);
                   if(!selectedArea.intersects(charArea) && tGLChar.isSelected())
                   {
                       tGLChar.setSelected(false);
                       g.drawImage(tGLChar.img(), tGLChar.location.x, tGLChar.location.y, null);
                   }
                   if(selectedArea.intersects(charArea) && !tGLChar.isSelected())
                   {
                       tGLChar.setSelected(true);
                       g.drawImage(tGLChar.img(), tGLChar.location.x, tGLChar.location.y, null);
                       selectedText += tGLChar.letter();
                   }
               }*/
        }
        updateDrawData();
    }

    public boolean type(char ch, KeyEvent ev) {
        ch += 96;
        if ((ch == 'c' || ch == 'C') && ev.isControlDown()) {
            setClipboardContents();
            return true;
        }
        ch -= 96;
        return super.type(ch, ev);
    }

    /**
     * Method lostOwnership
     *
     * @param clipboard
     * @param contents
     */
    @SuppressWarnings({"JavaDoc"})
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // TODO: Add your code here
    }

    public void setClipboardContents() {
        if (selectedText.trim().length() == 0) return;
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(selectedText), this);
    }
}
