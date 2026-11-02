package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "tasks")
    public List<Task> getAllTasks() {
        System.out.println("Pobieram z PostgreSQL (Brak w cache!)");
        return repository.findAll();
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task saveTask(Task task) {
        return repository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return repository.findById(id);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public boolean deleteTask(Long id) {
        if(repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}