package kr.co.yousin.login.service;

import kr.co.yousin.vo.SystemMessage;
import kr.co.yousin.vo.SystemMessageRepository;
import kr.co.yousin.vo.UserToken;
import kr.co.yousin.vo.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private SystemMessageRepository systemMessageRepository;

    public UserDetails loadUserByUsername(String toekn) throws AuthenticationException {

        Optional<UserToken> _userToken = tokenRepository.findByToken(toekn.replaceAll("-", "").trim().toUpperCase());

        //TODO 3회 안내 =>  5회 틀릴 경우 IP 장금 + IP 저장 로직 추가
        if(_userToken.isEmpty()) {
           throw new UsernameNotFoundException("NotFound");
        }
        
        LocalDateTime now = LocalDateTime.now();
        UserToken userToken = _userToken.get();
        LocalDateTime initDate = userToken.getInitialAccessDate();

        if(initDate == null){
            tokenRepository.updateInitialAccessDate(userToken.getToken(), now);
        }else{

            if(userToken.getPeriodValidity() != 0 && Duration.between(initDate, now).toDays() > userToken.getPeriodValidity()){
                throw new AccountExpiredException("AccountExpired");
            }
        }
        tokenRepository.updaetLastAccessDate(userToken.getToken(), now);

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(userToken.getRole().name()));

        return new User(userToken.getToken(), "{noop}", authorityList);

        //UserDetails user = new User(userToken.getToken(), "{noop}", authorityList);
        //return user;
    }

    public Object[] getSystemMessage(String checkDate){
        Object[][] msgInfo = systemMessageRepository.findInitSystemMessageInfo(checkDate);

        if(msgInfo == null || msgInfo.length == 0){
            return null;
        }

        return msgInfo[0];

    }

}
