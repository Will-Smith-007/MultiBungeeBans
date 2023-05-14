package de.will_smith_007.multibungeebans.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Message {

    PREFIX("§f[§cMultiBungeeBans§f] §7"),
    NO_PERMISSION(PREFIX + "§cYou don't have permissions to execute this command.");

    private final String message;


    @Override
    public String toString() {
        return message;
    }
}
