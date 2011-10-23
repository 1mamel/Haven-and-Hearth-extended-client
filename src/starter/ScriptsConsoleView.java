package starter;

import haven.scriptengine.ConsoleCommandListener;
import haven.scriptengine.ScriptsConsole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Writer;

public class ScriptsConsoleView extends JFrame implements ScriptsConsole {
    private JPanel contentPane;
    private JTextField inputLine;
    private JTextArea consoleLog;
    private JButton buttonOK;

//    private final List<String> myLastCommands = new ArrayList<String>();
//    private int myLastCommandIndex = 0;

    @NotNull
    private final Writer myConsoleWriter;
    @Nullable
    private ConsoleCommandListener myListener;

    public ScriptsConsoleView() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        myConsoleWriter = new Writer() {
            @Override
            public void write(final String str) throws IOException {
                consoleLog.append(str);
            }

            @Override
            public void write(final char[] cbuf, final int off, final int len) throws IOException {
                consoleLog.append(new String(cbuf, off, len));
            }

            @Override
            public void flush() throws IOException {
                // Do nothing
            }

            @Override
            public void close() throws IOException {
                // Do nothing
            }
        };
        inputLine.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                // TODO: add console memory for last commands
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {

                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    final String command = inputLine.getText();
                    if (myListener != null) {
                        myListener.onCommandSubmitted(command);
                        inputLine.setText("");
                    }
                } else {
                    super.keyPressed(e);
                }
            }
        });
    }

    @NotNull
    @Override
    public Writer getConsoleStdOutWriter() {
        return myConsoleWriter;
    }

    @NotNull
    @Override
    public Writer getConsoleStdErrWriter() {
        return myConsoleWriter;
    }

    @Override
    public void setCommandListener(@Nullable final ConsoleCommandListener listener) {
        myListener = listener;
    }
}
