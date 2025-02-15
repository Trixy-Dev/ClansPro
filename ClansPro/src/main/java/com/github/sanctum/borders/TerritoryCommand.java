package com.github.sanctum.borders;

import com.github.sanctum.borders.task.BorderTask;
import com.github.sanctum.clans.construct.DefaultClan;
import com.github.sanctum.clans.construct.api.ClanSubCommand;
import com.github.sanctum.labyrinth.formatting.TabCompletion;
import com.github.sanctum.labyrinth.library.Message;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TerritoryCommand extends ClanSubCommand {
	public TerritoryCommand() {
		super("territory");
	}

	@Override
	public boolean player(Player p, String label, String[] args) {
		int length = args.length;
		String prefix = "&f[&6&lX&f]&r";
		Message msg = Message.form(p).setPrefix(prefix);
		if (length == 0) {
			if (!BorderListener.toggled.containsKey(p.getUniqueId())) {
				msg.build(BorderListener.coToggle(DefaultClan.action.getPrefix().replace("&6", "&#eb9534&l") + " &#3ee67c&lChunk borders on &#ffffff| &#3ee6ddClick to toggle ", prefix.replace("&6", "&#eb9534")));
				BorderTask.run(p).cancelAfter(140).repeatReal(1, 40);
				BorderListener.toggled.put(p.getUniqueId(), true);
				return true;
			}
			if (BorderListener.toggled.get(p.getUniqueId())) {
				msg.build(BorderListener.coToggle(DefaultClan.action.getPrefix().replace("&6", "&#eb9534&l") + " &#eb4034&lChunk borders off &#ffffff| &#3ee6ddClick to toggle ", prefix.replace("&6", "&#eb9534")));
				BorderListener.toggled.remove(p.getUniqueId());
				BorderListener.baseLocate.remove(p.getUniqueId());
				BorderListener.spawnLocate.remove(p.getUniqueId());
				BorderListener.playerLocate.remove(p.getUniqueId());
				return true;
			}
			return true;
		}

		if (length == 1) {

			return true;
		}

		if (length == 2) {
			if (args[0].equalsIgnoreCase("-f")) {
				if (args[1].equalsIgnoreCase("base")) {
					if (DefaultClan.action.getClanID(p.getUniqueId()) != null) {
						if (!BorderListener.baseLocate.contains(p.getUniqueId())) {
							BorderListener.baseLocate.add(p.getUniqueId());
							BorderListener.spawnLocate.remove(p.getUniqueId());
							BorderListener.playerLocate.remove(p.getUniqueId());
							if (!BorderListener.toggled.containsKey(p.getUniqueId())) {
								msg.build(BorderListener.coToggle(DefaultClan.action.getPrefix().replace("&6", "&#eb9534&l") + " &#3ee67c&lChunk borders on &#ffffff| &#3ee6ddClick to toggle ", prefix.replace("&6", "&#eb9534")));
								BorderTask.run(p).cancelAfter(140).repeatReal(1, 40);
								BorderListener.toggled.put(p.getUniqueId(), true);
							}
							msg.send("&6&oBase flag has been enabled.");
							msg.send("&7You are now locating your clans base location.");
						} else {
							BorderListener.baseLocate.remove(p.getUniqueId());
							msg.send("&c&oBase flag has been disabled.");
						}
					} else {
						DefaultClan.action.sendMessage(p, DefaultClan.action.notInClan());
					}
					return true;
				}
				if (args[1].equalsIgnoreCase("player")) {
					if (!BorderListener.playerLocate.contains(p.getUniqueId())) {
						BorderListener.playerLocate.add(p.getUniqueId());
						BorderListener.spawnLocate.remove(p.getUniqueId());
						BorderListener.baseLocate.remove(p.getUniqueId());
						if (!BorderListener.toggled.containsKey(p.getUniqueId())) {
							msg.build(BorderListener.coToggle(DefaultClan.action.getPrefix().replace("&6", "&#eb9534&l") + " &#3ee67c&lChunk borders on &#ffffff| &#3ee6ddClick to toggle ", prefix.replace("&6", "&#eb9534")));
							BorderTask.run(p).cancelAfter(140).repeatReal(1, 40);
							BorderListener.toggled.put(p.getUniqueId(), true);
						}
						msg.send("&6&oPlayer flag has been enabled.");
						msg.send("&7You are now locating any player within 500 blocks");
					} else {
						BorderListener.playerLocate.remove(p.getUniqueId());
						msg.send("&c&oPlayer flag has been disabled.");
					}
					return true;
				}
				if (args[1].equalsIgnoreCase("spawn")) {
					if (!BorderListener.spawnLocate.contains(p.getUniqueId())) {
						BorderListener.spawnLocate.add(p.getUniqueId());
						BorderListener.playerLocate.remove(p.getUniqueId());
						BorderListener.baseLocate.remove(p.getUniqueId());
						if (!BorderListener.toggled.containsKey(p.getUniqueId())) {
							msg.build(BorderListener.coToggle(DefaultClan.action.getPrefix().replace("&6", "&#eb9534&l") + " &#3ee67c&lChunk borders on &#ffffff| &#3ee6ddClick to toggle ", prefix.replace("&6", "&#eb9534")));
							BorderTask.run(p).cancelAfter(140).repeatReal(1, 40);
							BorderListener.toggled.put(p.getUniqueId(), true);
						}
						msg.send("&6&oSpawn flag has been enabled.");
						msg.send("&7You are now locating the spawn location.");
					} else {
						BorderListener.spawnLocate.remove(p.getUniqueId());
						msg.send("&c&oSpawn flag has been disabled.");
					}
					return true;
				}
			}
			return true;
		}

		return true;
	}

	@Override
	public boolean console(CommandSender sender, String label, String[] args) {
		return false;
	}

	@Override
	public List<String> tab(Player player, String label, String[] args) {
		return TabCompletion.build(getLabel()).forArgs(args).level(1).completeAt(getLabel()).filter(() -> Collections.singletonList("territory")).collect()
				.level(2).completeAt(1).require(getLabel(), 0).filter(() -> Collections.singletonList("-f")).collect()
				.level(3).completeAt(2).require(getLabel(), 0).filter(() -> Arrays.asList("base", "player", "spawn")).collect().get(args.length);
	}
}
