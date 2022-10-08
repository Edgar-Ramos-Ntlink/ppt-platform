package com.business.unknow.services.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@Slf4j
public class AuthenticationFilter extends GenericFilterBean {

  private static final String ANONYMOUS_USER = "anonymousUser";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null) {
      if (!ANONYMOUS_USER.equals(authentication.getPrincipal().toString())) {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        if (oidcUser != null && oidcUser.getAttributes() != null && oidcUser.getEmail() != null) {
          log.info(
              "{} is requesting {}?{} from {}",
              oidcUser.getEmail(),
              req.getRequestURL(),
              req.getQueryString(),
              request.getRemoteAddr());
          filterChain.doFilter(request, response);
        } else {
          HttpServletResponse resp = (HttpServletResponse) response;
          resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Session invalida.");
          filterChain.doFilter(request, response);
        }
      } else {
        if (!req.getRequestURL().toString().contains("/actuator")) {
          HttpServletResponse resp = (HttpServletResponse) response;
          resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autorizado.");
        }
        filterChain.doFilter(request, response);
      }
    } else {
      log.warn(
          "NO AUTHENTICATED request to: {} from {}", req.getRequestURL(), request.getRemoteAddr());
      filterChain.doFilter(request, response);
    }
  }
}
