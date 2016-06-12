package vn.khtt.users;

import com.googlecode.objectify.Key;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;

import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthHanlder {
  boolean isConfigOK();
  String getAuthPath();
  String getAuthCallbackPath();
  void processAuth(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
  void authCallback(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
  
  public abstract class BaseAuthHanlder implements AuthHanlder {
    private static final String CLASS_SUFFIX = "AuthHanlder";
    protected String appId;
    protected String appSecret;

    @Override
    public boolean isConfigOK() {
      return appId != null && appSecret != null;
    }

    @Override
    public String getAuthPath() {
      UserServiceConfig config = UserServiceConfig.getInstance();
      String className = getClass().getSimpleName();
      if (className.endsWith(CLASS_SUFFIX)){
        className = className.substring(0, className.length() - CLASS_SUFFIX.length());
        return "/" + config.getPathPrefix() + "/" + className.toLowerCase();
      }
      
      throw new RuntimeException(getClass().getName() + " does not override the getAuthPath method");
    }

    @Override
    public String getAuthCallbackPath() {
      UserServiceConfig config = UserServiceConfig.getInstance();
      String className = getClass().getSimpleName();
      if (className.endsWith(CLASS_SUFFIX)){
        className = className.substring(0, className.length() - CLASS_SUFFIX.length());
        return "/" + config.getPathPrefix() + "/" + className.toLowerCase() + "/callback";
      }
      
      throw new RuntimeException(getClass().getName() + " does not override the getAuthCallbackPath method");
    }

    /**
     * Generate the OAuth state string
     * In our stateless implementation, state string consists of secret code and the return url
     * @param request
     * @return
     */
    protected String generateState(HttpServletRequest request){
      String returnUrl = request.getParameter(Constants.RETURN_URL_PARAM);
      if (returnUrl == null){
        returnUrl = "/";
      }
      
      String secret = generateSecret(request);
      
      // TODO
      return secret + ";" + returnUrl;
    }

    protected String getReturnUrl(HttpServletRequest request){
      String state = request.getParameter("state");
      int pos = state.indexOf(';');
      if (pos < 0){
        throw new RuntimeException("Invalid state");
      }
      
      String secret = state.substring(0, pos);
      String returnUrl = state.substring(pos+1);
      
      checkSecret(request, secret);
      
      return returnUrl;
    }
    
    /**
     * Save AuthProfile
     * @param authProfile
     * @param accessToken
     * @return User associated with the AuthProfile
     */
    protected User saveAuthProfile(AuthProfile authProfile, AccessToken accessToken){
      authProfile.setAccessToken(accessToken);
      ofy().save().entity(authProfile).now();
      Key<? extends AuthProfile> authProfileKey = Key.create(AuthProfile.class, authProfile.getId());
      User user = ofy().load().type(User.class).filter("authProfileKeys", authProfileKey).first().now();
      if (user == null){
        user = new User();
        user.setName(authProfile.getName());
        user.setEmail(authProfile.getEmail());
        user.getAuthProfileKeys().add(authProfileKey);
      }else{
        // Update AuthProfile
        for (Iterator<Key<? extends AuthProfile>> iterator = user.getAuthProfileKeys().iterator(); iterator.hasNext(); ) {
          Key<? extends AuthProfile> k = iterator.next();
          if (authProfileKey.equals(k)){
            iterator.remove();
          }
        }
        user.getAuthProfileKeys().add(authProfileKey);
      }
      ofy().save().entity(user).now();
      
      return user;
    }

    private String generateSecret(HttpServletRequest request){
      String remoteAddr = request.getRemoteAddr();
      String agent = request.getHeader("User-Agent");
      
      // TODO
      long num1 = remoteAddr.hashCode();
      long num2 = agent.hashCode();
      long num = num1 * num2;

      String secret = "" + num;
      
      return secret;
    }

    private void checkSecret(HttpServletRequest request, String secret){
      String originalSecret = generateSecret(request);
      if (!originalSecret.equals(secret)){
        throw new RuntimeException("Invalid secret");
      }
    }
  }
}
