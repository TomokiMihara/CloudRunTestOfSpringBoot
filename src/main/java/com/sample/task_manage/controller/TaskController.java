package com.sample.task_manage.controller;

import com.sample.task_manage.model.ViewModel.CreateTask;
import com.sample.task_manage.model.ViewModel.TaskLabel;
import com.sample.task_manage.model.ViewModel.TaskOverView;
import com.sample.task_manage.repository.TaskRepository;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository taskRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<TaskOverView> task_list = taskRepository.getAll();
        List<TaskLabel> label_list = taskRepository.getLabelList();

        model.addAttribute("TaskList", task_list);
        model.addAttribute("LabelList", label_list);

        return "task/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        List<TaskLabel> list = taskRepository.getLabelList();
        model.addAttribute("LabelList", list);
        return "task/create";
    }

    // タスク作成
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    String createTask(Model model, @ModelAttribute CreateTask taskForm) {
        taskRepository.createTask(taskForm);
        List<TaskOverView> task_list = taskRepository.getAll();
        List<TaskLabel> label_list = taskRepository.getLabelList();

        model.addAttribute("TaskList", task_list);
        model.addAttribute("LabelList", label_list);

        return "task/index";
    }

    // タスク作成
    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    String deleteTask(Model model, @PathVariable("id") int task_id) {
        taskRepository.deleteTask(task_id);

        return "ok";
    }

    // タスク名
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    String update(Model model, @ModelAttribute TaskOverView userForm, @PathVariable("id") int id) {
        return null;
    }
}