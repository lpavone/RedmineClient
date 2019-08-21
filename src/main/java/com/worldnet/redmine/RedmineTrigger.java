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
import com.worldnet.redmine.task.NetTraxion;
import com.worldnet.redmine.task.Sysadmin;
import com.worldnet.redmine.task.TaskCreator;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
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
                " * Copyright (c) " + LocalDate.now().getYear()
                + ". All rights reserved.                                         *\n"
                +
                " * Unauthorized copying of this file, via any medium is strictly prohibited.        *\n"
                +
                " * Proprietary and confidential, Written by Worldnet TPS.                           *\n"
                +
                " ************************************************************************************\n");
        System.out.println("Enter the new version name (e.g.: 4.6.0.0 - Squirtle): ");
        Scanner scanner = new Scanner(System.in);
        String newVersionReleaseName = scanner.nextLine().trim();
        System.out.println(String.format("Enter the due date for the new version (%s) or leave blank if it's not known:",
            Props.DUE_DATE_FORMAT));
        Date dueDate = null;
        try {
            String dateStr = scanner.nextLine().trim();
            if (StringUtils.isNotBlank(dateStr)){
                dueDate = Props.dueDateFormatter.parse(dateStr);
            }
        } catch (ParseException e) {
            System.out.println("ERROR: due date format must be " + Props.DUE_DATE_FORMAT);
            System.exit(0);
        }
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

        System.out.println(String.format("***** Confirm your data below: *****\n" +
                "->New branch to be created: %s\n->Previous version branch name: %s"
                + "\n->Is patch? %s\n->Due date: %s\nAre you sure to continue? [Y/N]: ",
            newBranchName, previousBranchName, isPatchStr, dueDate));
        String isOkStr = scanner.nextLine().trim();
        if (isOkStr != null &&
            (isOkStr.equalsIgnoreCase("no") || isOkStr.equalsIgnoreCase("n"))) {
            System.out.println("Execution aborted.");
            System.exit(0);
        }

        RedmineManager manager = RedmineManagerFactory
            .createWithApiKey(Props.REDMINE_URL, apiAccessKey);

        try {
            TaskCreator netTraxionTaskCreator = new NetTraxion(
                manager,
                newVersionReleaseName,
                newBranchName,
                previousBranchName,
                previousVersionReleaseName,
                shorterNewVersionName,
                isPatch,
                dueDate);
            netTraxionTaskCreator.create();
            TaskCreator sysadminTaskCreator = new Sysadmin(
                manager,
                shorterNewVersionName,
                dueDate);
            sysadminTaskCreator.create();

        } catch (RedmineException e) {
            e.printStackTrace();
        }
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
