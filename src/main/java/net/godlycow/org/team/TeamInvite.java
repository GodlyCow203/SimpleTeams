package net.godlycow.org.team;

import java.util.UUID;

public class TeamInvite {

    private final UUID teamId;
    private final UUID inviterUUID;
    private final long expiresAt;

    public TeamInvite(UUID teamId, UUID inviterUUID, int expireSeconds) {
        this.teamId = teamId;
        this.inviterUUID = inviterUUID;
        this.expiresAt = System.currentTimeMillis() + (long) expireSeconds * 1000L;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public UUID getInviterUUID() {
        return inviterUUID;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
