package it.catchword.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class contains game's constant
 */
public class Constant {

    public static int HOLES_NUMBER = 6;
    public static int MAX_SIMULTANEOUS_WORDS = 4;
    public static int MANCHE_TIME = 30;
    public static int MAX_OWNER_DELAY = 2000;
    public static int OWNER_REFRESH_TIME = 100;
    public static int MAX_PLAYER_NUMBER = 6;


    public final static int USER_STATUS_WAIT = 0;
    public final static int USER_STATUS_PLAY = 1;
    public final static int USER_STATUS_LOSE = 2;
    public final static int USER_STATUS_WIN = 3;
    public final static int USER_STATUS_DISCONNECTED = 4;
    public final static int USER_STATUS_INIT = 5;

    public final static int GAME_STATUS_INIT = 0;
    public final static int GAME_STATUS_WAIT = 1;
    public final static int GAME_STATUS_ACTIVE = 2;
    public final static int GAME_STATUS_END = 3;

    public static int FRAME_PER_SECOND = 30;

    public static boolean DEBUG = true;

    public static String SERVER_NAME = "Server";
    public static String SERVER_ADDRESS = "clara.cs.unibo.it";
    public static int SERVER_PORT = 1100;
    public static int CLIENT_PORT = 1101;

    public final static int MESSAGE_TYPE_CHANNEL_LIST = 1;
    public final static int MESSAGE_TYPE_CHANNEL = 2;
    public final static int MESSAGE_TYPE_JOIN = 3; //RemoteUser
    public final static int MESSAGE_TYPE_START_GAME = 4; //long time
    public final static int MESSAGE_TYPE_PINGPONG = 5; //null
    public final static int MESSAGE_TYPE_OWNER = 6; //int
    public final static int MESSAGE_TYPE_MANCHE_END = 7; //null
    //Messaggi per assegnare i punti
    public final static int MESSAGE_TYPE_OK = 8; //null
    public final static int MESSAGE_TYPE_CONFIRM = 9; //RemoteUser
    public final static int MESSAGE_TYPE_WORD_CAUGHT = 10; //"word;score"
    ///
    public final static int MESSAGE_TYPE_WHOIS_OWNER = 11; //RemoteUser
    public final static int MESSAGE_TYPE_USER_DISCONNECTED = 12; //RemoteUser
    public final static int MESSAGE_TYPE_CHAT = 13; //ChatMessage


    //Error codes
    public final static int OK = 0;
    public final static int NOT_ENOUGH_PLAYERS = 1;

    public static void readConf(){
        try {
            BufferedReader br = new BufferedReader(new FileReader("resources/config"));
            Map<String, String> config = new HashMap<>();
            String current = br.readLine();
            while(current != null){
                config.put(current.split(":")[0], current.split(":")[1]);
                current = br.readLine();
            }


            if(config.containsKey("SERVER_ADDR"))
                SERVER_ADDRESS = config.get("SERVER_ADDR");
            if(config.containsKey("SERVER_PORT"))
                SERVER_PORT = Integer.parseInt(config.get("SERVER_PORT"));
            if(config.containsKey("DEBUG"))
                DEBUG = new Boolean(config.get("DEBUG"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
