package de.minedesso.banPlugin.util;

public enum PermissionType {
    BAN_BASE("ban."),

    BAN_USE(BAN_BASE.perm + "use"),

    BAN_KICK(BAN_BASE.perm + "kick");

    public final String perm;

    PermissionType(String perm) {
        this.perm = perm;
    }
}
