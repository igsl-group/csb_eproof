package com.hkgov.csb.eproof.config;


import com.hkgov.csb.eproof.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class UserAuditorConfig implements AuditorAware<String> {

    @NotNull
    @Override
    public Optional<String> getCurrentAuditor() {

        User user;
        try {
            user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return Optional.of(user).map(User::getDpUserId);
        }catch (Exception e){
            return Optional.empty();
        }
    }
}