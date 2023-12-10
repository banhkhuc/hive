package fpt.edu.capstone.common.twilio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twilio")
@Data
public class TwilioProperties {
    private String accountSid = "ACa0d71f7974210689d3d4d2bb357b15b8";
    private String authToken = "9460f204aeb053cbf479b382db785f98";
    private String serviceId = "VAff7a425ec99829697c6a9daa941b6214";

    public TwilioProperties() {
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
