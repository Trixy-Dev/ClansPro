package com.github.sanctum.clans.construct.extra;

import com.github.sanctum.clans.construct.ClanAssociate;
import com.github.sanctum.clans.construct.DefaultClan;
import com.github.sanctum.clans.construct.api.ClansAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreTag {

	public static Team getTeam(Player player) {
		Team result = null;
		Scoreboard scoreboard = player.getScoreboard();
		ClanAssociate associate = ClansAPI.getInstance().getAssociate(player).orElse(null);
		if (associate != null && associate.isValid()) {
			result = scoreboard.getTeam(associate.getClan().getId().toString());
		}
		return result;
	}

	public static void set(Player player, String prefix) {
		ClanAssociate associate = ClansAPI.getInstance().getAssociate(player).orElse(null);
		Scoreboard scoreboard = player.getScoreboard();

		if (associate == null) {
			return;
		}

		if (getTeam(player) == null) {
			scoreboard.registerNewTeam(associate.getClan().getId().toString());
			set(player, prefix);
		} else {
			Team team = getTeam(player);
			team.setPrefix(DefaultClan.action.color(prefix));
			team.setDisplayName(associate.getClan().getName());
			team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
			team.addEntry(player.getName());
			if (scoreboard.getObjective("showhealth") == null) {
				if (ClansAPI.getData().getEnabled("Clans.nametag-prefix.show-health")) {
					Objective h = scoreboard.registerNewObjective("showhealth", Criterias.HEALTH, ChatColor.DARK_RED + "❤");
					h.setDisplaySlot(DisplaySlot.BELOW_NAME);
				}
			}
		}
	}

	public static void update(Player player, String prefix) {
		ClanAssociate associate = ClansAPI.getInstance().getAssociate(player).orElse(null);

		if (associate == null) {
			return;
		}
		if (getTeam(player) != null) {
			Team team = getTeam(player);
			team.setPrefix(DefaultClan.action.color(prefix));
			team.setDisplayName(associate.getClan().getName());
			team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
		}

	}

	public static void remove(Player player) {
		Scoreboard scoreboard = player.getScoreboard();
		try {
			if (getTeam(player) != null) {
				ClanAssociate associate = ClansAPI.getInstance().getAssociate(player).orElse(null);

				if (associate == null) {
					return;
				}
				Team team = getTeam(player);
				if (!team.getEntries().isEmpty()) {
					if (team.getEntries().contains(player.getName())) {
						team.removeEntry(player.getName());
					}
				} else {
					team.unregister();
				}
			} else {
				Team team;
				if (scoreboard.getTeam(player.getName()) != null) {
					team = scoreboard.getTeam(player.getName());
				} else {
					team = scoreboard.registerNewTeam(player.getName());
				}
				team.unregister();
			}
		} catch (IllegalArgumentException e) {
			Team team;
			if (scoreboard.getTeam(player.getName()) != null) {
				team = scoreboard.getTeam(player.getName());
			} else {
				team = scoreboard.registerNewTeam(player.getName());
			}
			team.unregister();
		}
	}

}
