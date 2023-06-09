package de.will_smith_007.multibungeebans.banned_user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class represents a banned user object and holds all ban information.
 */
@Getter
@Setter
@Builder
public class BannedUser {

    private final UUID bannedUUID;
    private String bannedUsername, bannedBy, banReason;
    private LocalDateTime bannedDateTime, unbanDateTime;
    private boolean isPermanentlyBanned;
    private long banID;
}
