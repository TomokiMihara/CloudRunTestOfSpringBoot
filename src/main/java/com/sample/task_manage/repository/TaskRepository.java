package com.sample.task_manage.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.sql.ResultSet;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.sample.task_manage.factory.ConnectorConnectionPoolFactory;
import com.sample.task_manage.model.Task;

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

    public List<Task> getAll() {
        DataSource pool = PoolHolder.getInstance();
        List<Task> list = new ArrayList<>();

        String sql = "SELECT task_id, task_name, task_ins_timestamp, task_upd_timestamp, task_del_flg FROM task_up.task";
        try {
            Connection conn = pool.getConnection();
            PreparedStatement taskStmt = conn.prepareStatement(sql);

            ResultSet taskResults = taskStmt.executeQuery();

            while (taskResults.next()) {
                int task_id = taskResults.getInt("task_id");
                String task_name = taskResults.getString("task_name");
                Date task_ins_timestamp = taskResults.getDate("task_ins_timestamp");
                Date task_upd_timestamp = taskResults.getDate("task_upd_timestamp");
                list.add(new Task(
                        task_id,
                        task_name,
                        task_ins_timestamp,
                        task_upd_timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
