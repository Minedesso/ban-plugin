package de.minedesso.banPlugin;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class BanPlugin extends JavaPlugin {

    @Getter
    private static BanPlugin instance;
    private final Logger logger = getLogger();

    @Override
    public void onEnable() {
        instance = this;
        logger.info("Ban plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        logger.info("Ban plugin has been disabled.");
    }
}
