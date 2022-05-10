package com.example.turnoffbluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.turnoffbluetooth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var blueToothState = false

    private val bluetoothBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                val state: Int? =
                    intent?.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )
                when (state) {
                    BluetoothAdapter.STATE_ON,
                    BluetoothAdapter.STATE_TURNING_ON -> blueToothState = true
                    BluetoothAdapter.STATE_OFF,
                    BluetoothAdapter.STATE_TURNING_OFF -> blueToothState = false
                }
                updateIcon()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bluetoothIcon.setOnClickListener {
            toggleBluetoothState()
        }
        updateIcon()
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        try {
            registerReceiver(bluetoothBroadcastReceiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        unregisterReceiver(bluetoothBroadcastReceiver)
        super.onDestroy()
    }

    private fun updateIcon(){
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter == null){
            Log.d(TAG, "onCreate: Device does not support")
            finish()
            return
        }

        if(bluetoothAdapter.isEnabled){
            binding.bluetoothIcon.setBackgroundResource(R.drawable.bluetooth_active)
        } else {
            binding.bluetoothIcon.setBackgroundResource(R.drawable.bluetooth_inactive)
        }
    }

    private fun toggleBluetoothState(){
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter == null){
            Log.d(TAG, "onCreate: Device does not support")
            finish()
            return
        }

        if(bluetoothAdapter.isEnabled){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            bluetoothAdapter.disable()
        } else {
            bluetoothAdapter.enable()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}