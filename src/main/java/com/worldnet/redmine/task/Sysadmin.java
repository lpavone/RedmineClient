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
package com.worldnet.redmine.task;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
import com.worldnet.redmine.Props;
import com.worldnet.redmine.Utils;
import com.worldnet.redmine.factory.CustomFields;
import com.worldnet.redmine.factory.IssueCustomFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Leonardo Pavone - 14 Aug 2019.
 */
public class Sysadmin implements TaskCreator {

    private RedmineManager manager;
    private String shorterNewVersionName;
    private Date dueDate;

    public Sysadmin(RedmineManager manager, String shorterNewVersionName, Date dueDate) {
        this.manager = manager;
        this.shorterNewVersionName = shorterNewVersionName;
        this.dueDate = dueDate;
    }

    @Override
    public void create() throws RedmineException {
        List<Tracker> trackers = manager.getIssueManager().getTrackers();
        Optional<Tracker> parentTaskTracker = trackers.stream()
            .filter(t -> t.getId().equals(Props.SYSADMIN_PARENT_TASK_TRACKER)).findAny();
        Optional<Tracker> maintenanceTracker = trackers.stream()
            .filter(t -> t.getId().equals(Props.SYSADMIN_MAINTENANCE_TRACKER)).findAny();

        Issue parentTask = createDeploymentTaskParentIssue(parentTaskTracker.get());
        createReRunJobsTask(maintenanceTracker.get(), parentTask.getId());
        createDeployToTest(maintenanceTracker.get(), parentTask.getId());
        createDeployToProduction(maintenanceTracker.get(), parentTask.getId());
    }

    /**
     * Create deployment tasks for sysadmins
     */
    private Issue createDeploymentTaskParentIssue(Tracker tracker) throws RedmineException {

        String subject = "Deploy " + shorterNewVersionName;
        Issue deploymentParentIssue = manager.getIssueManager().createIssue(
            IssueCustomFactory.of(
                subject,
                null,
                null,
                null,
                tracker,
                null,
                Props.PROJECT_ID_SYSADMIN,
                Props.SYSADMIN_TEAM_ASSIGNEE_ID,
                Props.NEW_STATUS_ID,
                dueDate,
                CustomFields.CUSTOMER_NOT_APPLICABLE));
        Utils.logNewIssueCreated(subject, deploymentParentIssue);
        return deploymentParentIssue;
    }

    private void createReRunJobsTask(Tracker tracker, Integer parentTaskId) throws RedmineException {
        String subject = "Rerun jobs after schedule window";
        Issue reRunJobsIssue = manager.getIssueManager().createIssue(
            IssueCustomFactory.of(
                subject,
                null,
                null,
                null,
                tracker,
                parentTaskId,
                Props.PROJECT_ID_SYSADMIN,
                Props.SIMON_CRUISE_ASSIGNEE_ID,
                Props.NEW_STATUS_ID,
                dueDate,
                CustomFields.CUSTOMER_PIVOTAL,
                CustomFields.REVIEWER_JOHN_BURKE,
                CustomFields.PLATFORM_NUVEI,
                CustomFields.ENVIRONMENT_ONPREM_BT));
        Utils.logNewIssueCreated(subject, reRunJobsIssue);
    }

    private void createDeployToTest(Tracker tracker, Integer parentTaskId) throws RedmineException {
        Date testDueDate = Utils.getTestDueDate(dueDate);
        String subject = "Deploy %s to %s test hosts";
        //Worldnet platform
        String wnSubject = String.format(subject,shorterNewVersionName,CustomFields.PLATFORM_WORLDNET.getValue());
        Issue wnIssue = IssueCustomFactory.of(
                wnSubject,
                null,
                null,
                null,
                tracker,
                parentTaskId,
                Props.PROJECT_ID_SYSADMIN,
                Props.JOHN_BOURKE_ASSIGNEE_ID,
                Props.NEW_STATUS_ID,
                testDueDate,
                CustomFields.CUSTOMER_NOT_APPLICABLE,
                CustomFields.REVIEWER_KEVIN_ELVIRA,
                CustomFields.PLATFORM_WORLDNET,
                CustomFields.ENVIRONMENT_GCP);
        wnIssue.setEstimatedHours(1F);
        wnIssue = manager.getIssueManager().createIssue(wnIssue);
        Utils.logNewIssueCreated(wnSubject, wnIssue);
        //Nuvei
        String nuveiSubject = String.format(subject,shorterNewVersionName,CustomFields.PLATFORM_NUVEI.getValue());
        Issue nuveiIssue = IssueCustomFactory.of(
            nuveiSubject,
            null,
            null,
            null,
            tracker,
            parentTaskId,
            Props.PROJECT_ID_SYSADMIN,
            Props.JOHN_BOURKE_ASSIGNEE_ID,
            Props.NEW_STATUS_ID,
            testDueDate,
            CustomFields.CUSTOMER_PIVOTAL,
            CustomFields.REVIEWER_KEVIN_ELVIRA,
            CustomFields.PLATFORM_NUVEI,
            CustomFields.ENVIRONMENT_GCP);
        nuveiIssue.setEstimatedHours(1F);
        nuveiIssue = manager.getIssueManager().createIssue(nuveiIssue);
        Utils.logNewIssueCreated(nuveiSubject, nuveiIssue);
    }

    private void createDeployToProduction(Tracker tracker, Integer parentTaskId) throws RedmineException {
        String subject = "Deploy %s to %s hosts";
        //Worldnet platform
        String wnSubject = String.format(subject,shorterNewVersionName,CustomFields.PLATFORM_WORLDNET.getValue());
        Issue wnIssue = IssueCustomFactory.of(
            wnSubject,
            null,
            null,
            null,
            tracker,
            parentTaskId,
            Props.PROJECT_ID_SYSADMIN,
            Props.JOHN_BOURKE_ASSIGNEE_ID,
            Props.NEW_STATUS_ID,
            dueDate,
            CustomFields.CUSTOMER_NOT_APPLICABLE,
            CustomFields.REVIEWER_KEVIN_ELVIRA,
            CustomFields.PLATFORM_WORLDNET,
            CustomFields.ENVIRONMENT_ONPREM_BOTH);
        wnIssue.setEstimatedHours(2F);
        wnIssue = manager.getIssueManager().createIssue(wnIssue);
        Utils.logNewIssueCreated(wnSubject, wnIssue);
        //Nuvei
        String nuveiSubject = String.format(subject,shorterNewVersionName,CustomFields.PLATFORM_NUVEI.getValue());
        Issue nuveiIssue = IssueCustomFactory.of(
            nuveiSubject,
            null,
            null,
            null,
            tracker,
            parentTaskId,
            Props.PROJECT_ID_SYSADMIN,
            Props.JOHN_BOURKE_ASSIGNEE_ID,
            Props.NEW_STATUS_ID,
            dueDate,
            CustomFields.CUSTOMER_PIVOTAL,
            CustomFields.REVIEWER_SIMON_CRUISE,
            CustomFields.PLATFORM_NUVEI,
            CustomFields.ENVIRONMENT_ONPREM_BOTH);
        nuveiIssue.setEstimatedHours(1F);
        nuveiIssue = manager.getIssueManager().createIssue(nuveiIssue);
        Utils.logNewIssueCreated(nuveiSubject, nuveiIssue);
    }
}
