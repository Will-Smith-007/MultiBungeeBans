package de.will_smith_007.multibungeebans.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.will_smith_007.multibungeebans.enums.Message;
import de.will_smith_007.multibungeebans.managers.ban.BanInformationManager;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.List;

/**
 * This {@link Command} allows you to show all banned players.
 */
public class BanListCommand extends Command implements TabExecutor {

    private final BanInformationManager banInformationManager;

    @Inject
    public BanListCommand(@NonNull @Named("BanListCommand") String name,
                          @NonNull BanInformationManager banInformationManager) {
        super(name);
        this.banInformationManager = banInformationManager;
    }

    @Override
    public void execute(@NonNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("multibans.banlist")) {
            sender.sendMessage(new TextComponent(Message.NO_PERMISSION.getMessage()));
            return;
        }

        final int length = args.length;
        final int maxResultsPerPage = 10;
        int selectedPage = 1;

        if (length == 1) {
            try {
                selectedPage = Integer.parseInt(args[0]);
                if (selectedPage < 1) throw new NumberFormatException();
            } catch (NumberFormatException numberFormatException) {
                sender.sendMessage(new TextComponent(Message.PREFIX + "§cPlease provide a valid page number."));
                return;
            }
        }

        final int finalSelectedPage = selectedPage;
        banInformationManager.getBannedPlayerNamesAsync().thenAccept(bannedUsernames -> {
            if (bannedUsernames.isEmpty()) {
                sender.sendMessage(new TextComponent(Message.PREFIX + "§cNo banned users found."));
                return;
            }

            // Calculation of pages and indexes
            final int bannedUsernameSize = bannedUsernames.size();
            final int maxPages = (bannedUsernameSize / maxResultsPerPage) +
                    (bannedUsernameSize % maxResultsPerPage == 0 ? 0 : 1);
            final String[] bannedUsernameArray = bannedUsernames.toArray(new String[0]);
            final int beginningIndex = ((finalSelectedPage * 10) - 10);
            final int endingIndex = (beginningIndex + maxResultsPerPage);

            if (finalSelectedPage > maxPages) {
                sender.sendMessage(new TextComponent(Message.PREFIX + "§cThere are currently only §e" + maxPages +
                        "§c pages available."));
                return;
            }

            sender.sendMessage(new TextComponent(Message.PREFIX + "§7The following players §8(§c" + bannedUsernameSize +
                    "§8)§7 were banned:"));

            for (int index = beginningIndex; index != bannedUsernameSize && index != endingIndex; index++) {
                final String bannedUsername = bannedUsernameArray[index];

                final BaseComponent unbanComponent = new TextComponent(" §8[§aUnban§8]");
                unbanComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unban " + bannedUsername));
                unbanComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new Text("§eClick to unban the player.")));

                final BaseComponent checkBanComponent = new TextComponent(" §8[§eCheck§8]");
                checkBanComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bancheck " + bannedUsername));
                checkBanComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new Text("§eClick to check the current ban.")));

                sender.sendMessage(new ComponentBuilder()
                        .append(new TextComponent(Message.PREFIX + "§c" + bannedUsername))
                        .append(unbanComponent)
                        .append(checkBanComponent).create());
            }

            // Clickable component to switch to the previous page
            final BaseComponent previousPageComponent = new TextComponent(
                    (finalSelectedPage != 1 ? "§c«" : ""));
            previousPageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/banlist " + (finalSelectedPage - 1)));
            previousPageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text("§eClick to show the previous page")));

            // Clickable Component to switch to the next page
            final BaseComponent nextPageComponent = new TextComponent(
                    (finalSelectedPage != maxPages ? "§a»" : ""));
            nextPageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/banlist " + (finalSelectedPage + 1)));
            nextPageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text("§eClick to show the next page")));

            sender.sendMessage(new ComponentBuilder()
                    .append(previousPageComponent)
                    .append(new TextComponent("§7Page §e" + finalSelectedPage + "§7 of §e" + maxPages))
                    .append(nextPageComponent).create());
        });
    }

    @Override
    public Iterable<String> onTabComplete(@NonNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("multibans.banlist")) return null;
        if (args.length == 1) {
            return List.of("Page");
        }
        return null;
    }
}
