package com.sample.task_manage.core.model.ViewModel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskStatus {
    // ステータスID
    private int tstus_id;

    // ステータス名
    private String tstus_name;

}