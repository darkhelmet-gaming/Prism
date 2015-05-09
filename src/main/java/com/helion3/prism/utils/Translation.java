package com.helion3.prism.utils;

import java.util.HashMap;
import java.util.Map;

final public class Translation {

    // @todo this is mega temporary, should be moved to a language file
    private static Map<String,String> lang = new HashMap<String,String>();

    private Translation() {}

    /**
     *
     * @param key
     * @return
     */
    public static String from(String key) {
        if (lang.isEmpty()) {
            lang.put("rollback.success", "{appliedCount} reversal(s).");
            lang.put("rollback.success.withskipped", "{appliedCount} reversal(s). {skippedCount} skipped.");
            lang.put("rollback.success.bonus", "It's like it never happened.");
        }
        return lang.get(key);
    }
}
