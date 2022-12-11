package org.starloco.locos.command;


import org.starloco.locos.area.map.entity.House;

import org.starloco.locos.client.Player;
import org.starloco.locos.client.Prestige;
import org.starloco.locos.client.other.Maitre;
import org.starloco.locos.client.other.Party;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.ConditionParser;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.Database;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Logging;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.util.lang.Lang;


import org.starloco.locos.fight.spells.*;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

public class ExecuteCommandPlayer {

    public final static String canal = "Général";
    

//    public void apply(String packet) {  // Ajouté pour commande .level
//    	String msg = packet.substring(2);
//    	String[] infos = msg.split(" ");  
    
//    	if (infos.length == 0) return false;
//    	String command = infos[0];
//    }
    

    	

    public static boolean analyse(Player player, String msg) {
        if (msg.charAt(0) == '.' && msg.charAt(1) != '.') {
        	
        	final String commandName = msg.substring(0, msg.length() - 1).trim().split(" ")[0].substring(1);
        	
        	final PlayerCommand playerCommand = World.world.getPlayerCommandByName(commandName);
        	if(playerCommand == null) 
    		{
        		player.sendErrorMessage("Commande non reconnue.");
        		return true;
    		}
        	
        	final int price = playerCommand.getPrice();
        	if(price != 0)
        		if(player.getAccount().getPoints() < price)
        		{
        			player.sendErrorMessage("Tu n'as pas assez de points pour pouvoir éxecuter cette commande.");
        			return true;
        		}
        	
        	if(playerCommand.getCondition() != null)
        		if(!ConditionParser.validConditions(player, playerCommand.getCondition()))
        		{
        			player.sendErrorMessage("Tu ne respectes pas les conditions d'utilisation pour cette commande.");
        			return true;
        		}
        			
        	if(playerCommand.isVip())
        		if(!player.getAccount().isVip())
        		{
        			player.sendErrorMessage("Tu dois êtres V.I.P pour pouvoir éxecuter cette commande.");
        			return true;
        		}
        	
        	final String args = playerCommand.getArgs();
        	
        	boolean removePoint = false;
        	switch(playerCommand.getType())
        	{
	        	case 0:
	        		removePoint = doTp(args, player);
	        		break;
	        	case 1:
	        		removePoint = doAll(msg, player);
	        		break;
	        	case 2:
	        		removePoint = noAll(player);
	        		break;
	        	case 3:
	        		removePoint = doMaster(msg, player);
	        		break;
	        	case 4:
	        		removePoint = doTeleportMaster(msg, player);
	        		break;
	        	case 5:
	        		removePoint = doTransfer(msg, player);
	        		break;
	        	case 6:
	        		removePoint = doOnBoard(msg, player);
	        		break;
	        	case 7:
	        		doPrestige(msg, player);
	        		break;
	        	case 8:
	        		removePoint = doFmCac(msg, player);
	        		break;
	        	case 9:
	        		removePoint = doParcho(msg, player);
	        		break;
	        	case 10:
	        		removePoint = doSpellMax(msg, player);
	        		break;
	        	case 11:
	        		removePoint = doBanque(msg, player);
	        		break;
	        	case 12:
	        		removePoint = doPass(msg, player);
	        		break;
	        	case 13:
	        		player.sendInformationMessage("Tu as <b>"+player.getAccount().getPoints()+"</b> points boutique !");
	        		removePoints(player, price);
	        		break;
	        	case 14:
	        		removePoint = doLevel(msg, player);
	        		break;
	        	case 15:
	        		removePoint = doIpDrop(msg, player);
	        		break;
	        	case 16:
	        		player.restatAll(0);
	        		removePoints(player, price);
	        		break;
	        	case 18:
	        		removePoint = doInfos(msg, player);
	        		break;
	        	case 19:
	        		removePoint = doStaff(msg, player);
	        		break;
	        	case 20:
	        		removePoint = doCommand(player);
	        		break;
	        	case 22:
	        		removePoint = doHouse(msg, player);
	        		break;
	        	case 23:
	        		removePoint = doDeblo(msg, player);
	        		break;
	        	case 25:
	        		removePoint = doGroupe(msg, player);
	        		break;
	        	case 26:
	        		removePoint = doVie(msg, player);
	        		break;
	        	case 27:
	        		removePoint = doExo(msg, player);
	        		break;
	        	case 28:
	        		removePoint = doJetMax(msg, player);
	        		break;
	        	case 29:
	        		removePoint = doBoost(msg, player);
	        		break;
	        	case 30:
	        		removePoint = doDemon(msg, player);
	        		break;
	        	case 31:
	        		removePoint = doAnge(msg, player);
	        		break;
	        	case 32:
	        		removePoint = doNeutre(msg, player);
	        		break;
	        	case 33:
	        		removePoint = doKralaOpen(msg, player);
	        		break;
	        	case 34:
	        		removePoint = doKralaClose(msg, player);
	        		break;
        	}
        	
        	if(removePoint) removePoints(player, price);
        	return true;
        	
        }
        return false;
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
    
    private static boolean doParcho(final String msg, final Player player)
    {
    	if(player.getFight() != null)
		{
			player.sendMessage("Commande impossible a exécuter en combat");
			return false;
		}	
		boolean parcho = false;
		
		final Prestige prestige = World.world.getPrestigeById(player.getPrestige());
		
		final int statsParchoMax;
		
		if(prestige != null) statsParchoMax = prestige.getPrestigeBonus().getParcho();
		else statsParchoMax = 101;
		
		if(player.getStatsParcho().getEffect(Constant.STATS_ADD_VITA) < statsParchoMax)
			parcho = true;
		else if(player.getStatsParcho().getEffect(Constant.STATS_ADD_SAGE) < statsParchoMax)
			parcho = true;
		else if(player.getStatsParcho().getEffect(Constant.STATS_ADD_FORC) < statsParchoMax)
			parcho = true;
		else if(player.getStatsParcho().getEffect(Constant.STATS_ADD_INTE) < statsParchoMax)
			parcho = true;
		else if(player.getStatsParcho().getEffect(Constant.STATS_ADD_AGIL) < statsParchoMax)
			parcho = true;
		else if(player.getStatsParcho().getEffect(Constant.STATS_ADD_CHAN) < statsParchoMax)
			parcho = true;
		
		if(parcho) {
			player.parcho();
			SocketManager.GAME_SEND_STATS_PACKET(player);
			return true;
		}
		SocketManager.GAME_SEND_Im_PACKET(player, "116;<i>Serveur: </i>~Vous êtes déjà parchotté dans tous les éléments !");
		return false;
    }
    
    private static void doPrestige(final String msg, final Player player)
    {
    	final String[] arguments = msg.substring(0, msg.length() - 1).trim().split(" ");
    	if(arguments.length == 1)
    	{
    		final String message = "Commande Prestige : <br><br>"
    				+ "<b>.prestige next</b> : <br>"
    				+ "Passer au prestige suivant.<br><br>"
    				+ "<b>.prestige condition [id]</b> :<br> "
    				+ "Voir les conditions d'un prestige.<br><br>"
    				+ "<b>.prestige bonus [id]</b> :<br> "
    				+ "Voir les bonus d'un prestige.<br><br>"
    				+ "<b>.prestige actuel</b> : <br>"
    				+ "Montre votre prestige<br>";
    		SocketManager.send(player, "BAIO"+message);
    		return;
    	}
    	
    	
    	if(arguments[1].equalsIgnoreCase("condition") || arguments[1].equalsIgnoreCase("bonus"))
    	{
    		if(arguments.length < 3) {
    			player.sendErrorMessage("Commande incorrect , faîtes .prestige pour voir les commandes.");
    			return;
    		}
    		
    		short prestigeId;
    		try {
    			prestigeId = Short.parseShort(arguments[2]);
    		}catch(NumberFormatException e)
    		{
    			player.sendErrorMessage("L'id doit être un nombre");
    			return;
    		}
    		
    		if(prestigeId < 1) prestigeId = (short) 1;
    		
    		final Prestige prestige = World.world.getPrestigeById(prestigeId);
    		if(prestige == null)
    		{
    			player.sendInformationMessage("Ce prestige n'éxiste pas.");
    			return;
    		}
    		
    		if(arguments[1].equalsIgnoreCase("condition") || arguments[1].equalsIgnoreCase("conditions"))
    			prestige.sendInfosCondition(player);
    		else if(arguments[1].equalsIgnoreCase("bonus"))
    			prestige.sendBonus(player, "Bonus du Prestige");
    		
    		return;
    	}
    	
    	if(arguments[1].equalsIgnoreCase("actuel") || arguments[1].equalsIgnoreCase("actual"))
    	{

    		final Prestige prestige = World.world.getPrestigeById(player.getPrestige());
    		if(prestige == null)
    		{
    			player.sendInformationMessage("Tu n'as pas de prestige.");
    			return;
    		}
    		
    		prestige.sendBonus(player, "Tu es Prestige");
    		return;
    	}
    	
    	if(!arguments[1].equalsIgnoreCase("next"))
    	{
    		player.sendErrorMessage("Commande incorrect , faîtes .prestige pour voir les commandes.");
			return;
    	}
    		
    	final Prestige prestige = World.world.getPrestigeById((short)(player.getPrestige() + 1));
    	if(prestige == null)
    	{
    		player.sendInformationMessage("Tu es déjà au prestige maximum ! Félicitation :)");
    		return;
    	}

    	prestige.apply(player, player.getPrestige(), false);
    }
    
    private static void removePoints(final Player player, final int price)
    {
    	if(price != 0) player.getAccount().setPoints(player.getAccount().getPoints() - price);
    }
    
    private static boolean doTp(final String args, final Player player)
    {
    	if(player.cantTP()) return false;
    	final String[] data = args.split(",");
    	player.teleport(Short.parseShort(data[0]), Integer.parseInt(data[1]));
    	return true;
    }
    
    private static boolean doAll(final String msg, final Player player) {
    	if (player.isInPrison())
            return false;
        if (player.noall) {
            SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 0), "C35617");
            return false;
        }
        if(player.getGroupe() == null && System.currentTimeMillis() - player.getGameClient().timeLastTaverne < 10000) {
            SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 2).replace("#1", String.valueOf(10 - ((System.currentTimeMillis() - player.getGameClient().timeLastTaverne) / 1000))), "C35617");
            return false;
        }

        player.getGameClient().timeLastTaverne = System.currentTimeMillis();

        String prefix = "<font color='#C35617'>[" + (new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()))) + "] (" + canal + ") (" + getNameServerById(Main.serverId) + ") <b><a href='asfunction:onHref,ShowPlayerPopupMenu," + player.getName() + "'>" + player.getName() + "</a></b>";

        Logging.getInstance().write("AllMessage", "[" + (new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()))) + "] : " + player.getName() + " : " + msg.substring(5, msg.length() - 1));

        final String message = "Im116;" + prefix + "~" + msg.substring(5, msg.length() - 1).replace(";", ":").replace("~", "").replace("|", "").replace("<", "").replace(">", "") + "</font>";

        World.world.getOnlinePlayers().stream().filter(p -> !p.noall).forEach(p -> p.send(message));
        Main.exchangeClient.send("DM" + player.getName() + "|" + getNameServerById(Main.serverId) + "|" + msg.substring(5, msg.length() - 1).replace("\n", "").replace("\r", "").replace(";", ":").replace("~", "").replace("|", "").replace("<", "").replace(">", "") + "|");
        return true;
    }
    
    private static boolean noAll(final Player player) {
    	if (player.noall) {
            player.noall = false;
            SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 3), "C35617");
        } else {
            player.noall = true;
            SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 4), "C35617");
        }
    	return true;
    }
    
    private static boolean doStaff(final String msg, final Player player) {
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
    }
    
    private static boolean doMaster(final String msg, final Player player) {
    	// 1) Création du groupe par IP joueur
		if (player.isInPrison() || player.getFight() != null)
            return false;

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
        if(player.cantTP()) return false;

        final Party party = player.getParty();

        if (party == null) {
            player.sendMessage("Action impossible. Vérifiez que vos joueurs sont connectés ou ne font pas déjà partie d'un groupe.");
            return false;
        }

        final List<Player> players = player.getParty().getPlayers();

        if (!party.getChief().getName().equals(player.getName())) {
            player.sendMessage("Vous n'êtes pas le chef du groupe.");
            return false;
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
            return false;
        }

        Player target = player;

        if (msg.length() > 8) {
            String name = msg.substring(8, msg.length() - 1);
            target = World.world.getPlayerByName(name);
        }

        if (target == null) {
            player.sendMessage("Le joueur est introuvable.");
            return false;
        }
        if (target.getParty() == null || !target.getParty().getPlayers().contains(player)) {
            player.sendMessage("Tu n'es pas dans le groupe du joueur indiqué.");
            return false;
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
					return true;
				}else
					SocketManager.GAME_SEND_MESSAGE(player, "Aucun héro n'a été trouvé.");
				
       	}
       	return false;
    }
    
    private static boolean doGroupe(final String msg, final Player player) {
    	if (player.isInPrison() || player.getFight() != null)
            return false;

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
    }
    
    private static boolean doDeblo(final String msg, final Player player) {
    	if (player.isInPrison())
            return false;
        if (player.cantTP())
            return false;
        if (player.getFight() != null)
            return false;
        if(player.getCurCell().isWalkable(true)) {
            SocketManager.GAME_SEND_MESSAGE(player, Lang.get(player, 7));
            return false;
        }
        player.teleport(player.getCurMap().getId(), player.getCurMap().getRandomFreeCellId());
        SocketManager.GAME_SEND_MESSAGE(player, "Vous avez été débloqué!" , "009900");
        return true;
    }
    
    private static boolean doHouse(final String msg, final Player player) {
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
    }
    
    private static boolean doInfos(final String msg, final Player player) {
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

    }
    
    private static boolean doBanque(final String msg, final Player player) {
    	if (player.getFight() != null){
            SocketManager.GAME_SEND_MESSAGE(player, "Vous ne pouvez pas utiliser cette commande en combat.");
            return false;

        }
        boolean ok = false;
                ok = true;

        if (ok) {
            Database.getStatics().getPlayerData().update(player);
            if (player.getDeshonor() >= 1) {
                SocketManager.GAME_SEND_Im_PACKET(player, "183");
                return false;
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
                        return false;
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
            return true;
        }
        return false;
    }
    
    
    private static boolean doExo(final String msg, final Player player) {
    	
    	if (player.getFight() != null) {
    		player.sendErrorMessage("Action impossible : vous ne devez pas être en combat.");
            return false;
        } 
    	
    	final String[] split = msg.substring(0, msg.length() - 1).trim().split(" ");
    	
    	if(split.length < 3) {
    		player.sendErrorMessage("La commande doit être : .[commandeName] [coiffe, cape, ceinture, bottes, amulette, anneauG, anneauD, cac] [pa, pm]");
    		return false;
    	}
    	
    	final String[] emplacements = new String[] {
	    		"coiffe", 
	    		"cape", 
	    		"ceinture", 
	    		"bottes",
	    		"amulette", 
	    		"anneauG",
	    		"anneauD",
	    		"cac"
	    };
    	final byte[] emplacementsID = new byte[] {
	    		Constant.ITEM_POS_COIFFE,
	    		Constant.ITEM_POS_CAPE, 
	    		Constant.ITEM_POS_CEINTURE, 
	    		Constant.ITEM_POS_BOTTES,
	    		Constant.ITEM_POS_AMULETTE, 
	    		Constant.ITEM_POS_ANNEAU1,
	    		Constant.ITEM_POS_ANNEAU2,
	    		Constant.ITEM_POS_ARME
	    };
    	
    	byte emplacementID = -1;
    	
    	for(byte i = 0; i < emplacements.length; ++i) {
    		if(!emplacements[i].equalsIgnoreCase(split[1])) continue;
    		emplacementID = emplacementsID[i];
    		break;
    	}
    	
    	if(emplacementID == -1) {
    		player.sendErrorMessage("Seul ces objets peuvent être exo : [coiffe, cape, ceinture, bottes, amulette, anneauG, anneauD, cac]");
    		return false;
    	}
    	
    	if(!split[2].equalsIgnoreCase("pa") && !split[2].equalsIgnoreCase("pm")) {
    		player.sendErrorMessage("Le exo doit soit être pa ou pm et rien d'autres.");
    		return false;
    	}
    	
    	final String statsToAdd = split[2].equalsIgnoreCase("pa") ? "6f" : "80";
    	final String statsToCheck = split[2].equalsIgnoreCase("pa") ? "80f" : "6f";
    	
        
      	   
		final GameObject obj = player.getObjetByPos(emplacementID);
		if (obj == null) {
			player.sendErrorMessage("Action impossible : vous ne portez pas de "+split[1]+".");
		    return false;
		}
		// Pour éviter d'avoir des items 2Pa/2Pm
		if(viewActualStatsItem(obj, statsToAdd) == 1) {
			player.sendErrorMessage("Cette "+split[1]+" possède déjà 1 "+split[2].toUpperCase()+", action impossible.");
		    return false;
		}
		// Pour éviter l'ajout de PA + PM sur un item qui n'en possède aucun de base         
		if(viewActualStatsItem(obj, statsToCheck) == 1 && viewBaseStatsItem(obj, statsToCheck) == 0) {
			player.sendErrorMessage("Cette "+split[1]+" est déjà exo.");
		    return false;
		}
		// Ajout de l'exo
		 
        final byte statsAdd = 1;
        final boolean negative = false;
		
		final String statsStr = obj.parseFMStatsString(statsToAdd, obj, statsAdd, negative)
		       + ","
		       + statsToAdd
		       + "#"
		       + Integer.toHexString(statsAdd)
		       + "#0#0#0d0+"
		       + statsAdd;
		obj.clearStats();
		obj.refreshStatsObjet(statsStr);
		
		SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, obj.getGuid());
		SocketManager.GAME_SEND_OAKO_PACKET(player, obj);
		SocketManager.GAME_SEND_STATS_PACKET(player);
		
		player.sendMessage("Votre item : <b>" + obj.getTemplate().getName() + "</b> a été exo avec succès !");
		 
		return true;

    }
    
    private static boolean doTeleportMaster(final String msg, final Player player)
    {
    	if(System.currentTimeMillis() - player.getGameClient().timeLastTP < 5000) {
            SocketManager.GAME_SEND_MESSAGE(player, "Cette commande est disponible toutes les 5 secondes.");
            return false;
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
            return false;
        }
        if (player.getExchangeAction() != null){
            SocketManager.GAME_SEND_MESSAGE(player, "Vous ne pouvez pas utiliser cette commande car vous êtes occupé.");
            return false;
        }
	    if(player.get_maitre() != null){
	    player.getGameClient().timeLastTP = System.currentTimeMillis();
		player.get_maitre().teleportAllEsclaves();
		SocketManager.GAME_SEND_MESSAGE(player, "Vous avez téléporté <b>"+player.get_maitre().getEsclaves().size()+"</b> joueurs." , "009900");
		return true;
		}
	    else
		SocketManager.GAME_SEND_MESSAGE(player, "Aucun joueur n'a été trouvé pour la téléportation.");
	    return false;
    }
    
    private static boolean doSpellMax(final String msg, final Player player)
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
            return true;
        }
        return false;
    }
    
    private static boolean doJetMax(final String msg, final Player player)
    {
    	if (player.getFight() != null) {
            SocketManager.GAME_SEND_MESSAGE(player,
                    "Action impossible : vous ne devez pas être en combat.");
            return false;
        } 
        String answer;
        try {
        answer = msg.substring(8, msg.length() - 1); // 5 = nbr carac après ".jetmax " (avec espace) 
        } catch (Exception e) {
            SocketManager.GAME_SEND_MESSAGE(player,
                    "Action impossible : vous n'avez pas spécifié l'item à améliorer.");
            return false;
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
            return false;
        }           
        // Cas d'une coiffe
        if (answer.equalsIgnoreCase("coiffe")) {
     	   
     	   GameObject obj = player.getObjetByPos(Constant.ITEM_POS_COIFFE);
            if (obj == null) {
                SocketManager.GAME_SEND_MESSAGE(player,
                        "Action impossible : vous ne portez pas de coiffe.");
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
                return false;
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
		return false;
    }
    
    private static boolean doVie(final String msg, final Player player)
    {
    	if(player.getFight() != null) return false;
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
    }
    
    private static boolean doIpDrop(final String msg, final Player player)
    {
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
    }
    
    private static boolean doPass(final String msg, final Player player)
    {
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
    }
    
    private static boolean doOnBoard(final String msg, final Player player)
    {
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
    }
    
    private static boolean doKralaClose(final String msg, final Player player)
    {
    	player.sendMessage("<b>Porte Kralamour Fermée !</b>");
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
    }
    
    private static boolean doKralaOpen(final String msg, final Player player)
    {
    	player.sendMessage("<b>Porte Kralamour Ouverte !</b>");
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
    
   
    
    private static boolean doLevel(final String msg, final Player player)
    {
    	if (player.getFight() != null) {
            SocketManager.GAME_SEND_MESSAGE(player,
                    "Action impossible : vous ne devez pas être en combat.");
            return false;       
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
		     	return false;
		     }
				if(count < player.getLevel())
		     {
		     	SocketManager.GAME_SEND_MESSAGE(player, 
		     			"Vous ne pouvez pas vous donner un niveau inférieur à votre niveau actuel.");
		     	return false;
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
		     return false;
		 }
		 return true;
    }
    
    private static boolean doDemon(final String msg, final Player player)
    {
    	byte align = 2;
        Player target = player;
        target.modifAlignement(align);
        if(target.isOnline())
        SocketManager.GAME_SEND_STATS_PACKET(target);
        SocketManager.GAME_SEND_MESSAGE(player, "Tu es désormais <b>Brâkmarien</b>." , "009900");
        return true;
    }
    
    private static boolean doTransfer(final String msg, final Player player)
    {
    	if (player.isInPrison() || player.getFight() != null )
            return false;
        if(player.getExchangeAction() == null || player.getExchangeAction().getType() != ExchangeAction.IN_BANK) {
            player.sendMessage("L'interface de ta banque doit être ouverte.");
            return false;
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
    }
    
    private static boolean doAnge(final String msg, final Player player)
    {
    	byte align = 1;
        Player target = player;
        target.modifAlignement(align);
        if(target.isOnline())
        SocketManager.GAME_SEND_STATS_PACKET(target);
        SocketManager.GAME_SEND_MESSAGE(player, "Tu es désormais <b>Bontarien</b>." , "009900");
        return true;
    }
    
    private static boolean doNeutre(final String msg, final Player player)
    {
    	byte align = 0;
        Player target = player;
        target.modifAlignement(align);
        if(target.isOnline())
        SocketManager.GAME_SEND_STATS_PACKET(target);
        SocketManager.GAME_SEND_MESSAGE(player, "Tu es désormais <b>Neutre</b>." , "009900");
        return true;
    }
    
    private static boolean doFmCac(final String msg, final Player player)
    {
    	// Cette commande est bug sur le deuxième jet neutre de suite ex : Épée Kukri Kura
		   // Le deuxième jet est fm à 100% de son jet initial.
        GameObject obj = player.getObjetByPos(Constant.ITEM_POS_ARME); // obj
        if (player.getFight() != null) {
            SocketManager.GAME_SEND_MESSAGE(player,
                    "Action impossible : vous ne devez pas être en combat.");
            return false;
        } else if (obj == null) {
            SocketManager.GAME_SEND_MESSAGE(player,
                    "Action impossible : vous ne portez pas d'arme");
            return false;
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
            return false;
        }

        String answer;

        try {
            answer = msg.substring(7, msg.length() - 1);
        } catch (Exception e) {
            SocketManager.GAME_SEND_MESSAGE(player,
                    "Action impossible : vous n'avez pas spécifié l'élément (air, feu, terre, eau) qui remplacera les dégats/vols de vies neutres");
            return false;
        }

        if (!answer.equalsIgnoreCase("air") && !answer.equalsIgnoreCase(
                "terre") && !answer.equalsIgnoreCase("feu") && !answer.equalsIgnoreCase(
                "eau")) {
            SocketManager.GAME_SEND_MESSAGE(player,
                    "Action impossible : l'élément " + answer + " est incorrect. (Disponible : air, feu, terre, eau)");
            return false;
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
    }
    
    private static boolean doBoost(final String msg, final Player player)
    {
	    if (player.getFight() != null) {
               player.sendErrorMessage("Action impossible : vous ne devez pas être en combat.");
               return false;       
	    }
	    
	    final String[] split = msg.substring(0, msg.length() - 1).trim().split(" ");
	    
	    if(split.length < 3) {
	    	player.sendErrorMessage("La commande doit être .boost [sagesse, force, vita, intel, chance, agi] [quantity]");
	    	return false;
	    }
	    
	    final String[] types = new String[] {
	    		"sagesse", 
	    		"force", 
	    		"vita", 
	    		"intel",
	    		"chance", 
	    		"agi",
	    };
	    
	    final byte[] statsID = new byte[] {
	    		12,
	    		10,
	    		11,
	    		15,
	    		13,
	    		14,
	    };
	    
	    final String type = split[1];
	    
	    byte statID = -1;
	    
	    for(byte i = 0; i < types.length; ++i) {
	    	if(!types[i].equalsIgnoreCase(type)) continue;
	    	statID = statsID[i];
	    	break;
	    }
	    
	    if(statID == -1) {
	    	player.sendErrorMessage("Le type doit être : [sagesse, force, vita, intel, chance, agi]");
	    	return false;
	    }
	    
	    int value;
	    try {
	    	value = Integer.parseInt(split[2]);
	    }catch(NumberFormatException e) {
	    	player.sendErrorMessage("La quantité doit être un nombre.");
	    	return false;
	    }
	    
	    if(value < 1) {
	    	player.sendErrorMessage("Vous ne pouvez pas retirer de boost, utilisez la commande  .restat  .");
	    	return false;
	    }
	    
	    if(player.get_capital() < 1 || player.get_capital() < value) {
	    	player.sendErrorMessage("Vous n'avez pas assez de capital.");
	    	return false;
	    }
	    
	    player.boostStatFixedCount(10, statID);
        player.sendInformationMessage("Vous avez boost " + value + " point de capitaux en " + type + ".");
        
        return true;
	    
    }
    
    private static boolean doCommand(final Player player)
    {
    	final StringBuilder sb = new StringBuilder();
    	
    	sb.append("<font color='#"+Constant.COLOR_CHANCE+"'>");
    	sb.append("<b>==== Commandes Gratuites : ====</b>").append("\n");
    	for(final PlayerCommand pc : World.world.getPlayerCommand())
    		if(pc.getPrice() == 0 && pc.getType() != 20 && !pc.isVip())
    			sb.append("."+pc.getName()[0]).append("\n");
    	sb.append("</font>");
    	
    	sb.append("<font color='#"+Constant.COLOR_INTEL+"'>");
    	sb.append("<b>==== Commandes Payantes : ====</b>").append("\n");
    	for(final PlayerCommand pc : World.world.getPlayerCommand())
    		if(pc.getPrice() != 0 && pc.getType() != 20 && !pc.isVip())
    			sb.append("."+pc.getName()[0]+" ["+pc.getPrice()+" Points]").append("\n");
    	sb.append("</font>");
    	
    	sb.append("<font color='#"+Constant.COLOR_PA+"'>");
    	sb.append("<b>==== Commandes V.I.P : ====</b>").append("\n");
    	for(final PlayerCommand pc : World.world.getPlayerCommand())
    		if(pc.getType() != 20 && pc.isVip())
    			sb.append("."+pc.getName()[0] + (pc.getPrice() != 0 ? " ["+pc.getPrice()+" Points]" : "")).append("\n");
    	sb.append("</font>");
    	
    	player.sendMessage(sb.toString());
    	return true;
    }
   
}