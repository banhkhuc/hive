package fpt.edu.capstone.utils.enums;

/**
 * @author hungnd on 19-Feb-2021
 */
public class SystemEnum {

  public enum RoleType {
    ADMIN("ADMIN"), RECRUITER("RECRUITER"),CANDIDATE("CANDIDATE");

    RoleType(String roleType) {
      this.roleType = roleType;
    }

    private String roleType;

    public String value() {
      return roleType;
    }
  }

  public enum Scopes {
    REFRESH_TOKEN;

    public String authority() {
      return "ROLE_" + this.name();
    }
  }
}
