package mx.edu.ittepic.ladm_u4_p1_xaviermonroy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log

class LlamadaEntrante : BroadcastReceiver() {
    var cursor : Context?= null
    var contestar = true
    var i = 0
    val nombreBD = "AUTOCONTESTADORA"

    override fun onReceive(context: Context, intent: Intent?) {
        try {
            cursor = context
            val tmgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            val PhoneListener = MyPhoneStateListener()

            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
            Log.e("Phone Receive Error", " $e")
        }
    }

    private inner class MyPhoneStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            Log.d("MyPhoneListener", "$state   incoming no:$incomingNumber")

            if(state == 2){
                contestar = false
            }

            if (state == 0 && contestar == true) {
                val num = "$incomingNumber"
                Log.d("LLamadaPerdida", num)
                i++
                try {
                    if(!num.isEmpty()) {
                        var BD = BaseDatos(cursor!!, nombreBD, null, 1)
                        var insertar = BD.writableDatabase
                        var SQL = "INSERT INTO LLAMADAS_PERDIDAS VALUES (NULL ,'${num}', 'false')"

                        insertar.execSQL(SQL)
                        BD.close()
                        Log.d("Insertar", "SE HA INSERTADO CORRECTAMENTE" + i)
                    }
                } catch (err : Exception) {

                }
            }
        }
    }
}