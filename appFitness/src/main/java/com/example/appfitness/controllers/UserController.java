package com.example.appfitness.controllers;

//import com.example.appfitness.auth.JwtService;
import com.example.appfitness.DTOs.Aluno.DashboardWorkoutDTO;
import com.example.appfitness.DTOs.Settings.SettingsDTO;
import com.example.appfitness.auth.AuthService;
import com.example.appfitness.models.User;
import com.example.appfitness.models.WorkoutExecution;
import com.example.appfitness.models.WorkoutPlan;
import com.example.appfitness.repositories.UserRepository;
import com.example.appfitness.repositories.WorkoutExecutionRepository;
import com.example.appfitness.repositories.WorkoutPlanRepository;

import com.example.appfitness.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private WorkoutExecutionRepository workoutExecutionRepository;
    private WorkoutPlanRepository workoutPlanRepository;
    private AuthService authService;
    private UserService userService;

    public UserController (AuthService authService, UserService userService, WorkoutExecutionRepository workoutExecutionRepository, WorkoutPlanRepository workoutPlanRepository) {
        this.workoutExecutionRepository = workoutExecutionRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/dashboard-workout")
    public ResponseEntity<DashboardWorkoutDTO> getDashboardWorkout(@CookieValue(value = "token", defaultValue = "") String token) {
        Integer userId = Integer.valueOf(authService.getUserIdFromToken(token));

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));


        Optional<WorkoutExecution> inProgressWorkout = workoutExecutionRepository.findByUserAndStatus(user, WorkoutExecution.WorkoutStatus.IN_PROGRESS);

        // se está a decorrer um workout
        if (inProgressWorkout.isPresent()) {
            WorkoutExecution workoutExecution = inProgressWorkout.get();
            return ResponseEntity.ok(DashboardWorkoutDTO.convertToExecution(workoutExecution));
        }

        // se temos algum scheduled

        List<WorkoutPlan> workoutPlans = workoutPlanRepository.findByOwnerIdAndScheduleTypeAndActiveTrue(userId, WorkoutPlan.WorkoutScheduleType.Fixed);

        // mapa plano -> dia
        Optional<Map.Entry<WorkoutPlan, LocalDate>> scheduledWorkouts = workoutPlans
                .stream()
                .map(plan -> {
                    Optional<LocalDate> nextDate = getNextScheduledWorkout(plan.getScheduledDays());
                    return nextDate.map(date -> Map.entry(plan, date)).orElse(null);
                })
                .min(Comparator.comparing(workoutPlanLocalDateEntry -> workoutPlanLocalDateEntry != null ? workoutPlanLocalDateEntry.getValue() : null));

        if (scheduledWorkouts.isPresent()) {
            Map.Entry<WorkoutPlan, LocalDate> candidate = scheduledWorkouts.get();
            WorkoutPlan plan = candidate.getKey();
            LocalDate nextDate = candidate.getValue();

            return ResponseEntity.ok(DashboardWorkoutDTO.converToScheduled(plan, nextDate));
        }

        return ResponseEntity.ok(DashboardWorkoutDTO.none());
    }

    private Optional<LocalDate> getNextScheduledWorkout(Set<DayOfWeek> scheduledDays){
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        if(scheduledDays.contains(dayOfWeek)){
            return Optional.of(today);
        }

        for(int i = 0; i <= 7; i++){
            LocalDate dateToCheck = today.plusDays(i);
            if(scheduledDays.contains(dateToCheck.getDayOfWeek())){
                return Optional.of(dateToCheck);
            }
        }
        return Optional.empty();
    }

    @PostMapping("/settings")
    private SettingsDTO updateSettings(@RequestBody SettingsDTO settings, @CookieValue(value = "token", defaultValue = "") String token){
        System.out.println(settings);

        Integer userId = Integer.valueOf(authService.getUserIdFromToken(token));

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        return userService.updateSettings(settings, user);
    }

    /*private final JwtService jwtService;
    private final UserService userService;

    public UserController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    // Get currently logged in user's info (by token)
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        String userIdStr = jwtService.extractUserId(token);

        int userId = Integer.parseInt(userIdStr);
        Optional<User> userOpt = userService.getUserById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User user = userOpt.get();
        // Avoid sending password back!
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }*/
}
