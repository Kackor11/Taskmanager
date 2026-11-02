package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    // Pobiera TYLKO zadania zalogowanego użytkownika
    @GetMapping("/api/tasks")
    public List<Task> getMyTasks(@AuthenticationPrincipal Jwt jwt) {
        String zitadelId = jwt.getSubject();
        return userRepository.findByZitadelId(zitadelId)
                .map(user -> taskRepository.findByUserId(user.getId()))
                .orElse(List.of());
    }

    // Pobiera zadania WSZYSTKICH (tylko dla admina)
    @GetMapping("/api/tasks/all")
    public List<Task> getAllSystemTasks() {
        return taskRepository.findAll();
    }

    // Tworzy zadanie przypisane do aktualnego użytkownika
    @PostMapping("/api/tasks")
    public Task createTask(@RequestBody Task task, @AuthenticationPrincipal Jwt jwt) {
        String zitadelId = jwt.getSubject();

        User user = userRepository.findByZitadelId(zitadelId).orElseGet(() -> {
            User newUser = new User();
            newUser.setZitadelId(zitadelId);
            String name = jwt.getClaimAsString("email");
            newUser.setUsername(name != null ? name : "Użytkownik");
            return userRepository.save(newUser);
        });

        task.setUserId(user.getId());
        return taskRepository.save(task);
    }

    // Użytkownik usuwa zadanie - sprawdzamy, czy zadanie należy do niego
    @DeleteMapping("/api/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String zitadelId = jwt.getSubject();
        User user = userRepository.findByZitadelId(zitadelId).orElseThrow();
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        if (task.getUserId().equals(user.getId())) {
            taskRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}