package org.starloco.locos.command;


import org.starloco.locos.area.map.entity.House;

import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Maitre; // pour .maitre
import org.starloco.locos.client.other.Party;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.Database; // Pour .banque
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.*;			// Ajouté pour .fmcac
import org.starloco.locos.util.lang.Lang;
import org.starloco.locos.fight.spells.*; // Ajouté pour .fmcac
import org.starloco.locos.object.GameObject; // Ajouté pour .exo
import org.starloco.locos.object.ObjectTemplate; // Ajouté pour .jetmax

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

public class CommandPlayer {

    public final static String canal = "Général";
    

//    public void apply(String packet) {  // Ajouté pour commande .level
//    	String msg = packet.substring(2);
//    	String[] infos = msg.split(" ");  
    
//    	if (infos.length == 0) return;
//    	String command = infos[0];
//    }
    

    	

    public static boolean analyse(Player player, String msg) {
        if (msg.charAt(0) == '.' && msg.charAt(1) != '.') {
            if (command(msg, "all") && msg.length() > 5) {
                if (player.isInPrison())
                    return true;
                if (player.noall) {
                    SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 0), "C35617");
                    return true;
                }
                if(player.getGroupe() == null && System.currentTimeMillis() - player.getGameClient().timeLastTaverne < 10000) {
                    SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 2).replace("#1", String.valueOf(10 - ((System.currentTimeMillis() - player.getGameClient().timeLastTaverne) / 1000))), "C35617");
                    return true;
                }

                player.getGameClient().timeLastTaverne = System.currentTimeMillis();

                String prefix = "<font color='#C35617'>[" + (new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()))) + "] (" + canal + ") (" + getNameServerById(Main.serverId) + ") <b><a href='asfunction:onHref,ShowPlayerPopupMenu," + player.getName() + "'>" + player.getName() + "</a></b>";

                Logging.getInstance().write("AllMessage", "[" + (new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()))) + "] : " + player.getName() + " : " + msg.substring(5, msg.length() - 1));

                final String message = "Im116;" + prefix + "~" + msg.substring(5, msg.length() - 1).replace(";", ":").replace("~", "").replace("|", "").replace("<", "").replace(">", "") + "</font>";

                World.world.getOnlinePlayers().stream().filter(p -> !p.noall).forEach(p -> p.send(message));
                Main.exchangeClient.send("DM" + player.getName() + "|" + getNameServerById(Main.serverId) + "|" + msg.substring(5, msg.length() - 1).replace("\n", "").replace("\r", "").replace(";", ":").replace("~", "").replace("|", "").replace("<", "").replace(">", "") + "|");
                return true;
            } else if (command(msg, "noall")) {
                if (player.noall) {
                    player.noall = false;
                    SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 3), "C35617");
                } else {
                    player.noall = true;
                    SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 4), "C35617");
                }
                return true;
            } else if (command(msg, "staff")) {
                String message = Lang.get(player, 5);
                boolean vide = true;
                for (Player target : World.world.getOnlinePlayers()) {
                    if (target == null)
                        continue;
                    if (target.getGroupe() == null || target.isInvisible())
                        continue;

                    message += "\n- <b><a href='asfunction:onHref,ShowPlayerPopupMenu," + target.getName() + "'>[" + target.getGroupe().getName() + "] " + target.getName() + "</a></b>";
                    vide = false;
                }
                if (vide)
                    message = Lang.get(player, 6);
                SocketManager.GAME_SEND_MESSAGE(player, message);
                return true;
            }  else if (command(msg, "house")) {
                String message = "";
                if(!msg.contains("all")) {
                    message = "L'id de la maison la plus proche est : ";
                    short lstDist = 999;
                    House nearest = null;
                    for (House house : World.world.getHouses().values()) {
                        if (house.getMapId() == player.getCurMap().getId()) {
                            short dist = (short) PathFinding.getDistanceBetween(player.getCurMap(), house.getCellId(), player.getCurCell().getId());
                            if (dist < lstDist) {
                                nearest = house;
                                lstDist = dist;
                            }
                        }
                    }
                    if (nearest != null) message += nearest.getId();
                } else {
                    for (House house : World.world.getHouses().values()) {
                        if (house.getMapId() == player.getCurMap().getId()) {
                            message += "Maison " + house.getId() + " | cellId : " + house.getId();
                        }
                    }
                    if(message.isEmpty()) message = "Aucune maison sur cette carte.";
                }
                SocketManager.GAME_SEND_MESSAGE(player, message);
                return true;
            } else if (command(msg, "deblo")) {//180min
                if (player.isInPrison())
                    return true;
                if (player.cantTP())
                    return true;
                if (player.getFight() != null)
                    return true;
                if(player.getCurCell().isWalkable(true)) {
                    SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 7));
                    return true;
                }
                player.teleport(player.getCurMap().getId(), player.getCurMap().getRandomFreeCellId());
                SocketManager.GAME_SEND_MESSAGE(player, "Vous avez été débloqué!" , "009900");
                return true;
            } else if (command(msg, "infos")) {
                long uptime = System.currentTimeMillis()
                        - Config.getInstance().startTime;
                int jour = (int) (uptime / (1000 * 3600 * 24));
                uptime %= (1000 * 3600 * 24);
                int hour = (int) (uptime / (1000 * 3600));
                uptime %= (1000 * 3600);
                int min = (int) (uptime / (1000 * 60));
                uptime %= (1000 * 60);
                int sec = (int) (uptime / (1000));
                int nbPlayer = Main.gameServer.getClients().size();
                int nbPlayerIp = Main.gameServer.getPlayersNumberByIp();

                String mess = Lang.get(player, 8).replace("#1", String.valueOf(jour)).replace("#2", String.valueOf(hour)).replace("#3", String.valueOf(min)).replace("#4", String.valueOf(sec));
                if (nbPlayer > 0)
                    mess +=  Lang.get(player, 9).replace("#1", String.valueOf(nbPlayer));
                if (nbPlayerIp > 0)
                    mess +=  Lang.get(player, 10).replace("#1", String.valueOf(nbPlayerIp));
                SocketManager.GAME_SEND_MESSAGE(player, mess);
                return true;
            } else if(command(msg, "banque")) { // commande .banque (Code de Nairelos)
                if (player.getFight() != null){
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous ne pouvez pas utiliser cette commande en combat.");
                    return true;

                }
                boolean ok = false;
                        ok = true;

                if (ok) {
                    Database.getStatics().getPlayerData().update(player);
                    if (player.getDeshonor() >= 1) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "183");
                        return true;
                    }
                    final int cost = player.getBankCost();
                    if (cost > 0) {
                        final long playerKamas = player.getKamas();
                        final long kamasRemaining = playerKamas - cost;
                        final long bankKamas = player.getAccount().getBankKamas();
                        final long totalKamas = bankKamas + playerKamas;
                        if (kamasRemaining < 0)//Si le joueur n'a pas assez de kamas SUR LUI pour ouvrir la banque
                        {
                            if (bankKamas >= cost) {
                                player.setBankKamas(bankKamas - cost); //On modifie les kamas de la banque
                            } else if (totalKamas >= cost) {
                                player.setKamas(0); //On puise l'entièreté des kamas du joueurs. Ankalike ?
                                player.setBankKamas(totalKamas - cost); //On modifie les kamas de la banque
                                SocketManager.GAME_SEND_STATS_PACKET(player);
                                SocketManager.GAME_SEND_Im_PACKET(player, "020;"
                                        + playerKamas);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE_SERVER(player, "10|"
                                        + cost);
                                return true;
                            }
                        } else
                        //Si le joueur a les kamas sur lui on lui retire directement
                        {
                            player.setKamas(kamasRemaining);
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                            SocketManager.GAME_SEND_Im_PACKET(player, "020;"
                                    + cost);
                        }
                    }
                    SocketManager.GAME_SEND_ECK_PACKET(player.getGameClient(), 5, "");
                    SocketManager.GAME_SEND_EL_BANK_PACKET(player);
                    player.setAway(true);
                    player.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_BANK, 0));
                }
                return true;
            } else if (command(msg, "maitre")) {
        		// 1) Création du groupe par IP joueur
        		if (player.isInPrison() || player.getFight() != null)
                    return true;

                World.world.getOnlinePlayers().stream().filter(p -> !p.equals(player) && p.getParty() == null && p.getAccount().getCurrentIp().equals(player.getAccount().getCurrentIp()) && p.getFight() == null && !p.isInPrison()).forEach(p -> {
                    if(player.getParty() == null) {
                        Party party = new Party(player, p);
                        SocketManager.GAME_SEND_GROUP_CREATE(player.getGameClient(), party);
                        SocketManager.GAME_SEND_PL_PACKET(player.getGameClient(), party);
                        SocketManager.GAME_SEND_GROUP_CREATE(p.getGameClient(), party);
                        SocketManager.GAME_SEND_PL_PACKET(p.getGameClient(), party);
                        player.setParty(party);
                        p.setParty(party);
                        SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(player.getGameClient(), party);
                        SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(p.getGameClient(), party);
                    } else {
                        SocketManager.GAME_SEND_GROUP_CREATE(p.getGameClient(), player.getParty());
                        SocketManager.GAME_SEND_PL_PACKET(p.getGameClient(), player.getParty());
                        SocketManager.GAME_SEND_PM_ADD_PACKET_TO_GROUP(player.getParty(), p);
                        player.getParty().addPlayer(p);;
                        p.setParty(player.getParty());
                        SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(p.getGameClient(), player.getParty());
                        SocketManager.GAME_SEND_PR_PACKET(p);
                    }
                });
                
                
                // 2) Création du maitre et des esclaves par le groupe, et donc par l'IP
                if(player.cantTP()) return true;

                final Party party = player.getParty();

                if (party == null) {
                    player.sendMessage("Action impossible. Vérifiez que vos joueurs sont connectés ou ne font pas déjà partie d'un groupe.");
                    return true;
                }

                final List<Player> players = player.getParty().getPlayers();

                if (!party.getChief().getName().equals(player.getName())) {
                    player.sendMessage("Vous n'êtes pas le chef du groupe.");
                    return true;
                }

                if (msg.length() <= 8 && party.getMaster() != null) {
                	SocketManager.GAME_SEND_MESSAGE(player, "Vous venez de désactiver le mode <b>maître</b>.", "009900"); //la
                    players.stream().filter(follower -> follower != party.getMaster())
                            .forEach(follower -> SocketManager.GAME_SEND_MESSAGE(follower, "Vous ne suivez plus <b>" + party.getMaster().getName() + "</b>."));
                    party.setMaster(null);
                    // ajout .maitre
                   	player.get_maitre()._esclaves.forEach(esclave -> esclave.setEsclave(false));
               		player.set_maitre(null);
               		// --
                    return true;
                }

                Player target = player;

                if (msg.length() > 8) {
                    String name = msg.substring(8, msg.length() - 1);
                    target = World.world.getPlayerByName(name);
                }

                if (target == null) {
                    player.sendMessage("Le joueur est introuvable.");
                    return true;
                }
                if (target.getParty() == null || !target.getParty().getPlayers().contains(player)) {
                    player.sendMessage("Tu n'es pas dans le groupe du joueur indiqué.");
                    return true;
                }

                party.setMaster(target);                

                final String message = "Vous suivez désormais <b>" + target.getName() + "</b>.";
                for (Player follower : players)
                    if(follower != target)
                        SocketManager.GAME_SEND_MESSAGE(follower, message, "009900");
                party.teleportAllEsclaves();
                party.moveAllPlayersToMaster(null);
                SocketManager.GAME_SEND_MESSAGE(target, "Vous êtes désormais le <b>maître</b>.", "009900");
                
                //SocketManager.GAME_SEND_MESSAGE(target, "Vous êtes désormais le maitre.", "009900");
                /*
                		+ "Vos <b>"+player.get_maitre().getEsclaves().size()+"</b> joueurs vont maintenant vous suivre."
                				+ " Faites <b>.tp</b> pour les faire venir s'ils se perdent!", "009900"); //la
                */
                
                // TEST
                /*
           		if(player.isEsclave() == true) {
           			SocketManager.GAME_SEND_MESSAGE(player, "Action impossible, vous êtes un héro.");
   					return true;
           		}
                   */
                //Enlever ce qui se répète
               	if(player.get_maitre() != null)
               	{
               	
                   	player.get_maitre()._esclaves.forEach(esclave -> esclave.setEsclave(false));
               		player.set_maitre(null);
               		//SocketManager.GAME_SEND_MESSAGE(player, "Commande désactivée." , "009900");
               		
               	} else if(player.get_maitre() != null && player.isEsclave() == true)
                   	{
                   	
                       	player.get_maitre()._esclaves.forEach(esclave -> esclave.setEsclave(false));
                   		player.set_maitre(null);
                   		//SocketManager.GAME_SEND_MESSAGE(player, "Commande désactivée." , "009900");
                   		
                   	}else{
       				player.set_maitre(new Maitre(player));
       				
       				if(player.get_maitre() != null){
       					//SocketManager.GAME_SEND_MESSAGE(player, "Commande activée, vous avez <b>"+player.get_maitre().getEsclaves().size()+"</b> héros. Faites <b>.tp</b> pour téléporter votre escouade." , "009900");
       					SocketManager.GAME_SEND_MESSAGE(target, "Vos <b>" + player.get_maitre().getEsclaves().size() + "</b> joueurs vont maintenant vous suivre.", "009900");
       					SocketManager.GAME_SEND_MESSAGE(target, " Faites <b>.tp</b> pour les faire revenir s'ils se perdent!", "009900");
       				}else
       					SocketManager.GAME_SEND_MESSAGE(player, "Aucun héro n'a été trouvé.");
       				
               	}
                return true;
            } else if (command(msg, "groupe")) {
                if (player.isInPrison() || player.getFight() != null)
                    return true;

                World.world.getOnlinePlayers().stream().filter(p -> !p.equals(player) && p.getParty() == null && p.getAccount().getCurrentIp().equals(player.getAccount().getCurrentIp()) && p.getFight() == null && !p.isInPrison()).forEach(p -> {
                    if(player.getParty() == null) {
                        Party party = new Party(player, p);
                        SocketManager.GAME_SEND_GROUP_CREATE(player.getGameClient(), party);
                        SocketManager.GAME_SEND_PL_PACKET(player.getGameClient(), party);
                        SocketManager.GAME_SEND_GROUP_CREATE(p.getGameClient(), party);
                        SocketManager.GAME_SEND_PL_PACKET(p.getGameClient(), party);
                        player.setParty(party);
                        p.setParty(party);
                        SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(player.getGameClient(), party);
                        SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(p.getGameClient(), party);
                    } else {
                        SocketManager.GAME_SEND_GROUP_CREATE(p.getGameClient(), player.getParty());
                        SocketManager.GAME_SEND_PL_PACKET(p.getGameClient(), player.getParty());
                        SocketManager.GAME_SEND_PM_ADD_PACKET_TO_GROUP(player.getParty(), p);
                        player.getParty().addPlayer(p);;
                        p.setParty(player.getParty());
                        SocketManager.GAME_SEND_ALL_PM_ADD_PACKET(p.getGameClient(), player.getParty());
                        SocketManager.GAME_SEND_PR_PACKET(p);
                    }
                });

                return true;
            } else if(command(msg, "tp")) {     // commande .tp pour téléporter ses esclaves (Code de Nairelos)
                if(System.currentTimeMillis() - player.getGameClient().timeLastTP < 5000) {
                    SocketManager.GAME_SEND_MESSAGE(player, "Cette commande est disponible toutes les 5 secondes.");
                    return true;
                }
                /*
                if (player.getCurMap().haveMobFix()) {
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous ne pouvez pas utiliser cette commande en donjon.");
                    return true;
                }
                if (player.isInDungeon()) {
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous ne pouvez pas utiliser cette commande en donjon.");
                    return true;
                }
                */
                if (player.getFight() != null){
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous ne pouvez pas utiliser cette commande en combat.");
                    return true;
                }
                if (player.getExchangeAction() != null){
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous ne pouvez pas utiliser cette commande car vous êtes occupé.");
                    return true;
                }
    		    if(player.get_maitre() != null){
    		    player.getGameClient().timeLastTP = System.currentTimeMillis();
    			player.get_maitre().teleportAllEsclaves();
    			SocketManager.GAME_SEND_MESSAGE(player, "Vous avez téléporté <b>"+player.get_maitre().getEsclaves().size()+"</b> joueurs." , "009900");
    			}
    		    else
    			SocketManager.GAME_SEND_MESSAGE(player, "Aucun joueur n'a été trouvé pour la téléportation.");
    			return true;
        	} else if(command(msg, "parcho")) //Commande .parcho (Code adapté de Cyon Emu)
			{
			if(player.getFight() != null)
			return true;

			@SuppressWarnings("unused")
			String element = "";
			int nbreElement = 0;
			if(player.getStats().getEffect(125) < 101)
			{
				player.getStats().addOneStat(125, 101 - player.getStats().getEffect(125));
				element += "vitalité";
				nbreElement++;
			}

			if(player.getStats().getEffect(124) < 101)
			{
				player.getStats().addOneStat(124, 101 - player.getStats().getEffect(124));
				if(nbreElement == 0)
						element += "sagesse";
					else
						element += ", sagesse";
						nbreElement++;
			}

			if(player.getStats().getEffect(118) < 101)
			{
				player.getStats().addOneStat(118, 101 - player.getStats().getEffect(118));
				if(nbreElement == 0)
					element += "force";
				else
					element += ", force";
					nbreElement++;
			}

			if(player.getStats().getEffect(126) < 101)
			{
				player.getStats().addOneStat(126, 101 - player.getStats().getEffect(126));
				if(nbreElement == 0)
						element += "intelligence";
					else
						element += ", intelligence";
						nbreElement++;
			}

			if(player.getStats().getEffect(119) < 101)
			{
				player.getStats().addOneStat(119, 101 - player.getStats().getEffect(119));
				if(nbreElement == 0)
					element += "agilité";
				else
					element += ", agilité";
					nbreElement++;
			}

			if(player.getStats().getEffect(123) < 101)
			{
				player.getStats().addOneStat(123, 101 - player.getStats().getEffect(123));
				if(nbreElement == 0)
					element += "chance";
				else
					element += ", chance";
					nbreElement++;
			}

			if(nbreElement == 0)
			{
			SocketManager.GAME_SEND_MESSAGE(player, "Vous avez déjà plus de 100 partout !");
			}
			else
			{
			SocketManager.GAME_SEND_STATS_PACKET(player);
			//SocketManager.GAME_SEND_MESSAGE(player, "Vous êtes parcho 101 en " + element + " !");
			SocketManager.GAME_SEND_MESSAGE(player, "Vous avez été parcho <b>101</b> dans tous les éléments !" , "009900");
			}
			return true;
			} else if(command(msg, "spellmax")) // Commande .spellmax (Adapté de Cyon Emu)
			{
				int lvlMax = player.getLevel() > 99 ? (6) : (5);
                boolean changed = false;
                for (Spell.SortStats sort : player.getSorts()) {
                    if (sort.getLevel() == lvlMax) {
                        continue;
                    }
                    player.learnSpell(sort.getSpellID(), lvlMax, false,
                            false, false);
                    changed = true;
                }
                if (changed) {
                    SocketManager.GAME_SEND_SPELL_LIST(player);
                    SocketManager.GAME_SEND_MESSAGE(player,
                            "Tous vos sorts ont été montés au niveau <b>" + lvlMax + "</b>." , "009900");
                }
                return true;
        	} else if(command(msg, "vie")) //Commande .vie (Adapté de Cyon Emu)
			{
				int count = 100;
				Player perso = player;
				int recupPDV = perso.getMaxPdv() - perso.getCurPdv();
				int newPDV = (perso.getMaxPdv() * count) / 100;
				perso.setPdv(newPDV);
				if(perso.isOnline())
				{
				SocketManager.GAME_SEND_STATS_PACKET(perso);
				}
				SocketManager.GAME_SEND_MESSAGE(player, "Vous avez récupéré <b>" + recupPDV + "</b> points de vie." , "009900");
				return true;
			} else if(command(msg, "start")) //Commande .start
			   {
				if (player.isInPrison()) 
					return true;
				if (player.cantTP()) 
					return true;
				if (player.getFight() != null) 
					return true;
				Player perso = player;
		        if (player.getFight() != null) return true;
		        perso.teleport((short) 164, 298);
		        SocketManager.GAME_SEND_MESSAGE(player, "Vous avez été téléporté à la map <b>start</b>." , "009900");
		        return true;
			   } else if(command(msg, "poutch")) //Commande .poutch
			   {  
				if (player.isInPrison()) 
					return true;
				if (player.cantTP()) 
					return true;
				if (player.getFight() != null) 
					return true;
				Player perso = player;
		        if (player.getFight() != null) return true;
		        perso.teleport((short) 534, 372);
		        SocketManager.GAME_SEND_MESSAGE(player, "Vous avez été téléporté au <b>poutch</b>." , "009900");
		        return true;
			   } else if(command(msg, "phoenix")) //Commande .phoenix
			   {
				if (player.isInPrison()) 
				return true;
				if (player.cantTP()) 
					return true;
				if (player.getFight() != null) 
					return true;
				Player perso = player;
		        if (player.getFight() != null) return true;
		        perso.teleport((short) 8534, 267);
		        SocketManager.GAME_SEND_MESSAGE(player, "Vous avez été téléporté vers un <b>phoenix</b>." , "009900");
		        return true;
			   } else if(command(msg, "enclos")) //Commande .enclos
			   {
				if (player.isInPrison()) 
					return true;
				if (player.cantTP()) 
					return true;
				if (player.getFight() != null) 
					return true;
				Player perso = player;
		        if (player.getFight() != null) return true;
		        perso.teleport((short) 8747, 633);
		        SocketManager.GAME_SEND_MESSAGE(player, "Vous avez été téléporté à un <b>enclos</b>." , "009900");
		        return true;
			   } else if(command(msg, "pvp")) //Commande .pvp
			   {
				if (player.isInPrison()) 
					return true;
				if (player.cantTP()) 
					return true;
				if (player.getFight() != null) 
					return true;
				Player perso = player;
		        if (player.getFight() != null) return true;
		        perso.teleport((short) 952, 295);
		        SocketManager.GAME_SEND_MESSAGE(player, "Vous avez été téléporté à la map <b>pvp</b>." , "009900");
		        return true;
			   } else if(command(msg, "pvm")) //Commande .pvm
			   {
				if (player.isInPrison()) 
					return true;
				if (player.cantTP()) 
					return true;
				if (player.getFight() != null) 
					return true;
				Player perso = player;
		        if (player.getFight() != null) return true;
		        perso.teleport((short) 957, 223);
		        SocketManager.GAME_SEND_MESSAGE(player, "Vous avez été téléporté à la map <b>pvm</b>." , "009900");
		        return true;
			   } else if (command(msg, "fmcac")) { // Commande .fmcac (eau, terre, feu, air) (Adapté de Cyon Emu)
				   // Cette commande est bug sur le deuxième jet neutre de suite ex : Épée Kukri Kura
				   // Le deuxième jet est fm à 100% de son jet initial.
                   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ARME); // obj
                   if (player.getFight() != null) {
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Action impossible : vous ne devez pas être en combat.");
                       return true;
                   } else if (obj == null) {
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Action impossible : vous ne portez pas d'arme");
                       return true;
                   }
                   boolean containNeutre = false;
                   for (SpellEffect effect : obj.getEffects()) {
                       if (effect.getEffectID() == 100 || effect.getEffectID() == 95) {
                           containNeutre = true;
                       }
                   }
                   if (!containNeutre) {
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Action impossible : votre arme n'a pas de dégats neutre");
                       return true;
                   }

                   String answer;

                   try {
                       answer = msg.substring(7, msg.length() - 1);
                   } catch (Exception e) {
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Action impossible : vous n'avez pas spécifié l'élément (air, feu, terre, eau) qui remplacera les dégats/vols de vies neutres");
                       return true;
                   }

                   if (!answer.equalsIgnoreCase("air") && !answer.equalsIgnoreCase(
                           "terre") && !answer.equalsIgnoreCase("feu") && !answer.equalsIgnoreCase(
                           "eau")) {
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Action impossible : l'élément " + answer + " est incorrect. (Disponible : air, feu, terre, eau)");
                       return true;
                   }
                   
                   // Ajout des 85% du jet (pas 100% debug)
                       for (SpellEffect effect : obj.getEffects()) {
                           if (effect.getEffectID() != 100)
                               continue;
                           String[] infos = effect.getArgs().split(";");
                           try {                      	   
                        	   int coef = 85;  // fm à 85%
                               int min = Integer.parseInt(infos[0], 16);
                               int max = Integer.parseInt(infos[1], 16);
                               int newMin = (min * coef) / 100;
                               int newMax = (max * coef) / 100;
                               if (newMin == 0)
                                   newMin = 1;
                               String newRange = "1d" + (newMax - newMin + 1) + "+"
                                       + (newMin - 1);
                               String newArgs = Integer.toHexString(newMin) + ";"
                                       + Integer.toHexString(newMax) + ";-1;-1;0;"
                                       + newRange;
                               effect.setArgs(newArgs);
                               
                               
                               /*
                               if (effect.getEffectID() != 95)
                                   continue;
                               effect.setArgs(newArgs); // Souci ; les jets sont ok mais restent neutre (Kukri Kura)
                               */
                               
                               for (int i = 0; i < obj.getEffects().size(); i++) {
                                   if (obj.getEffects().get(i).getEffectID() == 100) {
                                       if (answer.equalsIgnoreCase("air")) {
                                           obj.getEffects().get(i).setEffectID(98);
                                       }
                                       if (answer.equalsIgnoreCase("feu")) {
                                           obj.getEffects().get(i).setEffectID(99);
                                       }
                                       if (answer.equalsIgnoreCase("terre")) {
                                           obj.getEffects().get(i).setEffectID(97);
                                       }
                                       if (answer.equalsIgnoreCase("eau")) {
                                           obj.getEffects().get(i).setEffectID(96);
                                       }
                                   }

                                   if (obj.getEffects().get(i).getEffectID() == 95) {                             	   
                                       if (answer.equalsIgnoreCase("air")) {
                                           obj.getEffects().get(i).setEffectID(93);
                                       }
                                       if (answer.equalsIgnoreCase("feu")) {
                                           obj.getEffects().get(i).setEffectID(94);
                                       }
                                       if (answer.equalsIgnoreCase("terre")) {
                                           obj.getEffects().get(i).setEffectID(92);
                                       }
                                       if (answer.equalsIgnoreCase("eau")) {
                                           obj.getEffects().get(i).setEffectID(91);
                                       }
                                   }
                               }
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                       } 
                   long new_kamas = player.getKamas();
                   if (new_kamas < 0) //Ne devrait pas arriver...
                   {
                       new_kamas = 0;
                   }
                   player.setKamas(new_kamas);
                   
                   SocketManager.GAME_SEND_STATS_PACKET(player);
                   // Pour éviter de déco-reco :
                   GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_ARME);
                   int idObjPos = itemPos.getGuid();
                   SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                   SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                   SocketManager.GAME_SEND_STATS_PACKET(player);

                   SocketManager.GAME_SEND_MESSAGE(player,
                           "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été FM avec succès en <b>" + answer + "</b> !" , "009900");
                   return true;
                       
			   } else if (command(msg, "exo")) { // Commande .exo (coiffe ,cape)(pa, pm)
				   
                   if (player.getFight() != null) {
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Action impossible : vous ne devez pas être en combat.");
                       return true;
                   } 
                   String answer;
                   try {
                       answer = msg.substring(5, msg.length() - 1); // 5 = nbr carac après ".exo " (avec espace) 
                   } catch (Exception e) {
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Action impossible : vous n'avez pas spécifié l'item à exo.");
                       return true;
                   }
                   
                   
                   if (!answer.equalsIgnoreCase("coiffe pa") && !answer.equalsIgnoreCase(
                           "coiffe pm") && !answer.equalsIgnoreCase("cape pa") && !answer.equalsIgnoreCase(
                                   "cape pm") && !answer.equalsIgnoreCase("ceinture pa") && !answer.equalsIgnoreCase(
                                           "ceinture pm") && !answer.equalsIgnoreCase("bottes pa") && !answer.equalsIgnoreCase(
                                                   "bottes pm") && !answer.equalsIgnoreCase("amulette pa") && !answer.equalsIgnoreCase(
                                                           "amulette pm") && !answer.equalsIgnoreCase("anneauG pa") && !answer.equalsIgnoreCase(
                                                                   "anneauG pm") && !answer.equalsIgnoreCase("anneauD pa") && !answer.equalsIgnoreCase(
                                                                           "anneauD pm") && !answer.equalsIgnoreCase("cac pa") && !answer.equalsIgnoreCase(
                                                                                   "cac pm") ) {
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Action impossible : l'option " + answer + " est incomplète ou incorrecte. "
                               		+ "(Disponible : coiffe pa, coiffe pm, cape pa , cape pm, ceinture pa, ceinture pm,"
                               		+ " bottes pa, bottes pm, amulette pa, amulette pm, anneauG pa, anneauG pm, anneauD pa, anneauD pm, "
                               		+ "cac pa, cac pm)");
                       return true;
                   }
                   
                   
                   String statsObjectFmPa = "6f";
                   String statsObjectFmPm = "80";
                   int statsAdd = 1;
                   boolean negative = false;
                   
                   // Cas d'une coiffe pa
                   if (answer.equalsIgnoreCase("coiffe pa")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_COIFFE);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas de coiffe.");
                           return true;
                       }
                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPa);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette coiffe possède déjà 1 PA, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPm);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPm);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette cape est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PA
                       
                       String statsStr = obj.parseFMStatsString(statsObjectFmPa, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPa
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_COIFFE);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);

                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   // Cas coiffe pm
                   if (answer.equalsIgnoreCase("coiffe pm")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_COIFFE);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas de coiffe.");
                           return true;
                       }

                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPm);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette coiffe possède déjà 1 PM, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPa);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPa);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette coiffe est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PM
                       String statsStr = obj.parseFMStatsString(statsObjectFmPm, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPm
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_COIFFE);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   // Cas d'une cape pa
                   if (answer.equalsIgnoreCase("cape pa")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_CAPE);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas de cape.");
                           return true;
                       }
                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPa);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette cape possède déjà 1 PA, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPm);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPm);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette cape est déjà exo.");
                    	   
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   " Pensez à vous deco/reco pour voir les changements !");
                    	   
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PA
                       String statsStr = obj.parseFMStatsString(statsObjectFmPa, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPa
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_CAPE);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   // Cas cape pm
                   if (answer.equalsIgnoreCase("cape pm")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_CAPE);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas de cape.");
                           return true;
                       }

                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPm);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette cape possède déjà 1 PM, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPa);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPa);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette cape est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PM
                       String statsStr = obj.parseFMStatsString(statsObjectFmPm, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPm
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_CAPE);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                // Cas de bottes pa
                   if (answer.equalsIgnoreCase("bottes pa")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_BOTTES);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas de bottes.");
                           return true;
                       }
                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPa);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Ces bottes possèdent déjà 1 PA, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPm);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPm);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Ces bottes sont déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PA
                       String statsStr = obj.parseFMStatsString(statsObjectFmPa, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPa
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_BOTTES);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                   // Cas bottes pm
                   if (answer.equalsIgnoreCase("bottes pm")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_BOTTES);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas de bottes.");
                           return true;
                       }

                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPm);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Ces bottes possèdent déjà 1 PM, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPa);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPa);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Ces bottes sont déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PM
                       String statsStr = obj.parseFMStatsString(statsObjectFmPm, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPm
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_BOTTES);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                // Cas ceinture pa
                   if (answer.equalsIgnoreCase("ceinture pa")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_CEINTURE);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas de ceinture.");
                           return true;
                       }
                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPa);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette ceinture possède déjà 1 PA, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPm);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPm);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette ceinture est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PA
                       String statsStr = obj.parseFMStatsString(statsObjectFmPa, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPa
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_CEINTURE);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                   
                // Cas ceinture pm
                   if (answer.equalsIgnoreCase("ceinture pm")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_CEINTURE);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas de ceinture.");
                           return true;
                       }

                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPm);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette ceinture possède déjà 1 PM, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPa);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPa);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette ceinture est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PM
                       String statsStr = obj.parseFMStatsString(statsObjectFmPm, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPm
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_CEINTURE);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                // Cas amulette pa
                   if (answer.equalsIgnoreCase("amulette pa")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_AMULETTE);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas d'amulette.");
                           return true;
                       }
                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPa);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette amulette possède déjà 1 PA, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPm);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPm);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette amulette est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PA
                       String statsStr = obj.parseFMStatsString(statsObjectFmPa, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPa
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_AMULETTE);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                   // Cas amulette pm
                   if (answer.equalsIgnoreCase("amulette pm")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_AMULETTE);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas d'amulette.");
                           return true;
                       }

                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPm);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette amulette possède déjà 1 PM, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPa);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPa);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cette amulette est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PM
                       String statsStr = obj.parseFMStatsString(statsObjectFmPm, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPm
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_AMULETTE);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                   // Cas anneau gauche pa
                   if (answer.equalsIgnoreCase("anneauG pa")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ANNEAU1);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas d'anneau gauche.");
                           return true;
                       }
                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPa);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cet anneau possède déjà 1 PA, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPm);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPm);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cet anneau est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PA
                       String statsStr = obj.parseFMStatsString(statsObjectFmPa, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPa
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_ANNEAU1);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                // Cas anneau gauche pm
                   if (answer.equalsIgnoreCase("anneauG pm")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ANNEAU1);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas d'anneau gauche.");
                           return true;
                       }

                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPm);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cet anneau possède déjà 1 PM, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPa);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPa);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cet anneau est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PM
                       String statsStr = obj.parseFMStatsString(statsObjectFmPm, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPm
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_ANNEAU1);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                // Cas anneau droit pa
                   if (answer.equalsIgnoreCase("anneauD pa")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ANNEAU2);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas d'anneau droit.");
                           return true;
                       }
                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPa);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cet anneau possède déjà 1 PA, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPm);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPm);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cet anneau est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PA
                       String statsStr = obj.parseFMStatsString(statsObjectFmPa, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPa
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_ANNEAU2);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                // Cas anneau droit pm
                   if (answer.equalsIgnoreCase("anneauD pm")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ANNEAU2);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas d'anneau droit.");
                           return true;
                       }

                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPm);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cet anneau possède déjà 1 PM, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPa);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPa);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Cet anneau est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PM
                       String statsStr = obj.parseFMStatsString(statsObjectFmPm, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPm
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_ANNEAU2);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                // Cas cac pa
                   if (answer.equalsIgnoreCase("cac pa")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ARME);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas de CAC.");
                           return true;
                       }
                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPa);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Ce CAC possède déjà 1 PA, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPm);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPm);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Ce CAC est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PA
                       String statsStr = obj.parseFMStatsString(statsObjectFmPa, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPa
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_ARME);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
                   
                   
                    // Cac pm
                   if (answer.equalsIgnoreCase("cac pm")) {
                	   
                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ARME);
                       if (obj == null) {
                           SocketManager.GAME_SEND_MESSAGE(player,
                                   "Action impossible : vous ne portez pas de CAC.");
                           return true;
                       }

                       // Pour éviter d'avoir des items 2Pa/2Pm
                       int currentStats = viewActualStatsItem(obj, statsObjectFmPm);
                       if(currentStats == 1) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Ce CAC possède déjà 1 PM, action impossible.");
                    	   return true;
                       }
                       // Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base
                       int currentStats2 = viewActualStatsItem(obj, statsObjectFmPa);
                       
                       if(currentStats2 == 1) {
                    	   int baseStats = viewBaseStatsItem(obj, statsObjectFmPa);
                    	   if(baseStats == 0) {
                    	   SocketManager.GAME_SEND_MESSAGE(player,
                                   "Ce CAC est déjà exo.");
                    	   return true;
                    	   }
                       }
                	   // Ajout de l'exo PM
                       String statsStr = obj.parseFMStatsString(statsObjectFmPm, obj, statsAdd, negative)
                              + ","
                              + statsObjectFmPm
                              + "#"
                              + Integer.toHexString(statsAdd)
                              + "#0#0#0d0+"
                              + statsAdd;
                       obj.clearStats();
                       obj.refreshStatsObjet(statsStr);
                       // Pour éviter de déco-reco :
                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_ARME);
                       int idObjPos = itemPos.getGuid();
                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                       SocketManager.GAME_SEND_STATS_PACKET(player);
                       
                       SocketManager.GAME_SEND_MESSAGE(player,
                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !" , "009900");
                       
                	   return true;
                   }
			   	} else if (command(msg, "jetmax")) { // Commande .jetmax (coiffe ,cape, amulette...)
					   
	                   if (player.getFight() != null) {
	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Action impossible : vous ne devez pas être en combat.");
	                       return true;
	                   } 
	                   String answer;
	                   try {
	                   answer = msg.substring(8, msg.length() - 1); // 5 = nbr carac après ".jetmax " (avec espace) 
	                   } catch (Exception e) {
	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Action impossible : vous n'avez pas spécifié l'item à améliorer.");
	                       return true;
	                   }
	                   
	                   
	                   if (!answer.equalsIgnoreCase("coiffe") && !answer.equalsIgnoreCase(
	                           "cape") && !answer.equalsIgnoreCase(
	                                   "ceinture") && !answer.equalsIgnoreCase(
	                                           "bottes") && !answer.equalsIgnoreCase(
	                                                   "amulette") && !answer.equalsIgnoreCase(
	                                                           "anneauG") && !answer.equalsIgnoreCase(
	                                                                   "anneauD") && !answer.equalsIgnoreCase(
	                                                                           "cac") && !answer.equalsIgnoreCase(
	    	                                                                           "familier") && !answer.equalsIgnoreCase(
	    	    	                                                                           "dofus") && !answer.equalsIgnoreCase(
	    	    	    	                                                                           "bouclier") && !answer.equalsIgnoreCase(
	    	    	    	    	                                                                           "all")) {
	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Action impossible : l'option " + answer + " est incomplète ou incorrecte. "
	                               		+ "(Disponible : all, coiffe, cape, ceinture,"
	                               		+ " bottes, amulette, anneauG, anneauD, "
	                               		+ "cac, familier, dofus, bouclier.)");
	                       return true;
	                   }           
	                   // Cas d'une coiffe
	                   if (answer.equalsIgnoreCase("coiffe")) {
	                	   
	                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_COIFFE);
	                       if (obj == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : vous ne portez pas de coiffe.");
	                           return true;
	                       }
	                       Stats maxStats = obj.generateNewStatsFromTemplate(obj.getTemplate().getStrTemplate(), true);
	                       obj.setStats(maxStats);
	                       // Pour éviter de déco-reco :
	                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_COIFFE);
	                       int idObjPos = itemPos.getGuid();
	                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
	                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);
	        

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été modifié avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;
	                   }
	                   // Cas d'une cape
	                   if (answer.equalsIgnoreCase("cape")) {
	                	   
	                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_CAPE);
	                       if (obj == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : vous ne portez pas de cape.");
	                           return true;
	                       }
	                       Stats maxStats = obj.generateNewStatsFromTemplate(obj.getTemplate().getStrTemplate(), true);
	                       obj.setStats(maxStats);
	                       // Pour éviter de déco-reco :
	                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_CAPE);
	                       int idObjPos = itemPos.getGuid();
	                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
	                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);
	        

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été modifié avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;
	                   }          
	                // Cas de bottes
	                   if (answer.equalsIgnoreCase("bottes")) {
	                	   
	                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_BOTTES);
	                       if (obj == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : vous ne portez pas de bottes.");
	                           return true;
	                       }
	                       Stats maxStats = obj.generateNewStatsFromTemplate(obj.getTemplate().getStrTemplate(), true);
	                       obj.setStats(maxStats);
	                       // Pour éviter de déco-reco :
	                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_BOTTES);
	                       int idObjPos = itemPos.getGuid();
	                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
	                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été modifié avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;
	                   }
	                // Cas ceinture
	                   if (answer.equalsIgnoreCase("ceinture")) {
	                	   
	                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_CEINTURE);
	                       if (obj == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : vous ne portez pas de ceinture.");
	                           return true;
	                       }
	                       Stats maxStats = obj.generateNewStatsFromTemplate(obj.getTemplate().getStrTemplate(), true);
	                       obj.setStats(maxStats);
	                       // Pour éviter de déco-reco :
	                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_CEINTURE);
	                       int idObjPos = itemPos.getGuid();
	                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
	                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été modifié avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;
	                   }
	                // Cas amulette
	                   if (answer.equalsIgnoreCase("amulette")) {
	                	   
	                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_AMULETTE);
	                       if (obj == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : vous ne portez pas d'amulette.");
	                           return true;
	                       }
	                       Stats maxStats = obj.generateNewStatsFromTemplate(obj.getTemplate().getStrTemplate(), true);
	                       obj.setStats(maxStats);
	                       // Pour éviter de déco-reco :
	                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_AMULETTE);
	                       int idObjPos = itemPos.getGuid();
	                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
	                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été modifié avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;
	                   }
	                   // Cas anneau gauche
	                   if (answer.equalsIgnoreCase("anneauG")) {
	                	   
	                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ANNEAU1);
	                       if (obj == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : vous ne portez pas d'anneau gauche.");
	                           return true;
	                       }
	                       Stats maxStats = obj.generateNewStatsFromTemplate(obj.getTemplate().getStrTemplate(), true);
	                       obj.setStats(maxStats);
	                       // Pour éviter de déco-reco :
	                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_ANNEAU1);
	                       int idObjPos = itemPos.getGuid();
	                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
	                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été modifié avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;
	                   }
	                // Cas anneau droit
	                   if (answer.equalsIgnoreCase("anneauD")) {
	                	   
	                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ANNEAU2);
	                       if (obj == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : vous ne portez pas d'anneau droit.");
	                           return true;
	                       }
	                       Stats maxStats = obj.generateNewStatsFromTemplate(obj.getTemplate().getStrTemplate(), true);
	                       obj.setStats(maxStats);
	                       // Pour éviter de déco-reco :
	                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_ANNEAU2);
	                       int idObjPos = itemPos.getGuid();
	                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
	                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été modifié avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;
	                   }
	                // Cas du cac
	                   if (answer.equalsIgnoreCase("cac")) {
	                	   
	                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ARME);
	                       if (obj == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : vous ne portez pas de CAC.");
	                           return true;
	                       }
	                       Stats maxStats = obj.generateNewStatsFromTemplate(obj.getTemplate().getStrTemplate(), true);
	                       obj.setStats(maxStats);
	                       // Pour éviter de déco-reco :
	                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_ARME);
	                       int idObjPos = itemPos.getGuid();
	                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
	                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été modifié avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;
	                   }
		                // Cas du familier
	                   if (answer.equalsIgnoreCase("familier")) {
	                	   
	                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_FAMILIER);
	                       if (obj == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : vous ne portez pas de familier.");
	                           return true;
	                       }
	                       Stats maxStats = obj.generateNewStatsFromTemplate(obj.getTemplate().getStrTemplate(), true);
	                       obj.setStats(maxStats);
	                       // Pour éviter de déco-reco :
	                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_FAMILIER);
	                       int idObjPos = itemPos.getGuid();
	                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
	                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été modifié avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;  
	                   } 
		                // Cas du bouclier
	                   if (answer.equalsIgnoreCase("bouclier")) {
	                	   
	                	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_BOUCLIER);
	                       if (obj == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : vous ne portez pas de bouclier.");
	                           return true;
	                       }
	                       Stats maxStats = obj.generateNewStatsFromTemplate(obj.getTemplate().getStrTemplate(), true);
	                       obj.setStats(maxStats);
	                       // Pour éviter de déco-reco :
	                       GameObject itemPos = player.getObjetByPos(Constant.ITEM_POS_BOUCLIER);
	                       int idObjPos = itemPos.getGuid();
	                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
	                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Votre item : <b>" + obj.getTemplate().getName() + "</b> a été modifié avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;  
	                   
			   		   }
	                // Cas des dofus
	                   if (answer.equalsIgnoreCase("dofus")) {
	                	   
	                	   GameObject obj1 = player.getObjetByPos(Constant.ITEM_POS_DOFUS1);
	                	   GameObject obj2 = player.getObjetByPos(Constant.ITEM_POS_DOFUS2);
	                	   GameObject obj3 = player.getObjetByPos(Constant.ITEM_POS_DOFUS3);
	                	   GameObject obj4 = player.getObjetByPos(Constant.ITEM_POS_DOFUS4);
	                	   GameObject obj5 = player.getObjetByPos(Constant.ITEM_POS_DOFUS5);
	                	   GameObject obj6 = player.getObjetByPos(Constant.ITEM_POS_DOFUS6);
	                       if (obj1 == null && obj2 == null && obj3 == null && obj4 == null && obj5 == null && obj6 == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : il faut porter au moins un dofus.");
	                           return true;
	                       }
	                       // Dofus 1
	                       if(obj1 != null) {
		                       Stats maxStats1 = obj1.generateNewStatsFromTemplate(obj1.getTemplate().getStrTemplate(), true);
		                       obj1.setStats(maxStats1);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos1 = player.getObjetByPos(Constant.ITEM_POS_DOFUS1);
		                       int idObjPos1 = itemPos1.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos1);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos1);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Dofus 2
	                       if(obj2 != null) {
		                       Stats maxStats2 = obj2.generateNewStatsFromTemplate(obj2.getTemplate().getStrTemplate(), true);
		                       obj2.setStats(maxStats2);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos2 = player.getObjetByPos(Constant.ITEM_POS_DOFUS2);
		                       int idObjPos2 = itemPos2.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos2);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos2);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Dofus 3
	                       if(obj3 != null) {
		                       Stats maxStats3 = obj3.generateNewStatsFromTemplate(obj3.getTemplate().getStrTemplate(), true);
		                       obj3.setStats(maxStats3);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos2 = player.getObjetByPos(Constant.ITEM_POS_DOFUS3);
		                       int idObjPos2 = itemPos2.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos2);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos2);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Dofus 4
	                       if(obj4 != null) {
		                       Stats maxStats4 = obj4.generateNewStatsFromTemplate(obj4.getTemplate().getStrTemplate(), true);
		                       obj4.setStats(maxStats4);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos4 = player.getObjetByPos(Constant.ITEM_POS_DOFUS4);
		                       int idObjPos4 = itemPos4.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos4);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos4);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Dofus 5
	                       if(obj5 != null) {
		                       Stats maxStats5 = obj5.generateNewStatsFromTemplate(obj5.getTemplate().getStrTemplate(), true);
		                       obj5.setStats(maxStats5);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos5 = player.getObjetByPos(Constant.ITEM_POS_DOFUS5);
		                       int idObjPos5 = itemPos5.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos5);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos5);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Dofus 6
	                       if(obj6 != null) {
		                       Stats maxStats6 = obj6.generateNewStatsFromTemplate(obj6.getTemplate().getStrTemplate(), true);
		                       obj6.setStats(maxStats6);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos6 = player.getObjetByPos(Constant.ITEM_POS_DOFUS6);
		                       int idObjPos6 = itemPos6.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos6);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos6);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       
	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Tous vos <b>dofus</b> ont été modifiés avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;  
	                   
			   		   }
	                // Cas des dofus
	                   if (answer.equalsIgnoreCase("all")) {
	                	   
	                	   GameObject obj1 = player.getObjetByPos(Constant.ITEM_POS_COIFFE);
	                	   GameObject obj2 = player.getObjetByPos(Constant.ITEM_POS_CAPE);
	                	   GameObject obj3 = player.getObjetByPos(Constant.ITEM_POS_FAMILIER);
	                	   GameObject obj4 = player.getObjetByPos(Constant.ITEM_POS_AMULETTE);
	                	   GameObject obj5 = player.getObjetByPos(Constant.ITEM_POS_CEINTURE);
	                	   GameObject obj6 = player.getObjetByPos(Constant.ITEM_POS_ANNEAU1);
	                	   GameObject obj7 = player.getObjetByPos(Constant.ITEM_POS_ANNEAU2);
	                	   GameObject obj8 = player.getObjetByPos(Constant.ITEM_POS_BOTTES);
	                	   GameObject obj9 = player.getObjetByPos(Constant.ITEM_POS_ARME);
	                	   GameObject obj10 = player.getObjetByPos(Constant.ITEM_POS_BOUCLIER);
	                       if (obj1 == null && obj2 == null && obj3 == null && obj4 == null && obj5 == null && obj6 == null && obj7 == null && obj8 == null && obj9 == null && obj10 == null) {
	                           SocketManager.GAME_SEND_MESSAGE(player,
	                                   "Action impossible : il faut porter au moins un item qui ne soit pas un dofus.");
	                           return true;
	                       }
	                       
	                       // Coiffe
	                       if(obj1 != null ) {
		                       Stats maxStats1 = obj1.generateNewStatsFromTemplate(obj1.getTemplate().getStrTemplate(), true);
		                       obj1.setStats(maxStats1);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos1 = player.getObjetByPos(Constant.ITEM_POS_COIFFE);
		                       int idObjPos1 = itemPos1.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos1);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos1);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Cape
	                       if(obj2 != null ) {
		                       Stats maxStats2 = obj2.generateNewStatsFromTemplate(obj2.getTemplate().getStrTemplate(), true);
		                       obj2.setStats(maxStats2);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos2 = player.getObjetByPos(Constant.ITEM_POS_CAPE);
		                       int idObjPos2 = itemPos2.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos2);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos2);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Familier
	                       if(obj3 != null ) {
		                       Stats maxStats3 = obj3.generateNewStatsFromTemplate(obj3.getTemplate().getStrTemplate(), true);
		                       obj3.setStats(maxStats3);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos3 = player.getObjetByPos(Constant.ITEM_POS_FAMILIER);
		                       int idObjPos3 = itemPos3.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos3);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos3);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Amulette
	                       if(obj4 != null ) {
		                       Stats maxStats4 = obj4.generateNewStatsFromTemplate(obj4.getTemplate().getStrTemplate(), true);
		                       obj4.setStats(maxStats4);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos4 = player.getObjetByPos(Constant.ITEM_POS_AMULETTE);
		                       int idObjPos4 = itemPos4.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos4);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos4);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Ceinture
	                       if(obj5 != null ) {
		                       Stats maxStats5 = obj5.generateNewStatsFromTemplate(obj5.getTemplate().getStrTemplate(), true);
		                       obj5.setStats(maxStats5);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos5 = player.getObjetByPos(Constant.ITEM_POS_CEINTURE);
		                       int idObjPos5 = itemPos5.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos5);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos5);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Anneau 1
	                       if(obj6 != null ) {
		                       Stats maxStats6 = obj6.generateNewStatsFromTemplate(obj6.getTemplate().getStrTemplate(), true);
		                       obj6.setStats(maxStats6);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos6 = player.getObjetByPos(Constant.ITEM_POS_ANNEAU1);
		                       int idObjPos6 = itemPos6.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos6);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos6);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Anneau 2
	                       if(obj7 != null ) {
		                       Stats maxStats7 = obj7.generateNewStatsFromTemplate(obj7.getTemplate().getStrTemplate(), true);
		                       obj7.setStats(maxStats7);
		                       GameObject itemPos7 = player.getObjetByPos(Constant.ITEM_POS_ANNEAU2);
		                       int idObjPos7 = itemPos7.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos7);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos7);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Bottes
	                       if(obj8 != null ) {
		                       Stats maxStats8 = obj8.generateNewStatsFromTemplate(obj8.getTemplate().getStrTemplate(), true);
		                       obj8.setStats(maxStats8);
		                       // Pour éviter de déco-reco :
		                       GameObject itemPos8 = player.getObjetByPos(Constant.ITEM_POS_BOTTES);
		                       int idObjPos8 = itemPos8.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos8);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos8);
	                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Arme
	                       if(obj9 != null ) {
		                       Stats maxStats9 = obj9.generateNewStatsFromTemplate(obj9.getTemplate().getStrTemplate(), true);
		                       obj9.setStats(maxStats9);
		                       GameObject itemPos9 = player.getObjetByPos(Constant.ITEM_POS_ARME);
		                       int idObjPos9 = itemPos9.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos9);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos9);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       // Bouclier
	                       if(obj10 != null ) {
		                       Stats maxStats10 = obj10.generateNewStatsFromTemplate(obj10.getTemplate().getStrTemplate(), true);
		                       obj10.setStats(maxStats10);
		                       GameObject itemPos10 = player.getObjetByPos(Constant.ITEM_POS_BOUCLIER);
		                       int idObjPos10 = itemPos10.getGuid();
		                       SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos10);
		                       SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos10);
		                       SocketManager.GAME_SEND_STATS_PACKET(player);
	                       }
	                       

	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Tous vos <b>items</b> (à l'exception des dofus) ont été modifiés avec les caractéristiques maximales !" , "009900");
	                       
	                	   return true;  
			   	       }
				   	} else if (command(msg, "restat")) { //Commande .restat  (Adapté de Cyon Emu)
				    Player perso = player;
				    if (player.getFight() != null) {
	                       SocketManager.GAME_SEND_MESSAGE(player,
	                               "Action impossible : vous ne devez pas être en combat.");
	                       return true;       
				    }
		            perso.getStats().addOneStat(125, -perso.getStats().getEffect(125));
		            perso.getStats().addOneStat(124, -perso.getStats().getEffect(124));
		            perso.getStats().addOneStat(118, -perso.getStats().getEffect(118));
		            perso.getStats().addOneStat(123, -perso.getStats().getEffect(123));
		            perso.getStats().addOneStat(119, -perso.getStats().getEffect(119));
		            perso.getStats().addOneStat(126, -perso.getStats().getEffect(126));
		            perso.getStatsParcho().getMap().clear();
		            perso.addCapital((perso.getLevel() - 1) * 5 - perso.get_capital());
		            SocketManager.GAME_SEND_STATS_PACKET(perso);
		            SocketManager.GAME_SEND_MESSAGE(player, "Vos caractéristiques ont été réinitialisées.", "009900");
		            return true;
		            } else if (command(msg, "boost vita")) { //Commande .boost vita
		            	// Dans l'ordre du panel carac : Vita:11,Sagesse:12,Force:10,Intel:15,Chance:13,Agi:14
					    Player perso = player;
					    if (player.getFight() != null) {
		                       SocketManager.GAME_SEND_MESSAGE(player,
		                               "Action impossible : vous ne devez pas être en combat.");
		                       return true;  
					    }       
					    // On va checher le nombre de points de carac à utiliser dans la commande
			            String answer;
	                    try {
	                    	answer = msg.substring(12, msg.length() - 1); // 12 carac après .boost vita + espace
	                        int count = 0; // Ce qu'on demande
	                        int code = perso.get_capital(); // Capital du perso
	                        int codeAvant = code;
	                        int caracAvant = player.getStats().getEffect(125); 
							count = Integer.parseInt(answer);
							if(count < 1)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous ne pouvez pas retirer de boost, utilisez la commande  .restat  .");
				            	return true;
				            }
			                if (code < 1) // Si on a plus de point de caractéristique
			                {
			                	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous n'avez plus de point de caractéristique.");
				            	return true;
			                }
							if(count > code)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous demandez plus que votre nombre de point de caractéristiques.");
				            	return true;
				            }		
							// Implémenter
	                        perso.boostStatFixedCount(11, count);	
	                        int codeApres = perso.get_capital();
	                        int codeUtile = codeAvant - codeApres;
	                        int caracApres = player.getStats().getEffect(125);
	                        int caracAjout = caracApres - caracAvant;
	                        String mess = "Vous avez boosté <b>" + caracAjout
	                                + "</b> en <b>Vitalité</b> avec <b>" + codeUtile + "</b> points de caractéristiques."
	                                		+ "\nVous pouvez utiliser la commande .restat en cas d'erreur.";
	                        SocketManager.GAME_SEND_MESSAGE(player, mess, "009900");
	                        return true;
	                    } catch (Exception e) {
	                        // ok
	                    	SocketManager.GAME_SEND_MESSAGE(player, "Valeur incorrecte.");
	                        return true;
	                    }                   
			        } else if (command(msg, "boost sagesse")) { //Commande .boost sagesse
		            	// Dans l'ordre du panel carac : Vita:11,Sagesse:12,Force:10,Intel:15,Chance:13,Agi:14
					    Player perso = player;
					    if (player.getFight() != null) {
		                       SocketManager.GAME_SEND_MESSAGE(player,
		                               "Action impossible : vous ne devez pas être en combat.");
		                       return true;       
					    }
					    // On va checher le nombre de points de carac à utiliser dans la commande
			            String answer;
	                    try {
	                    	answer = msg.substring(15, msg.length() - 1); // 12 carac après .boost vita + espace
	                        int count = 0; // Ce qu'on demande
	                        int code = perso.get_capital(); // Capital du perso
	                        int codeAvant = code;
	                        int caracAvant = player.getStats().getEffect(124); 
							count = Integer.parseInt(answer);
							if(count < 1)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous ne pouvez pas retirer de boost, utilisez la commande  .restat  .");
				            	return true;
				            }
			                if (code < 1) // Si on a plus de point de caractéristique
			                {
			                	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous n'avez plus de point de caractéristique.");
				            	return true;
			                }
							if(count > code)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous demandez plus que votre nombre de point de caractéristiques.");
				            	return true;
				            }		
							// Implémenter
	                        perso.boostStatFixedCount(12, count);	
	                        int codeApres = perso.get_capital();
	                        int codeUtile = codeAvant - codeApres;
	                        int caracApres = player.getStats().getEffect(124); 
	                        int caracAjout = caracApres - caracAvant;
	                        String mess = "Vous avez boosté <b>" + caracAjout
	                                + "</b> en <b>Sagesse</b> avec <b>" + codeUtile + "</b> points de caractéristiques."
	                                		+ "\nVous pouvez utiliser la commande .restat en cas d'erreur.";
	                        SocketManager.GAME_SEND_MESSAGE(player, mess, "009900");
	                        return true;
	                    } catch (Exception e) {
	                        // ok
	                    	SocketManager.GAME_SEND_MESSAGE(player, "Valeur incorrecte.");
	                        return true;
	                    }                   
			        } else if (command(msg, "boost force")) { //Commande .boost force
		            	// Dans l'ordre du panel carac : Vita:11,Sagesse:12,Force:10,Intel:15,Chance:13,Agi:14
					    Player perso = player;
					    if (player.getFight() != null) {
		                       SocketManager.GAME_SEND_MESSAGE(player,
		                               "Action impossible : vous ne devez pas être en combat.");
		                       return true;       
					    }
					    // On va checher le nombre de points de carac à utiliser dans la commande
			            String answer;
	                    try {
	                    	answer = msg.substring(13, msg.length() - 1); // 12 carac après .boost vita + espace
	                        int count = 0; // Ce qu'on demande
	                        int code = perso.get_capital(); // Capital du perso
	                        int codeAvant = code;
	                        int caracAvant = player.getStats().getEffect(118); 
							count = Integer.parseInt(answer);
							if(count < 1)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous ne pouvez pas retirer de boost, utilisez la commande  .restat  .");
				            	return true;
				            }
			                if (code < 1) // Si on a plus de point de caractéristique
			                {
			                	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous n'avez plus de point de caractéristique.");
				            	return true;
			                }
							if(count > code)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous demandez plus que votre nombre de point de caractéristiques.");
				            	return true;
				            }		
							// Implémenter
	                        perso.boostStatFixedCount(10, count);	
	                        int codeApres = perso.get_capital();
	                        int codeUtile = codeAvant - codeApres;
	                        int caracApres = player.getStats().getEffect(118); 
	                        int caracAjout = caracApres - caracAvant;
	                        String mess = "Vous avez boosté <b>" + caracAjout
	                                + "</b> en <b>Force</b> avec <b>" + codeUtile + "</b> points de caractéristiques."
	                                		+ "\nVous pouvez utiliser la commande .restat en cas d'erreur.";
	                        SocketManager.GAME_SEND_MESSAGE(player, mess, "009900");
	                        return true;
	                    } catch (Exception e) {
	                        // ok
	                    	SocketManager.GAME_SEND_MESSAGE(player, "Valeur incorrecte.");
	                        return true;
	                    }                   
			        } else if (command(msg, "boost intel")) { //Commande .boost intel
		            	// Dans l'ordre du panel carac : Vita:11,Sagesse:12,Force:10,Intel:15,Chance:13,Agi:14
					    Player perso = player;
					    if (player.getFight() != null) {
		                       SocketManager.GAME_SEND_MESSAGE(player,
		                               "Action impossible : vous ne devez pas être en combat.");
		                       return true;       
					    }
					    // On va checher le nombre de points de carac à utiliser dans la commande
			            String answer;
	                    try {
	                    	answer = msg.substring(13, msg.length() - 1); // 12 carac après .boost vita + espace
	                        int count = 0; // Ce qu'on demande
	                        int code = perso.get_capital(); // Capital du perso
	                        int codeAvant = code;
	                        int caracAvant = player.getStats().getEffect(126); 
							count = Integer.parseInt(answer);
							if(count < 1)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous ne pouvez pas retirer de boost, utilisez la commande  .restat  .");
				            	return true;
				            }
			                if (code < 1) // Si on a plus de point de caractéristique
			                {
			                	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous n'avez plus de point de caractéristique.");
				            	return true;
			                }
							if(count > code)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous demandez plus que votre nombre de point de caractéristiques.");
				            	return true;
				            }		
							// Implémenter
	                        perso.boostStatFixedCount(15, count);	
	                        int codeApres = perso.get_capital();
	                        int codeUtile = codeAvant - codeApres;
	                        int caracApres = player.getStats().getEffect(126); 
	                        int caracAjout = caracApres - caracAvant;
	                        String mess = "Vous avez boosté <b>" + caracAjout
	                                + "</b> en <b>Intelligence</b> avec <b>" + codeUtile + "</b> points de caractéristiques."
	                                		+ "\nVous pouvez utiliser la commande .restat en cas d'erreur.";
	                        SocketManager.GAME_SEND_MESSAGE(player, mess, "009900");
	                        return true;
	                    } catch (Exception e) {
	                        // ok
	                    	SocketManager.GAME_SEND_MESSAGE(player, "Valeur incorrecte.");
	                        return true;
	                    }                   
			        } else if (command(msg, "boost chance")) { //Commande .boost chance
		            	// Dans l'ordre du panel carac : Vita:11,Sagesse:12,Force:10,Intel:15,Chance:13,Agi:14
					    Player perso = player;
					    if (player.getFight() != null) {
		                       SocketManager.GAME_SEND_MESSAGE(player,
		                               "Action impossible : vous ne devez pas être en combat.");
		                       return true;       
					    }
					    // On va checher le nombre de points de carac à utiliser dans la commande
			            String answer;
	                    try {
	                    	answer = msg.substring(14, msg.length() - 1); // 12 carac après .boost vita + espace
	                        int count = 0; // Ce qu'on demande
	                        int code = perso.get_capital(); // Capital du perso
	                        int codeAvant = code;
	                        int caracAvant = player.getStats().getEffect(123); 
							count = Integer.parseInt(answer);
							if(count < 1)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous ne pouvez pas retirer de boost, utilisez la commande  .restat  .");
				            	return true;
				            }
			                if (code < 1) // Si on a plus de point de caractéristique
			                {
			                	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous n'avez plus de point de caractéristique.");
				            	return true;
			                }
							if(count > code)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous demandez plus que votre nombre de point de caractéristiques.");
				            	return true;
				            }	
							// Implémenter
	                        perso.boostStatFixedCount(13, count);	
	                        int codeApres = perso.get_capital();
	                        int codeUtile = codeAvant - codeApres;
	                        int caracApres = player.getStats().getEffect(123); 
	                        int caracAjout = caracApres - caracAvant;
	                        String mess = "Vous avez boosté <b>" + caracAjout
	                                + "</b> en <b>Chance</b> avec <b>" + codeUtile + "</b> points de caractéristiques."
	                                		+ "\nVous pouvez utiliser la commande .restat en cas d'erreur.";
	                        SocketManager.GAME_SEND_MESSAGE(player, mess, "009900");
	                        return true;
	                    } catch (Exception e) {
	                        // ok
	                    	SocketManager.GAME_SEND_MESSAGE(player, "Valeur incorrecte.");
	                        return true;
	                    }                   
			        } else if (command(msg, "boost agi")) { //Commande .boost
		            	// Dans l'ordre du panel carac : Vita:11,Sagesse:12,Force:10,Intel:15,Chance:13,Agi:14
					    Player perso = player;
					    if (player.getFight() != null) {
		                       SocketManager.GAME_SEND_MESSAGE(player,
		                               "Action impossible : vous ne devez pas être en combat.");
		                       return true;       
					    }
					    // On va checher le nombre de points de carac à utiliser dans la commande
			            String answer;
	                    try {
	                    	answer = msg.substring(11, msg.length() - 1); // 12 carac après .boost vita + espace
	                        int count = 0; // Ce qu'on demande
	                        int code = perso.get_capital(); // Capital du perso
	                        int codeAvant = code;
	                        int caracAvant = player.getStats().getEffect(119); 
							count = Integer.parseInt(answer);
							if(count < 1)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous ne pouvez pas retirer de boost, utilisez la commande  .restat  .");
				            	return true;
				            }
			                if (code < 1) // Si on a plus de point de caractéristique
			                {
			                	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous n'avez plus de point de caractéristique.");
				            	return true;
			                }
							if(count > code)
				            {
				            	SocketManager.GAME_SEND_MESSAGE(player, 
				            			"Vous demandez plus que votre nombre de point de caractéristiques.");
				            	return true;
				            }			
							// Implémenter
	                        perso.boostStatFixedCount(14, count);	
	                        int codeApres = perso.get_capital();
	                        int codeUtile = codeAvant - codeApres;
	                        int caracApres = player.getStats().getEffect(119); 
	                        int caracAjout = caracApres - caracAvant;
	                        String mess = "Vous avez boosté <b>" + caracAjout
	                                + "</b> en <b>Agilité</b> avec <b>" + codeUtile + "</b> points de caractéristiques."
	                                		+ "\nVous pouvez utiliser la commande .restat en cas d'erreur.";
	                        SocketManager.GAME_SEND_MESSAGE(player, mess, "009900");
	                        return true;
	                    } catch (Exception e) {
	                        // ok
	                    	SocketManager.GAME_SEND_MESSAGE(player, "Valeur incorrecte.");
	                        return true;
	                    }                   
			        } else if (command(msg, "level")) { //Commande .level
		            	if (player.getFight() != null) {
		                       SocketManager.GAME_SEND_MESSAGE(player,
		                               "Action impossible : vous ne devez pas être en combat.");
		                       return true;       
					    }
		            String answer;
                    try {
                    	answer = msg.substring(7, msg.length() - 1);
                        int count = 0;
						count = Integer.parseInt(answer);						
						if(count == player.getLevel())
			            {
			            	SocketManager.GAME_SEND_MESSAGE(player, 
			            			"Le level demandé est identique à votre level actuel.");
			            	return true;
			            }
						if(count < player.getLevel())
			            {
			            	SocketManager.GAME_SEND_MESSAGE(player, 
			            			"Vous ne pouvez pas vous donner un niveau inférieur à votre niveau actuel.");
			            	return true;
			            }					
                        if (count < 1)
                            count = 1;
                        if (count > World.world.getExpLevelSize())
                            count = World.world.getExpLevelSize();
                            Player perso = player;
                        if (perso.getLevel() < count) {
                            while (perso.getLevel() < count)
                                perso.levelUp(false, true);
                            if (perso.isOnline()) {
                                SocketManager.GAME_SEND_SPELL_LIST(perso);
                                SocketManager.GAME_SEND_NEW_LVL_PACKET(perso.getGameClient(), perso.getLevel());
                                SocketManager.GAME_SEND_STATS_PACKET(perso);
                            }
                        }
                        String mess = "Vous avez fixé le niveau de <b>" + perso.getName()
                                + "</b> à <b>" + count + "</b>.";
                        SocketManager.GAME_SEND_MESSAGE(player, mess, "009900");
                        
                    } catch (Exception e) {
                        // ok
                    	SocketManager.GAME_SEND_MESSAGE(player, "Valeur incorrecte.");
                        return true;
                    }
                    return true;
			     
			   } else if(command(msg, "demon")) //Commande Brakmarien
	              {
		              byte align = 2;
		              Player target = player;
		              target.modifAlignement(align);
		              if(target.isOnline())
		              SocketManager.GAME_SEND_STATS_PACKET(target);
		              SocketManager.GAME_SEND_MESSAGE(player, "Tu es désormais <b>Brâkmarien</b>." , "009900");
		              return true;
		              
		            } else if(command(msg, "ange")) //Commande bontarien
		              {
		              byte align = 1;
		              Player target = player;
		              target.modifAlignement(align);
		              if(target.isOnline())
		              SocketManager.GAME_SEND_STATS_PACKET(target);
		              SocketManager.GAME_SEND_MESSAGE(player, "Tu es désormais <b>Bontarien</b>." , "009900");
		              return true;
		              
		            } else  if(command(msg, "neutre")) //Commande neutre
		              {
		              byte align = 0;
		              Player target = player;
		              target.modifAlignement(align);
		              if(target.isOnline())
		              SocketManager.GAME_SEND_STATS_PACKET(target);
		              SocketManager.GAME_SEND_MESSAGE(player, "Tu es désormais <b>Neutre</b>." , "009900");
		              return true;
		              
			    } else if(command(msg, "KralaO")) { player.sendMessage("<b>Porte Kralamour Ouverte !</b>");
                //player.teleport((short) 11939, 256);
            SocketManager.GAME_UPDATE_CELL(player.getCurMap(), "328;aaaaaaaaaa801;1");
            SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), 328, true);

            player.getCurMap().getCases().get(286).setWalkable(true);
            SocketManager.GAME_UPDATE_CELL(player.getCurMap(), "286;aaGaaaaaaa801;1");
            SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), 286, true);
            player.getCurMap().getCases().get(300).setWalkable(true);
            SocketManager.GAME_UPDATE_CELL(player.getCurMap(), "300;aaGaaaaaaa801;1");
            SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), 300, true);
            player.getCurMap().getCases().get(315).setWalkable(true);
            SocketManager.GAME_UPDATE_CELL(player.getCurMap(), "315;aaGaaaaaaa801;1");
            SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), 315, true);
          
            
                return true;
                
            }
            else if(command(msg, "KralaC")) { player.sendMessage("<b>Porte Kralamour Fermée !</b>");
            //player.teleport((short) 11939, 256);
        SocketManager.GAME_UPDATE_CELL(player.getCurMap(), "328;aaGaaaaaaa801;1");
        SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), 328, false);

        player.getCurMap().getCases().get(286).setWalkable(false);
        SocketManager.GAME_UPDATE_CELL(player.getCurMap(), "286;aaaaaaaaaa801;1");
        SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), 286, false);
        player.getCurMap().getCases().get(300).setWalkable(false);
        SocketManager.GAME_UPDATE_CELL(player.getCurMap(), "300;aaaaaaaaaa801;1");
        SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), 300, false);
        player.getCurMap().getCases().get(315).setWalkable(false);
        SocketManager.GAME_UPDATE_CELL(player.getCurMap(), "315;aaaaaaaaaa801;1");
        SocketManager.GAME_SEND_ACTION_TO_DOOR(player.getCurMap(), 315, false);
      
        
            return true;
            
        } else if (command(msg, "transfert")) {   // Commande .transfert  -  Fait laguer avec + de 30 items
		                if (player.isInPrison() || player.getFight() != null )
		                    return true;
		                if(player.getExchangeAction() == null || player.getExchangeAction().getType() != ExchangeAction.IN_BANK) {
		                    player.sendMessage("L'interface de ta banque doit être ouverte.");
		                    return true;
		                }

		                player.sendMessage("Veuillez patienter quelques instants..");
		                int count = 0;

		                for (GameObject object : new ArrayList<>(player.getItems().values())) {
		                    if (object == null || object.getTemplate() == null || !object.getTemplate().getStrTemplate().isEmpty())
		                        continue;
		                    switch (object.getTemplate().getType()) {
		                        case Constant.ITEM_TYPE_OBJET_VIVANT:case Constant.ITEM_TYPE_PRISME:
		                        case Constant.ITEM_TYPE_FILET_CAPTURE:case Constant.ITEM_TYPE_CERTIF_MONTURE:
		                        case Constant.ITEM_TYPE_OBJET_UTILISABLE:case Constant.ITEM_TYPE_OBJET_ELEVAGE:
		                        case Constant.ITEM_TYPE_CADEAUX:case Constant.ITEM_TYPE_PARCHO_RECHERCHE:
		                        case Constant.ITEM_TYPE_PIERRE_AME:case Constant.ITEM_TYPE_BOUCLIER:
		                        case Constant.ITEM_TYPE_SAC_DOS:case Constant.ITEM_TYPE_OBJET_MISSION:
		                        case Constant.ITEM_TYPE_BOISSON:case Constant.ITEM_TYPE_CERTIFICAT_CHANIL:
		                        case Constant.ITEM_TYPE_FEE_ARTIFICE:case Constant.ITEM_TYPE_MAITRISE:
		                        case Constant.ITEM_TYPE_POTION_SORT:case Constant.ITEM_TYPE_POTION_METIER:
		                        case Constant.ITEM_TYPE_POTION_OUBLIE:case Constant.ITEM_TYPE_BONBON:
		                        case Constant.ITEM_TYPE_PERSO_SUIVEUR:case Constant.ITEM_TYPE_RP_BUFF:
		                        case Constant.ITEM_TYPE_MALEDICTION:case Constant.ITEM_TYPE_BENEDICTION:
		                        case Constant.ITEM_TYPE_TRANSFORM:case Constant.ITEM_TYPE_DOCUMENT:
		                        case Constant.ITEM_TYPE_QUETES:
		                            break;
		                        default:
		                            count++;
		                            player.addInBank(object.getGuid(), object.getQuantity());
		                            break;
		                    }
		                }
		                SocketManager.GAME_SEND_MESSAGE(player, "Le transfert a été effectué, <b>" + count + "</b> objet(s) ont été déplacés." , "009900");
		                return true;
        } else if(command(msg, "onboard")) {
            final int[] onboardItems= {6894,8575,7114,739,694,6980,7754,7115,8474,8472,8877,9464};
            for(int i=0;i < onboardItems.length;i++) {
                final ObjectTemplate t = World.world.getObjTemplate(onboardItems[i]);
                final boolean useMax = true;
                final GameObject obj = t.createNewItem(1, useMax);
                
                World.world.addGameObject(obj,true);
                player.addObjet(obj);
                String mesg= obj.getTemplate().getName()+" a été ajouté a votre inventaire (stats MAX)";
                SocketManager.GAME_SEND_MESSAGE(player,
                        mesg , "009900");
          
            
            }
            //player.sendMessage("La création du pack Onboarding est est terminée");
            SocketManager.GAME_SEND_MESSAGE(player,
                    "La création du pack Onboarding est est terminée" , "009900");
            return true;
            
        } else if (command(msg, "ipdrop")) {  // Commande .ipdrop
		                  if(player.ipDrop)
		                  {
		                    player.ipDrop=false;
		                    SocketManager.GAME_SEND_MESSAGE(player,"Les drops associés à votre adresse IP ne vous seront plus attribués.", "009900");
		                  }
		                  else
		                  {
		                    player.ipDrop=true;
		                    SocketManager.GAME_SEND_MESSAGE(player,"Tous les drops associés à votre adresse IP vous seront attribués.", "009900");
		                  }
		                  return true;
        } else if (command(msg, "pass")) {  // Commande .pass  
		                if(player.getAutoSkip()==false)
		                {
		                  player.setAutoSkip(true);
		                  SocketManager.GAME_SEND_MESSAGE(player,"Vous tours seront passés automatiquement en combat.", "009900");
		                }
		                else
		                {
		                  player.setAutoSkip(false);
		                  SocketManager.GAME_SEND_MESSAGE(player,"Vous tours ne seront plus passés automatiquement en combat.", "009900");
		                }
		                return true;
		            } else {
                SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 12));
                return true;
            }
        }
        return false;
    }

    private static boolean command(String msg, String command) {
        return msg.length() > command.length() && msg.substring(1, command.length() + 1).equalsIgnoreCase(command);
    }

    private static String getNameServerById(int id) {
        switch(id) {
            case 13: return "Silouate";
            case 19: return "Allister";
            case 22: return "Oto Mustam";
            case 1: return "Jiva";
            case 37: return "Nostalgy";
            case 601 : return "Eratz" ;
            case 613 : return "Crail";
            case 4001: return "Alma";
            case 4002: return "Aguabrial";
        }
        return "Unknown";
    }
    
    // .exo 
    // Marche pas autrement qu'en faisant un gros copier coller de noob
    // Cette partie de code provient de locos.job.JobAction
    public static byte viewActualStatsItem(GameObject obj, String stats)//retourne vrai si la stats est actuellement sur l'item
    {
        if (!obj.parseStatsString().isEmpty()) {
            for (Entry<Integer, Integer> entry : obj.getStats().getMap().entrySet()) {
                if (Integer.toHexString(entry.getKey()).compareTo(stats) > 0)//Effets inutiles
                {
                    if (Integer.toHexString(entry.getKey()).compareTo("98") == 0
                            && stats.compareTo("7b") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9a") == 0
                            && stats.compareTo("77") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9b") == 0
                            && stats.compareTo("7e") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9d") == 0
                            && stats.compareTo("76") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("74") == 0
                            && stats.compareTo("75") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("99") == 0
                            && stats.compareTo("7d") == 0) {
                        return 2;
                    } else {
                    }
                } else if (Integer.toHexString(entry.getKey()).compareTo(stats) == 0)//L'effet existe bien !
                {
                    return 1;
                }
            }
            return 0;
        } else {
            return 0;
        }
    }
    
    public static byte viewBaseStatsItem(GameObject obj, String ItemStats)//retourne vrai si le stats existe de base sur l'item
    {

        String[] splitted = obj.getTemplate().getStrTemplate().split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            if (stats[0].compareTo(ItemStats) > 0)//Effets n'existe pas de base
            {
                if (stats[0].compareTo("98") == 0
                        && ItemStats.compareTo("7b") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9a") == 0
                        && ItemStats.compareTo("77") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9b") == 0
                        && ItemStats.compareTo("7e") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9d") == 0
                        && ItemStats.compareTo("76") == 0) {
                    return 2;
                } else if (stats[0].compareTo("74") == 0
                        && ItemStats.compareTo("75") == 0) {
                    return 2;
                } else if (stats[0].compareTo("99") == 0
                        && ItemStats.compareTo("7d") == 0) {
                    return 2;
                } else {
                }
            } else if (stats[0].compareTo(ItemStats) == 0)//L'effet existe bien !
            {
                return 1;
            }
        }
        return 0;
    }
       
    public static int getBaseMaxJet(int templateID, String statsModif) {
        ObjectTemplate t = World.world.getObjTemplate(templateID);
        String[] splitted = t.getStrTemplate().split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            if (stats[0].compareTo(statsModif) > 0)//Effets n'existe pas de base
            {
            } else if (stats[0].compareTo(statsModif) == 0)//L'effet existe bien !
            {
                int max = Integer.parseInt(stats[2], 16);
                if (max == 0)
                    max = Integer.parseInt(stats[1], 16);//Pas de jet maximum on prend le minimum
                return max;
            }
        }
        return 0;
    }
    
   
}