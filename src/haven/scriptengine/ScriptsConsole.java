package haven.scriptengine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Writer;

/**
 * @author Vlad.Rassokhin@gmail.com
 */
public interface ScriptsConsole {
    void setCommandListener(@Nullable ConsoleCommandListener listener);

    @NotNull
    Writer getConsoleStdOutWriter();

    @NotNull
    Writer getConsoleStdErrWriter();
}
