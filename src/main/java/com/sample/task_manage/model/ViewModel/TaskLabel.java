package com.sample.task_manage.model.ViewModel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskLabel {
    // ラベルID
    private int tlabl_id;

    // ラベル名
    private String tlabl_name;

}