package me.narutopro.illegalitemremover;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IllegalItemRemover extends JavaPlugin implements Listener {

    private final Set<Material> bannedItems = new HashSet<>(Arrays.asList(
        Material.BEDROCK,
        Material.BARRIER,
        Material.COMMAND_BLOCK,
        Material.COMMAND_BLOCK_MINECART,
        Material.STRUCTURE_BLOCK,
        Material.STRUCTURE_VOID,
        Material.JIGSAW,
        Material.DEBUG_STICK,
        Material.SPAWNER
    ));

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("IllegalItemRemover has been enabled.");
    }

    public void alertStaff(Player offender, Material item) {
        String msg = "§c§l[Alert] §r§c" + offender.getName() + " has §l[" + item.name() + "]§c Removed";
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp() || p.hasPermission("anticheat.alert")) {
                p.sendMessage(msg);
            }
        }
    }

    public void scanPlayer(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && bannedItems.contains(item.getType())) {
                alertStaff(player, item.getType());
                player.getInventory().remove(item);
            }
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand != null && bannedItems.contains(hand.getType())) {
            alertStaff(player, hand.getType());
            player.getInventory().setItemInMainHand(null);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        scanPlayer(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && bannedItems.contains(event.getCurrentItem().getType())) {
            Player player = (Player) event.getWhoClicked();
            alertStaff(player, event.getCurrentItem().getType());
            event.setCancelled(true);
            player.getInventory().remove(event.getCurrentItem());
        }
    }

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        if (bannedItems.contains(event.getItem().getItemStack().getType())) {
            Player player = event.getPlayer();
            alertStaff(player, event.getItem().getItemStack().getType());
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("versionillegalitemremover")) {
            sender.sendMessage("§aIllegalItemRemover Plugin");
            sender.sendMessage("§bAuthor: §fMrNaruto99");
            sender.sendMessage("§bVersion: §f1.21.4 (Paper)");
            return true;
        }
        return false;
    }
      }
                  
