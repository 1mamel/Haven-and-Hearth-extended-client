package wikilib;

import org.xml.sax.SAXException;
import wikilib.dom.Page;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WikiLib extends Thread {

    private final Queue<Request> requests = new ConcurrentLinkedQueue<Request>();
//    private static final Pattern removeHtml = Pattern.compile("<.*?>");
//    private Pattern findKeywords = Pattern.compile("\\[\\[([\\sa-zA-Z0-9]+)(\\|\\s*([\\sa-zA-Z0-9]+))?\\]\\]");
//    private Pattern findSpaces = Pattern.compile("\\s");

    public WikiLib() {
        super("WikiLib Thread");
        start();
    }

    // Thread main process
    @Override
    public void run() {
        while (true) {
            Request req;
            while ((req = requests.poll()) != null) {
                searchPage(req);
            }
            if (requests.peek() == null) {
                synchronized (requests) {
                    if (requests.peek() == null) {
                        try {
                            requests.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            }
        }
    }

    public void search(final Request request) {
        requests.add(request);
        // Synchronization does not stop thread flow, because used only for waking up fetcher thread.
        synchronized (requests) {
            requests.notifyAll();
        }
    }

    private static void searchPage(final Request request) {
        Page page = Page.FAILED_TO_PARSE;
        try {
            final InputStream input = request.getUrl().openStream();
            page = ParsingUtil.parseHtml(input);
            input.close();
        } catch (IOException ignored) {
        } catch (ParserConfigurationException ignored) {
        } catch (SAXException ignored) {
        } catch (ParsingUtil.PageParsingException ignored) {
        }
        request.setResult(page);
    }


//    private String formatSymbols(String content) {
//        return content.replaceAll("&gt;", ">")
//                .replaceAll("&lt;", "<")
//                .replaceAll("&nbsp;", " ")
//                .replaceAll("&#39;", "'")
//                .replaceAll("&amp;", "&");
//    }

//    private String formatKeywords(String text) {
//        Matcher ma = this.findKeywords.matcher(text);
//        StringBuffer buffer = new StringBuffer(text.length());
//        while (ma.find()) {
//            String link = "/wiki/";
//            link += this.findSpaces.matcher(ma.group(1)).replaceAll("_");
//            String title = (ma.group(3) == null) ? ma.group(1) : ma.group(3);
//            link = "$u{$col[0,0,192]{$a[" + link + "]{" + title + "}}}";
//            ma.appendReplacement(buffer, Matcher.quoteReplacement(link));
//        }
//        ma.appendTail(buffer);
//        return buffer.toString();
//    }

}
