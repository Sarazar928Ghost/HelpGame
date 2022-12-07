package org.starloco.locos.kernel;

import org.starloco.locos.database.Database;
import org.starloco.locos.util.Points;

import java.io.*;
//import java.util.ArrayList;

public class Config {

    public static final Config singleton = new Config();
    public static int config = 0;

    public final long startTime = System.currentTimeMillis();
    public boolean HALLOWEEN = false, NOEL = false, HEROIC = false;

    public String NAME, url, startMessage = "", colorMessage = "B9121B";
    public boolean autoReboot = true, allZaap = false, allEmote = false, onlyLocal = false;
    public int startMap = 0, startCell = 0;
    public int rateKamas = 1, rateDrop = 1, rateHonor = 1, rateJob = 1, rateFm = 1;
    public float rateXp = 1;
    public int erosion=10;
    
    //RevIA/Ligne de vue
    //public boolean mapAsBlocked=false, fightAsBlocked=false, tradeAsBlocked=false;
    
    public int AIDelay=100, AIMovementCellDelay=180, AIMovementFlatDelay=700, craftDelay=200, gameActionDelay=5; //delay in ms

    public Points points = new Points() {
        @Override
        public int load(String user) {
            return Database.getStatics().getAccountData().loadPointsWithoutUsersDb(user);
        }

        @Override
        public void update(int id, int points) {
            Database.getStatics().getAccountData().updatePointsWithoutUsersDb(id, points);
        }
    };

    public static Config getInstance() {
        return singleton;
    }

    public void set(int i) {
        config = i;
        switch (i) {
            case 601 ://Local
                //Exchange
                Main.exchangePort = 666;
                Main.exchangeIp = "127.0.0.1";
                //BD
                Main.loginHostDB = "127.0.0.1";
                Main.loginNameDB = "login";
                Main.loginUserDB = "root";
                Main.loginPassDB = "";
                Main.loginPortDB = "3306";
                //Game
                Main.gamePort = 5555;
                Main.hostDB = "127.0.0.1";
                Main.nameDB = "game";
                Main.userDB = "root";
                Main.passDB = "";
                Main.portDB = "3306";
                Main.Ip = "127.0.0.1";   // Seule IP à changer pour Hamachi
                this.NAME = "Eratz";
                this.url = "nashira";
                this.autoReboot = true;
                break;

            case 4://Aestia Ankalike Nostalgy
                //Exchange
                Main.exchangePort = 667;
                Main.exchangeIp = "91.134.164.189";
                //BD
                Main.loginHostDB = "91.134.164.188";
                Main.loginNameDB = "aestia_login";
                Main.loginUserDB = "aestia_admin";
                Main.loginPassDB = "MTkO@CFsb^q*";
                Main.loginPortDB = "3306";
                //Game
                Main.gamePort = 5559;
                Main.hostDB = "91.134.164.188";
                Main.nameDB = "aestia_nostalgy";
                Main.userDB = "aestia_admin";
                Main.passDB = "MTkO@CFsb^q*";
                Main.portDB = "3306";
                Main.Ip = "91.134.164.191";

                this.NAME = "Nostalgy";
                this.url = "http://aestia.eu/";
                this.autoReboot = true;
                break;
            case 5://Aestia Ankalike Jiva
                //Exchange
                Main.exchangePort = 667;
                Main.exchangeIp = "91.134.164.189";
                //BD
                Main.loginHostDB = "91.134.164.188";
                Main.loginNameDB = "aestia_login";
                Main.loginUserDB = "aestia_admin";
                Main.loginPassDB = "MTkO@CFsb^q*";
                Main.loginPortDB = "3306";
                //Game
                Main.gamePort = 5557;
                Main.hostDB = "91.134.164.188";
                Main.nameDB = "aestia_game";
                Main.userDB = "aestia_admin";
                Main.passDB = "MTkO@CFsb^q*";
                Main.portDB = "3306";
                Main.Ip = "91.134.164.190";

                this.NAME = "Jiva";
                this.url = "http://aestia.eu/";
                this.autoReboot = true;
                break;
            case 6://Aestia Ankalike Silouate
                //Exchange
                Main.exchangePort = 667;
                Main.exchangeIp = "91.134.164.189";
                //BD
                Main.loginHostDB = "91.134.164.188";
                Main.loginNameDB = "aestia_login";
                Main.loginUserDB = "aestia_admin";
                Main.loginPassDB = "MTkO@CFsb^q*";
                Main.loginPortDB = "3306";
                //Game
                Main.gamePort = 5558;
                Main.hostDB = "91.134.164.188";
                Main.nameDB = "aestia_silouate";
                Main.userDB = "aestia_admin";
                Main.passDB = "MTkO@CFsb^q*";
                Main.portDB = "3306";
                Main.Ip = "91.134.164.190";

                this.NAME = "Silouate";
                this.url = "http://aestia.eu/";
                this.autoReboot = true;
                break;
        }
    }

