package com.sample.task_manage.model;

import com.google.api.client.util.DateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskOverView {
    //タスクID
    private int task_id;

    //タスク名
    private String task_name;

    //ラベル名
    private String tlabl_name;

    //ステータス名
    private String tstus_name;

    //優先順位
    private String tpri_name;

    //期限日時
    private DateTime tsm_end_datetime;

    //タスク起票日時
    private DateTime task_ins_timestamp;

    //ステータス最終更新日時
    private DateTime latest_state_updtimestamp;

    //タスク更新日時
    private DateTime task_upd_timestamp;

}