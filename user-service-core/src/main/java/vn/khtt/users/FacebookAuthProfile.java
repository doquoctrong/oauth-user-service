package vn.khtt.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindex;

import java.util.Date;

@Subclass
public class FacebookAuthProfile extends AuthProfile {
  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  @Index 
  private String gender;
  
  @Index 
  private int timezone;
  
  @Index 
  private boolean verified;
  
  @Unindex 
  @JsonProperty("updated_time")
  private Date updatedTime;
  
  @Index 
  private String locale;

  private String link;

  public FacebookAuthProfile() {
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getGender() {
    return gender;
  }

  public int getTimezone() {
    return timezone;
  }

  public boolean isVerified() {
    return verified;
  }

  public Date getUpdatedTime() {
    return updatedTime;
  }

  public String getLocale() {
    return locale;
  }

  public String getLink() {
    return link;
  }
}
