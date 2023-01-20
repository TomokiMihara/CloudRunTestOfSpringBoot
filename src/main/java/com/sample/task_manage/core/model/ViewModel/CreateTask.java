package com.sample.task_manage.core.model.ViewModel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTask {

    private String task_name;

    private int task_label_id;

}