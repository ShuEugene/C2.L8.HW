package transport.tecnicalSpecifications;

public enum PassangerCapacity {
    VERY_SMALL(10),
    SMALL(25),
    MEDIUM(40, 50),
    LARGE(60, 80),
    EXTRA_LARGE(100, 120);

    private int minCapacity, maxCapacity;

    PassangerCapacity(int maxCapacity) {
        this(0, maxCapacity);
    }

    PassangerCapacity(int minCapacity, int maxCapacity) {
        this.minCapacity = minCapacity;
        this.maxCapacity = maxCapacity;
    }

    public int getMinCapacity() {
        return minCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public String toString() {
        return "вместимость: " + TS.getCapacityString(minCapacity, maxCapacity) + " человек(-а)";
    }
}
