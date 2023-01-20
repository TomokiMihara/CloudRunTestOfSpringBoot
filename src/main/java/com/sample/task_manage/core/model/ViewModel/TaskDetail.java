package com.sample.task_manage.core.model.ViewModel;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskDetail {
    // タスクID
    private int task_id;

    // タスク名
    private String task_name;

    // ラベル名
    private String tlabl_name;

    // タスク起票日時
    private String task_ins_timestamp;

    // ステータス最終更新日時
    private String latest_state_updtimestamp;

    // タスク更新日時
    private String task_upd_timestamp;

    // 紐づくステータスリスト
    private List<StatusManage> status_manage_list;

    protected class StatusManage {
        // タスクID
        protected int tsm_task_id;

        // ステータスID
        protected int tsm_tstus_id;

        // 優先順位ID
        protected int tsm_tpri_id;

        protected LocalDateTime tsm_end_datetime;
    }

}