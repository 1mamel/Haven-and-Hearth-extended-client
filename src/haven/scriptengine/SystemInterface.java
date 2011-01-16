package haven.scriptengine;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 11.01.11
 * Time: 13:43
 */
public class SystemInterface implements Loader {
    private PrintStream out = System.out;
    private PrintStream err = System.err;

    public boolean load(String fileName) {
        return SimpleScriptLoader.getInstance().load(fileName);
    }

    public void print(Object o) {
        out.print(o.toString());
    }

    public void println(Object o) {
        System.err.println(o);
        System.err.println(out);
        out.println(o.toString());
    }

    public void eprint(Object o) {
        err.print(o.toString());
    }

    public void eprintln(Object o) {
        err.println(o.toString());
    }

    void setOut(PrintStream stream) {
        out = stream;
    }

    void setErr(PrintStream stream) {
        err = stream;
    }

    private static final SystemInterface ourInstance = new SystemInterface();

    public static SystemInterface getInstance() {
        return ourInstance;
    }

    private SystemInterface() {
    }
}