    public void load() {
        FileReader file = null;
        try {
            file = new FileReader("config.txt");
        } catch (FileNotFoundException ignored) {}
        if (file != null) {
            try {
                BufferedReader config = new BufferedReader(new FileReader("config.txt"));
                String line;
                while ((line = config.readLine()) != null) {
                    if (line.split("=").length == 1)
                        continue;

                    String param = line.split("=")[0].trim().replace(" ", "");
                    String value = line.split("=")[1].trim();

                    if (value.isEmpty() || value.equals(" "))
                        continue;

                    switch (param.toUpperCase()) {
                        case "SERVER_ID":
                            Main.serverId = Integer.parseInt(value);
                            break;
                        case "SERVER_KEY":
                            Main.key = value;
                            break;
                        case "CONFIG_ID":
                            this.set(Integer.parseInt(value));
                            break;
                        case "DEBUG":
                            Main.modDebug = value.equals("true");
                            break;
                        case "USE_LOG":
                            Logging.USE_LOG = value.equals("true");
                            break;
                        case "SUBSCRIBER":
                            Main.useSubscribe = value.equals("true");
                            break;
                        case "START_PLAYER":
                            try {
                                this.startMap = Integer.parseInt(value.split("\\,")[0]);
                                this.startCell = Integer.parseInt(value.split("\\,")[1]);
                            } catch (Exception e) {
                                // ok
                            }
                            break;
                        case "ALL_ZAAP":
                            this.allZaap = value.equals("true");
                            break;
                        case "ALL_EMOTE":
                            this.allEmote = value.equals("true");
                            break;
                        case "RATE_XP":
                            this.rateXp = Float.parseFloat(value);
                            break;
                        case "RATE_DROP":
                            this.rateDrop = Integer.parseInt(value);
                            break;
                        case "RATE_JOB":
                            this.rateJob = Integer.parseInt(value);
                            break;
                        case "RATE_KAMAS":
                            this.rateKamas = Integer.parseInt(value);
                            break;
                        case "RATE_FM":
                            this.rateFm = Integer.parseInt(value);
                            break;
                        case "MESSAGE":
                            this.startMessage = value;
                            break;
                        case "NOEL":
                            this.NOEL = value.equals("true");
                            break;
                        case "HALLOWEEN":
                            this.HALLOWEEN = value.equals("true");
                            break;
                        case "HEROIC":
                            this.HEROIC = value.equals("true");
                            break;
                    }
                }
                config.close();
                file.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedWriter config = new BufferedWriter(new FileWriter("config.txt", true));
                String str = "## Configuration file of StarLoco ##\n\n"
                        + "## Server information : \n" + "SERVER_ID = 1\n"
                        + "SERVER_KEY = jiva\n" + "CONFIG_ID = 1\n"
                        + "DEBUG = false\n" + "USE_LOG = true\n"
                        + "SUBSCRIBER = false\n" + "PUB1 = \n" + "PUB2 = \n"
                        + "PUB3 = \n" + "START_PLAYER = 0,0\n"
                        + "ALL_ZAAP = false\n" + "ALL_EMOTE = false\n"
                        + "MESSAGE = Bienvenue sur <b>StarLoco</b> !\n\n"
                        + "## Server rate : \n" + "RATE_XP = 1\n"
                        + "RATE_DROP = 1\n" + "RATE_JOB = 1\n"
                        + "RATE_KAMAS = 1\n" + "RATE_FM = 1";
                config.write(str);
                config.newLine();
                config.flush();
                config.close();

                Main.logger.info("The configuration file was created.");
                this.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    

}