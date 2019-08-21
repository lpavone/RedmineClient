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
package com.worldnet.redmine.factory;

import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.CustomFieldFactory;

import java.util.Arrays;

/**
 * @author Leonardo Pavone - 14 Aug 2019.
 */
public class CustomFields {

    public static final CustomField CUSTOMER_NOT_APPLICABLE = CustomFieldFactory
        .create(4, "Customer", "n/a");
    public static final CustomField CUSTOMER_PIVOTAL = CustomFieldFactory
        .create(4, "Customer", "Pivotal");

    public static final CustomField REVIEWER_JOHN_BURKE = CustomFieldFactory
        .create(12, "Reviewer", "116");
    public static final CustomField REVIEWER_SIMON_CRUISE = CustomFieldFactory
        .create(12, "Reviewer", "1");
    public static final CustomField REVIEWER_KEVIN_ELVIRA = CustomFieldFactory
        .create(12, "Reviewer", "122");

    public static final CustomField PLATFORM_NUVEI = CustomFieldFactory
        .create(22, "Platform", "Nuvei");
    public static final CustomField PLATFORM_WORLDNET = CustomFieldFactory
        .create(22, "Platform", "Worldnet");

    public static final CustomField ENVIRONMENT_PRE_PRODUCTION = CustomFieldFactory
        .create(23, "Environment", "Pre-Production");
    public static final CustomField ENVIRONMENT_ONPREM_BT = CustomFieldFactory
        .create(23, "Environment", "OnPrem BT");
    public static final CustomField ENVIRONMENT_ONPREM_CARLOW = CustomFieldFactory
        .create(23, "Environment", "OnPrem Carlow");
    public static final CustomField ENVIRONMENT_ONPREM_BOTH = CustomFieldFactory.create(23);
    public static final CustomField ENVIRONMENT_GCP = CustomFieldFactory
        .create(23, "Environment", "GCP");
    public static final CustomField ENVIRONMENT_AWS = CustomFieldFactory
        .create(23, "Environment", "AWS");

    static {
        ENVIRONMENT_ONPREM_BOTH.setName("Environment");
        ENVIRONMENT_ONPREM_BOTH.setValues(Arrays.asList("OnPrem BT","OnPrem Carlow"));
    }
}
