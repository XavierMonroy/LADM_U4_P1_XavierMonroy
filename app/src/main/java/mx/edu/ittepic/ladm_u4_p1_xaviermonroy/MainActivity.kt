package mx.edu.ittepic.ladm_u4_p1_xaviermonroy

import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var REQUEST_PERMISOS = 111
    var hiloTest : HiloTest ?= null
    val nombreBD = "AUTOCONTESTADORA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //verifica o solicita permisos
        solicitaPermisos()

        //hilo para autocontestadora
        hiloTest = HiloTest(this)
        hiloTest?.start()

        //verificar conexion
        conexion()
        llamadasPerdidas()

        btnConfig.setOnClickListener {
            var vConfiguraciones = Intent(this, Main3Activity::class.java)
            startActivity(vConfiguraciones)
        }
    }

    private fun conexion(){
        try {
            var BD = BaseDatos(this, nombreBD, null, 1)
            var select = BD.readableDatabase
            var SQL = "SELECT * FROM MENSAJE WHERE ID = 1"
            var cursor = select.rawQuery(SQL, null)

            if(cursor.moveToFirst()){
                //SI HAY RESULTADO
            } else {
                //NO HAY RESULTADO
                agregaMensaje()
            }
            select.close()
            BD.close()
        } catch (error : SQLiteException){

        }
    }

    private fun agregaMensaje() {
        var BD = BaseDatos(this, nombreBD, null, 1)
        var insertar = BD.writableDatabase
        var SQL = "INSERT INTO MENSAJE VALUES(NULL, '')"

        insertar.execSQL(SQL)
        insertar.close()
        BD.close()
    }

    private fun solicitaPermisos() {
        var ReadCall = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALL_LOG)
        var ReadState = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_STATE)
        var SMS = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS)

        if(ReadCall != PackageManager.PERMISSION_GRANTED || ReadState != PackageManager.PERMISSION_GRANTED ||
            SMS != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.SEND_SMS), REQUEST_PERMISOS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_PERMISOS){
            llamadasPerdidas()
        }
    }

    private fun llamadasPerdidas() {
        try {
            var i = 0
            var BD = BaseDatos(this, nombreBD, null, 1)
            var select = BD.readableDatabase
            var SQL = "SELECT * FROM LLAMADAS_PERDIDAS ORDER BY ID DESC"
            var cursor = select.rawQuery(SQL, null)

            if(cursor.count > 0) {
                var arreglo = ArrayList<String>()
                cursor.moveToFirst()
                var cantidad = cursor.count-1
                (0..cantidad).forEach {
                    i++
                    if((i % 2) == 0){
                        var data = "Teléfono: ${cursor.getString(1)} \nLlamada: ${estadoTelefono(cursor.getString(1))}"
                        arreglo.add(data)
                    }
                    cursor.moveToNext()
                }
                listaLLamadas.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, arreglo)
            } else if(cursor.count == 0){
                var noHay = ArrayList<String>()
                var data = "NO HAY LLAMADAS PERDIDAS"

                noHay.add(data)
                listaLLamadas.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noHay)
            }

            select.close()
            BD.close()
        } catch (error : SQLiteException){
            mensaje(error.message.toString())
        }
    }

    fun enviarMensaje() {
        try {
            var i = 0
            var BD = BaseDatos(this, nombreBD, null, 1)
            var select = BD.readableDatabase
            var SQL = "SELECT * FROM LLAMADAS_PERDIDAS"
            var cursor = select.rawQuery(SQL, null)

            if(cursor.count > 0) {
                cursor.moveToFirst()
                var cantidad = cursor.count - 1

                (0..cantidad).forEach {
                    i++
                    if(cursor.getString(2) == "false" && (i % 2) == 0) {
                        if(estadoTelefono(cursor.getString(1)) == "AGRADABLE") {
                            actualizarEstado(cursor.getString(0))
                            actualizarEstado((cursor.getInt(0) + 1).toString())
                            SmsManager.getDefault().sendTextMessage(cursor.getString(1), null, obtenMensaje(1), null, null)
                        }

                        else if(estadoTelefono(cursor.getString(1)) == "NO AGRADABLE") {
                            actualizarEstado(cursor.getString(0))
                            actualizarEstado((cursor.getInt(0) + 1).toString())
                            SmsManager.getDefault().sendTextMessage(cursor.getString(1), null, obtenMensaje(2), null, null)
                        }
                    }
                    cursor.moveToNext()
                }
            }
            select.close()
            BD.close()
        } catch (error : SQLiteException){
            mensaje(error.message.toString())
        }
    }

    private fun estadoTelefono(tel: String) : String {
        try {
            var BD = BaseDatos(this, nombreBD, null, 1)
            var select = BD.readableDatabase
            var SQL = "SELECT * FROM TELEFONO"
            var cursor = select.rawQuery(SQL, null)

            if(cursor.count > 0) {
                cursor.moveToFirst()
                var cantidad = cursor.count - 1

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
        } catch (error : SQLiteException){
            mensaje(error.message.toString())
        }
        return "IGNORADA"
    }

    private fun actualizarEstado(ID: String){
        try {
            var BD = BaseDatos(this, nombreBD, null, 1)
            var actualizar = BD.writableDatabase
            var SQL = "UPDATE LLAMADAS_PERDIDAS SET ESTADO ='TRUE' WHERE ID=?"
            var parametros = arrayOf(ID)

            actualizar.execSQL(SQL, parametros)
            actualizar.close()
            BD.close()
        } catch (error : SQLiteException) {
            mensaje(error.message.toString())
        }
    }

    fun obtenMensaje(tipo: Int) : String {
        try {
            var BD = BaseDatos(this, nombreBD, null, 1)
            var select = BD.readableDatabase
            var SQL = "SELECT * FROM MENSAJE WHERE ID = ?"
            var parametros = arrayOf(tipo.toString())
            var cursor = select.rawQuery(SQL, parametros)

            if(cursor.moveToFirst()){
                return cursor.getString(1)
            }
            select.close()
            BD.close()
        } catch (error : SQLiteException){ }
        return "ERROR AL OBTENER EL MENSAJE"
    }

    fun mensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
