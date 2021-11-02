package com.shudss00.android_course

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ContactService : Service() {
    private var binder: Binder? = ContactBinder()
    private var executor: ExecutorService? = Executors.newFixedThreadPool(1)

    fun getContactList(resultListener: ContactListFragment.ContactsLoadListener) {
        val ref = WeakReference(resultListener)
        executor?.execute { ref.get()?.onLoaded(contactsList) }
    }

    fun getContactById(
        contactId: Int?,
        resultListener: ContactDetailsFragment.ContactLoadListener
    ) {
        val ref = WeakReference(resultListener)
        executor?.execute {
            ref.get()?.onLoaded(contactsList.find { it.id == contactId })
        }
    }

    inner class ContactBinder : Binder() {
        fun getService(): ContactService = this@ContactService
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        binder = null
        executor = null
        return super.onUnbind(intent)
    }
}
