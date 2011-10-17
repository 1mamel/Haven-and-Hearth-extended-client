package wikilib.dom;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 17.10.11
 * Time: 0:23
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class Page extends BranchingNode<Node> {
    public static final Page FAILED_TO_PARSE = new Page("Failed to parse HTML");
    
    
    private final String title;

    public Page(final String title) {
        super();
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
