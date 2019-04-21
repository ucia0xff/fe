package io.ucia0xff.fe.item;

import java.util.HashMap;
import java.util.Map;

public class Items {
    public static Item item;
    public static Map<String, Item> items = new HashMap<>();

    static {
//        item = new Item("IronSword");
//        items.put(item.getName(), item);
    }

    public static Item getItem(String itemKey) {
        for (Item item : items.values()) {
            if (item.getKey().equals(itemKey)) {
                return item;
            }
        }
        return null;
    }

    public static void addItem(Item item) {
        items.put(item.getKey(), item);
    }

    public static Map<String, Item> getItems() {
        return items;
    }
}
