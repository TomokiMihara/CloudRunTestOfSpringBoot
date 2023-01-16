package com.sample.task_manage.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task {
    private int task_id;
    private String task_name;
    //private int task_label_id;
    private Date task_ins_timestamp;
    private Date task_upd_timestamp;
    //private int task_del_flg;
}