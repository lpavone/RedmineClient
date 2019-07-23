package com.worldnet.redmine;
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

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.CustomFieldFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueCategory;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.Version;
import com.taskadapter.redmineapi.bean.VersionFactory;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import com.worldnet.redmine.action.IssueCustomFactory;

import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

/**
 * Main class to trigger all the Redmine tasks to be executed automatically.
 *
 * @author Leonardo Pavone - 25/09/17.
 */
public class RedmineTrigger {

    public static void main(String[] args) {
        System.out.println(
            "\n ************************************************************************************\n"
                +
                " * Copyright (c) 2017. All rights reserved.                                         *\n"
                +
                " * Unauthorized copying of this file, via any medium is strictly prohibited.        *\n"
                +
                " * Proprietary and confidential, Written by Worldnet TPS.                           *\n"
                +
                " ************************************************************************************\n");
        System.out.println("Enter the new version name (e.g.: 4.6.0.0 - Squirtle): ");
        Scanner scanner = new Scanner(System.in);
        String newVersionReleaseName = scanner.nextLine().trim();
        System.out.println("Enter the previous version number (e.g.: 4.5.0.0): ");
        String previousVersionReleaseName = scanner.nextLine().trim();
        System.out.println("Is this new version a patch ? [Y/N]: ");
        String isPatchStr = scanner.nextLine().trim();
        System.out.println(
            "Enter your API access key (find it in Redmine -> MyAccount -> API access key): ");
        String apiAccessKey = scanner.nextLine().trim();

        validateInput(newVersionReleaseName, previousVersionReleaseName);

        boolean isPatch = isPatchStr != null &&
            (isPatchStr.equalsIgnoreCase("yes") || isPatchStr.equalsIgnoreCase("y"));

        String previousBranchName =
            "VERSION_" + previousVersionReleaseName.trim().replace(".", "_");
        String shorterNewVersionName = newVersionReleaseName
            .substring(0, newVersionReleaseName.indexOf("-"))
            .trim();
        String newBranchName = "VERSION_" + shorterNewVersionName.replace(".", "_");
        String projectKey = "Net Traxion";

        System.out.println(String.format("***** Confirm your data below: *****\n" +
                "->New branch to be created: %s\n->Previous version branch name: %s\n->Is patch? %s\nAre you sure to continue? [Y/N]: ",
            newBranchName, previousBranchName, isPatchStr));
        String isOkStr = scanner.nextLine().trim();
        if (isOkStr != null &&
            (isOkStr.equalsIgnoreCase("no") || isOkStr.equalsIgnoreCase("n"))) {
            System.out.println("Execution aborted.");
            System.exit(0);
        }

        RedmineManager manager = RedmineManagerFactory
            .createWithApiKey(Props.REDMINE_URL, apiAccessKey);
        //create new version
        Version version = VersionFactory
            .create(Props.NET_TRAXION_PROJECT_ID, newVersionReleaseName);

        try {
            version.setSharing("system");// shares the new version with all the projects
            version = manager.getProjectManager().createVersion(version);
            System.out
                .println("New roadmap version \"" + newVersionReleaseName + "\" has been created");
            Optional<IssueCategory> category = manager.getIssueManager()
                .getCategories(Props.NET_TRAXION_PROJECT_ID)
                .stream().filter(c -> c.getId().equals(Props.DEV_PROCESS_CATEGORY_ID)).findAny();

            Optional<Tracker> tracker = manager.getIssueManager()
                .getTrackers()
                .stream().filter(t -> t.getId().equals(Props.DEV_PROCESS_TRACKER)).findAny();

            CustomField customerCustomField = CustomFieldFactory
                .create(4, "Customer", "n/a");

            Issue parentIssue = createDevelopmentProcessParentTask(newBranchName, manager, version,
                category, tracker, customerCustomField);

            createTaskForBranchCreation(previousBranchName, newBranchName, manager, version,
                category, tracker, customerCustomField, parentIssue);

            createDistributionFileTask(newBranchName, manager, version, category, tracker,
                customerCustomField, parentIssue);

            if (!isPatch) {
                createMergeTask(previousBranchName, newBranchName, manager, version, category,
                    tracker, customerCustomField, parentIssue);

                createWikiUpgradingSection(previousVersionReleaseName, shorterNewVersionName,
                    projectKey, manager);

                createDocumentationParentTask(newBranchName, manager, version, tracker,
                    customerCustomField, parentIssue);
            }

            createCustomQueriesForNewVersion(manager, projectKey);

        } catch (RedmineException e) {
            e.printStackTrace();
        }
    }

