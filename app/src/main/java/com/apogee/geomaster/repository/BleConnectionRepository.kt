package com.apogee.geomaster.repository

import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import com.apogee.geomaster.R
import com.apogee.geomaster.utils.safeNavigate
import com.apogee.updatedblelibrary.BleDeviceScanner
import com.apogee.updatedblelibrary.BleService
import com.apogee.updatedblelibrary.Utils.BleResponse
import com.apogee.updatedblelibrary.Utils.BleResponseListener
import com.apogee.updatedblelibrary.Utils.OnSerialRead
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BleConnectionRepository(private val context: Context) : ServiceConnection,
    BleResponseListener {


    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    private var service: BleService? = null


    private val _bleResponse = MutableStateFlow<BleResponse?>(null)
    val bleResponse: StateFlow<BleResponse?>
        get() = _bleResponse

    suspend fun onConnect(
        deviceAddress: String,
        readCharacteristic: String,
        writeCharacteristic: String,
        serviceUUIDId: String,
        descriptorId: String
    ) {


        _bleResponse.value = BleResponse.OnLoading("Connecting with $deviceAddress...")

        service?.connect(
            deviceAddress = deviceAddress,
            readCharacteristic = readCharacteristic,
            writeCharacteristic = writeCharacteristic,
            serviceUUIDId = serviceUUIDId,
            descriptorId = descriptorId
        )


    }


     fun setupConnection() {

        coroutineScope.launch {

            _bleResponse.value =
                BleResponse.OnLoading("Please wait \n Connecting with Service...")

            context.bindService(
                Intent(context, BleService::class.java),
                this@BleConnectionRepository,
                AppCompatActivity.BIND_AUTO_CREATE
            )
        }


    }

    override fun onResponse(res: BleResponse) {

        coroutineScope.launch {

            _bleResponse.value = res


        }

    }

    override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
        service = (binder as BleService.SerialBinder).service
        Log.d("BLE_TEST", "onServiceConnected: $service")
        service!!.attach(this)

        coroutineScope.launch {

            _bleResponse.value = BleResponse.OnConnected("Service Connected Successfully...")



        }

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Log.d(ContentValues.TAG, "onServiceDisconnected: " + p0)

        coroutineScope.launch {

            _bleResponse.value =
                BleResponse.OnDisconnected("Service Disconnected\nPlease try again...")

        }
    }


}