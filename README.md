```diff
- ATTENTION ! A partir de cette  version il faut avoir le Login qui va avec !
```
Cliquez sur ce lien pour voir le Github du login : [HelpLogin](https://github.com/Sarazar928Ghost/HelpLogin) 
## HelpGame

Cette émulateur est open source pour toute personne.  
Merci de pas le vendre , vous avez reçu gratuitement , donnez gratuitement 🤗

## Authors

- [@sarazar928ghost](https://github.com/sarazar928ghost) 
- Discord : Kevin#6537


## Nouveautées :

- Commandes joueurs lié avec la base de données.
- Prestige lié avec la base de données.
- Nouvelle condition : PRE , exemple : PRE>1 , prestige supérieur a 1. A mettre dans les colonnes conditions en base de données.
- Nouvelle config.txt. (Se génére toute seule lors du lancement si inéxistant)
- Possibilités de donner des items/pano lors de la création d'un personnage via la config.txt
- Commande joueur giveItem ( numéro 6 ) avec arguments exemple : idTemplate,idTemplate,idTemplate,idTemplate;true (true veut dire jetMax)
- Nouveau système de stockage des items équipés.
- Le serveur game n'envoie plus de packet au client pour créer un item/pet/mount/quest/guild
- Refonte système de parchotage et reset stats.
- Mimibiote ( Objet action 35 ) ( Item Template mimibiote : 4 )
- Cameleon DD ( Objet action action 36 ) ( Item Template Potion : 2 )
- Piège débugé a 100% ( Normalement 🤗 )
- Demande désormais au Login l'ID pour le futur player crée.
- Item classe fonctionne a 100% , vous pouvez mettre des stats items classe sur tout les items du jeu sans exception.
- Les commandes joueurs possèdent une description customamisable via la BDD.
- Boucle infini dans le bat du GameCompiled.rar afin de pouvoir reboot le serveur via la commande exit dans la console
- L'ordre des stats des items est le même pour tout les items. ( Pour les nouveaux items généré )

## DEBUG :

- Commande joueur FM cac fonctionne a 100%
- Ne peut plus équiper plusieurs items. ( exemple 20 anneaux )
- Enleve bien la morph des armes qui transforme lorsque on déséquipe l'arme.
- Drop item équipé fonctionne correctement.
- Ne génére plus d'ID object fantome.
- IA moins bugé
- Porter/Jeter panda débugé
- Les items de class gardent leur effets lors d'un reboot
- System de ban fonctionnel
- System de mute fonctionnel
- Les pets se générent dorénavant correctement ( Bug dû aux modification )
- Lors d'un FM d'un item de classe , les stats de class sont conservé.
- Les stats conservent le même ordre lors d'un FM
- Clonages d'items débugés ( Dorénavant les clones possèdent leur propres listes pour les stats )
- Les items mimibioté peuvent être FM en conservant leur stats mimi.
- Ne peut plus craft un item mimi avec plus de 1 en quantité , cela créera donc un nouveau item.

## AUTRES :

- Refactoring du code en masse afin de le rendre plus lisible et professionel.
- Refactoring mouvement des objects dans l'inventaire. Grosse optimisation
- N'envoie plus les packets de Stats lorsque on positionne un consomable dans la barre des raccourcis items.
- Refactoring des actions des objets + optimisation
- Refactoring addObjet et createNewItem
- Optimisation getDirBetweenTwoCase
- Les items utilisent dorénavant une LinkedHashMap pour les stats normaux , mais les joueurs gardent toujours les HashMap
- Fichier itemstats pour le fichier lang afin que l'ordre des stats des items templates soit le même côté serveur et client.
- Les stats template sont dans le même ordre que les stats des objets crée.
- Tout les objets déjà crée possèdent leur stats en ordre.
- SQL mis au gout du jour ( en lien avec le discord )


## TELECHARGEMENT :
- GameCompiled.rar contient le game compilé avec le .bat pour le lancer.
- Ne pas oublier d'executer les SQL
- Si vous n'avez jamais lancé cette ému , il faut supprimer l'ancienne config.txt.

## AIDE :

- Si vous avez des questions ou besoin d'aide vous pouvez me contacter via mon discord Kevin#6537 .
- Si vous rencontrez des bugs grave avec les nouveautés merci de m'en faire part afin de les résoudre au plus vite.
- Si vous avez des suggestions/idées vous pouvez m'en faire part aussi.

## REMERCIEMENT :

Je remercie fred6725#8702 de son aide actif pour les nouveaux sur son discord Dofus 1.29+ AIDE.  
Sans ce discord je pense que je n'aurais jamais fait ce github 🤗

Cette émulateur est basé sur le starloco modifié par la communauté de fred6725#8702 , un grand merci donc a vous qui avez contribué ❤️‍🔥


