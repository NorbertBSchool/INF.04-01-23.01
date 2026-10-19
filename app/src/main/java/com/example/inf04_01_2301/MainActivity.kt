package com.example.inf04_01_2301

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.random.Random

val ASCII_LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
val ASCII_UPPERCASE = ASCII_LOWERCASE.uppercase()
val SPECIAL_CHARS = "!@#\$%^&*()_+-="
val DIGITS = "0123456789"

class MainActivity : AppCompatActivity() {

    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, ime.bottom)
            )
            insets
        }

        val generateButton = findViewById<Button>(R.id.generateButton)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val lettersCountField = findViewById<EditText>(R.id.lettersCountField)
        val lettersCheckbox = findViewById<CheckBox>(R.id.lettersCheckbox)
        val digitsCheckbox = findViewById<CheckBox>(R.id.digitsCheckbox)
        val specialCharsCheckbox = findViewById<CheckBox>(R.id.specialCharsCheckbox)

        val stationSpinner = findViewById<Spinner>(R.id.stationSpinner)
        val fnField = findViewById<EditText>(R.id.fnField)
        val lnField = findViewById<EditText>(R.id.lnField)

        ArrayAdapter.createFromResource(
            this,
            R.array.stationSpinner,
            R.layout.item_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
            stationSpinner.adapter = adapter
        }

        generateButton.setOnClickListener {
            val length = lettersCountField.text.toString().trim().toIntOrNull() ?: 0
            if (length < 1) {
                showDialog(
                    getString(R.string.error_length_title),
                    getString(R.string.error_length_message)
                )
                return@setOnClickListener
            }

            password = generatePassword(
                length,
                lettersCheckbox.isChecked,
                digitsCheckbox.isChecked,
                specialCharsCheckbox.isChecked
            )
            showDialog(null, password)
        }

        submitButton.setOnClickListener {
            val firstName = fnField.text.toString().trim()
            val lastName = lnField.text.toString().trim()
            val station = stationSpinner.selectedItem?.toString().orEmpty()
            showDialog(
                null,
                getString(R.string.employee_summary, firstName, lastName, station, password)
            )
        }

    }

    private fun generatePassword(
        length: Int,
        withLetters: Boolean,
        withDigits: Boolean,
        withSpecialChars: Boolean
    ): String {
        val generated = StringBuilder()
        repeat(length) {
            generated.append(randomChar(ASCII_LOWERCASE))
        }

        val freePositions = MutableList(length) { it }
        freePositions.shuffle()

        if (withLetters && freePositions.isNotEmpty()) {
            val position = freePositions.removeAt(freePositions.lastIndex)
            generated.setCharAt(position, randomChar(ASCII_UPPERCASE))
        }

        if (withDigits && freePositions.isNotEmpty()) {
            val position = freePositions.removeAt(freePositions.lastIndex)
            generated.setCharAt(position, randomChar(DIGITS))
        }

        if (withSpecialChars && freePositions.isNotEmpty()) {
            val position = freePositions.removeAt(freePositions.lastIndex)
            generated.setCharAt(position, randomChar(SPECIAL_CHARS))
        }

        return generated.toString()
    }

    private fun randomChar(characters: String): Char {
        return characters[Random.nextInt(characters.length)]
    }

    private fun showDialog(title: String?, message: String) {
        val dialogBuilder = MaterialAlertDialogBuilder(
            this,
            R.style.ThemeOverlay_INF04012301_Dialog
        )
        if (title != null) {
            dialogBuilder.setTitle(title)
        }
        dialogBuilder
            .setMessage(message)
            .setPositiveButton(R.string.dialog_ok, null)
            .show()
    }
}
