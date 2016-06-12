package vn.khtt.users;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class AuthProfile {
  @Id
  private String id;
  
  @Index
  private String name;

  @Index
  private String email;

  private AccessToken accessToken;

  public AuthProfile() {
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
  }

  public AccessToken getAccessToken() {
    return accessToken;
  }
}
