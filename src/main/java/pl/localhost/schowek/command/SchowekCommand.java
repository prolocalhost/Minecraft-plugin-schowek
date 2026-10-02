package pl.localhost.schowek.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pl.localhost.schowek.Main;
import pl.localhost.schowek.model.SchowekItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SchowekCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public SchowekCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("schowek.use")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            plugin.getSchowekGUI().openGUI(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("dobierz") || sub.equals("restock")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("schowek.use")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            plugin.getSchowekManager().restockAllToLimit(player);
            return true;
        }

        if (sub.equals("schowaj") || sub.equals("deposit")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("schowek.use")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            plugin.getSchowekManager().checkAndDepositExcess(player, true);
            return true;
        }

        if (sub.equals("status") || sub.equals("stan")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("schowek.use")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            player.sendMessage(plugin.getConfigManager().color("&8---------- &6Stan Twojego Schowka &8----------"));
            for (SchowekItem item : plugin.getConfigManager().getItems().values()) {
                int inInv = item.countInInventory(player);
                int inSchowek = plugin.getDataManager().getAmount(player.getUniqueId(), item.getKey());
                player.sendMessage(plugin.getConfigManager().color(" &8» &f" + item.getDisplayName() +
                        " &7| W EQ: &b" + inInv + "&7/&b" + item.getLimit() +
                        " &7| W schowku: &a" + inSchowek + " szt."));
            }
            player.sendMessage(plugin.getConfigManager().color("&8-------------------------------------------"));
            return true;
        }

        if (sub.equals("reload") || sub.equals("rl")) {
            if (!sender.hasPermission("schowek.admin")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            plugin.getConfigManager().reloadConfig();
            sender.sendMessage(plugin.getConfigManager().getMessage("reload"));
            return true;
        }

        if (sub.equals("admin")) {
            if (!sender.hasPermission("schowek.admin")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            if (args.length < 5) {
                sender.sendMessage(plugin.getConfigManager().getMessage("admin-usage"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
                return true;
            }

            String action = args[2].toLowerCase();
            String itemKey = args[3].toLowerCase();
            SchowekItem item = plugin.getConfigManager().getItem(itemKey);

            if (item == null) {
                String available = String.join(", ", plugin.getConfigManager().getItems().keySet());
                sender.sendMessage(plugin.getConfigManager().getMessage("unknown-item").replace("%items%", available));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[4]);
                if (amount < 0) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid-number"));
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getConfigManager().getMessage("invalid-number"));
                return true;
            }

            if (action.equals("daj") || action.equals("add")) {
                plugin.getDataManager().addAmount(target.getUniqueId(), item.getKey(), amount);
            } else if (action.equals("zabierz") || action.equals("take")) {
                plugin.getDataManager().removeAmount(target.getUniqueId(), item.getKey(), amount);
            } else if (action.equals("ustaw") || action.equals("set")) {
                plugin.getDataManager().setAmount(target.getUniqueId(), item.getKey(), amount);
            } else {
                sender.sendMessage(plugin.getConfigManager().getMessage("admin-usage"));
                return true;
            }

            int newAmount = plugin.getDataManager().getAmount(target.getUniqueId(), item.getKey());
            String msg = plugin.getConfigManager().getMessage("admin-success")
                    .replace("%player%", target.getName())
                    .replace("%item%", item.getDisplayName())
                    .replace("%amount%", String.valueOf(newAmount));
            sender.sendMessage(msg);
            return true;
        }

        sender.sendMessage(plugin.getConfigManager().getMessage("usage"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("schowek.use")) {
                list.add("dobierz");
                list.add("schowaj");
                list.add("status");
            }
            if (sender.hasPermission("schowek.admin")) {
                list.add("admin");
                list.add("reload");
            }
            return filter(list, args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            if (!sender.hasPermission("schowek.admin")) return list;
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("admin")) {
            if (!sender.hasPermission("schowek.admin")) return list;
            list.add("daj");
            list.add("zabierz");
            list.add("ustaw");
            return filter(list, args[2]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("admin")) {
            if (!sender.hasPermission("schowek.admin")) return list;
            return filter(new ArrayList<>(plugin.getConfigManager().getItems().keySet()), args[3]);
        }

        if (args.length == 5 && args[0].equalsIgnoreCase("admin")) {
            if (!sender.hasPermission("schowek.admin")) return list;
            list.add("1");
            list.add("5");
            list.add("10");
            list.add("32");
            list.add("64");
            return filter(list, args[4]);
        }

        return list;
    }

    private List<String> filter(List<String> list, String query) {
        String lower = query.toLowerCase();
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .collect(Collectors.toList());
    }
}
