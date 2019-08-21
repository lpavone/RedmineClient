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
import com.taskadapter.redmineapi.bean.CustomFieldDefinition;
import com.taskadapter.redmineapi.bean.CustomFieldFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueCategory;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.Version;
import com.taskadapter.redmineapi.bean.VersionFactory;
import com.worldnet.redmine.Props;
import com.worldnet.redmine.factory.IssueCustomFactory;
import com.worldnet.redmine.task.Sysadmin;
import com.worldnet.redmine.task.TaskCreator;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Leonardo Pavone - 27/09/17.
 */
public class RedmineTest {

    @Test
    public void sysadminTest(){
        RedmineManager manager = RedmineManagerFactory
            .createWithApiKey(Props.REDMINE_URL, "xxxxxxxxxxx");
        TaskCreator sysadminTaskCreator = new Sysadmin(
            manager,
            "6.0.0.0",
            Date.from(
                LocalDate.of(2019,8,28)
                    .atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
        try {
            sysadminTaskCreator.create();
        } catch (RedmineException e) {
            e.printStackTrace();
        }
    }

    private static CustomFieldDefinition getCustomFieldById(
        List<CustomFieldDefinition> customFieldDefinitions,
        int fieldId) {
        for (CustomFieldDefinition customFieldDefinition : customFieldDefinitions) {
            if (customFieldDefinition.getId().equals(fieldId)) {
                return customFieldDefinition;
            }
        }
        throw new RuntimeException(
            "Custom Field definition '" + fieldId + "' is not found on server.");
    }

}
