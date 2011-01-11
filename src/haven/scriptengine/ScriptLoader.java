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
public class ScriptLoader {

    public boolean load(String filename) {
        try {
            FileReader inFile = new FileReader(filename);
            try {
                ScriptsMachine.engine.eval(inFile);
            } catch (ScriptException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return false;
    }
}
