package io.ucia0xff.fe.career;

import java.util.HashMap;
import java.util.Map;

public class Careers {
    public static Career career;
    public static Map<String, Career> careers = new HashMap<String, Career>();

    static {
        career = new Career("Lord");
        careers.put(career.getKey(), career);
        career = new Career("Paladin");
        careers.put(career.getKey(), career);
        career = new Career("Fighter");
        careers.put(career.getKey(), career);
    }

    public static Career getCareer(String careerKey) {
        for (Career career : careers.values()) {
            if (career.getKey().equals(careerKey)) {
                return career;
            }
        }
        return null;
    }

    public static void addCareer(Career career) {
        careers.put(career.getKey(), career);
    }
}
