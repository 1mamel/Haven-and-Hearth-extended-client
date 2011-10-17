package wikilib;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import wikilib.dom.Page;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 17.10.11
 * Time: 1:25
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class ParsingUtilTest {
    @Test
    public void testParseSearchResultsPage() throws Exception {
        final Document document = getDocument("test1.xml");
        final Page page = new Page("AAA");
        ParsingUtil.parseSearchResultsPage(page,document);
        System.out.println("page = \n" + page.toString());
    }

    @Test
    public void testParseWikiPage() throws Exception {
        final Document document = getDocument("test2.xml");
        final Page page = new Page("AAA");
        ParsingUtil.parseWikiPage(page,document );
        System.out.println("page = \n" + page.toString());
    }
    
    private static File getTestFile(final String name) {
        return new File(new File("test"),name);
    }

    private static Document getDocument(final String is) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(false);
        dbf.setExpandEntityReferences(false);

        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document dom = db.parse(getTestFile(is));
        return dom;
    }
}
