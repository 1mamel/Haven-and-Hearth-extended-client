package haven.scriptengine;

import org.jetbrains.annotations.NotNull;

/**
 * @author Vlad.Rassokhin@gmail.com
 */
public interface ConsoleCommandListener {
    void onCommandSubmitted(@NotNull String command);
}
