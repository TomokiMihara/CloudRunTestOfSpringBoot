package com.sample.task_manage.controller;

import com.sample.task_manage.model.Task;
import com.sample.task_manage.repository.TaskRepository;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GreetingController {
    private final TaskRepository taskRepository;
    
    @GetMapping("/")
    public String index(Model model) {
        List<Task> list = taskRepository.getAll();
		model.addAttribute("TaskList",list);
		return "test";
    }

}