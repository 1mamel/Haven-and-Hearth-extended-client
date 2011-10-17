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
public abstract class BranchingNode<E extends Node> implements Node {
    @NotNull
    final List<E> childs;

    @NotNull
    public List<E> getChilds() {
        return childs;
    }

    protected BranchingNode() {
        this.childs = new LinkedList<E>();
    }

    public BranchingNode<E> add(@NotNull final E node) {
        childs.add(node);
        return this;
    }

    public BranchingNode<E> remove(@NotNull final E node) {
        childs.remove(node);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final E child : childs) {
            sb.append(child.toString());
        }
        return sb.toString();
    }
}
