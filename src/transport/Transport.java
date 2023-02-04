package transport;

import auxiliaryLibrary.DataService;
import auxiliaryLibrary.TextService;
import auxiliaryLibrary.TextService.PrintModes;
import specialists.*;
import specialists.Mechanic.RepairType;
import specialists.Sponsor.SponsorException;
import specialists.Sponsor.Sponsored;

import java.sql.Time;
import java.util.*;


public abstract class Transport implements Competing {

    public static class TransportException extends Exception {

        public TransportException() {
        }

        public TransportException(String message) {
            super(message);
        }
    }

    public static class DriverException extends Exception {

        public DriverException() {
        }

        public DriverException(String message) {
            super(message);
        }
    }

    public enum Category {
        DLC_B("B", "легковой автомобиль", Car.class),
        DLC_C("C", "грузовой автомобиль", Truck.class),
        DLC_D("D", "автобус", Bus.class),
        DLC_N("<отсутствует>", "<ТС неизвестного типа>", Transport.class);

        private final String title, transportType;
        private final Class<?> transportClass;

        Category(String title, String transportType, Class<?> transportClass) {
            this.title = title;
            this.transportType = transportType;
            this.transportClass = transportClass;
        }

        public String getTitle() {
            return "«" + title + "»";
        }

        public String getTransportType() {
            return transportType;
        }

        public Class<?> getTransportClass() {
            return transportClass;
        }
    }

    protected static final String UNKNOWN_INFO = "<Информация не указана>";


    private static List<Driver<Transport>> drivers = new ArrayList<>();
    private static final List<Competing> competitionParticipants = new LinkedList<>();

    public static void getDiagnosesOfTheCompetitors() {
        for (Competing participantTransport :
                competitionParticipants) {

            Transport diagnosedTransport = (Transport) participantTransport;
            String techCard = diagnosedTransport.getTechnicalCard();

            StringBuilder resultMessage = new StringBuilder("\nДиагностика " + techCard + " не проводилась.");

            if (!(participantTransport instanceof Diagnosed))
                resultMessage.replace(0, resultMessage.length(),
                        "\nСпециалисты по диагностированию данного вида ТС (" + diagnosedTransport.getCategory().transportType
                                + ") отсутствуют.\nДиагностика " + techCard + " исключена.");

            else {
                Diagnosed diagnosed = (Diagnosed) participantTransport;

                RepairType diagnosis = null;
                try {
                    diagnosis = diagnosed.performDiagnostic();
                } catch (DriverException e) {
                    System.out.println("\nПрохождение диагностики исключено: " + e.getMessage());
                    continue;
                }

                if (diagnosis != null) {
                    resultMessage.replace(0, resultMessage.length(),
                            String.format("\n" + techCard.replace(" л)", " л; водитель - " + diagnosedTransport.getDriver().getName() + ")")
                                    + " прошёл диагностику."));

                    switch (diagnosis) {
                        case REPAIR:
                            resultMessage.replace(resultMessage.length() - 1, resultMessage.length(),
                                    " и требует ремонта.");
                            break;
                        case SERVICE:
                            resultMessage.replace(resultMessage.length() - 1, resultMessage.length(),
                                    " и требует сервисного обслуживания.");
                            break;
                        case PROPERLY:
                            resultMessage.replace(resultMessage.length() - 1, resultMessage.length(),
                                    " и находится в исправном состоянии.");
                            break;
                        default:
                            resultMessage.replace(resultMessage.length() - 1, resultMessage.length(),
                                    "\n. Состояние не определено: требуется диагностика более высокого уровня.");
                    }
                }
            }

            System.out.println(resultMessage);
        }
    }


    private String brand, model;
    private float engineVolume;

    private Category category;

    private Driver<?> driver;

    private Time bestLapTime;
    private float maxLapSpeed;

    private RepairType repairType;
    private Set<Mechanic> mechanics;
    private ServiceStation serviceStation;

    private LinkedList<Sponsor> sponsorList = new LinkedList<>();


    protected Transport(String brand, String model, float engineVolume) {
        this(brand, model, engineVolume, null);
    }

