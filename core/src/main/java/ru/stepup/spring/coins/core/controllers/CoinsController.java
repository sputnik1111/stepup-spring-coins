package ru.stepup.spring.coins.core.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.stepup.spring.coins.core.api.ExecuteCoinsRequest;
import ru.stepup.spring.coins.core.api.ExecuteCoinsResponse;
import ru.stepup.spring.coins.core.services.CoinsService;

@RestController
@RequestMapping("/api/v1/coins")
public class CoinsController {
    private final CoinsService coinsService;

    public CoinsController(CoinsService coinsService) {
        this.coinsService = coinsService;
    }

    @PostMapping("/execute")
    public ExecuteCoinsResponse execute(
            @RequestBody ExecuteCoinsRequest request,
            @RequestHeader("USERID") Long userId
    ) {
        return coinsService.execute(request,userId);
    }
}
