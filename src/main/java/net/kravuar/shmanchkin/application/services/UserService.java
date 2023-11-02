package net.kravuar.shmanchkin.application.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.domain.model.account.UserInfo;
import net.kravuar.shmanchkin.domain.model.dto.DetailedUserDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    public Mono<UserInfo> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new AccessDeniedException("Пользователь не авторизован.")))
                .map(securityContext -> (UserInfo) securityContext.getAuthentication().getPrincipal());
    }

    public Mono<DetailedUserDTO> getFullCurrentUser() {
        return getCurrentUser().map(DetailedUserDTO::new);
    }
}
