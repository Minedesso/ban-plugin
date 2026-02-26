package de.minedesso.banPlugin;

import de.minedesso.banPlugin.domain.BanService;
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
        initializeServices();
        logger.info("Ban plugin has been enabled.");
    }

    private void initializeServices() {
        try {
            BanService.getInstance();
        } catch (Exception e) {
            logger.warning("Failed to initialize Services: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        logger.info("Ban plugin has been disabled.");
    }
}
