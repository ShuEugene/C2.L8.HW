package transport;

import auxiliaryLibrary.DataService;
import auxiliaryLibrary.TextService;
import auxiliaryLibrary.TextService.PrintModes;
import specialists.Driver;
import transport.Transport.TransportException;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public interface Competing {

    String NO_PARTICIPANTS = "\nНедостаточное количество участников для проведения соревнования.";
    String PARTICIPANTS_PRINT_TITLE = "\nВ соревновании участвуют следующие <ТС>:";

    static List<String> getParticipantsTransportsTypes(List<?> participantsTransports) {
        if (!DataService.isCorrect(participantsTransports)) {
            return null;
        }
        List<String> participantsTypes;
        if (participantsTransports.size() < 2) {
            participantsTypes = new ArrayList<>(1);
            participantsTypes.add(participantsTransports.get(0).getClass().getSimpleName());
        } else {
            participantsTypes = new ArrayList<>();
            String currentParticipantType;
            for (int i = 0; i < participantsTransports.size() - 1; i++) {
                currentParticipantType = participantsTransports.get(i).getClass().getSimpleName();
                if (!participantsTypes.contains(currentParticipantType)) {
                    participantsTypes.add(currentParticipantType);
                }
            }
        }
        return participantsTypes;
    }

    static String getShowTitle(List<String> participantsTypes) {
        if (!DataService.isCorrect(participantsTypes))
            return NO_PARTICIPANTS;
        if (participantsTypes.size() > 1)
            return "\nУчастники соревнований:";
        else
            switch (participantsTypes.get(0)) {
                case "Car":
                    return PARTICIPANTS_PRINT_TITLE.replace("<ТС>", "Легковые автомобили");
                case "Truck":
                    return PARTICIPANTS_PRINT_TITLE.replace("<ТС>", "Грузовые автомобили");
                case "Bus":
                    return PARTICIPANTS_PRINT_TITLE.replace("<ТС>", "Автобусы");
                default:
                    return PARTICIPANTS_PRINT_TITLE.replace("<ТС>", "транспортные средства неизвестного типа");
            }
    }

    static <T> String[] getParticipantsList(List<T> participants) throws TransportException {

        String[] participantsList = new String[participants.size()];
        int index = 0;

        for (T participant :
                participants) {

            if (participant instanceof Transport) {
                Transport transport = (Transport) participant;
                String transportType;

                switch (participant.getClass().getSimpleName()) {

                    case "Car":
                        transportType = "легковым автомобилем";
                        break;

                    case "Truck":
                        transportType = "грузовым автомобилем";
                        break;

                    case "Bus":
                        transportType = "автобусом";
                        break;

                    default:
                        transportType = "транспортным средством неизвестного типа";
                }

                Driver<?> driver = transport.getDriver();
                if (driver != null)
                    participantsList[index++] = transport.getDriverName() + " управляет " + transportType + " " + transport.getTechnicalCard() + ".";

                else
                    participantsList[index++] = transport.getCategory().getTransportType() + " " + transport.getTechnicalCard()
                            + " зарегистрирован для использования в соревнованиях, но за ним пока ещё не закреплён водитель";

            } else
                throw new TransportException(participant + " не является транспортным средством и не может быть использован(-о) в соревнованиях.");

        }

        return participantsList;
    }

    static void showParticipants(List<?> participants) {

        String printTitle;

        if (!DataService.isCorrect(participants) || participants.size() < 2)
            printTitle = NO_PARTICIPANTS;

        else
            printTitle = getShowTitle(getParticipantsTransportsTypes(participants));

        try {
            TextService.printList(getParticipantsList(participants), PrintModes.NUMBERED_LIST_PM, printTitle);
        } catch (TransportException e) {
            TextService.printException(e);
        }
    }


    void pitStop();

    Time getBestLapTime();

    float getMaximumLapSpeed();

    boolean equals(Object o);
}
