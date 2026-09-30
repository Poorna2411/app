package com.studylock.app

import android.content.Context

object Prefs {
    val defaults = setOf(
        "com.instagram.android", "com.facebook.katana", "com.twitter.android",
        "com.snapchat.android", "com.google.android.youtube", "com.whatsapp",
        "org.telegram.messenger", "com.reddit.frontpage", "com.netflix.mediaclient",
        "com.zhiliaoapp.musically"
    )
    private fun sp(c: Context) = c.getSharedPreferences("study", Context.MODE_PRIVATE)
    fun hasSaved(c: Context) = sp(c).contains("blocked")
    fun blocked(c: Context): Set<String> = sp(c).getStringSet("blocked", defaults)!!
    fun setBlocked(c: Context, s: Set<String>) = sp(c).edit().putStringSet("blocked", HashSet(s)).apply()
    fun end(c: Context) = sp(c).getLong("end", 0L)
    fun setEnd(c: Context, t: Long) = sp(c).edit().putLong("end", t).apply()
    fun strict(c: Context) = sp(c).getBoolean("strict", false)
    fun setStrict(c: Context, v: Boolean) = sp(c).edit().putBoolean("strict", v).apply()
    fun active(c: Context) = System.currentTimeMillis() < end(c)
}
