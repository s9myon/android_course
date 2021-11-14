package com.shudss00.android_course

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.shudss00.android_course.databinding.ActivityMainBinding

private const val CONTACT_ID = "CONTACT_ID"
private const val MESSAGE = "MESSAGE"

class MainActivity : AppCompatActivity(),
    ContactAdapter.OnContactClickListener,
    ContactDetailsFragment.OnBirthdayNotificationButtonClick,
    ContactDetailsFragment.BirthdayNotificationButtonStateListener,
    IContactService {

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
        Intent(this, ContactService::class.java).also { i ->
            bindService(i, connection, Context.BIND_AUTO_CREATE)
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
            .setAction("birthdayNotifyAction")
            .putExtra(CONTACT_ID, contact?.id)
            .putExtra(MESSAGE, "Today is ${contact?.name}'s birthday")
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
                .setAction("birthdayNotifyAction")
                .putExtra(CONTACT_ID, contact.id)
                .putExtra(MESSAGE, "Today is ${contact.name}'s birthday")
            val alarmUp = PendingIntent.getBroadcast(
                this, contact.id, intent, PendingIntent.FLAG_NO_CREATE
            ) != null
            val pendingIntent = PendingIntent.getBroadcast(
                this, contact.id, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            if (alarmUp) {
                pendingIntent.cancel()
                alarmManager.cancel(pendingIntent)
                buttonStateListener(false)
            } else {
                alarmManager.set(
                    AlarmManager.RTC,
                    contact.dayOfBirth.timeInMillis,
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
        contactId: Int?,
        resultListener: ContactDetailsFragment.ContactLoadListener
    ) {
        if (bound) {
            contactService.getContactById(contactId, resultListener)
        }
    }
}
