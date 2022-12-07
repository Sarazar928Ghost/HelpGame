package org.starloco.locos.util.lang.type;

import org.starloco.locos.common.SocketManager;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.util.lang.AbstractLang;
import org.starloco.locos.client.*;
import org.starloco.locos.command.CommandPlayer.*;

/**
 * Created by Locos on 09/12/2015.
 */
public class French extends AbstractLang {

    public final static French singleton = new French();

    public static French getInstance() {
        return singleton;
    }

    
    public void initialize() {
        int index = 0;
        this.sentences.add(index, "Votre canal g�n�ral est d�sactiv�."); index++;
        this.sentences.add(index, "Les caract�res point virgule, chevrons et tild� sont d�sactiv�s."); index++;
        this.sentences.add(index, "Tu dois attendre encore #1 seconde(s)."); index++;
        this.sentences.add(index, "Vous avez activ� le canal g�n�ral."); index++;
        this.sentences.add(index, "Vous avez d�sactiv� le canal g�n�ral."); index++;
        this.sentences.add(index, "Liste des membres du staff connect�s :"); index++;
        this.sentences.add(index, "Il n'y a aucun membre du staff connect� ou peut-�tre y a t'il la pr�sence de Locos ?"); index++;
        this.sentences.add(index, "Vous n'�tes pas bloqu�.."); index++;
        this.sentences.add(index, "<b>StarLoco - " + Config.getInstance().NAME + "</b>\nEn ligne depuis : #1j #2h #3m #4s."); index++;
        this.sentences.add(index, "\nJoueurs en ligne : #1"); index++;
        this.sentences.add(index, "\nJoueurs uniques en ligne : #1"); index++;
        this.sentences.add(index, "\nRecord de connexion : #1"); index++;
        Player player = null;
		//            + "<b>.infos</b> - Permet d'obtenir des informations sur le serveur.\n"    
        //+ "<b>.noall</b> - Permet de ne plus recevoir les messages du canal g�n�ral."); index++;
        //+ "<b>.staff</b> - Permet de voir les membres du staff connect�s.\n"
        this.sentences.add(index, "Les commandes disponnibles sont :\n"
        		+ "<b>.all</b> - <b>.noall</b> - Permet d'envoyer un message � tous les joueurs. (ex : .all Hey!)\n"
                + "<b>.deblo</b> - Permet de vous d�bloquer en vous t�l�portant � une cellule libre.\n"
                + "<b>.onboard</b> - Vous donne tout ce dont vous avez besoin pour effectuer des tests.\n"
                + "<b>.level</b> - Permet de fixer son level � une valeur. (Ex : .level 100)\n"
                + "<b>.restat</b> - Permet de remettre ses caract�ristiques � 0.\n"
                + "<b>.parcho</b> - Permet de se parcho 101 dans tous les �l�ments.\n"
                + "<b>.boost</b> - Permet de booster ses caract�ristiques plus vite. "
                + "(ex : .boost [vita/sagesse/force/intel/chance/agi] [x] pour booster x dans l'�l�ment souhait�.) \n"
                + "<b>.spellmax</b> - Permet de monter ses sorts au niveau max.\n"
                + "<b>.jetmax</b> - Permet d'avoir le jet max sur un item. (ex : .jetmax all)\n"
                + "<b>.exo</b> - Permet d'exo un item PA ou PM. (ex : .exo cape pa)\n"
                + "<b>.fmcac</b> - Permet d'FM son cac dans un �l�ment d'attaque. (ex : .fmcac feu)\n"
                + "<b>.start</b> - Permet de se t�l�porter � la map de d�part.\n"
                + "<b>.poutch</b> - Permet de se t�l�porter au poutch.\n"
                + "<b>.maitre</b> - Permet de d�placer sa team � l'aide d'un seul terminal.\n"
                + "<b>.ipdrop</b> - Redirige tous les drops de votre team vers le maitre.\n"
                + "<b>.pass</b> - Permet de passer vos tours automatiquement en combat. \n"
                + "<b>.pvm</b> - Permet de se t�l�porter � la map pvm.\n"
                + "<b>.vie</b> - Permet de restaurer 100% de sa vie.\n"
                + "<b>.banque</b> - Permet d'ouvrir l'interface de sa banque.\n"
                + "<b>.transfert</b> - Permet de transf�rer toutes ses ressources en banque. (Lag apr�s 30 items.)\n"
                + "<b>.enclos</b> - Permet de se t�l�porter � un enclos.\n"
                + "<b>.phoenix</b> - Permet de se t�l�porter � la statue du Phoenix.\n"
                + "<b>.ange</b> - Permet de passer en Alignement Bontarien.\n"
                + "<b>.demon</b> - Permet de passer en Alignement Brakmarien.\n"
                + "<b>.neutre</b> - Permet de passer en Alignement Neutre.\n"); index++;
        this.sentences.add(index, "Retrouvez les commandes en tapant .commandes ou .x dans le chat.");
    }
}
