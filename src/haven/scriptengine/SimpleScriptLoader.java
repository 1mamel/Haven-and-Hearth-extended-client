package haven.scriptengine;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 11.01.11
 * Time: 1:59
 */
public class SimpleScriptLoader implements Loader {

    private static SimpleScriptLoader ourInstance = new SimpleScriptLoader();

    public static SimpleScriptLoader getInstance() {
        return ourInstance;
    }

    private SimpleScriptLoader() {
    }

    public boolean load(String fileName) {
        try {
            FileReader inFile = new FileReader(fileName);
            try {
                ScriptsMachine.engine.eval(inFile);
            } catch (ScriptException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
