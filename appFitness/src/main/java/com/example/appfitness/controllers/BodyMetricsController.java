package com.example.appfitness.controllers;

import com.example.appfitness.DTOs.bodyMetrics.BodyMetricsRequestDTO;
import com.example.appfitness.DTOs.bodyMetrics.BodyMetricsResposeDTO;
import com.example.appfitness.auth.AuthService;
import com.example.appfitness.models.BodyMetrics;
import com.example.appfitness.models.User;
import com.example.appfitness.services.BodyMetricsService;
import com.example.appfitness.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/bodyMetrics")
public class BodyMetricsController {

    private BodyMetricsService bodyMetricsService;
    private UserService userService;
    private AuthService authService;

    public BodyMetricsController(BodyMetricsService bodyMetricsService,AuthService authService, UserService userService) {
        this.bodyMetricsService = bodyMetricsService;
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping // se metermos campos no DTO podemos acrescentar @Valid para validar campos
    public ResponseEntity<Object> createBodyMetrics(@RequestBody BodyMetricsRequestDTO createDTO,
                                                    @CookieValue(value = "token", defaultValue = "") String token) {


        int userId = Integer.parseInt(authService.getUserIdFromToken(token));
        //Integer userId = createDTO.getUserId();

        // get the user
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // pop objeto
        BodyMetrics bodyMetrics = new BodyMetrics();
        bodyMetrics.setUser(user);
        bodyMetrics.setWeight(createDTO.getWeight());
        bodyMetrics.setHeight(createDTO.getHeight());
        bodyMetrics.setBodyFatPercentage(createDTO.getBodyFatPercentage());
        bodyMetrics.setUpdatedAt(LocalDateTime.now());

        // para ja so aceitamos metric
        double bmi = calculateBMI(createDTO.getWeight(), createDTO.getHeight());

        bodyMetrics.setBmi(bmi);

        BodyMetrics createdMetrics = bodyMetricsService.createBodyMetrics(bodyMetrics);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMetrics);
    }

    // pegar em todos (progresso)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BodyMetricsResposeDTO>> getBodyMetrics(@PathVariable Integer userId) {
        List<BodyMetrics> bodyMetricsList = bodyMetricsService.getBodyMetricsFromUser(userId);

        List<BodyMetricsResposeDTO> response = bodyMetricsList.stream()
                .map(BodyMetricsResposeDTO::fromEntity)
                .toList();

        if (response.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(response);
    }

    // fazer um para obter a partir de um certo ponto?

    // pegar no ultimo (dashboard / display)
    @GetMapping("/{userId}/latest")
    public ResponseEntity<BodyMetricsResposeDTO> getLatestBodyMetrics(@PathVariable Integer userId) {
        Optional<BodyMetrics> lastestBM = bodyMetricsService.latestBodyMetricUser(userId);
        if (lastestBM.isPresent()) {
            BodyMetrics BodyMetrics = lastestBM.get();
            BodyMetricsResposeDTO resposeDTO = BodyMetricsResposeDTO.fromEntity(BodyMetrics);
            return ResponseEntity.ok(resposeDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

    }

    private double calculateBMI(Double weight, Double height) {
        if (weight == null || height == null || height == 0) {
            return 0.0;
        }
        // BMI = weight (kg) / (height (m) ^ 2)
        double heightM = (height / 100);
        return weight / (heightM * heightM);
    }

}
