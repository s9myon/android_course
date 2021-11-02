package com.shudss00.android_course

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.shudss00.android_course.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),
    ContactAdapter.OnContactClickListener,
    IContactService {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contactService: ContactService
    private var bound = false
    private var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ContactService.ContactBinder
            contactService = binder.getService()
            bound = true
            openContactListFragment()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Intent(this, ContactService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
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
