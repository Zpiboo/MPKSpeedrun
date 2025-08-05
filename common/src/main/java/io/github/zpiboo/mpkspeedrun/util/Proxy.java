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
        } else if (minor == 19 && patch == 4) {
            return "fabric_1_19_4";
        } else if (minor == 20) {
            if (4 <= patch && patch < 6) {
                return "fabric_1_20_4";
            } else if (patch == 6) {
                return "fabric_1_20_6";
            }
        } else if (minor == 21) {
            if (0 <= patch && patch < 2) {
                return "fabric_1_21";
            } else if (2 <= patch && patch < 5) {
                return "fabric_1_21_3";
            } else if (patch == 5) {
                return "fabric_1_21_5";
            } else if (6 <= patch && patch < 9) {
                return "fabric_1_21_6";
            }
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
