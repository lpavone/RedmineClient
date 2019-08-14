/* All materials herein: Copyright (c) 2017 Worldnet TPS Ltd. All Rights Reserved.
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

package com.worldnet.redmine.action;

import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueCategory;
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.Version;

import java.util.Date;

/**
 * Create new issue.
 *
 * @author Leonardo Pavone - 25/09/17.
 */
public class IssueCustomFactory {

    public static Issue of(String subject, Version version, String description,
        IssueCategory category, Tracker tracker, Integer parentId, CustomField customerCustomField,
        int projectId, int assigneeId, int reviewerId, int statusId, Date dueDate) {

        Issue issue = IssueFactory.create(projectId, subject);
        issue.setTargetVersion(version);
        issue.setDescription(description);
        issue.setCategory(category);
        issue.setAssigneeId(assigneeId);
        issue.setTracker(tracker);
        issue.setStatusId(statusId);
        if (parentId != null) {
            issue.setParentId(parentId);
        }
        issue.addCustomField(customerCustomField);
        issue.addCustomFields();
        issue.setDueDate(dueDate);
        return issue;
    }


}
