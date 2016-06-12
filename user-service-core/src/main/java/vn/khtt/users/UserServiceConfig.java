package vn.khtt.users;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

public class UserServiceConfig {
  private static UserServiceConfig instance;
  
  public static UserServiceConfig getInstance(){
    if (instance == null){
      instance = new UserServiceConfig();
    }
    
    return instance;
  }
  
  private Properties props = new Properties();
  private String pathPrefix = "auth";

  private UserServiceConfig() {
    InputStream in = UserServiceConfig.class.getResourceAsStream("/user-service.properties");

    try {
      props.load(in);
      if (props.getProperty("path.prefix") != null){
        pathPrefix = props.getProperty("path.prefix");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getPathPrefix() {
    return pathPrefix;
  }

  public Properties getProps() {
    return props;
  }
}
