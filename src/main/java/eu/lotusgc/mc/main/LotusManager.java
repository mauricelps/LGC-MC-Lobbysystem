package eu.lotusgc.mc.main;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;

import eu.lotusgc.mc.command.BuildCMD;
import eu.lotusgc.mc.command.SpawnSystem;
import eu.lotusgc.mc.event.ColorSigns;
import eu.lotusgc.mc.event.EventBlocker;
import eu.lotusgc.mc.event.InventorySetterHandling;
import eu.lotusgc.mc.event.JoinEvent;
import eu.lotusgc.mc.event.LeaveEvent;
import eu.lotusgc.mc.event.PasswordSystem;
import eu.lotusgc.mc.event.ScoreboardHandler;
import eu.lotusgc.mc.ext.LotusController;
import eu.lotusgc.mc.misc.MySQL;
import eu.lotusgc.mc.misc.SyncServerdata;
import net.luckperms.api.LuckPerms;

public class LotusManager {
	
	public static File mainFolder = new File("plugins/LotusGaming");
	public static File mainConfig = new File("plugins/LotusGaming/config.yml");
	public static File propsConfig = new File("plugins/LotusGaming/propertiesBackup.yml");
	
	
	//will be loaded as first upon plugin loading!
	public void preInit() {
		long current = System.currentTimeMillis();
		
		//Configs
		
		
		if(!mainFolder.exists()) mainFolder.mkdirs();
		if(!mainConfig.exists()) try { mainConfig.createNewFile(); } catch (Exception ex) { };
		if(!propsConfig.exists()) try { propsConfig.createNewFile(); } catch (Exception ex) { };
		
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(mainConfig);
		cfg.addDefault("MySQL.Host", "127.0.0.1");
		cfg.addDefault("MySQL.Port", "3306");
		cfg.addDefault("MySQL.Database", "TheDataBaseTM");
		cfg.addDefault("MySQL.Username", "user");
		cfg.addDefault("MySQL.Password", "pass");
		cfg.options().copyDefaults(true);
		
		try { cfg.save(mainConfig); } catch (Exception ex) { }
		
		if(!cfg.getString("MySQL.Password").equalsIgnoreCase("pass")) {
			MySQL.connect(cfg.getString("MySQL.Host"), cfg.getString("MySQL.Port"), cfg.getString("MySQL.Database"), cfg.getString("MySQL.Username"), cfg.getString("MySQL.Password"));
		}
		
		Bukkit.getConsoleSender().sendMessage("§aPre-Initialisation took §6" + (System.currentTimeMillis() - current) + "§ams");
	}
	
	//Commands and Event registers will be thrown in here!
	public void mainInit() {
		long current = System.currentTimeMillis();
		
		Main.main.getCommand("unlock").setExecutor(new PasswordSystem());
		Main.main.getCommand("build").setExecutor(new BuildCMD());
		Main.main.getCommand("spawn-admin").setExecutor(new SpawnSystem());
		Main.main.getCommand("spawn").setExecutor(new SpawnSystem());
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new LeaveEvent(), Main.main);
		pm.registerEvents(new JoinEvent(), Main.main);
		pm.registerEvents(new PasswordSystem(), Main.main);
		pm.registerEvents(new BuildCMD(), Main.main);
		pm.registerEvents(new ScoreboardHandler(), Main.main);
		pm.registerEvents(new SpawnSystem(), Main.main);
		pm.registerEvents(new InventorySetterHandling(), Main.main);
		pm.registerEvents(new EventBlocker(), Main.main);
		pm.registerEvents(new ColorSigns(), Main.main);
		
		Bukkit.getConsoleSender().sendMessage("§aMain-Initialisation took §6" + (System.currentTimeMillis() - current) + "§ams");
	}
	
	//will loaded as last - for schedulers and misc stuff
	public void postInit() {
		long current = System.currentTimeMillis();
		
		LotusController lc = new LotusController();
		lc.initLanguageSystem();
		lc.initPlayerLanguages();
		lc.initPrefixSystem();
		lc.loadServerIDName();
		
		ScoreboardHandler.startScheduler(0, 50, 20);
		ScoreboardHandler.initRoles();
		SyncServerdata.startScheduler();
		InventorySetterHandling.loadServer();
		
		Main.luckPerms = (LuckPerms) Bukkit.getServer().getServicesManager().load(LuckPerms.class);
		
		Bukkit.getConsoleSender().sendMessage("§aPost-Initialisation took §6" + (System.currentTimeMillis() - current) + "§ams");
	}

}