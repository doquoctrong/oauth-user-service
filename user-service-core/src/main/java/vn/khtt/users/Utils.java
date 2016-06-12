package vn.khtt.users;

import javax.servlet.http.HttpServletRequest;

class Utils {
  public static String getRequestBaseUrl(HttpServletRequest request){
    StringBuilder sb = new StringBuilder();
    
    sb.append(request.getScheme());
    sb.append("://");
    sb.append(request.getServerName());
    if (("http".equals(request.getScheme()) && request.getServerPort() != 80) 
        || (("https".equals(request.getScheme()) && request.getServerPort() != 443))) {
      sb.append(":");
      sb.append(request.getServerPort());
    }
    
    return sb.toString();
  }
}
