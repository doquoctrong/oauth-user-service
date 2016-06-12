package vn.khtt.users;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
  @Id
  private Long id;

  @Index 
  private String email;
  
  private String name;

  @Index
  private List<Key<? extends AuthProfile>> authProfileKeys = new ArrayList<Key<? extends AuthProfile>>();
  
  public User() {
  }

  public long getId() {
    return id;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public List<Key<? extends AuthProfile>> getAuthProfileKeys() {
    return authProfileKeys;
  }
  
  public String toString(){
    return "id=" + id + ", email=" + email + ", name=" + name;
  }
}
