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
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueCategory;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.Version;
import com.taskadapter.redmineapi.bean.VersionFactory;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import com.worldnet.redmine.Props;
import com.worldnet.redmine.Utils;
import com.worldnet.redmine.factory.CustomFields;
import com.worldnet.redmine.factory.IssueCustomFactory;

import java.util.Date;
import java.util.Optional;

/**
 * @author Leonardo Pavone - 14 Aug 2019.
 */
public class NetTraxion implements TaskCreator {

    private RedmineManager manager;
    private String newVersionReleaseName;
    private String newBranchName;
    private String previousBranchName;
    private String previousVersionReleaseName;
    private String shorterNewVersionName;
    private boolean isPatch;
    private Date dueDate;

    public NetTraxion(RedmineManager manager, String newVersionReleaseName,
        String newBranchName, String previousBranchName, String previousVersionReleaseName,
        String shorterNewVersionName, boolean isPatch, Date dueDate) {
        this.manager = manager;
        this.newVersionReleaseName = newVersionReleaseName;
        this.newBranchName = newBranchName;
        this.previousBranchName = previousBranchName;
        this.previousVersionReleaseName = previousVersionReleaseName;
        this.shorterNewVersionName = shorterNewVersionName;
        this.isPatch = isPatch;
        this.dueDate = dueDate;
    }

    @Override
    public void create() throws RedmineException {
        //create new version
        Version version = VersionFactory
            .create(Props.PROJECT_ID_NET_TRAXION, newVersionReleaseName);
        version.setSharing("system");// shares the new version with all the projects
        version = manager.getProjectManager().createVersion(version);
        System.out
            .println("New roadmap version \"" + newVersionReleaseName + "\" has been created");
        Optional<IssueCategory> category = manager.getIssueManager()
            .getCategories(Props.PROJECT_ID_NET_TRAXION)
            .stream().filter(c -> c.getId().equals(Props.DEV_PROCESS_CATEGORY_ID)).findAny();

        Optional<Tracker> devProcessTracker = manager.getIssueManager()
            .getTrackers()
            .stream().filter(t -> t.getId().equals(Props.DEV_PROCESS_TRACKER)).findAny();


        Issue parentIssue = createDevelopmentProcessParentTask(newBranchName, manager, version,
            category, devProcessTracker, CustomFields.CUSTOMER_NOT_APPLICABLE);

        createTaskForBranchCreation(previousBranchName, newBranchName, manager, version,
            category, devProcessTracker, CustomFields.CUSTOMER_NOT_APPLICABLE, parentIssue);

        createDistributionFileTask(newBranchName, manager, version, category, devProcessTracker,
            CustomFields.CUSTOMER_NOT_APPLICABLE, parentIssue);

        if (!isPatch) {
            createMergeTask(previousBranchName, newBranchName, manager, version, category,
                devProcessTracker, CustomFields.CUSTOMER_NOT_APPLICABLE, parentIssue);

            createWikiUpgradingSection(previousVersionReleaseName, shorterNewVersionName,
                Props.PROJECT_KEY_NEXTRAXION, manager);

            createDocumentationParentTask(newBranchName, manager, version, devProcessTracker,
                CustomFields.CUSTOMER_NOT_APPLICABLE, parentIssue);
        }

    }

    private void createDocumentationParentTask(String newBranchName, RedmineManager manager,
        Version version, Optional<Tracker> tracker, CustomField customerCustomField,
        Issue parentIssue) throws RedmineException {

        String subject = "Documentation Tasks " + newBranchName;
        String description = "Parent task for all documentation updates required for this version";
        Optional<IssueCategory> category = manager.getIssueManager()
            .getCategories(Props.PROJECT_ID_NET_TRAXION)
            .stream().filter(c -> c.getId().equals(Props.DOCUMENTATION_CATEGORY_ID)).findAny();
        Issue documentationIssue = manager.getIssueManager().createIssue(
            IssueCustomFactory
                .of(subject,
                    version,
                    description,
                    category.get(),
                    tracker.get(),
                    parentIssue.getId(),
                    Props.PROJECT_ID_NET_TRAXION,
                    Props.DEV_TEAM_ASSIGNEE_ID,
                    Props.READY_FOR_DEV_STATUS_ID,
                    dueDate,
                    customerCustomField)
        );
        Utils.logNewIssueCreated(subject, documentationIssue);
    }

