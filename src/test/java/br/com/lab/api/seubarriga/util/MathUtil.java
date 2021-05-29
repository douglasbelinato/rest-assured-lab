package br.com.lab.api.seubarriga.util;

import java.util.Random;

public final class MathUtil {

    private MathUtil() {}

    public static int generateRandomInt() {
        return new Random().ints().findFirst().orElse(0);
    }
}
