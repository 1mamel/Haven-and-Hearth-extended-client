package haven;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GLLine {
    public final int y;
    public int width;
    private static final Pattern pat = Pattern.compile("((http://)(\\S+))|((www+\\.)(\\S+))|(\\S+\\.(co|net|com|org|edu|ru)(\\S+)?+)", Pattern.CASE_INSENSITIVE);
    @SuppressWarnings({"FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
    private final List<GLCharacter> characters = new LinkedList<GLCharacter>();
    public final Text.Foundry fnd = new Text.Foundry(new Font("SansSerif", Font.PLAIN, 9), Color.BLACK);
    public final BufferedImage img;

    public GLLine(String line, Color col, int y) {
        this.y = y;
        GLCharacter tGLChar;
        String[] words = line.trim().split(" ");
        int nextCharLoc = 3;
        Coord strSize = fnd.strsize(line);
        fnd.defcol = col;
        img = new BufferedImage(strSize.x, strSize.y, BufferedImage.TYPE_INT_ARGB);
        Matcher match;
        Graphics g = img.getGraphics();
        for (int i = 0; i < words.length; i++) {
            match = pat.matcher(words[i]);
            boolean isLink = match.find();

            for (int j = 0; j < words[i].length(); j++) {
                tGLChar = new GLCharacter(words[i].charAt(j), nextCharLoc, fnd, false);
                if (isLink) tGLChar.setLink(words[i]);
                synchronized (characters) {
                    characters.add(tGLChar);
                    g.drawImage(tGLChar.img(), tGLChar.x, 0, null);
                }
                nextCharLoc += tGLChar.size().x;
                width += nextCharLoc;
            }
            if (i != words.length) {
                tGLChar = new GLCharacter(' ', nextCharLoc, fnd, false);
                synchronized (characters) {
                    characters.add(tGLChar);
                    g.drawImage(tGLChar.img(), tGLChar.x, 0, null);
                }
                nextCharLoc += tGLChar.size().x;
                width += nextCharLoc;
            }
        }
    }
}