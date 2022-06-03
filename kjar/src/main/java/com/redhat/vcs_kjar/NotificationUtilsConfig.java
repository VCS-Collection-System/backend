package com.redhat.vcs_kjar;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Do we want to initialize all three maps for every environment? Or should we conditionally create and return
 *       the correct list based on the current environment?
 *
 */
public class NotificationUtilsConfig {
    public static final String PROD_ENV_NAME = "production";
    public static final String UAT_ENV_NAME = "uat";

    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    public static final String ENVIRONMENT = System.getProperty("vcs.environment", UAT_ENV_NAME);
    public static final String VCS = "VCS EXAMPLE GROUP";

    public static final String VCSHR = "vcs@redhat.com";

    private static final Map<String, String> hrMailingListMap = new HashMap<>();
    private static final Map<String, String> hrMailingListUatMap = new HashMap<>();
    private static final Map<String, String> hrMailingListDevMap = new HashMap<>();

    static {
        hrMailingListMap.put(NotificationUtilsConfig.VCS, VCSHR);
    }

    static {
        hrMailingListUatMap.put(NotificationUtilsConfig.VCS, VCSHR);
    }

    static {
        hrMailingListDevMap.put(NotificationUtilsConfig.VCS, VCSHR);
    }

    private NotificationUtilsConfig() {
        // Empty constructor to prevent instantiation
    }

    public static Map<String, String> getHrMailingListMap() {
        return hrMailingListMap;
    }

    public static Map<String, String> getHrMailingListUatMap() {
        return hrMailingListUatMap;
    }

    public static Map<String, String> getHrMailingListDevMap() {
        return hrMailingListDevMap;
    }
}
