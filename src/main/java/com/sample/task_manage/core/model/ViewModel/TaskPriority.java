package com.sample.task_manage.core.model.ViewModel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskPriority {
    // 優先順位ID
    private int tpri_id;

    // 優先順位名
    private String tpri_name;

}