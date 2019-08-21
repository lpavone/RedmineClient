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

package com.worldnet.redmine;

import java.text.SimpleDateFormat;

/**
 * @author Leonardo Pavone - 26/09/17.
 */
public class Props {

    /**
     * Project id in Redmine
     */
    public static final int PROJECT_ID_NET_TRAXION = 2;
    public static final int PROJECT_ID_SYSADMIN = 80;

    public static final String REDMINE_URL = "https://xxxxxxx/redmine";

    /**
     * Development process categoryId
     */
    public static final int DEV_PROCESS_CATEGORY_ID = 39;
    /**
     * Documentation categoryId
     */
    public static final int DOCUMENTATION_CATEGORY_ID = 170;

    public static final int DEV_TEAM_ASSIGNEE_ID = 24;
    public static final int SYSADMIN_TEAM_ASSIGNEE_ID = 79;
    public static final int SIMON_CRUISE_ASSIGNEE_ID = 1;
    public static final int JOHN_BOURKE_ASSIGNEE_ID = 116;
    public static final int KEVIN_ELVIRA_ASSIGNEE_ID = 122;

    public static final int DEV_PROCESS_TRACKER = 18;
    public static final int SYSADMIN_PARENT_TASK_TRACKER = 29;
    public static final int SYSADMIN_MAINTENANCE_TRACKER = 24;

    public static final Integer READY_FOR_DEV_STATUS_ID = 46;
    public static final Integer NEW_STATUS_ID = 1;

    public static final String CUSTOMER_PIVOTAL = "Pivotal";
    public static final String CUSTOMER_NOT_APPLICABLE = "n/a";

    public static final String DUE_DATE_FORMAT = "dd/MM/yyyy";
    public static final SimpleDateFormat dueDateFormatter = new SimpleDateFormat(DUE_DATE_FORMAT);

    public static final String PROJECT_KEY_NEXTRAXION = "Net Traxion";

}
