package net.snicktrix.HubJoin;

import net.Snicktrix.Database.Database;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Luke on 10/4/14.
 */
public class HubJoin extends JavaPlugin {
	public Database database;
	private Events events;

	@Override
	public void onEnable() {
		database = (Database) Bukkit.getPluginManager().getPlugin("Database");
		events = new Events(this);
		Bukkit.getPluginManager().registerEvents(events, this);
	}


}
