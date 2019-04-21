package io.ucia0xff.fe.actor;

import java.util.HashMap;
import java.util.Map;

public class Actors {
    public static Actor actor;
    public static Map<String, Actor> actors = new HashMap<String, Actor>();

    static {
        actor = new Actor("Natasha");
        actors.put(actor.getActorKey(), actor);
        actor = new Actor("MautheDog");
        actors.put(actor.getActorKey(), actor);
        actor = new Actor("Walter");
        actors.put(actor.getActorKey(), actor);
    }

    public static Actor getActor(int[] xyTile) {
        for (Actor actor : actors.values()) {
            if (actor.getXyInMapTile()[0] == xyTile[0] && actor.getXyInMapTile()[1] == xyTile[1]) {
                return actor;
            }
        }
        return null;
    }

    public static Actor getActor(String actorKey) {
        for (Actor actor : actors.values()) {
            if (actor.getActorKey().equals(actorKey)) {
                return actor;
            }
        }
        return null;
    }

    public static Map<String, Actor> getActors() {
        return actors;
    }
}
