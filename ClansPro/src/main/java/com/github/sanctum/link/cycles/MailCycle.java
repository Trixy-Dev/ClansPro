package com.github.sanctum.link.cycles;

import com.github.sanctum.clans.ClansPro;
import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.clans.util.StringLibrary;
import com.github.sanctum.clans.util.events.command.CommandHelpInsertEvent;
import com.github.sanctum.clans.util.events.command.CommandInsertEvent;
import com.github.sanctum.clans.util.events.command.TabInsertEvent;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.formatting.string.PaginatedAssortment;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.TextLib;
import com.github.sanctum.link.ClanVentBus;
import com.github.sanctum.link.CycleList;
import com.github.sanctum.link.EventCycle;
import com.github.sanctum.mail.GiftBox;
import com.github.sanctum.mail.GiftObject;
import com.github.sanctum.mail.MailBox;
import com.github.sanctum.mail.MailObject;
import com.github.sanctum.mail.controller.MailListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MailCycle extends EventCycle {

	@Override
	public boolean persist() {
		return ClansAPI.getData().getEnabled("Addon." + getName() + ".enabled");
	}

	@Override
	public HUID getId() {
		return super.getId();
	}

	@Override
	public String getName() {
		return "Mail";
	}

	@Override
	public String getDescription() {
		return "A new pro addon! Send mail & gifts to other clans!";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String[] getAuthors() {
		return new String[]{"Hempfest"};
	}

	@Override
	public void onLoad() {

		register(new MailListener());

	}

	private List<String> helpMenu() {
		return new ArrayList<>(Arrays.asList("&8|&b)&6&o /clan &7mail view", "&8|&b)&6&o /clan &7mail list", "&8|&b)&6&o /clan &7mail send", "&8|&b)&6&o /clan &7mail read"));
	}

	@Override
	public void onEnable() {

		ClanVentBus.subscribe(CommandHelpInsertEvent.class, Vent.Priority.HIGH, (e, subscription) -> {

			EventCycle cycle = CycleList.getAddon("Mail");

			if (cycle != null && !cycle.isActive()) {
				subscription.remove();
				return;
			}

			e.insert("&7|&e)&6&o /clan &fmail", "&7|&e)&6&o /clan &fgift");

		});


		ClanVentBus.subscribe(CommandInsertEvent.class, Vent.Priority.HIGH, (e, subscription) -> {

			EventCycle cycle = CycleList.getAddon("Mail");

			if (cycle != null && !cycle.isActive()) {
				subscription.remove();
				return;
			}

			Player p = e.getSender();
			String[] args = e.getArgs();
			StringLibrary u = e.stringLibrary();
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("gift")) {
					// need clan name
					u.sendMessage(p, "&a&oSend a clan the item you currently hold.");
					u.sendMessage(p, "&c&oInvalid usage: &6/clan &8gift &7<clanName>");
					e.setReturn(true);
					return;
				}
				if (args[0].equalsIgnoreCase("gifts")) {
					if (ClansAPI.getInstance().isInClan(p.getUniqueId())) {
						if (GiftBox.getGiftBox(ClansAPI.getInstance().getClan(p.getUniqueId())) != null) {
							GiftBox box = GiftBox.getGiftBox(ClansAPI.getInstance().getClan(p.getUniqueId()));
							List<String> array = new ArrayList<>();

							for (String s : box.getSenders()) {
								if (!array.contains(s)) {
									array.add(s);
								}
							}
							PaginatedAssortment assortment = new PaginatedAssortment(p, array);

							if (Bukkit.getVersion().contains("1.16")) {
								assortment.setListTitle("&7&m------------&7&l[&3&oGifts&7&l]&7&m------------");
								assortment.setNormalText("&aGift from ");
								assortment.setHoverText("&#787674%s");
								assortment.setHoverTextMessage("&7Click to view gifts from &6%s");
								assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
							} else {
								assortment.setListTitle("&7&m------------&7&l[&3&oGifts&7&l]&7&m------------");
								assortment.setNormalText("&aGift from ");
								assortment.setHoverText("&b%s");
								assortment.setHoverTextMessage("&7Click to view gifts from &6%s");
								assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
							}
							assortment.setLinesPerPage(10);
							assortment.setNavigateCommand("c gifts");
							assortment.setCommandToRun("c gifts %s");
							assortment.exportFancy(1);
						} else {
							u.sendMessage(p, "&c&oYour clan has no gifts to collect.");
							e.setReturn(true);
							return;
						}
					} else {
						u.sendMessage(p, u.notInClan());
						e.setReturn(true);
						return;
					}
					e.setReturn(true);
					return;
				}
				if (args[0].equalsIgnoreCase("mail")) {
					if (!ClansAPI.getInstance().isInClan(p.getUniqueId())) {
						u.sendMessage(p, u.notInClan());
						e.setReturn(true);
						return;
					}
					PaginatedAssortment assortment = new PaginatedAssortment(p, helpMenu());

					assortment.setListTitle("&7&m------------&7&l[&6&oMail Help&7&l]&7&m------------");
					assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					assortment.setLinesPerPage(10);
					assortment.setNavigateCommand("c mail");
					assortment.setCommandToRun("c mail list %s");
					assortment.export(1);
					e.setReturn(true);
					return;
				}
			}
			if (args.length == 2) {

				if (args[0].equalsIgnoreCase("gifts")) {
					if (ClansAPI.getInstance().isInClan(p.getUniqueId())) {
						if (GiftBox.getGiftBox(ClansAPI.getInstance().getClan(p.getUniqueId())) != null) {
							try {
								if (GiftBox.getGiftBox(ClansAPI.getInstance().getClan(p.getUniqueId())) != null) {
									GiftBox box = GiftBox.getGiftBox(ClansAPI.getInstance().getClan(p.getUniqueId()));
									List<String> array = new ArrayList<>();

									for (String s : box.getSenders()) {
										if (!array.contains(s)) {
											array.add(s);
										}
									}
									PaginatedAssortment assortment = new PaginatedAssortment(p, array);

									if (Bukkit.getVersion().contains("1.16")) {
										assortment.setListTitle("&7&m------------&7&l[&3&oGifts&7&l]&7&m------------");
										assortment.setNormalText("&aGift from ");
										assortment.setHoverText("&#787674%s");
										assortment.setHoverTextMessage("&7Click to view gifts from &6%s");
										assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
									} else {
										assortment.setListTitle("&7&m------------&7&l[&3&oGifts&7&l]&7&m------------");
										assortment.setNormalText("&aGift from ");
										assortment.setHoverText("&b%s");
										assortment.setHoverTextMessage("&7Click to view gifts from &6%s");
										assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
									}
									assortment.setLinesPerPage(10);
									assortment.setNavigateCommand("c gifts");
									assortment.setCommandToRun("c gifts %s");
									assortment.exportFancy(Integer.parseInt(args[1]));
								} else {
									u.sendMessage(p, "&c&oYour clan has no gifts to collect.");
									e.setReturn(true);
									return;
								}
							} catch (NumberFormatException ex) {
								if (ClansAPI.getInstance().getClanID(args[1]) == null) {
									u.sendMessage(p, u.clanUnknown(args[1]));
									e.setReturn(true);
									return;
								}

								Clan target = ClansAPI.getInstance().getClan(ClansAPI.getInstance().getClanID(args[1]));

								GiftBox box = GiftBox.getGiftBox(ClansAPI.getInstance().getClan(p.getUniqueId()));
								List<String> array = box.getInboxByClan(target.getName()).stream().map(GiftObject::getItem).map(ItemStack::getType).map(Material::name).collect(Collectors.toList());
								PaginatedAssortment assortment = new PaginatedAssortment(p, array);

								if (array.isEmpty()) {
									u.sendMessage(p, "&c&oYour clan has no gifts from " + target.getName());
									e.setReturn(true);
									return;
								}

								if (Bukkit.getVersion().contains("1.16")) {
									assortment.setListTitle("&7&m------------&7&l[&3&oGifts from " + target.getName() + "&7&l]&7&m------------");
									assortment.setNormalText("&aGift: ");
									assortment.setHoverText("&#787674%s");
									assortment.setHoverTextMessage("&7Click to retrieve gift &6%s");
									assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
								} else {
									assortment.setListTitle("&7&m------------&7&l[&3&oGifts from " + target.getName() + "&7&l]&7&m------------");
									assortment.setNormalText("&aGift: ");
									assortment.setHoverText("&b%s");
									assortment.setHoverTextMessage("&7Click to retrieve gift &6%s");
									assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
								}
								assortment.setLinesPerPage(10);
								assortment.setNavigateCommand("c gifts " + target.getName());
								assortment.setCommandToRun("c retrieve " + target.getName() + " %s");
								assortment.exportFancy(1);
							}

						} else {
							u.sendMessage(p, "&c&oYour clan has no gifts to collect.");
							e.setReturn(true);
							return;
						}
					}
					e.setReturn(true);
					return;
				}

				if (args[0].equalsIgnoreCase("gift")) {
					if (ClansAPI.getInstance().getClanID(args[1]) == null) {
						u.sendMessage(p, u.clanUnknown(args[1]));
						e.setReturn(true);
						return;
					}

					Clan target = ClansAPI.getInstance().getClan(ClansAPI.getInstance().getClanID(args[1]));


					if (target.getId().toString().equals(ClansAPI.getInstance().getClan(p.getUniqueId()).getId().toString())) {
						u.sendMessage(p, "&c&oYou cannot send stuff to your own clan.");
						e.setReturn(true);
						return;
					}


					if (ClansAPI.getInstance().isInClan(p.getUniqueId())) {
						if (GiftBox.getGiftBox(target) != null) {
							GiftBox box = GiftBox.getGiftBox(target);
							if (box.getInboxByClan(ClansAPI.getInstance().getClan(p.getUniqueId()).getName()).stream().map(GiftObject::getItem).map(ItemStack::getType).collect(Collectors.toList()).contains(p.getInventory().getItemInMainHand().getType())) {
								u.sendMessage(p, "&c&oA gift of this caliber has already been sent to the clan, try something else.");
								e.setReturn(true);
								return;
							}
							if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
								u.sendMessage(p, "&c&oYou must be holding something to send as a gift.");
								e.setReturn(true);
								return;
							}
							if (e.getUtil().getRankPower(p.getUniqueId()) < ClansAPI.getData().getInt("Addons.Mail.gift.clearance")) {
								e.getUtil().sendMessage(p, e.stringLibrary().noClearance());
								return;
							}
							box.receive(new GiftObject(ClansAPI.getInstance().getClan(p.getUniqueId()).getName(), p.getInventory().getItemInMainHand()));
						} else {
							if (e.getUtil().getRankPower(p.getUniqueId()) < ClansAPI.getData().getInt("Addons.Mail.gift.clearance")) {
								e.getUtil().sendMessage(p, e.stringLibrary().noClearance());
								return;
							}
							GiftBox box = new GiftBox(target);
							box.receive(new GiftObject(ClansAPI.getInstance().getClan(p.getUniqueId()).getName(), p.getInventory().getItemInMainHand()));
						}
						p.getInventory().getItemInMainHand().setAmount(0);
						u.sendMessage(p, "&3&oYour gift was sent directly to the clan.");
						target.broadcast("&b&oWe have received a new gift.");
					} else {
						u.sendMessage(p, u.notInClan());
						e.setReturn(true);
						return;
					}
					e.setReturn(true);
					return;
				}

				if (args[0].equalsIgnoreCase("mail")) {
					if (!ClansAPI.getInstance().isInClan(p.getUniqueId())) {
						u.sendMessage(p, u.notInClan());
						e.setReturn(true);
						return;
					}
					if (args[1].equalsIgnoreCase("read")) {
						u.sendMessage(p, "&c&oInvalid usage: &8&o/clan mail read &7<clanName> <topic>");
						e.setReturn(true);
						return;
					}

					if (args[1].equalsIgnoreCase("send")) {
						u.sendMessage(p, "&c&oInvalid usage: &8&o/clan mail send &7<clanName> <topic> <context...>");
						e.setReturn(true);
						return;
					}
					if (args[1].equalsIgnoreCase("view")) {

						if (MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())) != null) {
							List<String> array = new ArrayList<>();

							for (String s : MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())).getMailList()) {
								if (!array.contains(s)) {
									array.add(s);
								}
							}

							PaginatedAssortment assortment = new PaginatedAssortment(p, array);

							if (Bukkit.getVersion().contains("1.16")) {
								assortment.setListTitle("&7&m------------&7&l[&6&oInbox&7&l]&7&m------------")
										.setNormalText("&aMessage(s) from ")
										.setHoverText("&#787674%s")
										.setHoverTextMessage("&7Click to view mail from &6%s")
										.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
							} else {
								assortment.setListTitle("&7&m------------&7&l[&6&oInbox&7&l]&7&m------------")
										.setNormalText("&aMessage(s) from ")
										.setHoverText("&b%s")
										.setHoverTextMessage("&7Click to view mail from &6%s")
										.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
							}
							assortment.setLinesPerPage(10)
									.setNavigateCommand("c mail view")
									.setCommandToRun("c mail list %s")
									.exportFancy(1);
						} else {
							new MailBox(ClansAPI.getInstance().getClan(p.getUniqueId()));
							u.sendMessage(p, "&6&oA new clan mail box was built.");
						}
						e.setReturn(true);
						return;
					}
					try {
						Integer.parseInt(args[1]);
					} catch (NumberFormatException ex) {
						u.sendMessage(p, "&c&oUnknown page number.");
						e.setReturn(true);
						return;
					}
					new PaginatedAssortment(p, helpMenu())
							.setListTitle("&7&m------------&7&l[&6&oMail Help&7&l]&7&m------------")
							.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
							.setLinesPerPage(10)
							.setNavigateCommand("c mail")
							.setCommandToRun("c mail list %s")
							.export(Integer.parseInt(args[1]));
					e.setReturn(true);
					return;
				}
			}

			if (args.length == 3) {

				if (args[0].equalsIgnoreCase("gifts")) {
					if (ClansAPI.getInstance().isInClan(p.getUniqueId())) {
						if (GiftBox.getGiftBox(ClansAPI.getInstance().getClan(p.getUniqueId())) != null) {
							try {
								if (ClansAPI.getInstance().getClanID(args[1]) == null) {
									u.sendMessage(p, u.clanUnknown(args[1]));
									e.setReturn(true);
									return;
								}

								Clan target = ClansAPI.getInstance().getClan(ClansAPI.getInstance().getClanID(args[1]));

								GiftBox box = GiftBox.getGiftBox(ClansAPI.getInstance().getClan(p.getUniqueId()));
								List<String> array = new ArrayList<>();

								for (String s : box.getInboxByClan(target.getName()).stream().map(GiftObject::getItem).map(ItemStack::getType).map(Material::name).collect(Collectors.toList())) {
									if (!array.contains(s)) {
										array.add(s);
									}
								}
								PaginatedAssortment assortment = new PaginatedAssortment(p, array);

								if (array.isEmpty()) {
									u.sendMessage(p, "&c&oYour clan has no gifts from " + target.getName());
									e.setReturn(true);
									return;
								}

								if (Bukkit.getVersion().contains("1.16")) {
									assortment.setListTitle("&7&m------------&7&l[&3&oGifts from " + target.getName() + "&7&l]&7&m------------");
									assortment.setNormalText("&aGift: ");
									assortment.setHoverText("&#787674%s");
									assortment.setHoverTextMessage("&7Click to retrieve gift &6%s");
									assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
								} else {
									assortment.setListTitle("&7&m------------&7&l[&3&oGifts from " + target.getName() + "&7&l]&7&m------------");
									assortment.setNormalText("&aGift: ");
									assortment.setHoverText("&b%s");
									assortment.setHoverTextMessage("&7Click to retrieve gift &6%s");
									assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
								}
								assortment.setLinesPerPage(10);
								assortment.setNavigateCommand("c gifts " + target.getName());
								assortment.setCommandToRun("c retrieve " + target.getName() + " %s");
								assortment.exportFancy(Integer.parseInt(args[2]));
							} catch (NumberFormatException ex) {
								u.sendMessage(p, "&c&oUnknown page number.");
								e.setReturn(true);
								return;
							}

						} else {
							u.sendMessage(p, "&c&oYour clan has no gifts to collect.");
							e.setReturn(true);
							return;
						}
					}
					e.setReturn(true);
					return;
				}

				if (args[0].equalsIgnoreCase("retrieve")) {
					if (ClansAPI.getInstance().isInClan(p.getUniqueId())) {
						if (GiftBox.getGiftBox(ClansAPI.getInstance().getClan(p.getUniqueId())) != null) {
							GiftBox box = GiftBox.getGiftBox(ClansAPI.getInstance().getClan(p.getUniqueId()));

							if (ClansAPI.getInstance().getClanID(args[1]) == null) {
								u.sendMessage(p, u.clanUnknown(args[1]));
								e.setReturn(true);
								return;
							}

							Clan target = ClansAPI.getInstance().getClan(ClansAPI.getInstance().getClanID(args[1]));

							if (box.getInboxByClan(target.getName()).isEmpty()) {
								u.sendMessage(p, "&c&oClan " + args[1] + " hasn't sent you any more gifts.");
								e.setReturn(true);
								return;
							}

							if (box.getGiftByMaterial(Items.getMaterial(args[2].toUpperCase())) == null) {
								u.sendMessage(p, "&c&oNo gift was found by this type");
								e.setReturn(true);
								return;
							}

							GiftObject item = box.getGiftByMaterial(Items.getMaterial(args[2]));
							p.getInventory().addItem(item.getItem());
							p.sendMessage(" ");
							Bukkit.getScheduler().scheduleSyncDelayedTask(ClansPro.getInstance(), () -> u.sendMessage(p, "&3&oYou have collected &b" + item.getItem().getType() + " &3&ofrom clan " + item.getSender()), 2);
							u.sendComponent(p, TextLib.getInstance().textRunnable("&7Reopen the clan gift box ", "&f[&bCLICK&f]", "Click to reopen the clan gift box.", "c gifts " + args[1]));
							box.mark(item);
							p.sendMessage(" ");
						} else {
							u.sendMessage(p, "&c&oYour clan has no gifts to collect.");
							e.setReturn(true);
							return;
						}
					} else {
						u.sendMessage(p, u.notInClan());
						e.setReturn(true);
						return;
					}
					e.setReturn(true);
					return;
				}

				if (args[0].equalsIgnoreCase("mail")) {
					if (!ClansAPI.getInstance().isInClan(p.getUniqueId())) {
						u.sendMessage(p, u.notInClan());
						return;
					}
					if (args[1].equalsIgnoreCase("list")) {
						if (MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())) == null) {
							return;
						}
						if (MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())).getInboxByClan(args[2]) == null) {
							return;
						}
						List<String> array = MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())).getInboxByClan(args[2]).stream().map(MailObject::getTopic).collect(Collectors.toList());

						if (array.isEmpty()) {
							u.sendMessage(p, "&c&oYou have no unread messages from " + args[2]);
							e.setReturn(true);
							return;
						}

						PaginatedAssortment assortment = new PaginatedAssortment(p, array);

						if (Bukkit.getVersion().contains("1.16")) {
							assortment.setListTitle("&7&m------------&7&l[&6&oMail from " + args[2] + "&7&l]&7&m------------");
							assortment.setNormalText("");
							assortment.setHoverText("&#787674%s");
							assortment.setHoverTextMessage("&7Click to read message.");
							assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
						} else {
							assortment.setListTitle("&7&m------------&7&l[&6&oMail from " + args[2] + "&7&l]&7&m------------");
							assortment.setNormalText("");
							assortment.setHoverText("&b%s");
							assortment.setHoverTextMessage("&7Click to read message.");
							assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
						}
						assortment.setLinesPerPage(10);
						assortment.setNavigateCommand("c mail list " + args[2]);
						assortment.setCommandToRun("c mail read " + args[2] + " %s");
						assortment.exportFancy(1);
						e.setReturn(true);
						return;
					}

					if (args[1].equalsIgnoreCase("view")) {
						try {
							Integer.parseInt(args[2]);
						} catch (NumberFormatException ex) {
							u.sendMessage(p, "&c&oUnknown page number.");
							e.setReturn(true);
							return;
						}
						if (MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())) != null) {

							List<String> array = new ArrayList<>();

							for (String s : MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())).getMailList()) {
								if (!array.contains(s)) {
									array.add(s);
								}
							}

							PaginatedAssortment assortment = new PaginatedAssortment(p, array);

							if (Bukkit.getVersion().contains("1.16")) {
								assortment.setListTitle("&7&m------------&7&l[&6&oInbox&7&l]&7&m------------");
								assortment.setNormalText("&aMessage(s) from ");
								assortment.setHoverText("&#787674%s");
								assortment.setHoverTextMessage("&7Click to view mail from &6%s");
								assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
							} else {
								assortment.setListTitle("&7&m------------&7&l[&6&oInbox&7&l]&7&m------------");
								assortment.setNormalText("&aMessage(s) from ");
								assortment.setHoverText("&b%s");
								assortment.setHoverTextMessage("&7Click to view mail from &6%s");
								assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
							}
							assortment.setLinesPerPage(10);
							assortment.setNavigateCommand("c mail view");
							assortment.setCommandToRun("c mail list %s");
							assortment.exportFancy(Integer.parseInt(args[2]));
						} else {
							new MailBox(ClansAPI.getInstance().getClan(p.getUniqueId()));
							u.sendMessage(p, "&6&oA new clan mail box was built.");
						}
						e.setReturn(true);
						return;
					}

					if (args[1].equalsIgnoreCase("read")) {
						u.sendMessage(p, "&c&oInvalid usage: &8&o/clan mail read &7<clanName> <topic>");
						e.setReturn(true);
						return;
					}

					if (args[1].equalsIgnoreCase("send")) {
						u.sendMessage(p, "&c&oInvalid usage: &8&o/clan mail send &7<clanName> <topic> <context...>");
						e.setReturn(true);
						return;
					}
					u.sendMessage(p, u.commandUnknown("clan"));
					e.setReturn(true);
					return;
				}
			}

			if (args.length > 3) {
				if (args[0].equalsIgnoreCase("mail")) {
					if (!ClansAPI.getInstance().isInClan(p.getUniqueId())) {
						u.sendMessage(p, u.notInClan());
						return;
					}
					if (args[1].equalsIgnoreCase("list")) {
						try {
							Integer.parseInt(args[3]);
						} catch (NumberFormatException ex) {
							u.sendMessage(p, "&c&oUnknown page number.");
							e.setReturn(true);
							return;
						}
						if (MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())) == null) {
							return;
						}
						if (MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())).getInboxByClan(args[2]) == null) {
							return;
						}
						List<String> array = MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())).getInboxByClan(args[2]).stream().map(MailObject::getTopic).collect(Collectors.toList());
						PaginatedAssortment assortment = new PaginatedAssortment(p, array);

						if (Bukkit.getVersion().contains("1.16")) {
							assortment.setListTitle("&7&m------------&7&l[&6&oMail from " + args[2] + "&7&l]&7&m------------");
							assortment.setNormalText("");
							assortment.setHoverText("&#787674%s");
							assortment.setHoverTextMessage("&7Click to read message.");
							assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
						} else {
							assortment.setListTitle("&7&m------------&7&l[&6&oMail from " + args[2] + "&7&l]&7&m------------");
							assortment.setNormalText("");
							assortment.setHoverText("&b%s");
							assortment.setHoverTextMessage("&7Click to read message.");
							assortment.setListBorder("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
						}
						assortment.setLinesPerPage(10);
						assortment.setNavigateCommand("c mail list " + args[2]);
						assortment.setCommandToRun("c mail read " + args[2] + " %s");
						assortment.exportFancy(Integer.parseInt(args[3]));
						e.setReturn(true);
						return;
					}
					if (args[1].equalsIgnoreCase("read")) {
						if (MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())) == null) {
							u.sendMessage(p, "&c&oWe don't have a mail box.");
							return;
						}
						if (MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId())).getInboxByClan(args[2]) == null) {
							u.sendMessage(p, "&c&oWe don't have a mail box for the given clan.");
							return;
						}

						String clanID = ClansAPI.getInstance().getClanID(args[2]);

						if (clanID == null) {
							u.sendMessage(p, "&c&oClan " + args[2] + " not found.");
							return;
						}

						MailBox box = MailBox.getMailBox(ClansAPI.getInstance().getClan(p.getUniqueId()));
						MailObject mail = box.getMailByTopic(args[3]);
						if (box.getMailByTopic(args[3]) == null) {
							// mail not found.
							u.sendMessage(p, "&c&oMail not found.");
							e.setReturn(true);
							return;
						}
						p.sendMessage(" ");
						u.sendMessage(p, mail.getTopic() + " :\n" + mail.getContext());
						p.sendMessage(" ");
						u.sendMessage(p, "&e&oMessage marked as read.");
						u.sendComponent(p, TextLib.getInstance().textRunnable("&7Reopen the clan inbox ", "&f[&bCLICK&f]", "Click to reopen the clan inbox.", "c mail list " + args[2]));
						box.markRead(mail);
						e.setReturn(true);
						return;
					}
					if (args[1].equalsIgnoreCase("send")) {
						String name = args[2];
						String clanID = e.getUtil().getClanID(name);
						if (clanID == null) {
							u.sendMessage(p, "&c&oClan " + name + " not found.");
							return;
						}

						if (clanID.equals(ClansAPI.getInstance().getClan(p.getUniqueId()).getId().toString())) {
							u.sendMessage(p, "&c&oYou cannot send stuff to your own clan.");
							e.setReturn(true);
							return;
						}


						StringBuilder builder = new StringBuilder();
						for (int i = 4; i < args.length; i++) {
							builder.append(args[i]).append(" ");
						}
						String result = builder.substring(0, builder.toString().length() - 1);
						Clan target = ClansAPI.getInstance().getClan(clanID);
						MailBox box = MailBox.getMailBox(target);
						if (box == null) {
							// no target mail box make one a send mail
							box = new MailBox(target);
						}

						if (box.getInboxByClan(ClansAPI.getInstance().getClan(p.getUniqueId()).getName()).stream().map(MailObject::getTopic).collect(Collectors.toList()).contains(args[3])) {
							u.sendMessage(p, "&c&oMail with this topic has already been sent.");
							e.setReturn(true);
							return;
						}

						box.sendMail(new MailObject(ClansAPI.getInstance().getClan(p.getUniqueId()).getName(), args[3], result));
						u.sendMessage(p, "Mail sent to clan " + target.getName() + " with topic " + args[3] + " and context : " + result);
						target.broadcast("&e&oNew incoming mail from clan &6" + ClansAPI.getInstance().getClan(p.getUniqueId()).getName());
						e.setReturn(true);
						return;
					}
					u.sendMessage(p, u.commandUnknown("clan"));
					e.setReturn(true);
				}

			}

		});

		ClanVentBus.subscribe(TabInsertEvent.class, Vent.Priority.HIGH, (e, subscription) -> {

			EventCycle cycle = CycleList.getAddon("Mail");

			if (cycle != null && !cycle.isActive()) {
				subscription.remove();
				return;
			}

			List<String> args = Arrays.asList("mail", "gift", "gifts", "retrieve");
			for (String arg : args) {
				if (!e.getArgs(1).contains(arg)) {
					e.add(1, arg);
				}
			}
			if (e.getCommandArgs()[0].equalsIgnoreCase("mail")) {
				List<String> args2 = Arrays.asList("read", "view", "send", "list");
				for (String arg : args2) {
					if (!e.getArgs(2).contains(arg)) {
						e.add(2, arg);
					}
				}
			}

		});

	}

	@Override
	public void onDisable() {

	}
}
