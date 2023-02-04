package specialists;

import auxiliaryLibrary.DataService;
import auxiliaryLibrary.TextService;
import transport.Competing;
import transport.Transport;
import transport.Transport.TransportException;

import java.util.LinkedList;
import java.util.Objects;

public abstract class Sponsor {

    protected static final String UNKNOWN = "<не известно>";

    private static Sponsor processedSponsor = null;

    public static class Sponsored {


        private final Competing participantTransport;
        private LinkedList<Sponsor> sponsors = new LinkedList<>();
        private float sponsorshipAmount;


        public Sponsored(Competing participantTransport) throws SponsorException {
            this(participantTransport, 0);
        }

        public Sponsored(Competing participantTransport, float sponsorshipAmount) throws SponsorException {

            if (participantTransport == null)
                throw new SponsorException("Не указано транспортное средство для спонсирования.");

            else
                this.participantTransport = participantTransport;

            this.sponsorshipAmount = sponsorshipAmount;
        }


        public final String getTechCard() {

            if (participantTransport != null)

                if (participantTransport instanceof Transport)
                    return ((Transport) participantTransport).getTechnicalCard();

                else return "<транспортное средство неизвестного типа>";

            else
                return "<нет сведений>";
        }

        public final Competing getParticipantTransport() {
            return participantTransport;
        }

        public final float getSponsorshipAmount() {
            return sponsorshipAmount;
        }

        protected final void setSponsorshipAmount(float sponsorshipAmount) {
            try {
                if (processedSponsor == null)
                    throw new SponsorException("Не выбран Спонсор для изменения суммы спонсирования.");
            } catch (SponsorException e) {
                TextService.printException(e);
            }

            if (sponsorshipAmount > 0) {

                if (getSponsorshipAmount() != sponsorshipAmount) {
                    processedSponsor.correctSponsorshipAmountSum(-this.sponsorshipAmount + sponsorshipAmount);
                    this.sponsorshipAmount = sponsorshipAmount;
                } else
                    processedSponsor.correctSponsorshipAmountSum(sponsorshipAmount);
            }
        }

        public final LinkedList<Sponsor> getSponsors() {
            if (sponsors == null)
                sponsors = new LinkedList<>();
            return sponsors;
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Sponsored)) return false;
            Sponsored sponsored = (Sponsored) o;
            return getParticipantTransport().equals(sponsored.getParticipantTransport());
        }

        @Override
        public final int hashCode() {
            return Objects.hash(getParticipantTransport());
        }
    }

    public static class SponsorException extends Exception {

        public SponsorException() {
        }

        public SponsorException(String message) {
            super(message);
        }
    }


    protected LinkedList<Sponsored> sponsored = new LinkedList<>();
    private float sponsorshipAmountSum = 0;


    public Sponsor() {
    }


    protected abstract void addSponsored(Sponsored sponsored) throws SponsorException;

    public abstract void addSponsored(Sponsored sponsored, float sponsorshipAmount) throws SponsorException;

    protected abstract void addSponsored(Sponsored... sponsored) throws SponsorException;

    protected String finishSponsoredAdding(String sponsor, Sponsored newSponsored) {
        Transport newSponsTransp = (Transport) newSponsored.getParticipantTransport();
        String finishMessage = "\n" + sponsor + " теперь спонсирует заезды " + newSponsTransp.getTechnicalCard() + ".";

        if (this.sponsored.size() - 1 > 0)
            finishMessage = finishMessage.replace("заезды", "заезды и");

        if (!newSponsTransp.getSponsors().contains(this)) {
            try {
                newSponsTransp.addSponsor(this, newSponsored.getSponsorshipAmount());
            } catch (TransportException | SponsorException e) {
                TextService.printException(e);
            }
        }

        return finishMessage;
    }

    public void showSponsored() {
        if (!DataService.isCorrect(sponsored)) {
            System.out.println("\n" + this + " пока ещё не спонсирует заезды.");
            return;
        }

        System.out.println("\n" + this + " спонсирует заезды следующих транспортных средств:");
        TextService.printList(getSponsoredList(), TextService.PrintModes.NUMBERED_LIST_PM);

        float sponsorshipAmountSum = getSponsorshipAmountSum();
        if (sponsorshipAmountSum > 0)
            System.out.printf("Общая сумма спонсирования: %.2f руб..\n", sponsorshipAmountSum);
    }


    protected static Sponsor getProcessedSponsor() {
        return processedSponsor;
    }

    protected static void setProcessedSponsor(Sponsor processedSponsor) {
        Sponsor.processedSponsor = processedSponsor;
    }


    public final void setSponsorshipAmount(Sponsored sponsored, float sponsorshipAmount) {
        if (sponsored != null && sponsorshipAmount > 0 && this.sponsored.contains(sponsored)) {
            if (sponsored.getSponsorshipAmount() == 0)
                this.correctSponsorshipAmountSum(sponsorshipAmount);
            sponsored.setSponsorshipAmount(sponsorshipAmount);
        }
    }

    protected final float getSponsorshipAmountSum() {
        return sponsorshipAmountSum;
    }

    protected final void correctSponsorshipAmountSum(float sponsorshipAmount) {
        sponsorshipAmountSum += sponsorshipAmount;
    }

    public LinkedList<Sponsored> getSponsored() {
        if (sponsored == null)
            sponsored = new LinkedList<>();
        return sponsored;
    }

    public Sponsored getSponsored(Competing participantTransport) throws TransportException, SponsorException {
        if (!DataService.isCorrect(sponsored))
            throw new SponsorException(this + " пока не спонсирует заезды.");

        if (!(participantTransport instanceof Transport))
            throw new TransportException(participantTransport + " не является допустимым транспортным средством.");
        Sponsored competingTransport = new Sponsored(participantTransport);

        if (!sponsored.contains(competingTransport))
            throw new SponsorException(this + " не спонсирует " + ((Transport) participantTransport).getTechnicalCard());

        return sponsored.get(sponsored.indexOf(competingTransport));
    }

    private String[] getSponsoredList() {
        if (!DataService.isCorrect(sponsored))
            return null;

        String[] sponsoredList = new String[sponsored.size()];
        int index = -1;

        for (Sponsored sponsored :
                this.sponsored) {
            if (sponsored != null) {
                float sponsorshipAmount = sponsored.getSponsorshipAmount();
                sponsoredList[++index] = String.format("%s (сумма - %.2f руб.)", sponsored.getTechCard(), sponsorshipAmount);

                if (sponsorshipAmount <= 0)
                    sponsoredList[index] = sponsoredList[index].replace("0,00 руб.", "не обозначена");
            }
        }

        if (index < 0)
            return null;

        return sponsoredList;
    }
}