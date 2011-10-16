package wikilib.dom;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 17.10.11
 * Time: 0:24
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class Text implements Node {
    public static final Node BR;
    public static final Node SPACE;

    private static Pattern removeSpaces;
    private static Pattern clean;

    static {
        removeSpaces = Pattern.compile(" ( )*");
        clean = Pattern.compile("[\\{\\}]*");
        SPACE = new Text(" ");
        BR = new Text("\n");
    }


    @NotNull
    private final String text;

    public Text(@NotNull final String text) {
        this.text = cleanText(text);
    }

    private String cleanText(String text) {
        String s = removeSpaces.matcher(text).replaceAll(" ");
        s = clean.matcher(s).replaceAll("");
        //s.replaceAll("<br/>","\n");
        //s.replaceAll("<p/>","\n");
        return s;
    }


    @NotNull
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return getText();
    }
}