    protected Transport(String brand, String model, float engineVolume, RepairType repairType) {
        setBrand(brand);
        setModel(model);
        setEngineVolume(engineVolume);
        setCategory();
        setRepairType(repairType);
    }


    public abstract void printType();


    public void started() {
        System.out.println("\n" + getTitle() + " начал движение.");
    }

    public void stopped() {
        System.out.println("\n" + getTitle() + " остановился.");
    }

    @Override
    public void pitStop() {
        System.out.println("\n" + getTitle() + " заехал в пит-стоп.");
    }

    public void showSpecialists() {
        String printTitle = "Специалисты и спонсоры " + getTechnicalCard() + ":";
        System.out.println("\n" + "=".repeat(printTitle.length()));
        System.out.println(printTitle);
        System.out.println("=".repeat(printTitle.length()));

        showDriverInfo();
        showSponsorsInfo();
        showMechanicsInfo();
    }

    public void showMechanicsInfo() {
        String techCard = getTechnicalCard();

        if (getRepairType() == RepairType.PROPERLY) {
            System.out.println("\n" + techCard + " полностью исправно и не требует внимания механиков.");
            return;
        }

        String printTitle = null;
        if (!DataService.isCorrect(getMechanics())) {
            printTitle = "\nДля обслуживания " + techCard + " механики пока не назначены.";

            if (repairType != RepairType.PROPERLY)
                printTitle = printTitle.replace(".",
                        ", несмотря на то, что ему требуется " + repairType.getTitle() + ".");

            System.out.println(printTitle);
            return;
        }

        if (mechanics.size() < 2)
            printTitle = "\nОбслуживанием " + techCard + " занимается " + ((Mechanic) mechanics.toArray()[0]).getInfo() + ".";

        else
            printTitle = "\nВ " + (repairType == RepairType.SERVICE ? "техобслуживании" : "ремонте")
                    + techCard + " задействованы следующие механики:";

        System.out.println(printTitle);
        if (mechanics.size() > 1)
            TextService.printList(getMechsList(), PrintModes.NUMBERED_LIST_PM);
    }

    public RepairType performDiagnostic() throws DriverException {
        if (driver == null)
            throw new DriverException("за " + getTitle() + " не закреплён водитель.");

        if (driver.getDriverLicenseCategory() == Category.DLC_N)
            throw new DriverException("у водителя (" + driver.getName() + ") отсутствуют водительские права.");

        return getRepairType();
    }

    public void showSponsAmountSum() {
        float sum = 0;
        try {
            sum = getSponsAmounSum();
        } catch (SponsorException e) {
            TextService.printException(e);
        }
        String resultMessage = sum > 0 ?
                String.format("\nОбщая сумма спонсорских взносов - %.2f руб..", sum)
                : "\nЗаезды " + getTechnicalCard() + " пока никто не спонсирует.";

        System.out.println(resultMessage);
    }

    public void showSponsorsInfo() {
        String[] sponsorList = getSponsorList();
        if (!DataService.isCorrect(sponsorList)) {
            System.out.println("\nУ " + getTechnicalCard() + " пока нет спонсоров.");
            return;
        }

        System.out.println("\nСпонсоры " + getTechnicalCard() + ":");
        TextService.printList(sponsorList, PrintModes.NUMBERED_LIST_PM);

        showSponsAmountSum();
    }

    //    Добавить новый кейс в свитч после создания нового типа Спонсора
    private void addSpecifiedSponsor(Sponsor sponsor, float sponsorshipAmount) throws SponsorException {

        switch (sponsor.getClass().getSimpleName()) {

            case "Physical":
            case "Juridical":
                try {
                    sponsor.addSponsored(new Sponsored(this), sponsorshipAmount);
                } catch (SponsorException e) {
                    TextService.printException(e);
                }
                break;

            default:
                throw new SponsorException("Неизвестный тип Спонсора.");
        }
    }

    public final void addSponsor(Sponsor sponsor) throws TransportException, SponsorException {
        addSponsor(sponsor, 0);
    }

