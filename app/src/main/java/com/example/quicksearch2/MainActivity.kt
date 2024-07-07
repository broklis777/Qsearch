package com.example.quicksearch2

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var textInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listUserInstalledApps()

        textInput = findViewById(R.id.textInput)

        // Add TextWatcher to the EditText
        textInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Log the text input whenever it changes
                Log.d("TAG", s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed after text changes
            }
        })

        // Ensure the keyboard is shown when the activity is created
        textInput.post {
            textInput.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(textInput, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun listUserInstalledApps() {
        val pm: PackageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val apps: List<ApplicationInfo> = pm.queryIntentActivities(intent, 0)
            .mapNotNull { resolveInfo -> resolveInfo.activityInfo.applicationInfo }
            .distinctBy { it.packageName }

        val app_names = mutableListOf<String>()

        for (app in apps) {
            val app_name = app.loadLabel(pm).toString()
            app_names.add(app_name)
        }
    }
}
