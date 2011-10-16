package wikilib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import wikilib.dom.*;
import wikilib.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * Date: 17.10.11
 * Time: 1:22
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class ParsingUtil {
    static void parseDom(final Request request, final InputStream is) throws ParserConfigurationException, IOException, SAXException, PageParsingException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(false);
        dbf.setExpandEntityReferences(false);

        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document dom = db.parse(is);
        final String title = getTitle(dom);


        final Page resultPage = new Page(title == null ? "<No title>" : title);

        if (title == null) {
            resultPage.add(new Header("Strange result", 13));
        } else if (title.contains("Search result")) {
            parseSearchResultsPage(resultPage, dom);
        } else {
            parseWikiPage(resultPage, dom);
        }
        request.setResult(resultPage);

    }

    public static void parseWikiPage(@NotNull Page page, @NotNull Document dom) throws PageParsingException {
        final Node bodyContent = filterNodesById(dom.getElementsByTagName("div"), "bodyContent");
        if (bodyContent == null) {
            throw new PageParsingException("Does not contains body");
        }

        deepRemove(bodyContent, new RemoveFunctor() {
            @Override
            public boolean canRemove(@NotNull final Node node) {
                return hasName(node, "span") && hasClass(node, "smwsortkey");
            }
        });
        deepRemove(bodyContent, new RemoveFunctor() {
            @Override
            public boolean canRemove(@NotNull final Node node) {
                return hasName(node, "table") && hasClass(node, "toc");
            }
        });
        deepRemove(bodyContent, new RemoveFunctor() {
            @Override
            public boolean canRemove(@NotNull final Node node) {
                return hasName(node, "script");
            }
        });


        // TODO: format tables  & page generating

        final Stack<BranchingNode> stack = new Stack<BranchingNode>();
        stack.push(page);

        deepDo(bodyContent, new Functor() {
            @Override
            public void processOnEnter(@NotNull Node node) {
                if (hasName(node, "a")) {
                    stack.peek().add(new Link(getAttribute(node, "href"), node.getTextContent()));
                } else if (hasName(node, "h2")) {
                    stack.peek().add(new Header(node.getTextContent(), 13));
                } else if (hasName(node, "h3")) {
                    stack.peek().add(new Header(node.getTextContent(), -1));
                } else if (hasName(node, "ol")) {
                    List ol = new List(true);
                    stack.peek().add(ol);
                    stack.push(ol);
                } else if (hasName(node, "ul")) {
                    List ol = new List(false);
                    stack.peek().add(ol);
                    stack.push(ol);
                } else if (hasName(node, "li")) {
                    stack.peek().add(new LI(node.getTextContent()));
                } else if (node instanceof Element) {
                    stack.peek().add(new Text(node.getTextContent()));
                } else {
//                    stack.peek().add(new Text(node.getTextContent()));
                }
            }

            @Override
            public void processOnLeave(@NotNull Node node) {
                if (hasName(node, "ol")) {
                    stack.pop();
                } else if (hasName(node, "ul")) {
                    stack.pop();
                }
            }

            @Override
            public boolean needProcessChilds(@NotNull Node node) {
                if (hasName(node, "a")) {
                    return false;
                } else if (hasName(node, "h2")) {
                    return false;
                } else if (hasName(node, "h3")) {
                    return false;
                } else if (hasName(node, "ol")) {
                    return true;
                }
                return true;
            }
        });

    }

    @Nullable
    private static Node filterNodesById(NodeList nlist, String id) {
        for (int i = 0; i < nlist.getLength(); i++) {
            final Node node = nlist.item(i);
            if (id.equals(getAttribute(node, "id"))) return node;
        }
        return null;
    }

    public static void parseSearchResultsPage(@NotNull Page page, @NotNull Document dom) throws PageParsingException {
        final Node searchResults;
        {
            java.util.List<Node> sr = filterNodesByClass(dom.getElementsByTagName("div"), "searchresults");
            if (sr.isEmpty()) {
                throw new PageParsingException("Does not contains <div class='searchresults'>");
            }
            searchResults = sr.get(0);
        }
        {
            final NodeList childNodes = searchResults.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                final Node node = childNodes.item(i);
                if (hasName(node, "h2")) {
                    page.add(new Header(node.getTextContent(), 14));
                } else if (hasName(node, "ul") && hasClass(node, "mw-search-results")) {
                    for (Node li = node.getFirstChild(); li != null; li = li.getNextSibling()) {
                        if (!hasName(li, "li")) continue;
                        for (Node pp = li.getFirstChild(); pp != null; pp = pp.getNextSibling()) {
                            if (!hasName(pp, "div")) continue;
                            if (hasClass(pp, "mw-search-result-heading")) {
                                final Node link = pp.getFirstChild();
                                page.add(new Link(getAttribute(link, "href"), link.getTextContent()));
                            } else if (hasClass(pp, "searchresult")) {
                                page.add(new Text(pp.getTextContent())).add(Text.BR).add(Text.BR);
                            }
                        }
                    }
                }
            }
        }


    }

    @Nullable
    private static String getAttribute(@NotNull Node node, @NotNull String key) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return null;
        }
        final Node namedItem = attributes.getNamedItem(key);
        return namedItem == null ? null : namedItem.getTextContent();
    }

    private static boolean hasClass(@NotNull Node node, @NotNull String className) {
        return className.equals(getAttribute(node, "class"));
    }

    private static boolean hasName(@NotNull Node node, @NotNull String name) {
        return name.equals(node.getNodeName());
    }

    @NotNull
    private static java.util.List<Node> filterNodesByClass(@NotNull NodeList nlist, @NotNull String className) {
        final java.util.List<Node> ret = new LinkedList<Node>();
        for (int i = 0; i < nlist.getLength(); i++) {
            final Node node = nlist.item(i);
            NamedNodeMap a = node.getAttributes();
            if (a != null) {
                Node b = a.getNamedItem("class");
                if (b != null) {
                    String c = b.getNodeValue();
                    if (className.equals(c)) {
                        ret.add(node);
                    }
                }
            }
        }
        return ret;
    }

    @Nullable
    private static String getTitle(@NotNull final Document document) {
        final NodeList title = document.getElementsByTagName("title");
        if (title.getLength() > 0) {
            return title.item(0).getNodeValue();
        }
        return null;
    }

    public static class PageParsingException extends Exception {
        private PageParsingException() {
        }

        private PageParsingException(final String message) {
            super(message);
        }

        private PageParsingException(final Throwable cause) {
            super(cause);
        }

        private PageParsingException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    static void deepRemove(final Node startNode, final RemoveFunctor rf) {
        Node it = startNode.getFirstChild();
        while (it != null) {
            if (rf.canRemove(it)) {
                final Node next = it.getNextSibling();
                startNode.removeChild(it);
                it = next;
            } else {
                deepRemove(it, rf);
                it = it.getNextSibling();
            }
        }
    }

    static void deepDo(final Node startNode, final Functor f) {
        Node it = startNode.getFirstChild();
        while (it != null) {
            f.processOnEnter(it);
            if (f.needProcessChilds(it)) {
                deepDo(it, f);
            }
            f.processOnLeave(it);
            it = it.getNextSibling();
        }
    }

    static private interface RemoveFunctor {
        boolean canRemove(@NotNull Node node);
    }

    static private interface Functor {
        void processOnEnter(@NotNull Node node);

        void processOnLeave(@NotNull Node node);

        boolean needProcessChilds(@NotNull Node node);
    }
}
