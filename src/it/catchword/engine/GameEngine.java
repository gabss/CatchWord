package it.catchword.engine;

import it.catchword.config.Constant;
import it.catchword.gui.GamePanel;
import it.catchword.gui.StartPanel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Stefano on 01/03/2016.
 */
public class GameEngine {

    static List<GameObject> gameObjects = new ArrayList<>();
    static List<GameObject> graphicObjects = new ArrayList<>();

    public static void main(String[] args) {
        initialize();
        run();
    }

    /**
     * Inizialize the GameEngine.
     * <p>It starts the main gui panel.</p>
     */
    private static void initialize() {
        Constant.readConf();
        graphicObjects.add(new StartPanel());
    }

    /**
     * This method will call the update of all the listening GameObjects every frame
     */
    private static void run(){
        int i = 1;

        while(true) {
            boolean stop = false;

            long time = System.currentTimeMillis();
         /*   if(Constant.DEBUG)
                System.out.println("Frame number: " + (i++) + " Time: "+time);
          */
            List<GameObject> currents = new ArrayList<>();
            currents.addAll(gameObjects);
            for (GameObject go : currents) {
                if(!go.update()) stop = true;
            }
            currents.clear();
            currents.addAll(graphicObjects);
            for (GameObject go : currents) {
                if(!go.update()) stop = true;
            }

            if(stop) break;

            //  delay for each frame  -   time it took for one frame
            time = (1000 / Constant.FRAME_PER_SECOND) - (System.currentTimeMillis() - time);
            if (time > 0) {
                try {
                    Thread.sleep(time);
                }
                catch(Exception e){}
            }
        }
        System.exit(0);
    }

    /**
     * This method permits to add a GameObject to the GameEngine so that the GameObject will be updated every frame.
     * The GameObject must be different from null and it will be added to the list only if the list doesn't already contains it
     * @param go The GameObject to add
     */
    public static void addObject(GameObject go){
        if(go != null && !gameObjects.contains(go))
            gameObjects.add(go);
    }

    /**
     * This method permits to add a GameObject to the GameEngine so that the GameObject will be updated every frame.
     * The GameObject must be different from null and it will be added to the list only if the list doesn't already contain it.
     * The GameObject on the graphics list will be updated later than classic game objects so that they can refresh
     * graphic accordingly to the state of object.
     * @param go The GameObject to add
     */
    public static void addGraphic(GameObject go){
        if(go != null && !graphicObjects.contains(go))
            graphicObjects.add(go);
    }

    /**
     * This method permits to remove a GameObject from the graphic objects' list.
     * @param go The GameObject to remove, if found
     */
    public static void removeGraphic(GameObject go){
        Iterator<GameObject> it = graphicObjects.listIterator();

        while(it.hasNext()){
            if(it.next().equals(go))
                it.remove();
        }
    }


}
