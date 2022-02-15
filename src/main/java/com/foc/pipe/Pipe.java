package com.foc.pipe;

import jep.Interpreter;
import jep.JepException;
import jep.SharedInterpreter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public final class Pipe extends JavaPlugin {
    public Interpreter pyEnv = null;
    private File mainPath = Paths.get(getDataFolder().getAbsolutePath(), "main.py").toFile();

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        if (mainPath.isDirectory()) {
            getLogger().severe("`main.py` cannot be a directory!");
        } else if (!mainPath.exists()) {
            try {
                mainPath.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mainPath.exists() && pyEnv == null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    getLogger().info("Enabling v"+getDescription().getVersion()+"*");
                    try {
                        pyEnv = new SharedInterpreter();
                        pyEnv.exec("__import__('sys').path.append('" + getDataFolder().getAbsolutePath() + "')");
                        try {
                            pyEnv.runScript(mainPath.getAbsolutePath());
                        } catch (JepException e) {
                            getLogger().severe(e.getMessage());
                        }
                    } catch (UnsatisfiedLinkError e) {
                        getLogger().severe("Jep ***DOES NOT*** support reloading! If you've just started the server and have just gotten this error, add the jep installation (from pip) to your library path!");
                    }
                }
            }, 0L);
        }
    }

    @Override
    public void onDisable() {
        if (pyEnv != null) {
            pyEnv.close();
            pyEnv = null;
        }
    }
}
