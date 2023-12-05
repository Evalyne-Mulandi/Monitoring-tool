package Forms;
import play.data.validation.Constraints;


public class Code {

    @Constraints.Required
    private String user;


    @Constraints.Required
    private String pass;

    @Constraints.Required
    private String svc;


    @Constraints.Required
    private String pin;


    @Constraints.Pattern(value = "\\+2547\\d{8}", message = "Msisdn must be in the format +2547--------")
    private String msisdn;

    public Code() {
        // Set default values in the constructor
        this.user = "Admin";
        this.pass = "Admin";
        this.svc = "codegen";
        this.pin="1234";
        this.msisdn="+254792785777";
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getSvc() {
        return svc;
    }

    public void setSvc(String svc) {
        this.svc = svc;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
}

//secure cookies
