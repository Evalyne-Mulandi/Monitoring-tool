package Forms;


import play.data.validation.Constraints;


public class Login {
    @Constraints.Required(message = "username is required")
    public String username;


    @Constraints.Required(message = "password is required")
    public String password;

    public String validatePassword;

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






}