package portals.github.com;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
//import org.bukkit.event.block.SignChangeEvent;
//import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }
    @Override
    public void onDisable() {

    }
    @Override
    public boolean onCommand(CommandSender sender,
        Command command,
        String label,
        String[] args) {
        if (command.getName().equalsIgnoreCase("home")) {
        	String pluginFolder = getDataFolder().getAbsolutePath();
        	
            sender.sendMessage("You ran /home!");
            String[] destination = args;
            sender.sendMessage("Attempting to teleport to " + destination[0] +"...");
            JSONObject main = null;
            //Try to assign JSONObject to the already defined data.
            //If this fails, catch will initialize it as a brand new JSON Object
            JSONParser parser = new JSONParser();
               
            File file = new File(pluginFolder + File.separator + "coordinateData.json");
            try {
				main = (JSONObject) parser.parse(new FileReader(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch blocka
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Player player = (Player) sender;
            Location loc = player.getLocation();
            boolean dist = false;
            long x = (long) loc.getBlockX();
            long y = (long) loc.getBlockY();
            long z = (long) loc.getBlockZ();
            for(Iterator iterator = main.keySet().iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                try {
                	JSONObject coords = (JSONObject) parser.parse((main.get(key).toString()));
                	long distance = (long) Math.sqrt(Math.pow(x-(long) coords.get("x"),2) +  Math.pow(y-(long) coords.get("y"),2) +  Math.pow(z-(long) coords.get("z"),2));
					sender.sendMessage(key + ":" + coords.get("x") + ","+ coords.get("y") + ","+ coords.get("z") + "," + distance);
					if(distance < 5) {
						dist = true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            JSONObject coords = null;
			try {
				coords = (JSONObject) parser.parse((main.get(destination[0]).toString()));
			} catch (ParseException e1) {
				sender.sendMessage("Not a valid portal name!");
			}
            if(isValidName((long) coords.get("x"),(long) coords.get("y"), (long) coords.get("z"),destination[0])) {
	            if(dist) {
		            if(player.getInventory().contains(Material.QUARTZ)) {
						Location location = new Location(Bukkit.getWorld("world"),(long) coords.get("x") + 0.5,(long) coords.get("y"),(long) coords.get("z") + 0.5);
						player.teleport(location);
						player.getInventory().removeItem(new ItemStack[] {new ItemStack(Material.getMaterial("QUARTZ"), 1) });
					} else {
						sender.sendMessage("Teleport failed! Teleportation requires 1 nether quartz ore.");
					}
	            } else {
	            	sender.sendMessage("You are not within five blocks of a teleport shrine!");
	            }
            } else {
            	sender.sendMessage("This portal has been obstructed!");
            }

            
         
            return true;
        }
        return false;
    }

     public boolean isValidName(long x, long y, long z, String title) {
    	 boolean condition = true;
    	 if(isValid(x,y,z)) {
    		 
    		 	Location location = new Location(Bukkit.getWorld("world"),x,y,z);
    	    	System.out.println(location.getBlock().getType().toString());
    	    	if(location.getBlock().getType().toString().equals("SIGN_POST")) {
    	    		Sign s = (Sign) location.getBlock().getState();
    	    		System.out.println(title);
    	    		System.out.println(s.getLine(0));
    	    		System.out.println(s.getLine(1));
    	    		return s.getLine(0).equals("[Portal]") && s.getLine(1).equals(title);
    	    	} else {
    	    		condition = false;
    	    	}
    	 } else {
    		 condition = false;
    	 }
    	 if(!condition) {
    		 String pluginFolder = getDataFolder().getAbsolutePath();
             JSONObject main;
             //Try to assign JSONObject to the already defined data.
             //If this fails, catch will initialize it as a brand new JSON Object
             try {
                 JSONParser parser = new JSONParser();
                
                 File file = new File(pluginFolder + File.separator + "coordinateData.json");
                 main = (JSONObject) parser.parse(new FileReader(file));
                    

               } catch (Exception e) {
             	  main = new JSONObject();
               }
             main.remove(title);
             
             try {
                 File file = new File(pluginFolder + File.separator + "coordinateData.json");
                 File filePath = new File(pluginFolder + File.separator);
                 filePath.mkdirs();
                 if (!file.exists()) {
                     file.createNewFile();
                 }
                 FileWriter fileWriter = new FileWriter(file);
                 fileWriter.write(main.toJSONString());
                 fileWriter.flush();
                 fileWriter.close();
             } catch (Exception e) {}
             

    	 }
    	 return condition;
     }
     
    public boolean isValid(long x, long y, long z){
    	Location location = new Location(Bukkit.getWorld("world"),x,y,z);
    	boolean condition = true;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                String name = location.getBlock().getRelative(i, -1, j).getType().toString();
                if (i == 0 && j == 0) {
                    if (!name.equals("GOLD_BLOCK")) {
                        condition = false;
                    }
                } else if (!name.equals("OBSIDIAN")) {
                    condition = false;
                }
            }
        }
    	return condition;
    }
    @EventHandler
    public void onSignChanged(SignChangeEvent event) {
        if (event.getLines()[0].equals("[Portal]")) {
            Player player = (Player) event.getPlayer();
            //boolean condition = true;
            /*for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    String name = event.getBlock().getRelative(i, -1, j).getType().toString();
                    if (i == 0 && j == 0) {
                        if (!name.equals("GOLD_BLOCK")) {
                            condition = false;
                        }
                    } else if (!name.equals("OBSIDIAN")) {
                        condition = false;
                    }
                }
            }*/
            
            if (isValidName((long) event.getBlock().getLocation().getX(),(long) event.getBlock().getLocation().getY(),(long) event.getBlock().getLocation().getZ(),event.getLine(1))) {
                String pluginFolder = getDataFolder().getAbsolutePath();
                JSONObject main;
                //Try to assign JSONObject to the already defined data.
                //If this fails, catch will initialize it as a brand new JSON Object
                try {
                    JSONParser parser = new JSONParser();
                   
                    File file = new File(pluginFolder + File.separator + "coordinateData.json");
                    main = (JSONObject) parser.parse(new FileReader(file));
                       

                  } catch (Exception e) {
                	  main = new JSONObject();
                  }
                
                JSONObject coord = new JSONObject();
                coord.put("x", event.getBlock().getX());
                coord.put("y", event.getBlock().getY());
                coord.put("z", event.getBlock().getZ());
                main.put(event.getLines()[1], coord);
                if(main.get(event.getLine(1)) != null) {
                    player.sendMessage("The shrine could not be created! Someone else has a portal named " + event.getLine(1));

                	return;
                }
                player.sendMessage("Created shrine " + main.get(event.getLine(1)) + ", get to it with /warp " + event.getLine(1));

                try {
                    File file = new File(pluginFolder + File.separator + "coordinateData.json");
                    File filePath = new File(pluginFolder + File.separator);
                    filePath.mkdirs();
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(main.toJSONString());
                    fileWriter.flush();
                    fileWriter.close();
                } catch (Exception e) {}

            } else {
                player.sendMessage("The shrine could not be created! Did you make sure to place 8 obsidian in a square around a diamond block?");
            }


        }
    }
}