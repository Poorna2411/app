package com.studylock.app

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class BlockerService : AccessibilityService() {
    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        if (e?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = e.packageName?.toString() ?: return
        if (pkg == packageName || !Prefs.active(this)) return
        val blocked = Prefs.blocked(this).toMutableSet()
        if (Prefs.strict(this)) blocked.add("com.android.settings")
        if (pkg in blocked) {
            performGlobalAction(GLOBAL_ACTION_HOME)
            startActivity(Intent(this, BlockActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
    override fun onInterrupt() {}
}
