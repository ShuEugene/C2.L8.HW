package transport.tecnicalSpecifications;

public enum CarBody {
    SEDAN("«Седан»"),
    HATCHBACK("«Хетчбэк»"),
    COMPARTMENT("«Купе»"),
    STATION_WAGON("«Универсал»"),
    OFF_ROAD("«Внедорожник»"),
    CROSSOVER("«Кроссовер»"),
    PICKUP("«Пикап»"),
    VAN("«Фургон»"),
    MINIVAN("«Минивэн»");

    private String title;

    CarBody(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "тип кузова: " + title;
    }
}
