package wikilib.dom;

import org.jetbrains.annotations.NotNull;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 17.10.11
 * Time: 0:23
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class Header extends Text {
    private int size;

    public Header(@NotNull final String text, final int i) {
        super(text);
        size = i;
    }

    @Override
    public String toString() {
        return "\n\n" + (size > 0 ? ("$size[" + size + "]{") : "") + "{$b{" + getText() + ":}" + (size > 0 ? "}" : "") + "\n\n";
    }
}
