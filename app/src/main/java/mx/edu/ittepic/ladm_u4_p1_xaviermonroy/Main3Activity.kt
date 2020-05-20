package mx.edu.ittepic.ladm_u4_p1_xaviermonroy

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main3.*

class Main3Activity : AppCompatActivity() {
    val nombreBD = "AUTOCONTESTADORA"
    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        mostrarLista()

        btnMensaje.setOnClickListener {
            var vMensajes = Intent(this, Main4Activity::class.java)
            startActivity(vMensajes)
        }

        btnGuardar.setOnClickListener {
            if(txtTelefono.text.isEmpty() || txtNombre.text.isEmpty()){
                mensaje("INGRESE TODOS LOS CAMPOS")
                return@setOnClickListener
            }
            agregarContacto(txtTelefono.text.toString(), txtNombre.text.toString())
            txtTelefono.setText("")
            txtNombre.setText("")
            mostrarLista()
        }
    }

    fun agregarContacto(numero: String, nombre : String) {
        var BD = BaseDatos(this, nombreBD, null, 1)
        var insertar = BD.writableDatabase
        var SQL1 = "INSERT INTO TELEFONO VALUES(NULL, '${numero}','${nombre}', '1')"
        var SQL2 = "INSERT INTO TELEFONO VALUES(NULL, '${numero}','${nombre}', '2')"

        if(checkTelefono.isChecked){
            insertar.execSQL(SQL1)
            mensaje("AGRADABLE")
        }else if(!checkTelefono.isChecked){
            insertar.execSQL(SQL2)
        }
        insertar.close()
        BD.close()

        mensaje("SE REGISTRO CORRECTAMENTE EL CONTACTO")
    }

    fun mostrarLista() {
        try {
            var BD = BaseDatos(this, nombreBD, null, 1)
            var select = BD.readableDatabase
            var SQL = "SELECT * FROM TELEFONO"
            var cursor = select.rawQuery(SQL, null)

            if(cursor.count > 0) {
                var arreglo = ArrayList<String>()
                this.listaID.clear()
                cursor.moveToFirst()
                var cantidad = cursor.count-1

                (0..cantidad).forEach {
                    var data = "Teléfono: ${cursor.getString(1)} \nNombre: ${cursor.getString(2)}" +
                            "\nEs: ${estadoContacto(cursor.getString(1))}"
                    arreglo.add(data)
                    listaID.add(cursor.getString(0))
                    cursor.moveToNext()
                }

                listaContactos.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, arreglo)
                listaContactos.setOnItemClickListener { parent, view, position, id ->
                    AlertDialog.Builder(this)
                        .setTitle("ATENCIÓN")
                        .setMessage("¿Desea realmente eliminar el teléfono seleccionado?")
                        .setPositiveButton("Eliminar") {d, i ->
                            eliminaContacto(listaID[position])
                        }
                        .setNegativeButton("Cancelar") {d, i -> }
                        .show()
                }
            }

            select.close()
            BD.close()
        } catch (error : SQLiteException){
            mensaje(error.message.toString())
        }
    }

    fun eliminaContacto(id: String) {
        try {
            var BD = BaseDatos(this, nombreBD, null, 1)
            var eliminar = BD.writableDatabase
            var SQL = "DELETE FROM TELEFONO WHERE ID = ?"
            var parametros = arrayOf(id)

            eliminar.execSQL(SQL,parametros)
            eliminar.close()
            BD.close()
            mensaje("SE ELIMINÓ CORRECTAMENTE")
            mostrarLista()
        } catch (error : SQLiteException) {
            mensaje(error.message.toString())
        }
    }

    fun mensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    fun estadoContacto(tel: String) : String {
        try {
            var BD = BaseDatos(this, nombreBD, null, 1)
            var select = BD.readableDatabase
            var SQL = "SELECT * FROM TELEFONO"
            var cursor = select.rawQuery(SQL, null)

            if(cursor.count > 0) {
                cursor.moveToFirst()
                var cantidad = cursor.count-1

                (0..cantidad).forEach {
                    if(cursor.getString(1) == tel) {
                        if(cursor.getString(3) == "1") {
                            return "AGRADABLE"
                        } else if(cursor.getString(3) == "2") {
                            return "NO AGRADABLE"
                        }
                    }
                    cursor.moveToNext()
                }
            }
            select.close()
            BD.close()
        } catch (error : SQLiteException){ }
        return "IGNORADA"
    }
}
