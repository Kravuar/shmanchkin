package net.kravuar.shmanchkin.application.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import net.kravuar.shmanchkin.application.services.UserService;
import net.kravuar.shmanchkin.domain.model.dto.DetailedUserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Информация о пользователе", description = "Получить информацию о текущем пользователе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация получена."),
    })
    @GetMapping("/userInfo")
    public Mono<DetailedUserDTO> userInfo() {
        return userService.getFullCurrentUser();
    }
}
