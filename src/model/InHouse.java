package model;

/**
 * A part that is created in-house.
 *
 * @author Zachary Mollenhour
 */
public class InHouse extends Part {
    private int machineId;

    public InHouse(int id, String name, double price, int stock, int min, int max, int machineId) {
        super(id, name, price, stock, min, max);
        this.machineId = machineId;
    }

    /**
     * @return the machine ID of the Part
     */
    public int getMachineId() {
        return machineId;
    }

    /**
     * @param machineId the machine ID to set the Part to
     */
    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }
}