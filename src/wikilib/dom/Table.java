package wikilib.dom;

import org.jetbrains.annotations.NotNull;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 17.10.11
 * Time: 12:51
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class Table extends BranchingNode<Table.Line> {
    public Table() {
        super();
    }

    public Table addLine(final Line line) {
        childs.add(line);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Table.Line child : childs) {
            sb.append(child).append('\n');
        }
        return sb.toString();
    }

    public static class Line extends BranchingNode<Cell> {
        public Line addCell(final Cell cell) {
            add(cell);
            return this;
        }

        public int getCurrentWidth() {
            return childs.size();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append('|');
            for (final Cell child : childs) {
                sb.append(' ').append(child).append(" |");
            }
            return sb.toString();
        }
    }

    public static class Cell extends Text {

        private boolean th;

        public Cell(@NotNull final String text, final boolean isTh) {
            super(text);
            th = isTh;
        }

        @Override
        public String toString() {
            if (th) {
                return "$b{" + super.toString() + "}";
            } else {
                return super.toString();
            }
        }
    }
}
