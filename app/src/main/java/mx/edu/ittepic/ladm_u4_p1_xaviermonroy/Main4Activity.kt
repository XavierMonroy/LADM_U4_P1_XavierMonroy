package mx.edu.ittepic.ladm_u4_p1_xaviermonroy

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main4.*

class Main4Activity : AppCompatActivity() {
    val nombreBD = "AUTOCONTESTADORA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        cargarMensaje()

        btnGuardar.setOnClickListener {
            if(txtMensajeAgradable.text.isEmpty() || txtMensajeNegativo.text.isEmpty()) {
                mensaje("DEBE ESCRIBIR UN MENSAJE")
                return@setOnClickListener
            }

            actualizarMensaje(txtMensajeAgradable.text.toString(),txtMensajeNegativo.text.toString())
        }
    }

    fun actualizarMensaje(mensajeA: String, mensajeN: String) {
        try {
            var BD = BaseDatos(this, nombreBD, null, 1)
            var actualizar = BD.writableDatabase
            var SQL = "UPDATE MENSAJE SET MENSAJE='${mensajeA}' WHERE ID=?"
            var SQL2 = "UPDATE MENSAJE SET MENSAJE='${mensajeN}' WHERE ID=?"
            var parametros1 = arrayOf(1)
            var parametros2 = arrayOf(2)

            actualizar.execSQL(SQL, parametros1)
            actualizar.execSQL(SQL2, parametros2)

            actualizar.close()
            BD.close()
            mensaje("SE ACTUALIZÓ CORRECTAMENTE EL MENSAJE")
        } catch (error : SQLiteException) {
            mensaje(error.message.toString())
        }
    }

    fun cargarMensaje() {
        try {
            var BD = BaseDatos(this, nombreBD, null, 1)
            var select = BD.readableDatabase
            var SQL1 = "SELECT * FROM MENSAJE WHERE ID = 1"
            var SQL2 = "SELECT * FROM MENSAJE WHERE ID = 2"
            var cursor1 = select.rawQuery(SQL1, null)
            var cursor2 = select.rawQuery(SQL2, null)

            if(cursor1.moveToFirst()){
                //SI HAY RESULTADO
                txtMensajeAgradable.setText(cursor1.getString(1))
            } else {
                    //NO HAY RESULTADO
            }

            if(cursor2.moveToFirst()){
                //SI HAY RESULTADO
                txtMensajeNegativo.setText(cursor2.getString(1))
            } else {
                //NO HAY RESULTADO
            }
            select.close()
            BD.close()
        } catch (error : SQLiteException){ }
    }

    fun mensaje(mensaje : String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
