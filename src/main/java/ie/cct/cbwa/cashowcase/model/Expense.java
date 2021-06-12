package ie.cct.cbwa.cashowcase.model;

/**
 *
 * @author User
 */
public class Expense {
    private String tripId;
    private Integer amount;
    private String description;
    private Boolean paid;
    
    public Expense() {
        super();
    }

    public Expense(String TripId, Integer Amount) {
        this.tripId = TripId;
        this.amount = Amount;
    }

    public Expense(String TripId, Integer Amount, Boolean Paid, String Description) {
        super();
        this.tripId = TripId;
        this.amount = Amount;
        this.paid = Paid;
        this.description = Description;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String TripId) {
        this.tripId = TripId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setPrice(Integer Amount) {
        this.amount = Amount;
    }

    public String getDescription() {
        return description;
    }

    public void setClient(String Description) {
        this.description = Description;
    }

    public boolean hasPaid() {
        return paid;
    }

    public void setPaid(Boolean Paid) {
        this.paid = Paid;
    }

    public String toString() {
        //returns string in JSON format
        return "\nExpense : {\n" + 
                "\'tripId\' = \'" + tripId + 
                "\', \n\'amount\' = \'" + amount + 
                "\', \n\'description\' = \'" + description +  "\'\n}\n";
    }
}
