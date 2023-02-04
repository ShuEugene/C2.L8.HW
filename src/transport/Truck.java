package transport;

import transport.tecnicalSpecifications.LoadCapacity;

import java.util.LinkedList;
import java.util.List;

public class Truck extends Transport implements Diagnosed {

    private static List<Competing> competitionParticipants = new LinkedList<>();

    public static List<Competing> getCompetitionParticipants() {
        return competitionParticipants;
    }


    private LoadCapacity type;

    public Truck(String brand, String model, float engineVolume) {
        this(brand, model, engineVolume, null);
    }

    public Truck(String brand, String model, float engineVolume, LoadCapacity type) {
        super(brand, model, engineVolume);
        this.type = type;
        competitionParticipants.add(this);
        Transport.getCompetitionParticipants().add(this);
    }


    @Override
    public void printType() {
        if (type == null) {
            System.out.println("\nДанных по " + getTitle() + " недостаточно.");
            return;
        }
        System.out.println("\nТип " + getTitle() + " - «" + getType().name() + "» (" + type + ").");
    }


    LoadCapacity getType() {
        return type;
    }
}