    private void createWikiUpgradingSection(String previousVersionReleaseName,
        String shorterNewVersionName, String projectKey, RedmineManager manager)
        throws RedmineException {

        String pageTitle = String.format("Updating to %s from %s", shorterNewVersionName,
            previousVersionReleaseName);
        WikiPageDetail wikiPageDetail = manager.getWikiManager()
            .getWikiPageDetailByProjectAndTitle(projectKey, "Upgrading");
        StringBuilder upgradingSection = new StringBuilder(wikiPageDetail.getText());
        upgradingSection.append("\n\n" + "[[" + pageTitle + "]]");
        wikiPageDetail.setText(upgradingSection.toString());
        manager.getWikiManager().update(projectKey, wikiPageDetail);
        System.out.println(
            String.format("Wiki Upgrading page updated to add page: %s", pageTitle));
    }

    private void createDistributionFileTask(String newBranchName, RedmineManager manager,
        Version version, Optional<IssueCategory> category, Optional<Tracker> tracker,
        CustomField customerCustomField, Issue parentIssue) throws RedmineException {

        String subject = "Create release file for " + newBranchName;
        String description =
            "Version distribution file has to be created and verified as described in:\n"
                + Props.REDMINE_URL
                + "/projects/net%20traxion/wiki/Development_Processes#Verifying-a-release";
        Issue distIssue = manager.getIssueManager().createIssue(
            IssueCustomFactory.of(
                subject,
                version,
                description,
                category.get(),
                tracker.get(),
                parentIssue.getId(),
                Props.PROJECT_ID_NET_TRAXION,
                Props.DEV_TEAM_ASSIGNEE_ID,
                Props.READY_FOR_DEV_STATUS_ID,
                dueDate,
                customerCustomField)
        );
        Utils.logNewIssueCreated(subject, distIssue);
    }

    private void createMergeTask(String previousBranchName, String newBranchName,
        RedmineManager manager, Version version, Optional<IssueCategory> category,
        Optional<Tracker> tracker, CustomField customerCustomField, Issue parentIssue)
        throws RedmineException {

        String subject =
            "Merges changes from " + previousBranchName + " into branch " + newBranchName;
        String description = "Merge changes from " + previousBranchName
            + " branch and any patch of this version." +
            " Auto-merger is set up to do following daily merges:\n"
            + "\n"
            + "| *Origin branch* | *Target branch* | *Status* |\n"
            + "| VERSION_? | VERSION_? | ? |";
        Issue mergeIssue = manager.getIssueManager().createIssue(
            IssueCustomFactory
                .of(
                    subject,
                    version,
                    description,
                    category.get(),
                    tracker.get(),
                    parentIssue.getId(),
                    Props.PROJECT_ID_NET_TRAXION,
                    Props.DEV_TEAM_ASSIGNEE_ID,
                    Props.READY_FOR_DEV_STATUS_ID,
                    dueDate,
                    customerCustomField)
        );
        Utils.logNewIssueCreated(subject, mergeIssue);
    }

    private void createTaskForBranchCreation(String previousBranchName, String newBranchName,
        RedmineManager manager, Version version, Optional<IssueCategory> category,
        Optional<Tracker> tracker, CustomField customerCustomField, Issue parentIssue)
        throws RedmineException {

        String subject = "Create branch " + newBranchName + " off head of " + previousBranchName;
        String description = "Create " + newBranchName + " branch in code repository.";
        Issue createBranch = manager.getIssueManager().createIssue(
            IssueCustomFactory.of(
                subject,
                version,
                description,
                category.get(),
                tracker.get(),
                parentIssue.getId(),
                Props.PROJECT_ID_NET_TRAXION,
                Props.DEV_TEAM_ASSIGNEE_ID,
                Props.READY_FOR_DEV_STATUS_ID,
                dueDate,
                customerCustomField)
        );
        Utils.logNewIssueCreated(subject, createBranch);
    }

    private Issue createDevelopmentProcessParentTask(String newBranchName,
        RedmineManager manager, Version version, Optional<IssueCategory> category,
        Optional<Tracker> tracker, CustomField customerCustomField) throws RedmineException {

        String subject = "Development Tasks " + newBranchName;
        String description = "Parent task for all development related operations / tasks in this version.";
        Issue parentIssue = manager.getIssueManager().createIssue(
            IssueCustomFactory
                .of(
                    subject,
                    version,
                    description,
                    category.get(),
                    tracker.get(),
                    null,
                    Props.PROJECT_ID_NET_TRAXION,
                    Props.DEV_TEAM_ASSIGNEE_ID,
                    Props.READY_FOR_DEV_STATUS_ID,
                    dueDate,
                    customerCustomField)
        );
        Utils.logNewIssueCreated(subject, parentIssue);
        return parentIssue;
    }

}
