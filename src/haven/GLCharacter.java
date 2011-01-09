package haven;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

class GLCharacter {
    private final char letter;
    public final int x;
    private final Coord size;
    private Color color;
    private BufferedImage img;
    private BufferedImage background;
    private Tex tex;
    private final Text.Foundry foundry;
    private boolean isLink = false;
    public URI address;
    private boolean aa;
    private boolean selected = false;

    GLCharacter(char letter, int x, Text.Foundry foundry, boolean aa) {
        this.letter = letter;
        this.x = x;
        this.foundry = foundry;
        color = foundry.defcol;
        size = foundry.charsize(letter);
        init();
    }

    private void init() {
        if (size.x <= 0) size.x = 1;
        img = TexI.mkbuf(size);
        Graphics g = img.createGraphics();
        Graphics bkgndG;
        if (background == null) {
            background = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
        }
        bkgndG = background.createGraphics();
        if (aa) {
            Utils.AA(g);
        }
        g.setFont(foundry.font);
        g.setColor(color);
        if (selected) {
            bkgndG.setColor(Color.BLUE.darker());
            bkgndG.fillRect(0, 0, size.x, size.y);
            g.setColor(Color.WHITE);
        } else {
            bkgndG.drawImage(((TexI) ExtTextlog.texpap).back, 0, 0, size.x, size.y, null);
        }
        g.drawImage(background, 0, 0, size.x, size.y, null);
        g.drawString(String.valueOf(letter), 0, size.y - 2);
    }

    public boolean setLink(String address) {
        if (address == null || address.trim().length() == 0)
            return false;
        try {
            this.address = new URI(address);
            isLink = true;
            color = Color.blue.darker();
            init();
        } catch (URISyntaxException e) {
            return false;
        }
        return isLink;
    }

    public boolean activate() {
        if (isLink) {
            try {
                Desktop.getDesktop().browse(address);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        init();
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isLink() {
        return isLink;
    }

    public Tex tex() {
        if (tex == null) {
            tex = new TexI(img);
        }
        return tex;
    }

    public BufferedImage img() {
        return img;
    }

    public char letter() {
        return letter;
    }

    public Coord size() {
        return size;
    }
}
