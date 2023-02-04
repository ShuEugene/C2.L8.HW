package transport.tecnicalSpecifications;

public enum LoadCapacity {
    N1(3.5f),
    N2(3.5f, 12),
    N3(12, 0);

    private float minCapacity, maxCapacity;


    LoadCapacity(float maxCapacity) {
        this(0f, maxCapacity);
    }

    LoadCapacity(float minCapacity, float maxCapacity) {
        this.minCapacity = minCapacity;
        this.maxCapacity = maxCapacity;
    }


    public float getMinCapacityCapacity() {
        return minCapacity;
    }

    public float getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public String toString() {
        return "грузоподъёмность: " + TS.getCapacityString(minCapacity, maxCapacity) + " тонн";
    }
}
