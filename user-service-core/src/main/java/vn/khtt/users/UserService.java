package vn.khtt.users;

import com.github.scribejava.core.builder.api.DefaultApi20;

public interface UserService {
  String createLoginURL(String destinationURL, DefaultApi20 api);
  
  User getCurrentUser();
}
