package com.todo.todowebapp.controller;

import com.todo.todowebapp.model.Task;
import com.todo.todowebapp.model.User;
import com.todo.todowebapp.repository.TaskRepository;
import com.todo.todowebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/uncompleted")
    public List<Task> getUncompletedTasks() {
        return taskRepository.findByCompletedDateIsNull();
    }

    @GetMapping("/completed")
    public List<Task> getCompletedTasks() {
        return taskRepository.findByCompletedDateIsNotNullOrderByCompletedDateDesc();
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Task createTask(@RequestBody Task task) {
        // Ensure the assigned user exists
        Optional<User> user = userRepository.findById(task.getAssignedTo().getId());
        user.ifPresent(task::setAssignedTo);
        return taskRepository.save(task);
    }

    @PostMapping("/complete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Task markAsCompleted(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setCompletedDate(LocalDate.now());
        return taskRepository.save(task);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}

