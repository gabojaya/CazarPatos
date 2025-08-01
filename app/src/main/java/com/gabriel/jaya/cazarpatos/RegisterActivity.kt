package com.gabriel.jaya.cazarpatos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {


    private lateinit var editTextEmailRegister: EditText
    private lateinit var editTextPasswordRegister: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var buttonSignUp: Button
    private lateinit var buttonBackToLogin: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextEmailRegister = findViewById(R.id.editTextEmailRegister)
        editTextPasswordRegister = findViewById(R.id.editTextPasswordRegister)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin)

        auth = Firebase.auth

        buttonSignUp.setOnClickListener {
            if (validarCampos()) {
                val email = editTextEmailRegister.text.toString().trim()
                val password = editTextPasswordRegister.text.toString().trim()
                registrarUsuario(email, password)
            }
        }

        buttonBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun registrarUsuario(email: String, clave: String) {
        auth.createUserWithEmailAndPassword(email, clave)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("FIREBASE_AUTH", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Registro exitoso.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w("FIREBASE_AUTH", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Falló el registro: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun validarCampos(): Boolean {
        val email = editTextEmailRegister.text.toString().trim()
        val password = editTextPasswordRegister.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()

        if (email.isEmpty()) {
            editTextEmailRegister.error = "El correo electrónico es obligatorio."
            editTextEmailRegister.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmailRegister.error = "Ingrese un correo electrónico válido."
            editTextEmailRegister.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            editTextPasswordRegister.error = "La contraseña es obligatoria."
            editTextPasswordRegister.requestFocus()
            return false
        }

        if (password.length < 8) {
            editTextPasswordRegister.error = "La contraseña debe tener al menos 8 caracteres."
            editTextPasswordRegister.requestFocus()
            return false
        }

        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.error = "Confirme su contraseña."
            editTextConfirmPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            editTextConfirmPassword.error = "Las contraseñas no coinciden."
            editTextConfirmPassword.requestFocus()
            return false
        }

        editTextEmailRegister.error = null
        editTextPasswordRegister.error = null
        editTextConfirmPassword.error = null

        return true
    }
}