/*
Navicat MySQL Data Transfer

Source Server         : dofus
Source Server Version : 50651
Source Host           : localhost:3306
Source Database       : game

Target Server Type    : MYSQL
Target Server Version : 50651
File Encoding         : 65001

Date: 2023-01-24 18:24:57
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `playercommands`
-- ----------------------------
DROP TABLE IF EXISTS `playercommands`;
CREATE TABLE `playercommands` (
  `name` varchar(255) NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `args` varchar(255) DEFAULT NULL,
  `price` int(11) NOT NULL DEFAULT '0',
  `vip` tinyint(1) NOT NULL DEFAULT '0',
  `condition` varchar(255) DEFAULT NULL,
  `description` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of playercommands
-- ----------------------------
INSERT INTO `playercommands` VALUES ('all', '1', null, '0', '0', null, 'Permet d\'envoyer un message à tous les joueurs. (ex : .all Hey!)');
INSERT INTO `playercommands` VALUES ('ange', '31', null, '0', '0', null, 'Permet de passer en Alignement Bontarien.');
INSERT INTO `playercommands` VALUES ('bank|openbank', '11', null, '0', '0', null, 'Permet d\'ouvrir l\'interface de sa banque.');
INSERT INTO `playercommands` VALUES ('boost', '29', null, '0', '0', null, 'Permet de booster ses caractéristiques plus vite. (ex : .boost [vita/sagesse/force/intel/chance/agi] [x] pour booster x dans l\'élément souhaité.)');
INSERT INTO `playercommands` VALUES ('command|commands|help', '20', null, '0', '0', null, 'Permet de voir toute les commandes.');
INSERT INTO `playercommands` VALUES ('deblo', '23', '', '0', '0', null, 'Permet de vous débloquer en vous téléportant à une cellule libre.');
INSERT INTO `playercommands` VALUES ('demon', '30', null, '0', '0', null, 'Permet de passer en Alignement Brakmarien.');
INSERT INTO `playercommands` VALUES ('enclos', '0', '8747,633', '0', '0', null, 'Permet de se téléporter à un enclos.');
INSERT INTO `playercommands` VALUES ('exo', '27', null, '0', '0', null, 'Permet d\'exo un item PA ou PM. (ex : .exo cape pa)');
INSERT INTO `playercommands` VALUES ('fmcac|fm', '8', null, '0', '0', null, 'Permet d\'FM son cac dans un élément d\'attaque. (ex : .fmcac feu)');
INSERT INTO `playercommands` VALUES ('group|groupe', '25', null, '0', '0', null, '');
INSERT INTO `playercommands` VALUES ('house|maison', '22', null, '0', '0', null, '');
INSERT INTO `playercommands` VALUES ('info|infos', '18', null, '0', '0', null, '');
INSERT INTO `playercommands` VALUES ('ipdrop|dropip', '15', null, '0', '0', null, 'Redirige tous les drops de votre team vers le maitre.');
INSERT INTO `playercommands` VALUES ('jetmax|maxjet|jp', '28', null, '0', '0', null, 'Permet d\'avoir le jet max sur un item. (ex : .jetmax all)');
INSERT INTO `playercommands` VALUES ('kralaclose|closekrala', '34', null, '0', '0', null, 'Permet de fermer la porte du kralamour.');
INSERT INTO `playercommands` VALUES ('kralaopen|openkrala', '33', null, '0', '0', null, 'Permet d\'ouvrir la porte du kralamour.');
INSERT INTO `playercommands` VALUES ('level', '14', null, '0', '0', null, 'Permet de fixer son level à une valeur. (Ex : .level 100)');
INSERT INTO `playercommands` VALUES ('master|maitre|sensei', '3', null, '0', '0', null, 'Permet de déplacer sa team à l\'aide d\'un seul terminal.');
INSERT INTO `playercommands` VALUES ('neutre', '32', null, '0', '0', null, 'Permet de passer en Alignement Neutre.');
INSERT INTO `playercommands` VALUES ('noall', '2', null, '0', '0', null, 'Permet de désactiver le chat all.');
INSERT INTO `playercommands` VALUES ('onboard', '6', '6894,8575,7114,739,694,6980,7754,7115,8474,8472,8877,9464;false', '0', '0', null, 'Vous donne tout ce dont vous avez besoin pour effectuer des tests.');
INSERT INTO `playercommands` VALUES ('parcho', '9', null, '0', '0', null, 'Permet de se parcho 101 dans tous les éléments.');
INSERT INTO `playercommands` VALUES ('pass', '12', null, '0', '0', null, 'Permet de passer vos tours automatiquement en combat.');
INSERT INTO `playercommands` VALUES ('phoenix', '0', '8534,267', '0', '0', null, 'Permet de se téléporter à la statue du Phoenix.');
INSERT INTO `playercommands` VALUES ('points|point', '13', null, '0', '0', null, 'Permet de voir ses points de boutique.');
INSERT INTO `playercommands` VALUES ('poutch', '0', '534,372', '0', '0', null, 'Permet de se téléporter au poutch.');
INSERT INTO `playercommands` VALUES ('prestige', '7', null, '0', '0', null, 'Permet de voir tout ce qui concerne les prestiges.');
INSERT INTO `playercommands` VALUES ('pvm', '0', '957,223', '0', '0', null, 'Permet de se téléporter à la map pvm.');
INSERT INTO `playercommands` VALUES ('pvp', '0', '952,295', '0', '0', null, 'Permet de se téléporter à la map pvp.');
INSERT INTO `playercommands` VALUES ('restat', '16', null, '0', '0', null, 'Permet de remettre ses caractéristiques à 0.');
INSERT INTO `playercommands` VALUES ('spellmax|maxspell', '10', null, '0', '0', null, 'Permet de monter ses sorts au niveau max.');
INSERT INTO `playercommands` VALUES ('staff|equipe', '19', null, '0', '0', null, 'Permet de voir les gens du staff.');
INSERT INTO `playercommands` VALUES ('start', '0', '164,298', '0', '0', null, 'Permet de se téléporter à la map de départ.');
INSERT INTO `playercommands` VALUES ('tp|tpesclaves|tpmaster|tpmaitre', '4', null, '0', '0', null, 'Permet de téléporter ses membres du groupe ayants la même IP si le mode maitre est activé.');
INSERT INTO `playercommands` VALUES ('transfer', '5', null, '0', '0', null, 'Permet de transférer toutes ses ressources en banque. (Lag après 30 items.)');
INSERT INTO `playercommands` VALUES ('vie|life', '26', null, '0', '0', null, 'Permet de restaurer 100% de sa vie.');
