package transport;

import transport.tecnicalSpecifications.PassangerCapacity;

import java.util.LinkedList;
import java.util.List;

public class Bus extends Transport implements Competing{

    private static List<Competing> competitionParticipants = new LinkedList<>();

    public static List<Competing> getCompetitionParticipants() {
        return competitionParticipants;
    }


    private final PassangerCapacity type;


    public Bus(String brand, String model, float engineVolume) {
        this(brand, model, engineVolume, null);
    }

    public Bus(String brand, String model, float engineVolume, PassangerCapacity type) {
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


    PassangerCapacity getType() {
        return type;
    }
}