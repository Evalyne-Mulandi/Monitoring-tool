package Forms;


import play.data.validation.Constraints;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Pattern;


public class User {
    @Constraints.Required(message = "username is required")
    public String username;

    @Constraints.Required(message = "firstname is required")
    public String firstName;

    @Constraints.Required(message = "lastname is required")
    public String lastName;

    @Constraints.Required(message = "email is required")
    @Email(message = "Invalid email address")
    public String email;


    @Constraints.Required(message = "phone number is required")
    @Constraints.MinLength(value = 10, message = "Phone Number should be at least 10 characters long")
    public String phoneNumber;

    @Constraints.Required(message = "password is required")
    @Constraints.MinLength(value = 6, message = "Password should be at least 6 characters long")
    public String password;
    @Constraints.Required(message = "password is required")
    @Constraints.MinLength(value = 6, message = "Password should be at least 6 characters long")
    public String repeatPassword;



    public String validatePassword;

    public String validateUsername;


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }




    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }


    public String getValidatePassword() {
        return validatePassword;
    }

    public void setValidatePassword(String validatePassword) {
        this.validatePassword = validatePassword;
    }


}