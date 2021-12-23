package com.shudss00.android_course

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shudss00.android_course.databinding.ActivityMainBinding

private const val CONTACT_ID = "CONTACT_ID"
private const val MESSAGE = "MESSAGE"
private const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity(),
    ContactAdapter.OnContactClickListener,
    ContactDetailsFragment.OnBirthdayNotificationButtonClick,
    ContactDetailsFragment.BirthdayNotificationButtonStateListener,
    IContactService {

    private var readContactsGranted = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var contactService: ContactService
    private val alarmManager by lazy { getSystemService(ALARM_SERVICE) as AlarmManager }
    private var bound = false
    private var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ContactService.ContactBinder
            contactService = binder.getService()
            bound = true
            val intent = this@MainActivity.intent
            val contactId = intent.getIntExtra(CONTACT_ID, -1)
            if (contactId != -1) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, ContactDetailsFragment.newInstance(contactId))
                    .commit()
            } else {
                openContactListFragment()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hasReadContactPermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            )

        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            readContactsGranted = true
            Intent(this, ContactService::class.java).also { i ->
                bindService(i, connection, Context.BIND_AUTO_CREATE)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContactsGranted = true
            }
        }
        if (readContactsGranted) {
            Intent(this, ContactService::class.java).also { i ->
                bindService(i, connection, Context.BIND_AUTO_CREATE)
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.requires_set_permissions),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        if (bound) {
            unbindService(connection)
            bound = false
        }
        super.onDestroy()
    }

    private fun openContactListFragment() = supportFragmentManager
        .beginTransaction()
        .replace(R.id.container, ContactListFragment())
        .commit()

    private fun openContactDetailsFragment(contactId: Int) = supportFragmentManager
        .beginTransaction()
        .replace(R.id.container, ContactDetailsFragment.newInstance(contactId))
        .addToBackStack(null)
        .commit()

    override fun onContactClick(contactId: Int) {
        openContactDetailsFragment(contactId)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun getBirthdayNotificationButtonState(contact: Contact?): Boolean {
        val intent = Intent(this, ContactReceiver::class.java)
            .setAction(getString(R.string.birthday_notify_action))
            .putExtra(CONTACT_ID, contact?.id)
            .putExtra(MESSAGE, R.string.birthday_notify_string)
        return PendingIntent.getBroadcast(
            this, contact!!.id, intent, PendingIntent.FLAG_NO_CREATE
        ) != null
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onBirthdayNotificationButtonClick(
        contact: Contact?,
        buttonStateListener: (buttonState: Boolean) -> Unit
    ) {
        contact?.let {
            val intent = Intent(this, ContactReceiver::class.java)
                .setAction(getString(R.string.birthday_notify_action))
                .putExtra(CONTACT_ID, contact.id)
                .putExtra(MESSAGE, R.string.birthday_notify_string)
            val alarmUp = contact.id.let { id ->
                PendingIntent.getBroadcast(
                    this, id, intent, PendingIntent.FLAG_NO_CREATE
                )
            } != null
            val pendingIntent = contact.id.let { id ->
                PendingIntent.getBroadcast(
                    this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            if (alarmUp) {
                pendingIntent?.cancel()
                alarmManager.cancel(pendingIntent)
                buttonStateListener(false)
            } else {
                alarmManager.set(
                    AlarmManager.RTC,
                    contact.dayOfBirth!!.timeInMillis,
                    pendingIntent
                )
                buttonStateListener(true)
            }
        }
    }

    override fun getContactList(resultListener: ContactListFragment.ContactsLoadListener) {
        if (bound) {
            contactService.getContactList(resultListener)
        }
    }

    override fun getContactById(
        contactId: Int,
        resultListener: ContactDetailsFragment.ContactLoadListener
    ) {
        if (bound) {
            contactService.getContactById(contactId, resultListener)
        }
    }
}
