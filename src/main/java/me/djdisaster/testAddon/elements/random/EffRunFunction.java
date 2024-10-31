package me.djdisaster.testAddon.elements.random;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.function.Functions;
import ch.njol.util.Kleenean;
import me.djdisaster.testAddon.utils.AsyncManager;
import me.djdisaster.testAddon.utils.CompiledJavaClass;
import me.djdisaster.testAddon.utils.CompiledJavaClassInstance;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EffRunFunction extends Effect {
    static {
        Skript.registerEffect(EffRunFunction.class, "run function %string% [from %-string%] [(1¦async)] [[called] with %-objects%]");
    }

    private Expression<String> functionName;
    private Expression<String> file;
    private Expression<Object> arguments;
    private Kleenean runAsync;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        this.functionName = (Expression<String>) expressions[0];
        if (expressions[1] != null) {
            this.file = (Expression<String>) expressions[1];
        }
        if (expressions[2] != null) {
            this.arguments = (Expression<Object>) expressions[2];
        }

        if (parser.mark == 1) {
            runAsync = Kleenean.TRUE;
        } else {
            runAsync = Kleenean.FALSE;
        }

        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "";//""Send bungee message to " + players.toString(event, debug) + " with text " + text.toString(event, debug);
    }

    @Override
    protected void execute(Event event) {

        if (runAsync.isTrue()) {
            AsyncManager.getSingleThreadExecutor().execute(() -> {
                runEvent(event);
            });
        } else {
            runEvent(event);
        }
    }

    public void runEvent(Event event) {

        String fName = functionName.getSingle(event);
        // lil stealing from skript-reflect
        Object[] args = arguments == null ? new Object[0] : arguments.getArray(event);

        List<Object> list = new ArrayList<>();
        for (Object arg : args) {
            list.add(args);
        }
        Object[][] finalArgs = new Object[list.size()][];

        int n = 0;
        for (Object object : list) {
            finalArgs[n] = new Object[]{object};
            n++;
        }


        if (file == null) {
            Functions.getGlobalFunction(fName).execute(finalArgs);
        } else {
            String fileName = file.getSingle(event);
            if (!fileName.endsWith(".sk")) {
                fileName += ".sk";
            }
            Functions.getFunction(fName, fileName).execute(finalArgs);
        }
    }
}