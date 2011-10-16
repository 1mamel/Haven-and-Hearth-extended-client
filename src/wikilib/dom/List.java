package wikilib.dom;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 17.10.11
 * Time: 2:49
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class List extends BranchingNode {
    private boolean ordered;

    public List(boolean ordered) {
        this.ordered = ordered;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        int i = 1;
        for (final Node child : childs) {
            sb.append("   ");
            if (ordered) {
                sb.append(i);
            } else {
                sb.append(" * ");
            }
            sb.append(child.toString());
            sb.append('\n');
            ++i;
        }
        return sb.toString();
    }
}
