package io.ucia0xff.fe.actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Actors {
    public static Actor actor;
    public static ArrayList<Actor> actors = new ArrayList<>();

    static {
        actor = new Actor("Eirika");
        actors.add(actor);
        actor = new Actor("Seth");
        actors.add(actor);
        actor = new Actor("Innes");
        actors.add(actor);
        actor = new Actor("ONeill");
        actors.add(actor);
        actor = new Actor("Enemy_0_0");
        actors.add(actor);
        actor = new Actor("Enemy_0_1");
        actors.add(actor);
    }

    public static Actor getActor(int[] xyTile) {
        for (Actor actor : actors) {
            if ((actor.getXyInMapTile()[0] == xyTile[0]) && (actor.getXyInMapTile()[1] == xyTile[1])) {
                return actor;
            }
        }
        return null;
    }

    public static void addActor(Actor actor) {
        actors.add(actor);
    }

    public static void restore() {
        Iterator<Actor> iterator = actors.iterator();
        while (iterator.hasNext()) {
            Actor actor = iterator.next();
            if (!actor.isVisible()) {
                iterator.remove();
            }
        }
    }

    public static ArrayList<Actor> getActors() {
        return actors;
    }
}