    public final void addSponsor(Sponsor sponsor, float sponsorshipAmount) throws TransportException, SponsorException {

        if (sponsor == null)
            throw new SponsorException("Спонсор не указан.");

        if (sponsorList == null)
            sponsorList = new LinkedList<>();

        if (sponsorList.contains(sponsor))
            throw new SponsorException(getSponsorPrint(sponsor) + " уже спонсирует " + getTitle() + ".");

        if (sponsorList.offer(sponsor)) {

            if (!sponsor.getSponsored().contains(new Sponsored(this))) {
                addSpecifiedSponsor(sponsor, sponsorshipAmount);
            }

        } else
            throw new SponsorException("Добавление Спонсора прервано по неизвестной причине.");
    }

    public void showDriverInfo() {
        String trTypeTitle = "транспортным средством";
        switch (getCategory()) {
            case DLC_B:
                trTypeTitle = "легковым автомобилем";
                break;
            case DLC_C:
                trTypeTitle = "грузовым автомобилем";
                break;
            case DLC_D:
                trTypeTitle = "автобусом";
                break;
        }
        String techCard = getTechnicalCard();

        String printTitle = "\n За " + trTypeTitle + " " + techCard + " пока не закреплён водитель.";

        if (driver != null && DataService.isCorrect(driver.getName()))
            printTitle = "\nВодителем " + techCard + " является " + driver + ".";

        System.out.println(printTitle);
    }


    public static List<Competing> getCompetitionParticipants() {
        return competitionParticipants;
    }


    public final String getBrand() {
        if (!DataService.isCorrect(brand))
            setBrand();
        return brand;
    }

    private final void setBrand() {
        setBrand(null);
    }

    private final void setBrand(String brand) {
        if (DataService.isCorrect(brand))
            this.brand = brand;
        else
            this.brand = UNKNOWN_INFO;
    }

    public final String getModel() {
        if (!DataService.isCorrect(model))
            setModel();
        return model;
    }

    public final String getTitle() {
        return "«" + getBrand() + " " + getModel() + "»";
    }

    private final void setModel() {
        setModel(null);
    }

    private final void setModel(String model) {
        if (DataService.isCorrect(model))
            this.model = model;
        else
            this.model = UNKNOWN_INFO;
    }

    protected final String getStrEngineVolume() {
        if (engineVolume <= 0) {
            return UNKNOWN_INFO;
        }
        return engineVolume + " л";
    }

    protected final float getEngineVolume() {
        if (engineVolume <= 0)
            engineVolume = 1.5f;
        return engineVolume;
    }

    protected final void setEngineVolume(float engineVolume) {
        if (engineVolume == 0)
            this.engineVolume = 1.5f;
        else
            this.engineVolume = Math.abs(engineVolume);
    }

    public final String getTechnicalCard() {
        return String.format("«%s %s» (объём двигателя: %.1f л)", getBrand(), getModel(), getEngineVolume());
    }

    public final Category getCategory() {
        if (category == null)
            category = Category.DLC_N;

        return category;
    }

    public final void setCategory() {
        switch (this.getClass().getSimpleName()) {
            case "Car":
                category = Category.DLC_B;
                break;
            case "Truck":
                category = Category.DLC_C;
                break;
            case "Bus":
                category = Category.DLC_D;
                break;
            default:
                category = Category.DLC_N;
        }
    }

    public final String getDriverName() {

        if (driver == null)
            return "<не закреплён>";

        return driver.getName();
    }

    public final Driver<?> getDriver() {
        return driver;
    }

    public final void setDriver(Driver<?> driver) throws TransportException, DriverException {
        if (driver == null)
            throw new TransportException("Водитель не указан.");

        if (driver.getDriverLicenseCategory() != getCategory())
            throw new DriverException("Водитель не имеет права управлять данным типом транспортного средства.");

        this.driver = driver;
    }

    @Override
    public final Time getBestLapTime() {
        if (bestLapTime == null) {
            return null;
        }
        return bestLapTime;
    }

    public final void setBestLapTime(Time bestLapTime) {
        if (bestLapTime != null) {
            this.bestLapTime = bestLapTime;
        }
    }

    @Override
    public final float getMaximumLapSpeed() {
        if (maxLapSpeed <= 0) {
            return 0;
        }
        return maxLapSpeed;
    }

    public final void setMaxLapSpeed(float maxLapSpeed) {
        if (maxLapSpeed > 0) {
            this.maxLapSpeed = maxLapSpeed;
        }
    }

