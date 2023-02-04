package specialists;

import auxiliaryLibrary.DataService;
import auxiliaryLibrary.TextService;
import transport.Competing;
import transport.Transport;
import transport.Transport.Category;
import transport.Transport.DriverException;
import transport.Transport.TransportException;

public class Driver<TC extends Transport & Competing> {

    private String name;
    private Category driverLicenseCategory;
    private int drivingExperience;
    private TC transport;


    public Driver(String name, Category driverLicenseCategory) {
        this(name, driverLicenseCategory, null);
    }

    public Driver(String name, TC transport) {
        this(name, null, transport);
    }

    public Driver(String name, Category driverLicenseCategory, TC transport) {
        this(name, driverLicenseCategory, transport, 1);
    }

    public Driver(String name, TC transport, int drivingExperience) {
        this(name, null, transport, 1);
    }

    public Driver(String name, Category driverLicenseCategory, TC transport, int drivingExperience) {

        setName(name);

        if (driverLicenseCategory == null)
            setDriverLicenseCategory(transport);
        else
            setDriverLicenseCategory(driverLicenseCategory);

        try {
            getOnTheTransport(transport);
        } catch (TransportException | DriverException e) {
            TextService.printException(e);
        } finally {
            setDrivingExperience(drivingExperience);
        }
    }


    public void started() {
        if (transport != null) {
            transport.started();
            System.out.println(getName() + " стартовал.");
        }
    }

    public void finished() {
        if (transport != null) {
            transport.stopped();
            System.out.println(getName() + " финишировал.");
        }
    }

    public void pitStop() {
        if (transport != null) {
            System.out.print("\n" + getName() + " отправил свой " + transport.getCategory().getTransportType() + " в пит-стоп.");
            transport.pitStop();
        }
    }

    public void transportRefuel() {
        if (transport != null) {
            pitStop();
            System.out.println(getName() + " заправил свой " + transport.getCategory().getTransportType() + " и продолжил соревнование.");
        }
    }


    public String getName() {
        if (!DataService.isCorrect(name)) {
            return "<не известно>";
        }
        return name;
    }

    public void setName(String name) {
        if (DataService.isCorrect(name)) {
            this.name = name;
        }
    }

    public Category getDriverLicenseCategory() {
        if (driverLicenseCategory == null) {
            driverLicenseCategory = Category.DLC_N;
        }
        return driverLicenseCategory;
    }

    public void setDriverLicenseCategory(Category licenseCategory) {
        if (driverLicenseCategory != null)
            driverLicenseCategory = licenseCategory;
        else
            driverLicenseCategory = Category.DLC_N;
    }

    public void setDriverLicenseCategory(TC transport) {
        if (transport == null)
            driverLicenseCategory = Category.DLC_N;
        else
            driverLicenseCategory = getTransportCategory(transport);
    }

    public int getDrivingExperience() {
        if (drivingExperience < 0) {
            return 0;
        }
        return drivingExperience;
    }

    public void setDrivingExperience(int drivingExperience) {
        if (drivingExperience > 0) {
            this.drivingExperience = drivingExperience;
        }
    }

    public String getTitleOfTransport() {
        if (transport == null) {
            return "\nСредство соревнований ещё не выбрано.";
        }
        return transport.toString();
    }

    private Category getTransportCategory() {
        return getTransportCategory(transport);
    }

    private Category getTransportCategory(TC transport) {
        if (transport == null) {
            return null;
        }
        switch (transport.getClass().getSimpleName()) {
            case "Car":
                return Category.DLC_B;
            case "Truck":
                return Category.DLC_C;
            case "Bus":
                return Category.DLC_D;
            default:
                return null;
        }
    }

    public TC getTransport() throws TransportException {
        if (transport == null)
            throw new TransportException("За " + getName() + " пока не закреплено транспортное средство");
        return transport;
    }

    public void getOnTheTransport(TC transport) throws DriverException, TransportException {
        if (transport != null) {
            Category transportCategories = getTransportCategory(transport);

            if (transportCategories == getDriverLicenseCategory()) {

                Driver<?> transportDriver = transport.getDriver();
                if (!this.equals(transportDriver))
                    transport.setDriver(this);

                this.transport = transport;

                String transportType;
                switch (transportCategories) {

                    case DLC_B:
                        transportType = "легковом автомобиле";
                        break;

                    case DLC_C:
                        transportType = "грузовом автомобиле";
                        break;

                    case DLC_D:
                        transportType = "автобусе";
                        break;

                    default:
                        transportType = "<неизвестном транспорте>";
                }

                System.out.println("\n" + getName() + " соревнуется на " + transportType + " " + transport.getTechnicalCard() + ".");
            } else
                throw new DriverException(getName() + " не имеет права управлять данной категорией транспортного средства.");
        }
    }

    @Override
    public String toString() {
        return getName() + " (категория прав: " + getDriverLicenseCategory().getTitle() + "; "
                + "опыт вождения (лет): " + drivingExperience + ")";
    }
}