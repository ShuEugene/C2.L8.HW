package specialists;

import auxiliaryLibrary.DataService;
import auxiliaryLibrary.TextService;
import specialists.Mechanic.Repaired.RepairException;
import transport.Bus;
import transport.Transport;
import transport.Transport.Category;
import transport.Transport.TransportException;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;


public class Mechanic {

    protected static final String UNKNOWN = "<не указана>";
    private final int MIN_QUEUE_CAP = 3;

    public static class MechanicException extends Exception {
        public MechanicException() {
        }

        public MechanicException(String message) {
            super(message);
        }
    }

    public enum RepairType {
        SERVICE("техобслуживание"), REPAIR("ремонт"), PROPERLY("исправно");

        private final String title;

        RepairType(String title) {
            this.title = title;
        }

        public final String getTitle() {
            return title;
        }
    }

    public static class Repaired {

        public static class RepairException extends Exception {
            public RepairException() {
            }

            public RepairException(String message) {
                super(message);
            }
        }

        private final Transport transport;
        private RepairType repairType;


        public Repaired(Transport transport) throws TransportException {
            this(transport, null);
        }

        public Repaired(Transport transport, RepairType repairType) throws TransportException {

            if (transport == null)
                throw new TransportException("Транспортное средство должно быть указано.");

            this.transport = transport;
            setRepairType(repairType);
        }


        public final String getTechnicalCard() {
            return transport.getTechnicalCard();
        }

        public final Transport getTransport() {
            return transport;
        }

        public final RepairType getRepairType() {
            if (repairType == null)
                repairType = RepairType.PROPERLY;

            return repairType;
        }

