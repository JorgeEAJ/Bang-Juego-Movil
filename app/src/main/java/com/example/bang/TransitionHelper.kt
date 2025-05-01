package com.example.bang

import android.app.Activity
import android.os.Build

object TransitionHelper {

    fun applyTransition(activity: Activity, enterAnim: Int, exitAnim: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+ (API 34)
            activity.overrideActivityTransition(
                /* transit */ Activity.OVERRIDE_TRANSITION_OPEN,
                /* enterAnim */ enterAnim,
                /* exitAnim */ exitAnim
            )
        } else {
            @Suppress("DEPRECATION")
            activity.overridePendingTransition(enterAnim, exitAnim)
        }
    }

    fun applyExitTransition(activity: Activity, enterAnim: Int, exitAnim: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activity.overrideActivityTransition(
                /* transit */ Activity.OVERRIDE_TRANSITION_OPEN,
                /* enterAnim */ enterAnim,
                /* exitAnim */ exitAnim
            )
        } else {
            @Suppress("DEPRECATION")
            activity.overridePendingTransition(enterAnim, exitAnim)
        }
    }
}
