/* All materials herein: Copyright (c) 2019 Worldnet TPS Ltd. All Rights Reserved.
 *
 * These materials are owned by Worldnet TPS Ltd and are protected by copyright laws
 * and international copyright treaties, as well as other intellectual property laws
 * and treaties.
 *
 * All right, title and interest in the copyright, confidential information,
 * patents, design rights and all other intellectual property rights of
 * whatsoever nature in and to these materials are and shall remain the sole
 * and exclusive property of Worldnet TPS Ltd.
 */
package com.worldnet.redmine;

import com.taskadapter.redmineapi.bean.Issue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Leonardo Pavone - 14 Aug 2019.
 */
public class Utils {

    public static void logNewIssueCreated(String subject, Issue issue) {
        System.out.println(
            String.format("New issue %s created with subject: %s", issue.getId(), subject));
    }

    /**
     * Return a date a week earlier
     * @param dueDate   production due date
     * @return  a date a week earlier
     */
    public static Date getTestDueDate(Date dueDate){
        LocalDate localDueDate = Instant.ofEpochMilli(dueDate.getTime())
            .atZone(ZoneId.systemDefault()).toLocalDate();

        return Date.from(
            localDueDate.minusWeeks(1)
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant());
    }

}
