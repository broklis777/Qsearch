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
    private val app_names = mutableListOf<String>()

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
                val new_list_after_filter = findFragmentsInAppNames(app_names, s.toString())
                if (new_list_after_filter.size != 1) {
                    // Show all apps
                    Log.d("TAG", "All apps: $new_list_after_filter")
                } else if (new_list_after_filter.isNotEmpty()) {
                    // Open the only app
                    Log.d("TAG", "Open the only app: ${new_list_after_filter[0]}")
                }
            }
        })

        // Ensure the keyboard is shown when the activity is created
        textInput.post {
            textInput.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(textInput, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun findFragmentsInAppNames(list: List<String>, fragment: String): List<String> {
        return list.filter { it.contains(fragment, ignoreCase = true) }
    }

    private fun listUserInstalledApps() {
        val pm: PackageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val apps = pm.queryIntentActivities(intent, 0)
            .mapNotNull { resolveInfo -> resolveInfo.activityInfo.applicationInfo }
            .distinctBy { it.packageName }

        for (app in apps) {
            val app_name = app.loadLabel(pm).toString()
            app_names.add(app_name)
        }

        // Log the list of app names to verify they are being correctly populated
        Log.d("TAG", "Installed apps: $app_names")
    }
}
