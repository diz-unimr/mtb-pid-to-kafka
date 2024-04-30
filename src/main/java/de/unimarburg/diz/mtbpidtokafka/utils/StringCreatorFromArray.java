/*
 This file is part of MTB-PID-TO-KAFKA.

MTB-PID-TO-KAFKA - Get a CSV als Plane Text from MTB (Onkostar), extract the PIDs from the csv file, search all the oder_ids for each PID in NexusDB and produce the info as JSON in a Kafka
topic.

Copyright (C) 2024  Datenintegrationszentrum Philipps-Universität Marburg

MTB-PID-TO-KAFKA  is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

MTB-ID-TO-KAFKA is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package de.unimarburg.diz.mtbpidtokafka.utils;

public class StringCreatorFromArray {
    public static String createInStringPidsArray(String[] pids, String sqlStatement) {
        StringBuilder sql = new StringBuilder(sqlStatement);
        // Add the placeholder for each pid in the array
        for (int i = 0; i < pids.length; i++) {
            sql.append("?");
            if (i < pids.length - 1) {
                sql.append(",");
            }
        }
        sql.append(");");
        return sql.toString();
    }

    public static String createStringPidsForSql(String pid) {
        // Check the double cote and remove it
        StringBuilder sqlPid = new StringBuilder();
        if (pid.contains("\"")) {
            pid = pid.replaceAll("\"", "");
            return sqlPid.append("'").append(pid).append("'").toString();
        } else {
            return sqlPid.append("'").append(pid).append("'").toString();
        }
    }

    public static String createInStringPidsArrayOld(String[] pids) {
        StringBuilder sql = new StringBuilder(" (");
        for (int i = 0; i < pids.length; i++) {
            if (pids[i].contains("\"")) {
                String currentPid = pids[i].replaceAll("\"", "");
                sql.append("'").append(currentPid).append("'");
                if (i < pids.length - 1) {
                    sql.append(", ");
                }
            } else {
                sql.append("'").append(pids[i]).append("'");
                if (i < pids.length - 1) {
                    sql.append(", ");
                }

            }
        }
            sql.append(")");
            return sql.toString();

        }


}
