package com.springboot.demo_park_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class SpringJpaAuditingConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        //classe para pegar o nome do usuário
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //testar se o objeto de autenticacao != nulo p/ evitar NullPointerException
        if(authentication != null && authentication.isAuthenticated()) {
            return Optional.of(authentication.getName()); //retorna o username
        }
        return null;
    }
}