    //    Добавить новый кейс в свитч после создания нового типа Спонсора
    private final String getSponsorPrint(Sponsor sponsor) {
        switch (sponsor.getClass().getSimpleName()) {
            case "Physical":
                return ((Physical) sponsor).getName();
            case "Juridical":
                return ((Juridical) sponsor).getTitle();
            default:
                return "<неизвестный спонсор>";
        }
    }

    public final List<Sponsor> getSponsors() {
        if (sponsorList == null) {
            sponsorList = new LinkedList<>();
        }
        return sponsorList;
    }

    private final String[] getSponsorList() {

        if (!DataService.isCorrect(this.sponsorList))
            return null;

        String[] sponsorList = new String[this.sponsorList.size()];
        int index = -1;

        for (Sponsor sponsor :
                this.sponsorList) {

            Sponsored sponsored = null;
            if (sponsor != null) {

                for (Sponsored current :
                        sponsor.getSponsored()) {

                    try {
                        if (current.equals(new Sponsored(this))) {
                            sponsored = current;
                            break;
                        }
                    } catch (SponsorException e) {
                        sponsorList[++index] = e.getClass() + ": " + e.getMessage();
                    }
                }
            }

            if (sponsored != null) {
                float sponsorshipAmount = sponsored.getSponsorshipAmount();
                sponsorList[++index] = String.format("%s (сумма - %.2f руб.)", sponsor, sponsorshipAmount);
                if (sponsorshipAmount <= 0)
                    sponsorList[index] = sponsorList[index].replace("0,00 руб.", "пока не обозначена");
            }
        }

        if (index < 0)
            return null;

        return sponsorList;
    }

    private final float getSponsAmounSum() throws SponsorException {
        if (!DataService.isCorrect(sponsorList))
            return 0;

        Sponsored thisSponsored = new Sponsored(this);
        float sponSum = 0;

        for (Sponsor current :
                getSponsors())
            if (current.getSponsored().contains(thisSponsored))
                sponSum += current.getSponsored().get(current.getSponsored().indexOf(thisSponsored)).getSponsorshipAmount();

        return sponSum;
    }

    public final RepairType getRepairType() {
        if (repairType == null)
            repairType = RepairType.PROPERLY;

        return repairType;
    }

    public final void setRepairType() {
        setRepairType(null);
    }

    public final void setRepairType(RepairType repairType) {
        this.repairType = repairType == null ? RepairType.PROPERLY : repairType;
    }

    public final boolean mechAlreadyAssigned(Mechanic mechanic) {
        if (!DataService.isCorrect(getMechanics()))
            return false;

        for (Mechanic current :
                mechanics) {
            if (current.equals(mechanic))
                return true;
        }

        return false;
    }

    public String[] getMechsList() {
        if (!DataService.isCorrect(getMechanics()))
            return null;

        String[] list = new String[mechanics.size()];
        int index = -1;
        for (Mechanic current :
                mechanics) {
            if (current != null)
                list[++index] = current.getInfo();
        }
        return list;
    }

    public final Set<Mechanic> getMechanics() {
        if (getRepairType() == RepairType.PROPERLY || mechanics == null)
            mechanics = new HashSet<>();

        return mechanics;
    }

    public final void setMechanics(Set<Mechanic> mechanics) {
        if (mechanics != null) {
            this.mechanics = mechanics;
        }
    }

    public final ServiceStation getServiceStation() {
        return serviceStation;
    }

    public void setServiceStation(ServiceStation serviceStation) {
        if (serviceStation != null)
            this.serviceStation = serviceStation;
    }

    @Override
    public final String toString() {
        return String.format("%s марки «%s %s» (объём двигателя: %.1f л)",
                getCategory().getTransportType(), getBrand(), getModel(), getEngineVolume());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transport)) return false;
        Transport transport = (Transport) o;
        return Float.compare(transport.getEngineVolume(), getEngineVolume()) == 0 && getBrand().equals(transport.getBrand()) && getModel().equals(transport.getModel()) && getCategory().equals(transport.getCategory());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getBrand(), getModel(), getEngineVolume(), getCategory());
    }
}