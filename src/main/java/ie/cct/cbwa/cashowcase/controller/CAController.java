package ie.cct.cbwa.cashowcase.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ie.cct.cbwa.cashowcase.model.*;
import ie.cct.cbwa.cashowcase.util.JWTIssuer;
import ie.cct.cbwa.cashowcase.exceptions.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Collections;

@RestController
public class CAController {

    //Key-TripId,  Value-ArrayList<Expense>
    private Map<String, ArrayList<Expense>> allExpenses;
    
    //Key-TripId,  Value-Status(Active or Inactive)
    private Map<String, Boolean> myTrips;
    
    //Key-Username,  Value-ArrayList<Expense>
    private Map<String, ArrayList<Expense>> myExpense;
    
    //List of Users
    private List<User> users;

    
    //Constructor
    public CAController() {
        allExpenses = new HashMap<>();
        users = new ArrayList<>();
        myTrips = new HashMap<>();
        myExpense = new HashMap<>();
    }

    
    
    
    
    
    
    
    //signs up a new user to the application
    @PostMapping("/users")
    // 204 means no content, it s success, but it does not have content
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void registerUser(
            // if i don't specify the name, it will take the name of the variable
            @RequestParam(required = true) String username,
            @RequestParam(required = true) String password) {
        if (password.length() < 15) {
            throw new BadRequestException("password should be at least 15 characters");
        }
        if (!containsAtLeastOneSymbol(password)) {
            throw new BadRequestException("password should cointain at least 1 of the following symbols: @#[]{}+*/&()%$-:_<>=!");
        }
        if (userExist(username)) {
            throw new BadRequestException("User already exist");
        }
        users.add(new User(username, password));
    }

    
    
    
    
    
    
    
    
    
    //checks if password contains at least one of the stated symbols
    private boolean containsAtLeastOneSymbol(String string) {

        String symbols = "@#[]{}+*/&()%$-:_<>=!";

        if (string != null) {
            for (int i = 0; i < symbols.length(); i++) {
                //CharAt returns the index specified in the string
                //indexOf returns the position of the first value specified in the string (it locates the character)
                //if the string contains any of this symbols the result is going to be different the -1
                if (string.indexOf(symbols.charAt(i)) != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    
    //check if user exists
    private boolean userExist(String user) {
        //foreach loop to check if the user exist
        for (User u : users) {
            if (u.getUsername().contentEquals(user)) {
                return true;
            }
        }
        return false;
    }

    
    
    
    
    
    
    
    
    
    //print the list of users
    @GetMapping("/users")
    public List<User> getUsers() {
        return users;
    }

    
    
    
    
    
    
    //logs in a user into the application
    @GetMapping("/login")
    public String login(@RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "password", required = true) String password) {

        // TODO: We want an array of users.
        //for(int i = 0; i< users.size(); i++) {
        // if (username.equals(users.get(i)) && password.equals(users.get(i))) {
        for (User u : users) {
            if ((u.getUsername().contentEquals(username)) && (u.getPassword().contentEquals(password))) {
                //first parameter username
                //second parameter= issuer = ca-showcase 
                //third parameter is the subject
                //four = time in miliseconds
                //return a token to keep the user loged in
                return JWTIssuer.createJWT(username, "ca-showcase", username, 86400000);
            }

        }

        // TODO: We want to return 401, when the username or password do not match. 
        throw new UnauthorizedException("The credentials are not valid, please try again ");
    }
    
    
    
    
    
    
    //Allows a user to post an expense with a trip label
    @PostMapping("/{trip}/expense") // Authorization: Bearer <token>
    public ArrayList<Expense> addExpense(@PathVariable("trip") String trip,
            @RequestHeader(name = "Authorization", required = true) String token,
            @RequestBody(required = true) Expense expense) { //i'm receiving an object type Expense

        // TODO return 401 instead of 500. when the claim is not valid 
        Claims claims = JWTIssuer.decodeJWT(token.split(" ")[1]);
        String subClaim = claims.get("sub", String.class);
        
        expense.setTripId(trip);
        
        for (User u : users) {
            
            //username is the same as the subject   "sub"  
            if (u.getUsername().equals(subClaim)) {
                 String user = u.getUsername();
                 
                //if trips exists already
                if(myTrips.get(trip) != null){
                    //return summary report if trip has been closed
                    if(!myTrips.get(trip)){
                        throw new BadRequestException("Trip has been closed");
                    }
                }
                //if it is a new trip
                else {
                    myTrips.put(trip, true);
                    allExpenses.put(trip, new ArrayList<Expense>());
                }
                if(myExpense.get(user) == null){
                    myExpense.put(user, new ArrayList<Expense>());
                }
                
                allExpenses.get(trip).add(expense);
                myExpense.get(user).add(expense);
                
                return myExpense.get(user);
            }
        }

        throw new UnauthorizedException("Unauthorized user");
    }
    
    
    
    
    
    
    
    //Gets all expenses under a trip
    @GetMapping("/{trip}")
    public ArrayList<Expense> getTrip(@PathVariable("trip") String trip) {

       if(myTrips.get(trip) != null){
            return allExpenses.get(trip);
       }
        
        throw new BadRequestException("Trip does not exist");	
    }
    
    
    
    
    
    //Closes a trip(No expense can be added)
    @PostMapping("/{trip}/close")
    public String closeTrip(@PathVariable("trip") String trip) {
        //close trip if it exists
        if(myTrips.get(trip) != null){
            if(myTrips.get(trip)){
                myTrips.put(trip, false);
                return "Trip is Successfully Closed";
            }
            else {
                return "Trip is Already Closed";
            }
        }
        
        throw new BadRequestException("Trip does not exist");
    }
    
    
    
    
    
    
    
    //Gets all the summary report of a trip
    @GetMapping("/{trip}/summary")
    public String getSummary(@PathVariable("trip") String trip,
            @RequestHeader(name = "Authorization", required = true) String token) {
        
        // TODO return 401 instead of 500. when the claim is not valid 
        Claims claims = JWTIssuer.decodeJWT(token.split(" ")[1]);
        String subClaim = claims.get("sub", String.class);
        
        for (User u : users) {
            
            //username is the same as the subject   "sub"  
            if (u.getUsername().equals(subClaim)) {
                 String user = u.getUsername();
                 
                //if trips exists already
                if(myTrips.get(trip) != null){
                   //Get all expense with a tripId
                    ArrayList<Expense> exp = new ArrayList<>();
                    
                    for(Expense ex : myExpense.get(user)){
                        if(ex.getTripId().equals(trip)){
                            exp.add(ex);
                        }
                    }

                    int[] status = getPaidAndNotPaid(exp);
                    int total = getTotal(exp);
                    int numOfPurchase = getNumberOfItems(exp);
                    double average = getAverage(exp);
                    int max = getHighestPrice(exp);
                    int min = getLowestPrice(exp);

                    return "The total ammount of the your expense is " + total +
                        "\nThe number of purchases is " + numOfPurchase + 
                        "\nThe price average of the expenses is " + average + 
                        "\nThe highest expense is " + max + 
                        "\nThe lowest expense is " + min +
                        "\nThe amount paid is " + status[0] +
                        "\nThe amount left to pay is " + status[1];
                }
                
                throw new BadRequestException("Trip does not exist");
            }
        }
        
        throw new UnauthorizedException("Unauthorized user");

    }
    
    
    
    
    
    //returns the total of all expenses in a list
    public int getTotal(ArrayList<Expense> exp){
        int total = 0;
        
        for(Expense ex : exp){
            total += ex.getAmount();
        }
        
        return total;
    }
    
    //returns the number of expenses for a user or in a trip
    public int getNumberOfItems(ArrayList<Expense> exp){
        return exp.size();
    }
    
    //returns the average amount for all expenses in alist
    public double getAverage(ArrayList<Expense> exp){
        
        //Calculate average by dividing total by the number of items
        return getTotal(exp)/exp.size();
    }
    
    //returns the highest price in an expense list
    public int getHighestPrice(ArrayList<Expense> exp){
        int max = 0;//set the highest value to a very low value initially
        
        for(Expense ex : exp){
            //if the amount of the expense is greater than the previous highest value
            //we set a new highest value to the new item's value
            if(ex.getAmount() > max){
                max = ex.getAmount();
            }
        }
        
        return max;
    }
    
    //returns the lowest price in an expense list
    public int getLowestPrice(ArrayList<Expense> exp){
        int min = 99999999; //set the lowest value to a very high value initially
        
        for(Expense ex : exp){
            if(ex.getAmount() < min){
                min = ex.getAmount();
            }
        }
        
        return min;
    }
    
    
    //returns an array of two values(Amount Paid and Amount to be paid)
    public int[] getPaidAndNotPaid(ArrayList<Expense> exp){
        //create an array of size 2 for storing value of paid and to_be_paid amount
        int[] status = new int[2];
        
        
        for(Expense ex : exp){
            if(ex.hasPaid()){
                status[0] += ex.getAmount();
            }
            else {
                status[1] += ex.getAmount();
            }
        }
        
        return status;
    }
}