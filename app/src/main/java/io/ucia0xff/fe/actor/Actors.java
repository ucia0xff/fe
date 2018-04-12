package io.ucia0xff.fe.actor;

import java.util.HashMap;
import java.util.Map;

public class Actors {
    public static Map<String, Actor> actors = new HashMap<String, Actor>();

    static {
        Actor actor = new Actor("Natasha");
        actors.put(actor.getActorKey(), actor);
    }

    public static Actor getActor(int[] xyTile){
        for (Actor actor:actors.values()) {
            if(actor.getXyTile()[0]==xyTile[0] && actor.getXyTile()[1]==xyTile[1])
                return actor;
        }
        return null;
    }

    public static Map<String, Actor> getActors() {
        return actors;
    }
}
