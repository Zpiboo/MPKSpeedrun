package io.github.zpiboo.mpkspeedrun.util;

import io.github.zpiboo.mpkspeedrun.api.world.IWorld;

import java.util.Arrays;

public class Proxy {
    public static IWorld WORLD;

    public static final String BASE_PACKAGE = "io.github.zpiboo.mpkspeedrun.compatibility";

    public static boolean init(String mcVersion) {
        if (mcVersion.matches("\\d+w\\d+[a-z]")) return false;

        String versionPackage = getPackageForVersion(mcVersion);
        if (versionPackage == null) return false;

        String compatibilityPackage = BASE_PACKAGE + "." + versionPackage;
        WORLD = loadImpl(compatibilityPackage, "world.WorldImpl");

        return true;
    }

    private static String getPackageForVersion(String mcVersion) {
        int[] version = Arrays.stream(
                mcVersion.split("[+-]")[0].split("\\.")
        ).mapToInt(Integer::parseInt).toArray();

        int major = version[0];
        int minor = version[1];
        int patch = version.length > 2 ? version[2] : 0;

        if (major != 1) return null;

        if (minor == 8 && patch == 9) {
            return "forge_1_8_9";
        } else if (minor == 21 && 6 <= patch && patch < 9) {
            return "fabric_1_21_6";
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T loadImpl(String compatibilityPackage, String implClassName) {
        String className = compatibilityPackage + "." + implClassName;
        try {
            Class<T> implClass = (Class<T>) Class.forName(className);
            return implClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
