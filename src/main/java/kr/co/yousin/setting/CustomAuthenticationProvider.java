package kr.co.yousin.setting;

import kr.co.yousin.login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
/*
public class CustomAuthenticationProvider {

}
*/

/* */
@Component
public class CustomAuthenticationProvider  implements AuthenticationProvider {

    @Autowired
    private LoginService loginService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getName();
        UserDetails user = loginService.loadUserByUsername(token);
        return new UsernamePasswordAuthenticationToken(user, "");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
