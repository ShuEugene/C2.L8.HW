package specialists;

import auxiliaryLibrary.DataService;
import auxiliaryLibrary.TextService;
import transport.Transport;

public class Physical extends Sponsor {

    private String name;

    public Physical(String name, Sponsored... sponsored) {
        super();

        if (DataService.isCorrect(name))
            this.name = name;
        else
            this.name = UNKNOWN;

        try {
            addSponsored(sponsored);
        } catch (SponsorException e) {
            TextService.printException(e);
        }

    }


    public final void addSponsored(Sponsored sponsored) throws SponsorException {
        float sponsorshipAmount = sponsored.getSponsorshipAmount();
        if (sponsorshipAmount > 0)
            addSponsored(sponsored, sponsorshipAmount);

        else
            addSponsored(sponsored, 0);
    }

    public final void addSponsored(Sponsored sponsored, float sponsorshipAmount) throws SponsorException {

        String resultMessage;

        if (sponsored == null)
            throw new SponsorException("Не указано транспортное средство для спонсирования.");

        Transport newSponsTransp = (Transport) sponsored.getParticipantTransport();
        String transportTechCard = newSponsTransp.getTechnicalCard();

        if (getSponsored().contains(sponsored)) {
            Sponsored thisSponsored = this.sponsored.get(this.sponsored.indexOf(sponsored));
            if (thisSponsored.getSponsorshipAmount() != sponsorshipAmount) {
                setProcessedSponsor(this);
                thisSponsored.setSponsorshipAmount(sponsorshipAmount);
            }

            resultMessage = this + " уже спонсирует " + transportTechCard + ".";

        } else if (this.sponsored.offer(sponsored)) {
            Sponsored thisSponsored = this.sponsored.get(this.sponsored.indexOf(sponsored));
            if (!thisSponsored.getSponsors().contains(this))
                thisSponsored.getSponsors().offer(this);

            setProcessedSponsor(this);
            thisSponsored.setSponsorshipAmount(sponsorshipAmount);
            resultMessage = finishSponsoredAdding(getName(), thisSponsored);

        } else
            resultMessage = "\n" + getName() + " не смог добавить " + transportTechCard + " в список спонсируемых.";

        System.out.println(resultMessage);
    }


    public final void addSponsored(Sponsored... sponsored) throws SponsorException {
        for (Sponsored participant :
                sponsored) {
            addSponsored(participant);
        }
    }


    public final String getName() {
        if (!DataService.isCorrect(name))
            return UNKNOWN;
        return name;
    }

    public final void setName(String name) {
        if (DataService.isCorrect(name))
            this.name = name;
        else
            this.name = UNKNOWN;
    }

    @Override
    public final String toString() {
        if (!DataService.isCorrect(name))
            return UNKNOWN;
        return name;
    }
}
