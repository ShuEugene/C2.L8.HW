package transport;

import transport.tecnicalSpecifications.CarBody;

import java.util.LinkedList;
import java.util.List;

public class Car extends Transport implements Diagnosed {

    private static List<Competing> competitionParticipants = new LinkedList<>();

    public static List<Competing> getCompetitionParticipants() {
        return competitionParticipants;
    }


    private CarBody type;


    public Car(String brand, String model, float engineVolume) {
        this(brand, model, engineVolume, null);
    }

    public Car(String brand, String model, float engineVolume, CarBody type) {
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


    CarBody getType() {
        return type;
    }
}