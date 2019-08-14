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

/**
 * @author Leonardo Pavone - 14 Aug 2019.
 */
public class CustomerFactory {

    CustomField customerNotApplicable = CustomFieldFactory
        .create(4, "Customer", "n/a");

    CustomField customerPivotal = CustomFieldFactory
        .create(4, "Customer", "n/a");

    CustomField customerWorldnet = CustomFieldFactory
        .create(4, "Customer", "n/a");

}
