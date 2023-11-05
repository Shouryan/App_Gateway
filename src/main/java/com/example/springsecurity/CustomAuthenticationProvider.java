package com.example.springsecurity;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.model.User;
import com.model.UserLoginRequest;

import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final WebClient webClient;

    public CustomAuthenticationProvider(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = webClient.post()
            .uri("/login")
            .body(BodyInserters.fromValue(new UserLoginRequest(username, password))) 
            .retrieve()
            .bodyToMono(User.class)
            .block();

        if (user != null) {
            Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getUserType()));
            return new UsernamePasswordAuthenticationToken(username, password, authorities);
        } else {
            throw new AuthenticationException("Authentication failed") {
            	
            };
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
