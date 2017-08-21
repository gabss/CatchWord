package it.catchword.util;

import it.catchword.config.Constant;

/**
 * Created by Stefano on 09/04/2016.
 */
public class Debug {

    public static void print(String message){
        if(Constant.DEBUG)
            System.out.println(message);
    }
}
