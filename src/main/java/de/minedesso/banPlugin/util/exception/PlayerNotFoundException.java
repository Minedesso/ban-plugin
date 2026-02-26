package de.minedesso.banPlugin.util.exception;

public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException(String targetName) {
        super("Player with name '" + targetName + "' not found.");
    }
}
