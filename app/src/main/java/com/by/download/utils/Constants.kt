package com.yf.accelerator.update.utils


/**
+--------------------------------------+
+ @author Catherine Liu
+--------------------------------------+
+ 2021/3/30 18:20
+--------------------------------------+
+ Des:
+--------------------------------------+
 */

class Constants {
    companion object {
        const val UPGRADE_FORCE = 2
    }

    interface IntentKey {
        companion object {
            const val updateInfo = "appUpdate"
        }
    }
}