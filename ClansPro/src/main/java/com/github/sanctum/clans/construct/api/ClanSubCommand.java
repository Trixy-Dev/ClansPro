package com.github.sanctum.clans.construct.api;

import com.github.sanctum.labyrinth.library.Message;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ClanSubCommand {

	private List<String> ALIASES = Collections.emptyList();

	private String NOPERMISSION = "&cYou don't have permission <permission>";

	private String PERMISSION;

	private final String LABEL;

	public ClanSubCommand(String label) {
		this.LABEL = label;
	}

	public String getLabel() {
		return this.LABEL;
	}

	public void setNoPermissionMessage(String message) {
		this.NOPERMISSION = message;
	}

	public void setPermission(String permission) {
		this.PERMISSION = permission;
	}

	public void setAliases(List<String> ALIASES) {
		this.ALIASES = ALIASES;
	}

	public String getPermissions() {
		return this.PERMISSION;
	}

	public boolean testPermission(Player target) {
		if (this.PERMISSION != null && !this.PERMISSION.isEmpty()) {
			if (target.hasPermission(this.PERMISSION)) {
				return true;
			} else {
				Message.form(target).send(this.NOPERMISSION);
				return false;
			}
		} else {
			return true;
		}
	}

	public List<String> getAliases() {
		return this.ALIASES;
	}

	public abstract boolean player(Player player, String label, String[] args);

	public abstract boolean console(CommandSender sender, String label, String[] args);

	public abstract List<String> tab(Player player, String label, String[] args);

}
