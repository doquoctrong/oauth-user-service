package vn.khtt.users;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.api.DefaultApi20;

import com.googlecode.objectify.Key;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;

import java.net.URLEncoder;

import java.util.Iterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class UserServiceFactory {
  private static class UserServiceImpl implements UserService{
    @Override
    public String createLoginURL(String destinationURL, DefaultApi20 api) {
      try {
        destinationURL = URLEncoder.encode(destinationURL, Constants.UTF8);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      
      HttpServletRequest request = UserServiceFilter.getCurrentRequest();
      
      String baseUrl = Utils.getRequestBaseUrl(request);
      
      String loginUrl = baseUrl + "/" + UserServiceConfig.getInstance().getPathPrefix();
      if (api == FacebookApi.instance()){
        loginUrl += "/facebook";
        loginUrl += "?" + Constants.RETURN_URL_PARAM + "=" + destinationURL;
      }else{
        throw new RuntimeException("Unsupported OAuth provider " + api);
      }
      
      // TODO Implement this method
      return loginUrl;
    }

    @Override
    public User getCurrentUser() {
      HttpServletRequest request = UserServiceFilter.getCurrentRequest();
      String userId = null;
      for (Cookie c : request.getCookies()){
        if (Constants.COOKIE_NAME.equals(c.getName())){
          userId = c.getValue();
        }
      }
      
      if (userId == null){
        return null;
      }
      long id = Long.parseLong(userId);
      Key<User> key = Key.create(User.class, id);
      User user = ofy().load().key(key).now();
      if (user == null){
        return null;
      }
      // Remove the expired AuthProfiles
      for (Iterator<Key<? extends AuthProfile>> iterator = user.getAuthProfileKeys().iterator(); iterator.hasNext(); ) {
        Key<? extends AuthProfile> k = iterator.next();
        AuthProfile profile = ofy().load().key(k).now();
        if (profile == null){
          iterator.remove();
        }else{
          AccessToken accessToken = profile.getAccessToken();
          if (accessToken == null){
            iterator.remove();
          }else{
            if (accessToken.getExpired() < System.currentTimeMillis()){
              iterator.remove();
            }
          }
        }
      }
      
      if (user.getAuthProfileKeys().size() == 0){
        return null;
      }
      
      return user;
    }
  }
  
  private static UserService userService;
  public static UserService getUserService(){
    if (userService == null){
      userService = new UserServiceImpl();
    }
    
    return userService;
  }
}
