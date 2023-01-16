package com.sample.task_manage.factory;
/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 // [START cloud_sql_mysql_servlet_connect_connector]
 // [START cloud_sql_mysql_servlet_connect_unix]
 
 import com.zaxxer.hikari.HikariConfig;
 import com.zaxxer.hikari.HikariDataSource;
 import javax.sql.DataSource;
 
 public class ConnectorConnectionPoolFactory extends ConnectionPoolFactory {

   private static final String INSTANCE_CONNECTION_NAME =
       System.getenv("INSTANCE_CONNECTION_NAME");
   private static final String INSTANCE_UNIX_SOCKET = System.getenv("INSTANCE_UNIX_SOCKET");
   private static final String DB_USER = System.getenv("DB_USER");
   private static final String DB_PASS = System.getenv("DB_PASS");
   private static final String DB_NAME = System.getenv("DB_NAME");
 
   public static DataSource createConnectionPool() {
     // The configuration object specifies behaviors for the connection pool.
     HikariConfig config = new HikariConfig();

     config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
     config.setUsername(DB_USER); // e.g. "root", "mysql"
     config.setPassword(DB_PASS); // e.g. "my-password"
 
     config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
     config.addDataSourceProperty("cloudSqlInstance", INSTANCE_CONNECTION_NAME);

     if (INSTANCE_UNIX_SOCKET != null) {
       config.addDataSourceProperty("unixSocketPath", INSTANCE_UNIX_SOCKET);
     }

     config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");

     configureConnectionPool(config);

     return new HikariDataSource(config);
   }
 }
