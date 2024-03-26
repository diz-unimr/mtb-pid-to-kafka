package de.unimarburg.diz.mtbpidtokafka.utils;

public class StringCreatorFromArray {
    public static String createInStringPidsArray(String[] pids) {
        StringBuilder sql = new StringBuilder("(");
        for (int i = 0; i < pids.length; i++) {
            sql.append("'").append(pids[i]).append("'");
            if (i < pids.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        return sql.toString();
    }

}
