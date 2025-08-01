package com.gabriel.jaya.cazarpatos

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword:EditText
    lateinit var buttonLogin: Button
    lateinit var buttonNewUser:Button
    lateinit var mediaPlayer: MediaPlayer

    lateinit var manejadorArchivo: FileHandler
    lateinit var checkBoxRecordarme: CheckBox
    private lateinit var auth: FirebaseAuth


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

            //Inicialización de variables
            manejadorArchivo = SharedPreferencesManager(this)
            editTextEmail = findViewById(R.id.editTextEmail)
            editTextPassword = findViewById(R.id.editTextPassword)
            buttonLogin = findViewById(R.id.buttonLogin)
            buttonNewUser = findViewById(R.id.buttonNewUser)
            checkBoxRecordarme = findViewById(R.id.checkBoxRecordarme)
            // Initialize Firebase Auth
            auth = Firebase.auth

            /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }*/
            leerDatosDePreferencias()
        //Eventos clic

            buttonLogin.setOnClickListener {
                val email = editTextEmail.text.toString()
                val clave = editTextPassword.text.toString()
                //Validaciones de datos requeridos y formatos
                if(!validarDatosRequeridos())
                    return@setOnClickListener
                //Guardar datos en preferencias.
                guardarDatosEnPreferencias()
                //Si pasa validación de datos requeridos, ir a pantalla principal
                //val intencion = Intent(this, MainActivity::class.java)
                //intencion.putExtra(EXTRA_LOGIN, email)
                //startActivity(intencion)
                AutenticarUsuario(email, clave)
            }

            buttonNewUser.setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            mediaPlayer=MediaPlayer.create(this, R.raw.title_screen)
            mediaPlayer.start()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(EXTRA_LOGIN, currentUser.email)
            startActivity(intent)
        }
    }

    fun AutenticarUsuario(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(EXTRA_LOGIN, "signInWithEmail:success")
                    //Si pasa validación de datos requeridos, ir a pantalla principal
                    val intencion = Intent(this, MainActivity::class.java)
                    intencion.putExtra(EXTRA_LOGIN, auth.currentUser!!.email)
                    startActivity(intencion)
                    //finish()
                } else {
                    Log.w(EXTRA_LOGIN, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }



        private fun validarDatosRequeridos():Boolean{
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.error_email_required))
            editTextEmail.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.error_password_required))
            editTextPassword.requestFocus()
            return false
        }
        if (password.length < 8) {
            editTextPassword.setError(getString(R.string.error_password_min_length))
            editTextPassword.requestFocus()
            return false
        }
        return true
    }
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    private fun guardarDatosEnPreferencias(){
        manejadorArchivo = SharedPreferencesManager(this)
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        val listadoAGrabar:Pair<String,String>
        if(checkBoxRecordarme.isChecked){
            listadoAGrabar = email to clave
        }
        else{
            listadoAGrabar ="" to ""
        }
        // Guardar en SharedPreferences
        manejadorArchivo = SharedPreferencesManager(this)
        manejadorArchivo.SaveInformation(listadoAGrabar)

        // Guardar en EncryptedSharedPreferences (usando los datos del checkbox para el ejemplo)
        //manejadorArchivo = EncriptedSharedPreferencesManager(this)
        //manejadorArchivo.SaveInformation(listadoAGrabar)

        // Guardar en Archivo Interno
        manejadorArchivo = FileInternalManager(this)
        manejadorArchivo.SaveInformation(listadoAGrabar)

        // Guardar en Archivo Externo
        manejadorArchivo = FileExternalManager(this)
        manejadorArchivo.SaveInformation(listadoAGrabar)



    }

    private fun leerDatosDePreferencias(){
        manejadorArchivo = SharedPreferencesManager(this)
        var listadoLeido : Pair<String, String>

        listadoLeido = manejadorArchivo.ReadInformation()
        if(listadoLeido.first != null){
            checkBoxRecordarme.isChecked = true
        }
        editTextEmail.setText ( listadoLeido.first )
        editTextPassword.setText ( listadoLeido.second )

        //EncriptedSharedPreferencesManager
        manejadorArchivo = SharedPreferencesManager(this)

        listadoLeido = manejadorArchivo.ReadInformation()
        if(listadoLeido.first != null){
            checkBoxRecordarme.isChecked = true
        }

        editTextEmail.setText ( listadoLeido.first )
        editTextPassword.setText ( listadoLeido.second )

        // Leer de EncryptedSharedPreferences
        //manejadorArchivo = EncriptedSharedPreferencesManager(this)
        //listadoLeido = manejadorArchivo.ReadInformation()
        //Log.d("TAG", "EncryptedSharedPreferences: ${listadoLeido.toList()}")

        // Leer de Archivo Interno
        manejadorArchivo = FileInternalManager(this)
        listadoLeido = manejadorArchivo.ReadInformation()
        Log.d("TAG", "FileInternalManager: ${listadoLeido.toList()}")

        // Leer de Archivo Externo
        manejadorArchivo = FileExternalManager(this)
        listadoLeido = manejadorArchivo.ReadInformation()
        Log.d("TAG", "FileExternalManager " + listadoLeido.toList().toString())

    }


}