package io.github.srdjanv.localgitdependency.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public final class VersionUtil {

    private static final Pattern pattern = Pattern.compile("^(\\d+\\.\\d+\\.\\d+)(?:-(\\w+))?$");

    public static LGDVersion version(String version) {
        return new LGDVersion(version);
    }

    public static boolean equal(String main, String second) {
        return equal(version(main), version(second));
    }

    public static boolean equal(LGDVersion main, LGDVersion second) {
        return main.compareTo(second) == 0;
    }

    public static boolean smaller(String main, String second) {
        return smaller(version(main), version(second));
    }

    public static boolean smaller(LGDVersion main, LGDVersion second) {
        return main.compareTo(second) < 0;
    }

    public static boolean greater(String main, String second) {
        return greater(version(main), version(second));
    }

    public static boolean greater(LGDVersion main, LGDVersion second) {
        return main.compareTo(second) > 0;
    }

    public static boolean check(String main, String second, Predicate<LGDVersionCheck> consumer) {
        return check(version(main), version(second), consumer);
    }

    public static boolean check(LGDVersion main, LGDVersion second, Predicate<LGDVersionCheck> consumer) {
        var lgd = new LGDVersionCheck(main, second);
        return consumer.test(lgd);
    }

    public static final class LGDVersionCheck {
        private final LGDVersion main, second;

        private LGDVersionCheck(LGDVersion main, LGDVersion second) {
            this.main = main;
            this.second = second;
        }

        public boolean equal() {
            return VersionUtil.equal(main, second);
        }

        public boolean greater() {
            return VersionUtil.greater(main, second);
        }

        public boolean smaller() {
            return VersionUtil.smaller(main, second);
        }
    }

    public static final class LGDVersion implements Comparable<LGDVersion> {
        private final int[] version = new int[3];
        private final boolean dev;

        private LGDVersion(String version) {
            var mather = pattern.matcher(version);
            if (mather.matches()) {
                String[] split = mather.group(1).split("\\.");
                dev = "dev".equals(mather.group(2));
                if (split.length != 3)
                    throw new IllegalArgumentException("Invalid version string length: " + split.length);
                for (int i = 0; i < split.length; i++) this.version[i] = Integer.parseInt(split[i]);
            } else throw new IllegalArgumentException("Invalid version string: " + version);
        }

        @Override
        public int compareTo(@NotNull VersionUtil.LGDVersion other) {
            for (int i = 0; i < version.length && i < other.version.length; i++) {
                if (version[i] > other.version[i]) return 1;
                if (other.version[i] > version[i]) return -1;
            }
            if (dev != other.dev) {
                if (dev) return 1;
                return -1;
            }
            return 0;
        }
    }

    private VersionUtil() {}
}
