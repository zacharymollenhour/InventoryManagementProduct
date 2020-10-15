package model;

/**
 * A part that is bought from other companies.
 *
 * @author Zachary Mollenhour
 */
public class Outsourced extends Part {
    private String companyName;

    public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName) {
        super(id, name, price, stock, min, max);
        this.companyName = companyName;
    }

    /**
     * @return the name of the company this Part is made by
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @param companyName the name of the company this Part is made by to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}