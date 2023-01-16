package com.sample.task_manage.repository;

import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.google.api.client.util.DateTime;
import com.sample.task_manage.factory.ConnectorConnectionPoolFactory;
import com.sample.task_manage.model.TaskOverView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class TaskRepository {
    private static class PoolHolder {

        // Making the default constructor private prohibits instantiation of this class
        private PoolHolder() {
        }

        // This value is initialized only if (and when) the getInstance() function below
        // is called
        private static final DataSource INSTANCE = setupPool();

        private static DataSource setupPool() {
            DataSource pool;
            pool = ConnectorConnectionPoolFactory.createConnectionPool();

            return pool;
        }

        private static DataSource getInstance() {
            return PoolHolder.INSTANCE;
        }
    }

    public List<TaskOverView> getAll() {
        DataSource pool = PoolHolder.getInstance();
        List<TaskOverView> list = new ArrayList<>();

        String sql = """
                    SELECT
                    task_id,
                    task_name,
                    tlabl_name,
                    tstus_name,
                    tpri_name,
                    tsm_end_datetime,
                    tsk.task_ins_timestamp,
                    latest_task_state.latest_ins_timestamp latest_state_updtimestamp,
                    task_upd_timestamp
                FROM
                    task_up.task tsk
                    LEFT JOIN task_label tlbl ON tlbl.tlabl_id = tsk.task_label_id
                    LEFT JOIN (
                        SELECT
                            tsm_task_id,
                            MAX(task_ins_timestamp) latest_ins_timestamp
                        FROM
                            task_state_manage
                        GROUP BY
                            tsm_task_id
                    ) latest_task_state ON tsk.task_id = latest_task_state.tsm_task_id
                    LEFT JOIN (
                        SELECT
                            tsm_task_id,
                            tstus_name,
                            tpri_name,
                            tsm_end_datetime,
                            task_ins_timestamp
                        FROM
                            task_state_manage tsm
                            LEFT JOIN task_status tstus ON tstus.tstus_id = tsm.tsm_tstus_id
                            LEFT JOIN task_priority tpri ON tpri.tpri_id = tsm.tsm_tpri_id
                    ) latest_task_state_manage ON latest_task_state.tsm_task_id = latest_task_state_manage.tsm_task_id
                    AND latest_task_state_manage.task_ins_timestamp = latest_task_state.latest_ins_timestamp
                WHERE
                    task_del_flg = 0
                        """;
        try {
            Connection conn = pool.getConnection();
            PreparedStatement taskStmt = conn.prepareStatement(sql);

            ResultSet taskResults = taskStmt.executeQuery();

            while (taskResults.next()) {
                int task_id = taskResults.getInt("task_id");
                String task_name = taskResults.getString("task_name");
                String tlabl_name = taskResults.getString("tlabl_name");
                String tstus_name = taskResults.getString("tstus_name");
                String tpri_name = taskResults.getString("tpri_name");
                DateTime tsm_end_datetime = new DateTime(taskResults.getTimestamp("tsm_end_datetime").getTime());
                DateTime task_ins_timestamp = new DateTime(taskResults.getTimestamp("task_ins_timestamp").getTime());
                DateTime latest_state_updtimestamp = new DateTime(
                        taskResults.getTimestamp("latest_state_updtimestamp").getTime());
                DateTime task_upd_timestamp = new DateTime(taskResults.getTimestamp("task_upd_timestamp").getTime());

                list.add(new TaskOverView(
                        task_id,
                        task_name,
                        tlabl_name,
                        tstus_name,
                        tpri_name,
                        tsm_end_datetime,
                        task_ins_timestamp,
                        latest_state_updtimestamp,
                        task_upd_timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
