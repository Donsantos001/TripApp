package ie.cct.cbwa.cashowcase.model;

//constructor
public class User {

    private String username;
    private String password;

    //constructor
    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public User() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString(){
        return this.username;
    }
}