        public final void setRepairType(RepairType repairType) {
            this.repairType = repairType == null ? RepairType.SERVICE : repairType;
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Repaired)) return false;
            Repaired repaired = (Repaired) o;
            return getTransport().equals(repaired.getTransport());
        }

        @Override
        public final int hashCode() {
            return Objects.hash(getTransport());
        }

        @Override
        public final String toString() {
            Transport repTransport = getTransport();
            String repStatus = "требуется диагностика";
            switch (repTransport.getRepairType()) {
                case SERVICE:
                case REPAIR:
                    repStatus = repStatus.replace("диагностика", repTransport.getRepairType().title);
                    break;
                case PROPERLY:
                    repStatus = repTransport.getRepairType().title;
                    break;
            }

            return getTransport().getTechnicalCard() + ". Состояние: " + repStatus;
        }
    }

    private final String firstName, lastName;
    private String company;
    private ArrayList<Category> specializations;

    private ServiceStation serviceStation;

    private int queueCapacity;
    private Deque<Repaired> repaired;


    public Mechanic(String lastName, String firstName, Category... specializations)
            throws MechanicException {
        this(lastName, firstName, null, 0, specializations);
    }

    public Mechanic(String lastName, String firstName, String company, Category... specializations)
            throws MechanicException {
        this(lastName, firstName, company, 0, specializations);
    }

    public Mechanic(String lastName, String firstName, int queueCapacity, Category... specializations)
            throws MechanicException {
        this(lastName, firstName, null, queueCapacity, specializations);
    }

    public Mechanic(String lastName, String firstName, String company, int queueCapacity, Category... specializations)
            throws MechanicException {
        if (!DataService.isCorrect(lastName))
            throw new MechanicException("Фамилия механика должна быть указана.");
        else
            this.lastName = lastName;

        if (!DataService.isCorrect(firstName))
            throw new MechanicException("Имя механика должно быть указано.");
        else
            this.firstName = firstName;

        setCompany(company);

        setQueueCapacity(queueCapacity);

        setSpecializations(specializations);
    }


    private final void performTechService(Repaired repairedTransport) {
        System.out.println("\nТехобслуживание проведено.");
    }

    public void showRepaired() {
        if (getRepaired().size() < 1) {
            System.out.println("\nВ очереди задач механика (" + this + ") пока нет ТС.");
            return;
        }

        System.out.println("\nПеречень ТС в очереди на обслуживание (" + this + "):");
        TextService.printList(repaired.toArray(), TextService.PrintModes.NUMBERED_LIST_PM);
    }

    private final boolean isRepaired(Transport transport) {
        if (transport == null)
            return false;

        for (Repaired current :
                getRepaired()) {
            if (current != null && current.getTransport().equals(transport))
                return true;
        }

        return false;
    }

    public void addRepaired(Transport transport)
            throws MechanicException, TransportException {

        if (transport == null || transport instanceof Bus)
            return;

        String techCard = transport.getTechnicalCard();
        RepairType repairType = transport.getRepairType();

        if (repairType == RepairType.PROPERLY)
            throw new MechanicException(techCard + " не требует ни ремонта, ни техобслуживания.");

        if (isRepaired(transport) || transport.getMechanics().contains(this))
            throw new MechanicException(techCard + " уже стои́т в очереди на " + repairType.title + " у этого механика ("
                    + this + ").");

        Repaired newRepaired = new Repaired(transport, repairType);
        String trCategory = transport.getCategory().getTitle();
        try {
            if (getSpecializationsEnum().contains(trCategory)) {
                getRepaired().add(newRepaired);
                if (!transport.mechAlreadyAssigned(this))
                    transport.getMechanics().add(this);
            } else
                throw new MechanicException("данная категория транспортных средств (" + trCategory + ")" +
                        " не входит в область специализации этого механика (" + getInfo() + ").");

        } catch (Exception e) {
            StringBuilder exceptionMess = new StringBuilder(transport.getTechnicalCard() + " не добавлен в очередь к механику (" + this + "): ");
            switch (e.getClass().getSimpleName()) {
                case "IllegalStateException":
                    exceptionMess.append(this.getFirstName()).append(" полностью занят (очередь его задач заполнена)." +
                            " Выберите другого механика.");
                    break;
                case "ClassCastException":
                    exceptionMess.append("несоответствие типов.");
                    break;
                case "NullPointerException":
                    exceptionMess.append("транспортное средство не указано (\"").append(e.getMessage()).append("\").");
                    break;
                case "IllegalArgumentException":
                    exceptionMess.append("объект с непредусмотренными свойствами (\"").append(e.getMessage()).append("\").");
                    break;
                case "MechanicException":
                    exceptionMess.append(e.getMessage());
                    break;
                default:
                    exceptionMess.append("причина не известна.");
            }

            throw new MechanicException(exceptionMess.toString());
        }

        System.out.println("\n" + techCard + " теперь в очереди на " + repairType.title + " к механику (" + this + ").");

        if (serviceStation != null) {
            Deque<Repaired> repairedAtStation = serviceStation.getRepaired();

            if (!repairedAtStation.contains(newRepaired))
                if (repairedAtStation.offer(newRepaired))
                    System.out.println("Станция ТО " + serviceStation + " приняла на обслуживание " + techCard + ".");
                else
                    System.out.println("Станция ТО " + serviceStation + "не приняла на обслуживание " + techCard + ".");

            else
                System.out.println("\n" + techCard + " уже стоит в очереди на обслуживание на станции ТО " + serviceStation + ".");
        }
    }

    public void addRepaired(Transport... transports)
            throws MechanicException, TransportException {

        if (DataService.isCorrect(transports))

            for (Transport current
                    : transports) {
                if (current != null && !(current instanceof Bus))
                    addRepaired(current);
            }
    }

    public String getInfo() {
        return String.format("%s %s (компания %s; категории специализации: %s)",
                lastName, firstName, getCompany(), getSpecializationsEnum());
    }

    private String getFirstName() {
        return firstName;
    }

    private String getLastName() {
        return lastName;
    }

    public String getCompany() {
        if (DataService.isCorrect(company) && !company.equals(UNKNOWN))
            return "«" + company + "»";

        if (company.equals(UNKNOWN))
            return company;

        company = UNKNOWN;
        return company;
    }

    public void setCompany(String company) {
        if (DataService.isCorrect(company))
            this.company = company;
        else
            this.company = UNKNOWN;
    }

    private String getSpecializationsEnum() {
        StringBuilder specializationsEnum = new StringBuilder();

        if (getSpecializations().size() < 1)
            return "<отсутствуют>";

        Category curSpec;
        for (int index = 0; index < specializations.size(); ++index) {
            curSpec = specializations.get(index);

            if (curSpec != null) {
                specializationsEnum.append(curSpec.getTitle());

                if (index < specializations.size() - 1) specializationsEnum.append(", ");
            }
        }

        if (!DataService.isCorrect(specializationsEnum.toString()))
            return "<отсутствуют>";

        return specializationsEnum.toString();
    }

    public List<Category> getSpecializations() {
        if (specializations == null)
            specializations = new ArrayList<>(0);

        return specializations;
    }

    public void setSpecializations(Category... specializations) {
        specializations = DataService.getNotNullObjects(specializations);
        if (!DataService.isCorrect(specializations))
            return;

        this.specializations = new ArrayList<>(specializations.length);

        for (Category curSpecialisation :
                specializations) {
            if (curSpecialisation != null && !this.specializations.contains(curSpecialisation))
                this.specializations.add(curSpecialisation);
        }
    }

    public final Deque<Repaired> getRepaired() {
        if (repaired == null)
            repaired = new LinkedBlockingDeque<>(getQueueCapacity());

        return repaired;
    }

    public final void setRepaired(Deque<Repaired> repaired) {
        this.repaired = repaired != null ? repaired : newRepaired();
    }

    private Deque<Repaired> newRepaired() {
        Deque<Repaired> curRepaired = getRepaired();

        if (this.queueCapacity < curRepaired.size()) {
            int numberOfReassigned = curRepaired.size() - this.queueCapacity;
            try {
                throw new RepairException("Новые ограничения очереди меньше количества ТС в текущей очереди.\n" +
                        "Перед введением нового ограничения переназначьте " + numberOfReassigned + " ТС другому механику.");
            } catch (RepairException e) {
                TextService.printException(e);
            }
        }
        Deque<Repaired> newRepaired = new LinkedBlockingDeque<>(this.queueCapacity);
        newRepaired.addAll(curRepaired);
        return newRepaired;
    }

    public int getQueueCapacity() {
        if (queueCapacity < MIN_QUEUE_CAP) {
            queueCapacity = MIN_QUEUE_CAP;
            repaired = newRepaired();
        }

        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = Math.max(queueCapacity, MIN_QUEUE_CAP);
        repaired = newRepaired();
    }

    public ServiceStation getServiceStation() {
        return serviceStation;
    }

    public void setServiceStation(ServiceStation serviceStation) {
        if (serviceStation != null)
            this.serviceStation = serviceStation;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " (компания: " + getCompany() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mechanic)) return false;
        Mechanic mechanic = (Mechanic) o;
        return getFirstName().equals(mechanic.getFirstName()) && getLastName().equals(mechanic.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }
}