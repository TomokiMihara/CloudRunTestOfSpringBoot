package com.sample.task_manage.repository;

import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.sample.task_manage.factory.ConnectorConnectionPoolFactory;
import com.sample.task_manage.factory.TcpConnectionPoolFactory;
import com.sample.task_manage.model.ViewModel.CreateTask;
import com.sample.task_manage.model.ViewModel.TaskLabel;
import com.sample.task_manage.model.ViewModel.TaskOverView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Repository
@RequiredArgsConstructor
public class TaskRepository {
    private final static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH時mm分ss秒")
            .withZone(ZoneId.of("Asia/Tokyo"));

    private static class PoolHolder {

        private PoolHolder() {
        }

        private static final DataSource INSTANCE = setupPool();

        private static DataSource setupPool() {
            DataSource pool;

            String is_prod = System.getenv("is_prod");
            if (is_prod != null) {
                pool = ConnectorConnectionPoolFactory.createConnectionPool();
            } else {
                pool = TcpConnectionPoolFactory.createConnectionPool();
            }

            return pool;
        }

        private static DataSource getInstance() {
            return PoolHolder.INSTANCE;
        }
    }

    // 論理削除
    public void deleteTask(int task_id) {
        DataSource pool = PoolHolder.getInstance();

        String sql = """
                    update task set task_del_flg = 1 where task_id = ?
                """;

        Connection conn;

        try {
            conn = pool.getConnection();
            PreparedStatement taskStmt = conn.prepareStatement(sql);
            taskStmt.setInt(1, task_id);

            taskStmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTask(CreateTask task) {
        DataSource pool = PoolHolder.getInstance();

        String sql = """
                    INSERT INTO task (task_name,task_label_id,task_ins_timestamp,task_upd_timestamp,task_del_flg) values (?,?,?,?,0)
                """;

        Connection conn;

        try {
            conn = pool.getConnection();
            PreparedStatement taskStmt = conn.prepareStatement(sql);
            taskStmt.setString(1, task.getTask_name());
            taskStmt.setInt(2, task.getTask_label_id());
            taskStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            taskStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            taskStmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TaskLabel> getLabelList() {
        DataSource pool = PoolHolder.getInstance();
        List<TaskLabel> list = new ArrayList<>();

        String sql = """
                SELECT
                    tlabl_id,
                    tlabl_name
                FROM
                    task_up.task_label
                ORDER BY
                    tstus_ins_timestamp desc
                                """;
        try {
            Connection conn = pool.getConnection();
            PreparedStatement taskStmt = conn.prepareStatement(sql);

            ResultSet labelResults = taskStmt.executeQuery();

            while (labelResults.next()) {
                int tlabl_id = labelResults.getInt("tlabl_id");
                String tlabl_name = labelResults.getString("tlabl_name");

                list.add(new TaskLabel(
                        tlabl_id,
                        tlabl_name));
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
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

                String tsm_end_datetime_str = "";
                Timestamp tsm_end_timestamp = taskResults.getTimestamp("tsm_end_datetime");
                if (tsm_end_timestamp != null) {
                    LocalDateTime tsm_end_localDateTime = null;
                    tsm_end_localDateTime = tsm_end_timestamp.toLocalDateTime();
                    tsm_end_datetime_str = timeFormatter.format(tsm_end_localDateTime);
                }

                String task_ins_datetime_str = "";
                Timestamp task_ins_timestamp = taskResults.getTimestamp("task_ins_timestamp");
                if (task_ins_timestamp != null) {
                    LocalDateTime task_ins_localDateTime = null;
                    task_ins_localDateTime = task_ins_timestamp.toLocalDateTime();
                    task_ins_datetime_str = timeFormatter.format(task_ins_localDateTime);
                }

                String latest_states_datetime_str = "";
                Timestamp latest_state_updtimestamp = taskResults.getTimestamp("latest_state_updtimestamp");
                if (latest_state_updtimestamp != null) {
                    LocalDateTime latest_states_localDateTime = null;
                    latest_states_localDateTime = latest_state_updtimestamp.toLocalDateTime();
                    latest_states_datetime_str = timeFormatter.format(latest_states_localDateTime);
                }

                String task_upd_datetime_str = "";
                Timestamp task_upd_timestamp = taskResults.getTimestamp("task_upd_timestamp");
                if (task_upd_timestamp != null) {
                    LocalDateTime task_upd_localDateTime = null;
                    task_upd_localDateTime = task_upd_timestamp.toLocalDateTime();
                    task_upd_datetime_str = timeFormatter.format(task_upd_localDateTime);
                }

                list.add(new TaskOverView(
                        task_id,
                        task_name,
                        tlabl_name,
                        tstus_name,
                        tpri_name,
                        tsm_end_datetime_str,
                        task_ins_datetime_str,
                        latest_states_datetime_str,
                        task_upd_datetime_str));
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
