package com.studylock.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.InputType
import android.widget.*

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var strict: CheckBox
    private val boxes = LinkedHashMap<String, CheckBox>()
    private val handler = Handler(Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() { refresh(); handler.postDelayed(this, 1000) }
    }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val pad = (16 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(pad, pad, pad, pad) }
        status = TextView(this).apply { textSize = 18f }
        val minutes = EditText(this).apply {
            hint = "Minutes (e.g. 60)"; inputType = InputType.TYPE_CLASS_NUMBER; setText("60")
        }
        strict = CheckBox(this).apply {
            text = "Strict mode (also blocks Settings during the session)"
            isChecked = Prefs.strict(this@MainActivity)
        }
        val start = Button(this).apply {
            text = "Start study session"
            setOnClickListener {
                if (Prefs.active(this@MainActivity)) { toast("A session is already running"); return@setOnClickListener }
                val m = minutes.text.toString().toLongOrNull() ?: 0L
                if (m <= 0) { toast("Enter minutes"); return@setOnClickListener }
                Prefs.setBlocked(this@MainActivity, boxes.filter { it.value.isChecked }.keys)
                Prefs.setStrict(this@MainActivity, strict.isChecked)
                Prefs.setEnd(this@MainActivity, System.currentTimeMillis() + m * 60000)
                refresh()
            }
        }
        val acc = Button(this).apply {
            text = "Enable accessibility service (required once)"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        }
        listOf(status, minutes, strict, start, acc,
            TextView(this).apply { text = "\nApps to block:"; textSize = 16f }).forEach { root.addView(it) }

        val pm = packageManager
        val launcher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val saved = Prefs.blocked(this)
        pm.queryIntentActivities(launcher, 0)
            .map { it.activityInfo.packageName to it.loadLabel(pm).toString() }
            .distinctBy { it.first }
            .filter { it.first != packageName }
            .sortedBy { it.second.lowercase() }
            .forEach { (pkg, label) ->
                val cb = CheckBox(this).apply { text = label; isChecked = pkg in saved }
                boxes[pkg] = cb
                root.addView(cb)
            }
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun serviceOn(): Boolean {
        val s = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: ""
        return s.contains(packageName)
    }

    private fun refresh() {
        val active = Prefs.active(this)
        val left = (Prefs.end(this) - System.currentTimeMillis()) / 1000
        val svc = if (serviceOn()) "Accessibility: ON" else "Accessibility: OFF (enable it below)"
        status.text = (if (active) "Studying: %02d:%02d left".format(left / 60, left % 60)
                       else "No active session") + "\n" + svc
        boxes.values.forEach { it.isEnabled = !active }
        strict.isEnabled = !active
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    override fun onResume() { super.onResume(); handler.post(ticker) }
    override fun onPause() { super.onPause(); handler.removeCallbacks(ticker) }
}
