package transport;

import specialists.Mechanic.RepairType;

public interface Diagnosed {

    RepairType performDiagnostic() throws Transport.DriverException;
}
