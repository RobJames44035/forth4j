/*
 * Copyright 2024 Robert A. James
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rajames.forth.init

import org.h2.tools.RunScript
import org.h2.tools.Script
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException

@Service
class DatabaseBackupService {

    @Autowired
    private DataSource dataSource

    void backupDatabase(String path, String filename) throws SQLException {
        // saving the backup to an SQL file
        if (StringUtils.isEmpty(filename)) {
            filename = "fourth4j.sql"
        }
        if (StringUtils.isEmpty(path)) {
            path = "/home/rajames/PROJECTS/forth4j"
        }
        final String backupFilePath = path + "/" + filename
        try (final Connection conn = this.dataSource.getConnection()) {
            Script.process(conn, backupFilePath, "", "")
        }
    }

    void loadDatabase(String path, String filename) throws SQLException {
        // loading the backup from the SQL file
        if (filename == "") {
            filename = "fourth4j.sql"
        }
        if (path == "") {
            path = "/home/rajames/PROJECTS/forth4j"
        }
        String backupFilePath = path + "/" + filename

        try (final Connection conn = this.dataSource.getConnection()) {
            RunScript.execute(conn, new FileReader(backupFilePath))
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e)
        }
    }
}
