package fpt.edu.capstone.utils;

import java.util.HashMap;
import java.util.Map;

public class Enums {
    public enum ResponseStatus {
        SUCCESS("Success"), ERROR("Error"), NOTFOUND("NotFound");

        ResponseStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        private String status;

    }

    public enum ApprovalStatus {
        APPROVED("Approved"), REJECT("Reject"), PENDING("Pending");

        ApprovalStatus(String status) {this.status = status;}

        public String getStatus() {return status;}

        private String status;
    }

    public enum FileUploadType {
        Image("image"), CV("cv");

        private String status;

        public String getStatus() {return status;}

        FileUploadType(String status) {
            this.status = status;
        }

        private  static final Map<String, FileUploadType> map = new HashMap<>();
        static {
            for (FileUploadType value:values()) {
                map.put(value.getStatus(),value);
            }
        }
        public static FileUploadType parse(String type) {
            return  map.get((type));
        }
    }

    public enum Flag {
        Draft("Draft"), Posted("Posted");

        Flag(String status) {this.status = status;}

        public String getStatus() {return status;}

        private String status;
    }

    public enum BannerPosition {
        SPOTLIGHT("spotlight"), HOME_BANNER_A("homepageBannerA"), HOME_BANNER_B("homepageBannerB"), HOME_BANNER_C("homepageBannerC"),
        JOB_BANNER_A("jobBannerA"), JOB_BANNER_B("jobBannerB"), JOB_BANNER_C("jobBannerC");;

        BannerPosition(String status) {this.status = status;}

        public String getStatus() {return status;}

        private String status;
    }

}