package wikilib.dom;

import org.jetbrains.annotations.NotNull;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 17.10.11
 * Time: 0:52
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class Link extends Text {
    private final String link;

    public Link(@NotNull final String href, @NotNull final String text) {
        super(text);

        this.link = href;
    }

    @Override
    public String toString() {
        return "$b{$u{$col[0,0,192]{$a[" + link + "]{" + getText() + "}}}}";
    }
}
