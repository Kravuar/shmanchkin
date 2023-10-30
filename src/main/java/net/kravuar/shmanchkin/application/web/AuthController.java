package net.kravuar.shmanchkin.application.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.AuthService;
import net.kravuar.shmanchkin.application.services.CookieService;
import net.kravuar.shmanchkin.domain.model.dto.SignInFormDTO;
import net.kravuar.shmanchkin.domain.model.dto.UserDTO;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;

    @Operation(summary = "Авторизация", description = "Войти в аккаунт.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная авторизация, jwt cookie получены."),
    })
    @PostMapping("/signIn")
    public UserDTO signIn(@RequestBody @Valid SignInFormDTO signInForm, ServerHttpResponse response) {
        var loggedUser = authService.singIn(signInForm);
        assert loggedUser != null;
        cookieService.setJWTCookies(response, loggedUser.getAccessToken(), loggedUser.getRefreshToken());
        return new UserDTO(loggedUser.getUserInfo());
    }

    @Operation(summary = "Обновление JWT", description = "Обновить пару JWT токенов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное обновление, jwt cookie получены."),
            @ApiResponse(responseCode = "403", description = "Отсутствует refresh token"),
    })
    @GetMapping("/refresh")
    public void refresh(ServerHttpRequest request, ServerHttpResponse response) {
        var loggedUser = authService.refresh(request);
        assert loggedUser != null;
        cookieService.setJWTCookies(response, loggedUser.getAccessToken(), loggedUser.getRefreshToken());
    }

    @Operation(summary = "Выход из аккаунта", description = "Выйти из аккаунта.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JWT cookie удалены."),
    })
    @GetMapping("/logout")
    public void logout(ServerHttpResponse response) {
        cookieService.invalidateJWTCookies(response);
    }
}
