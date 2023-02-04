package specialists;

import auxiliaryLibrary.DataService;
import auxiliaryLibrary.TextService;
import specialists.Mechanic.RepairType;
import specialists.Mechanic.Repaired;
import transport.Bus;
import transport.Transport;
import transport.Transport.TransportException;

import java.util.Deque;
import java.util.LinkedList;

import static specialists.Mechanic.RepairType.PROPERLY;


public class ServiceStation {

    public static class ServiceException extends Exception {
        public ServiceException() {
        }

        public ServiceException(String message) {
            super(message);
        }
    }

    private final String title;
    private LinkedList<Mechanic> mechanics = new LinkedList<>();
    private Deque<Repaired> repaired = new LinkedList<>();


    public ServiceStation(String title, Mechanic... mechanics) throws ServiceException {

        if (!DataService.isCorrect(title))
            throw new ServiceException("Название станции техобслуживания должно быть указано.");

        this.title = title;
        System.out.println("\nТолько что налажена работа новой станции ТО - " + getTitle() + ".");
        addMechanics(mechanics);
    }


    public void performDiagnostic(Repaired repaired) {
        try {
            if (repaired == null)
                throw new ServiceException("Транспортное средство никак не обозначено. Проведение диагностики исключено.");

            if (!this.getRepaired().contains(repaired))
                throw new ServiceException("Транспортное средство (" + repaired.getTechnicalCard()
                        + " пока ещё не обслуживается данной станцией ТО (" + this + ".\n"
                + "Для проведения диагностики добавьте его в очередь обслуживания данной станции.");

            RepairType repairType = repaired.getRepairType();
            System.out.println("\nТранспортное средство (" + repaired.getTechnicalCard() + ") " +
                    (repairType == PROPERLY ? "исправно" : "неисправно; требуется " + repairType.getTitle()) + ".");

        } catch (ServiceException e) {
            TextService.printException(e);
        }
    }

    public void addRepaired(Transport... transports) {
        if (!DataService.isCorrect(transports))
            return;

        for (Transport current :
                transports) {

            if (current != null && !(current instanceof Bus)) {
                if (current.getRepairType() == PROPERLY) {
                    System.out.println("Транспортное средство (" + current.getTechnicalCard()
                            + ") исправно и не требует обслуживания на станции ТО (" + this + ".");
                    continue;
                }

                try {
                    Repaired curRepaired = new Repaired(current, current.getRepairType());
                    if (!repaired.contains(curRepaired)) {
                        if (repaired.offer(curRepaired)) {
                            System.out.println("\n" + current.getTechnicalCard()
                                    + " теперь в очереди на обслуживание на станции ТО " + getTitle() + ".");
                            current.setServiceStation(this);

                        } else
                            System.out.println("\n" + current.getTechnicalCard()
                                    + " не удалось добавить в очередь на обслуживание на станции ТО " + getTitle() + ".");
                    } else
                        System.out.println("\n" + current.getTechnicalCard()
                                + " уже стоит в очереди на обслуживание на станции ТО " + getTitle() + ".");

                } catch (TransportException e) {
                    TextService.printException(e);
                }
            }
        }
    }

    public void addMechanics(Mechanic... mechanics) {
        if (!DataService.isCorrect(mechanics))
            return;

        String resultMess;
        for (Mechanic current :
                mechanics) {
            resultMess = null;

            if (current != null)
                if (!getMechanics().contains(current))
                    try {
                        this.mechanics.add(current);
                        resultMess = "\n" + current + " теперь работает на этой станции ТО (" + getTitle() + ").";
                        current.setServiceStation(this);
                    } catch (Exception e) {
                        System.out.println(current + " не может работать на этой станции ТО: " + e.getMessage() + ".");
                    }

                else
                    resultMess = "\n" + current + " уже числиться на этой станции ТО (" + getTitle() + ").";

            if (resultMess != null)
                System.out.println(resultMess);
        }
    }


    public final String getTitle() {
        return "«" + title + "»";
    }

    public final LinkedList<Mechanic> getMechanics() {
        if (mechanics == null)
            mechanics = new LinkedList<>();

        return mechanics;
    }

    public final void setMechanics(LinkedList<Mechanic> mechanics) {
        if (mechanics != null)
            this.mechanics = mechanics;
    }

    public final Deque<Repaired> getRepaired() {
        if (repaired == null)
            repaired = new LinkedList<>();

        return repaired;
    }

    public final void setRepaired(Deque<Repaired> repaired) {
        if (DataService.isCorrect(repaired))
            this.repaired = repaired;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}