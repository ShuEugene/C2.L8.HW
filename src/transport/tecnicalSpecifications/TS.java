package transport.tecnicalSpecifications;

public class TS {

    public static String getCapacityString(float minCapacity, float maxCapacity) {
        if (minCapacity <= 0 && maxCapacity <= 0) {
            return "<не указана>.";
        }
        if (minCapacity <= 0) {
            return " до " + maxCapacity;
        } else if (maxCapacity <= 0) {
            return " от " + minCapacity;
        } else {
            return " от " + minCapacity + " до " + maxCapacity;
        }

    }

    public static String getCapacityString(int minCapacity, int maxCapacity) {
        if (minCapacity <= 0 && maxCapacity <= 0) {
            return "<не указана>.";
        }
        if (minCapacity <= 0) {
            return " до " + maxCapacity;
        } else if (maxCapacity <= 0) {
            return " от " + minCapacity;
        } else {
            return " от " + minCapacity + " до " + maxCapacity;
        }

    }
}