    private static void createDocumentationParentTask(String newBranchName, RedmineManager manager,
        Version version, Optional<Tracker> tracker, CustomField customerCustomField,
        Issue parentIssue) throws RedmineException {

        String subject = "Documentation Tasks " + newBranchName;
        String description = "Parent task for all documentation updates required for this version";
        Optional<IssueCategory> category = manager.getIssueManager()
            .getCategories(Props.NET_TRAXION_PROJECT_ID)
            .stream().filter(c -> c.getId().equals(Props.DOCUMENTATION_CATEGORY_ID)).findAny();
        Issue documentationIssue = manager.getIssueManager().createIssue(
            IssueCustomFactory
                .of(subject, version, description, category.get(), tracker.get(),
                    parentIssue.getId(),
                    customerCustomField)
        );
        logNewIssueCreated(subject, documentationIssue);
    }

    private static void logNewIssueCreated(String subject, Issue issue) {
        System.out.println(
            String.format("New issue %s created with subject: %s", issue.getId(), subject));
    }

    private static void createWikiUpgradingSection(String previousVersionReleaseName,
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

    private static void createDistributionFileTask(String newBranchName, RedmineManager manager,
        Version version, Optional<IssueCategory> category, Optional<Tracker> tracker,
        CustomField customerCustomField, Issue parentIssue) throws RedmineException {

        String subject = "Create release file for " + newBranchName;
        String description =
            "Version distribution file has to be created and verified as described in:\n"
                + Props.REDMINE_URL
                + "/projects/net%20traxion/wiki/Development_Processes#Verifying-a-release";
        Issue distIssue = manager.getIssueManager().createIssue(
            IssueCustomFactory.of(subject, version, description, category.get(), tracker.get(),
                parentIssue.getId(),
                customerCustomField)
        );
        logNewIssueCreated(subject, distIssue);
    }

    private static void createMergeTask(String previousBranchName, String newBranchName,
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
                .of(subject, version, description, category.get(), tracker.get(),
                    parentIssue.getId(),
                    customerCustomField)
        );
        logNewIssueCreated(subject, mergeIssue);
    }

    private static void createTaskForBranchCreation(String previousBranchName, String newBranchName,
        RedmineManager manager, Version version, Optional<IssueCategory> category,
        Optional<Tracker> tracker, CustomField customerCustomField, Issue parentIssue)
        throws RedmineException {

        String subject = "Create branch " + newBranchName + " off head of " + previousBranchName;
        String description = "Create " + newBranchName + " branch in code repository.";
        Issue createBranch = manager.getIssueManager().createIssue(
            IssueCustomFactory.of(subject, version, description, category.get(), tracker.get(),
                parentIssue.getId(),
                customerCustomField)
        );
        logNewIssueCreated(subject, createBranch);
    }

    private static Issue createDevelopmentProcessParentTask(String newBranchName,
        RedmineManager manager, Version version, Optional<IssueCategory> category,
        Optional<Tracker> tracker, CustomField customerCustomField) throws RedmineException {

        String subject = "Development Tasks " + newBranchName;
        String description = "Parent task for all development related operations / tasks in this version.";
        Issue parentIssue = manager.getIssueManager().createIssue(
            IssueCustomFactory
                .of(subject, version, description, category.get(), tracker.get(), null,
                    customerCustomField)
        );
        logNewIssueCreated(subject, parentIssue);
        return parentIssue;
    }

    private static void createCustomQueriesForNewVersion(RedmineManager manager,
        String projectKey) {
        //TODO to implement
    }

    private static void validateInput(String newVersionReleaseName,
        String previousVersionReleaseName) {

        if (StringUtils.isEmpty(newVersionReleaseName) ||
            !StringUtils.contains(newVersionReleaseName, ".") ||
            !StringUtils.contains(newVersionReleaseName, "-")) {
            System.out.println("ERROR: New version name format must be like: 4.6.0.0 - Squirtle");
            System.exit(0);
        }
        if (StringUtils.isEmpty(previousVersionReleaseName) ||
            !StringUtils.contains(previousVersionReleaseName, ".") ||
            previousVersionReleaseName.chars().anyMatch(Character::isLetter) ||
            previousVersionReleaseName.chars().anyMatch(Character::isSpaceChar)) {
            System.out.println("Previous release version name format must be like: 4.6.0.0");
            System.exit(0);
        }
    }

}
