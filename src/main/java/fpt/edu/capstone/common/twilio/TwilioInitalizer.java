package fpt.edu.capstone.common.twilio;


import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class TwilioInitalizer {
    private final TwilioProperties twilioProperties;

    @Autowired
    public TwilioInitalizer(TwilioProperties twilioProperties){
        this.twilioProperties = twilioProperties;
        Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
        System.out.println("Inital with account : " + twilioProperties.getAccountSid());
    }

}
