package com.example.taskmanager.controller;

import com.example.taskmanager.model.Meeting;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.MeetingRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = "*")
public class MeetingController {
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    public MeetingController(MeetingRepository meetingRepository, UserRepository userRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
    }

    // Bezpieczne pobieranie na podstawie JWT (Zgodne z wymogami projektu)
    @GetMapping
    public List<Meeting> getMyMeetings(@AuthenticationPrincipal Jwt jwt) {
        String zitadelId = jwt.getSubject();
        return userRepository.findByZitadelId(zitadelId)
                .map(user -> meetingRepository.findByUserId(user.getId()))
                .orElse(List.of());
    }

    // Bezpieczne zapisywanie z wymuszeniem ID z JWT
    @PostMapping
    public Meeting createMeeting(@RequestBody Meeting meeting, @AuthenticationPrincipal Jwt jwt) {
        String zitadelId = jwt.getSubject();

        // Synchronizacja z bazą "w locie"
        User user = userRepository.findByZitadelId(zitadelId).orElseGet(() -> {
            User newUser = new User();
            newUser.setZitadelId(zitadelId);
            String name = jwt.getClaimAsString("preferred_username");
            newUser.setUsername(name != null ? name : "Użytkownik Zitadel");
            return userRepository.save(newUser);
        });

        meeting.setUserId(user.getId());
        return meetingRepository.save(meeting);
    }

    @DeleteMapping("/{id}")
    public void deleteMeeting(@PathVariable Long id) {
        meetingRepository.deleteById(id);
    }
}