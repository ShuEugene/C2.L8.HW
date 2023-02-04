import auxiliaryLibrary.TextService;
import specialists.Driver;
import specialists.Mechanic;
import specialists.Mechanic.MechanicException;
import specialists.Mechanic.RepairType;
import transport.Bus;
import transport.Car;
import transport.Transport.Category;
import transport.Transport.TransportException;
import transport.Truck;
import transport.tecnicalSpecifications.CarBody;
import transport.tecnicalSpecifications.LoadCapacity;
import transport.tecnicalSpecifications.PassangerCapacity;

public class Main {
    public static void main(String[] args) {

        Car lada21099 = new Car("Лада", "21099", 1.5f, CarBody.SEDAN);
//        Car ladaGranta = new Car("Лада", "Гранта", 1.6f, CarBody.SEDAN);
//        Car ladaVesta = new Car("Лада", "Веста", 1.6f, CarBody.SEDAN);
        Car moskvich = new Car("Москвич", "2140", 1.5f);
        Truck kamaz = new Truck("КамАЗ", "43253", 4.5f, LoadCapacity.N2);
//        Truck uralNext = new Truck("Урал", "Next", 6.7f, LoadCapacity.N3);
//        Truck zil5301Bychok = new Truck("ЗИЛ", "5301 \"Бычок\"", 5f, LoadCapacity.N1);
//        Truck kraz = new Truck("КрАЗ", "6505", 11f, LoadCapacity.N2);
//        Bus ikarus = new Bus("Ikarus", "250", 10.7f, PassangerCapacity.MEDIUM);
//        Bus liaz = new Bus("ЛиАз", "5292", 6.9f,PassangerCapacity.EXTRA_LARGE);
        Bus pazVector = new Bus("ПАЗ", "Вектор Next 7.6", 4.4f, PassangerCapacity.LARGE);
//        Bus nefaz5299 = new Bus("НефАЗ", "5299 \"Городской\"", 11.8f, PassangerCapacity.SMALL);

//        Competing.showParticipants(Transport.getCompetitionParticipants());

        Driver<Car> ivan = new Driver<>("Иван", moskvich, 15);
        Driver<Truck> stepan = new Driver<>("Степан", kamaz, 10);
        Driver<Bus> vasiliy = new Driver<>("Василий", pazVector, 5);
//        Driver<Bus> fedor = new specialists.Driver<>("Фёдор", (Transport.Category) null);
        Driver<Car> eulampiy = new Driver<>("Евлампий", lada21099);

//        try {
//            kraz.setDriver(fedor);
//        } catch (TransportException | DriverException e) {
//            TextService.printException(e);
//        }

//        vasiliy.started();
//        ivan.started();
//        stepan.started();
//
//        ivan.transportRefuel();
//
//        ivan.finished();
//        stepan.finished();
//        vasiliy.finished();
//
//        ladaVesta.printType();
//        moskvich.printType();
//        zil5301Bychok.printType();
//        ikarus.printType();
//
        lada21099.setRepairType(RepairType.REPAIR);
        moskvich.setRepairType(RepairType.SERVICE);
        kamaz.setRepairType(RepairType.REPAIR);
        pazVector.setRepairType(RepairType.REPAIR);

//        Transport.getDiagnosesOfTheCompetitors();

        Mechanic potapych = null;
        try {
            potapych = new Mechanic("Бывалый", "Семён Потапович", Category.DLC_B, Category.DLC_C, Category.DLC_D);
            potapych.addRepaired(lada21099, moskvich, kamaz, pazVector);
//            potapych.showRepaired();
        } catch (MechanicException | TransportException e) {
            TextService.printException(e);
        }

        Mechanic trofimych = null, trofimych2 = null;
        try {
            trofimych = new Mechanic("Ворчалкин", "Егор Трофимович", Category.DLC_B, Category.DLC_C, Category.DLC_D);
            trofimych2 = new Mechanic("Ворчалкин", "Егор Трофимович", Category.DLC_B, Category.DLC_C, Category.DLC_D);
            trofimych.addRepaired(kamaz);
            trofimych2.addRepaired(kamaz);
//            trofimych.showRepaired();
        } catch (MechanicException | TransportException e) {
            TextService.printException(e);
        }

        kamaz.showSpecialists();

//        ServiceStation station;
//        try {
//            station = new ServiceStation("СТО-1", potapych, trofimych);
//            station.performDiagnostic(new Repaired(kamaz));
//            station.addRepaired(kamaz);
//            station.performDiagnostic(new Repaired(kamaz));
//
//        } catch (ServiceException | TransportException e) {
//            TextService.printException(e);
//        }

//        Physical karavaevPA;
//        try {
//            karavaevPA = new Physical("Караваев Павел Андреевич",
//                    new Sponsored(kamaz),
//                    new Sponsored(kraz, 50_000),
//                    new Sponsored(pazVector, 25_000));

//            ikarus.showSponsors();
//            ikarus.addSponsor(karavaevPA, 35_000);

//            karavaevPA.setSponsorshipAmount(karavaevPA.getSponsored(kamaz), 30_000);
//            karavaevPA.addSponsored(new Sponsored(kamaz));
//            karavaevPA.showSponsored();
//        } catch (TransportException | SponsorException e) {
//            TextService.printException(e);
//        }
//
//        Physical grozniyIV;
//        try {
//            grozniyIV = new Physical("Грозный Иван Васильевич",
//                    new Sponsored(kamaz, 123),
//                    new Sponsored(kraz, 10123));

//            grozniyIV.showSponsored();
//        } catch (Sponsor.SponsorException e) {
//            TextService.printException(e);
//        }
//
//        pazVector.showSponsors();
//
//        Juridical hotHeads;
//        try {
//            hotHeads = new Juridical("Горячие головы",
//                    new Sponsored(kamaz, 200_000),
//                    new Sponsored(ikarus, 300_000));

//            hotHeads.showSponsored();
//        } catch (SponsorException e) {
//            TextService.printException(e);
//        }
//
//        kamaz.showSponsors();
    }
}