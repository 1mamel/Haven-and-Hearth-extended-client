package wikilib.dom;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 17.10.11
 * Time: 0:25
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public abstract class BranchingNode implements Node {
    @NotNull
    final List<Node> childs;

    @NotNull
    public List<Node> getChilds() {
        return childs;
    }

    protected BranchingNode(@NotNull final List<Node> childs) {
        this.childs = childs;
    }

    protected BranchingNode() {
        this.childs = new LinkedList<Node>();
    }

    public BranchingNode add(@NotNull final Node node) {
        childs.add(node);
        return this;
    }

    public BranchingNode remove(@NotNull final Node node) {
        childs.remove(node);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Node child : childs) {
            sb.append(child.toString());
        }
        return sb.toString();
    }
}
