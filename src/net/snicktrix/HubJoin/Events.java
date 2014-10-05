package net.snicktrix.HubJoin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

/**
 * Created by Luke on 10/4/14.
 */
public class Events implements Listener {
	private HubJoin hubJoin;

	public Events(HubJoin hubJoin) {
		this.hubJoin = hubJoin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		event.setJoinMessage("");

		//If the player is new
		Bukkit.getScheduler().runTaskAsynchronously(hubJoin, new Runnable() {
			@Override
			public void run() {
				int joinid = hubJoin.database.isNew(player);
				if (joinid != 0) {
					excludeMessage(player, ChatColor.GOLD + player.getName() +
							ChatColor.AQUA + " has joined the server for the first time " + ChatColor.YELLOW +
							"(#" + Integer.toString(joinid) + ")");
				} else {
					excludeMessage(player, ChatColor.GOLD + player.getName() + ChatColor.GREEN +
							" joined the game");
				}
			}
		});
		equip(player);
		joinMessage(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage("");
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		//Builder Check
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

		if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
			if (event.getPlayer().getItemInHand().getType() == Material.DIAMOND_AXE) {
				hubJoin.database.sendToServer(event.getPlayer(), "towny");
				return;
			}
			if (event.getPlayer().getItemInHand().getType() == Material.GRASS) {
				hubJoin.database.sendToServer(event.getPlayer(), "creative");
				return;
			}
		}
	}

	@EventHandler
	public void onInvDrag(InventoryDragEvent event) {
		//Builder Check
		if(event.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;

		event.setCancelled(true);
		equip(Bukkit.getPlayer(event.getWhoClicked().getName()));
	}

	@EventHandler
	public void onInvDrop(PlayerDropItemEvent event) {
		//Builder Check
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

		event.setCancelled(true);
		equip(event.getPlayer());
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

		event.setCancelled(true);
	}

	//**********************************************************************************//
	//**********************************************************************************//

	private void joinMessage(Player player) {
		player.sendMessage(ChatColor.YELLOW + "███████████████████████████████████");
		player.sendMessage("");
		player.sendMessage(ChatColor.AQUA + "            Welcome to the " + ChatColor.RED +
				ChatColor.BOLD + "Snicktrix Network");
		player.sendMessage("");
		player.sendMessage(ChatColor.AQUA + "            Use the items in your " + ChatColor.RED +
				"hotbar " + ChatColor.AQUA + "to join a server!");
		player.sendMessage("");
		player.sendMessage(ChatColor.YELLOW + "███████████████████████████████████");
	}

	private void excludeMessage(Player player, String message) {
		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
			if (!otherPlayer.getName().equals(player.getName())) {
				otherPlayer.sendMessage(message);
			}
		}
	}

	private void equip(Player player) {
		PlayerInventory inv = player.getInventory();
		inv.clear();

		inv.setItem(1, itemMaker(Material.DIAMOND_AXE, ChatColor.AQUA + "Towny Survival"));
		inv.setItem(3, itemMaker(Material.GRASS, ChatColor.GREEN + "Creative Server"));

		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	private ItemStack itemMaker(Material material, String name) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
}
