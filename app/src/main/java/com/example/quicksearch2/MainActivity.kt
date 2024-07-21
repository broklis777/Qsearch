package com.example.quicksearch2

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var textInput: EditText
    private val appNames = mutableListOf<String>()
    private val appPackageMap = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listUserInstalledApps()

        textInput = findViewById(R.id.textInput)

        animateEditText()

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
                val newListAfterFilter = findFragmentsInAppNames(appNames, s.toString())
                if (newListAfterFilter.size != 1) {
                    // Show all apps
                    Log.d("TAG", "All apps: $newListAfterFilter")
                } else if (newListAfterFilter.isNotEmpty()) {
                    // Open the only app
                    val appName = newListAfterFilter[0]
                    val packageName = appPackageMap[appName]
                    clearInputField()
                    if (packageName != null) {
                        openApp(packageName)
                    }
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

    override fun onResume() {
        super.onResume()
        clearInputField()
        animateEditText()
    }

    private fun animateEditText() {
        // Ensure the view is fully laid out before starting the animation
        textInput.post {
            textInput.scaleX = 0f
            val scaleXAnimator = ObjectAnimator.ofFloat(textInput, "scaleX", 0f, 1f)
            scaleXAnimator.duration = 500
            scaleXAnimator.interpolator = AccelerateDecelerateInterpolator()
            scaleXAnimator.start()
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
            val appName = app.loadLabel(pm).toString()
            appNames.add(appName)
            appPackageMap[appName] = app.packageName
        }

        // Log the list of app names to verify they are being correctly populated
        Log.d("TAG", "Installed apps: $appNames")
    }

    private fun openApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            Log.d("TAG", "Unable to find launch intent for package: $packageName")
        }
    }

    private fun clearInputField() {
        textInput.setText("")
    }
}
