package com.studylock.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class BlockActivity : Activity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val mins = ((Prefs.end(this) - System.currentTimeMillis()) / 60000).coerceAtLeast(0) + 1
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }
        root.addView(TextView(this).apply {
            text = "Back to study!"; textSize = 32f; gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = "That app is blocked for about $mins more min."; textSize = 18f; gravity = Gravity.CENTER
        })
        root.addView(Button(this).apply {
            text = "OK"
            setOnClickListener {
                startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }
        })
        setContentView(root)
    }
}
