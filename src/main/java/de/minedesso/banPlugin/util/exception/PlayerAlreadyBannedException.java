package de.minedesso.banPlugin.util.exception;

public class PlayerAlreadyBannedException extends Exception {
    public PlayerAlreadyBannedException(String name) {
        super("Player '" + name + "' is already banned.");
    }
}
