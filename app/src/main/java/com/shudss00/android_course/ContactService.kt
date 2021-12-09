package com.shudss00.android_course

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.provider.ContactsContract
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.*
import java.text.ParseException
import java.text.SimpleDateFormat


class ContactService : Service() {
    private var binder: Binder? = ContactBinder()
    private var executor: ExecutorService? = Executors.newFixedThreadPool(1)

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

    fun getContactList(resultListener: ContactListFragment.ContactsLoadListener) {
        val ref = WeakReference(resultListener)
        executor?.execute {
            val contactList = mutableListOf<Contact>()
            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.PHOTO_URI,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
                ),
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
            cursor.use {
                if (cursor != null) {
                    val idColumnIndex = cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                    )
                    val nameColumnIndex = cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                    val imageUriColumnIndex = cursor.getColumnIndex(
                        ContactsContract.Contacts.PHOTO_URI
                    )
                    val hasPhoneNumberColumnIndex = cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER
                    )
                    var imageString: String?
                    var numberList: List<String>?
                    var hasNumbers: Int?
                    var contact: Contact?
                    cursor.moveToFirst()
                    while (!cursor.isAfterLast) {
                        contact = Contact()
                        contact.id = cursor.getInt(idColumnIndex)
                        contact.name = cursor.getString(nameColumnIndex)

                        imageString = cursor.getString(imageUriColumnIndex)
                        if (imageString != null) {
                            contact.img = Uri.parse(imageString)
                        }

                        hasNumbers = cursor.getInt(hasPhoneNumberColumnIndex)
                        if (hasNumbers == 1) {
                            numberList = getContactPhoneNumbersFromContentProvider(contact.id)
                            if (numberList.isNotEmpty()) {
                                contact.phoneNumber = numberList[0]
                            }
                        }

                        contactList.add(contact)
                        cursor.moveToNext()
                    }
                }
            }
            ref.get()?.onLoaded(contactList)
        }
    }

    fun getContactById(
        contactId: Int,
        resultListener: ContactDetailsFragment.ContactLoadListener
    ) {
        val ref = WeakReference(resultListener)
        executor?.execute {
            val contact = Contact()
            val contactCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.PHOTO_URI,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
                ),
                ContactsContract.Contacts._ID + " = ?",
                arrayOf(contactId.toString()),
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
            contactCursor.use {
                if (contactCursor != null) {
                    val nameColumnIndex = contactCursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                    val imageUriColumnIndex = contactCursor.getColumnIndex(
                        ContactsContract.Contacts.PHOTO_URI
                    )
                    val hasPhoneNumberColumnIndex = contactCursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER
                    )
                    var imageString: String?
                    var numberList: List<String>?
                    var emailList: List<String>?
                    var hasNumbers: Int?
                    contactCursor.moveToFirst()
                    while (!contactCursor.isAfterLast) {
                        contact.name = contactCursor.getString(nameColumnIndex)

                        imageString = contactCursor.getString(imageUriColumnIndex)
                        if (imageString != null) {
                            contact.img = Uri.parse(imageString)
                        }

                        contact.dayOfBirth = getContactBirthdayFromContentProvider(contactId)

                        hasNumbers = contactCursor.getInt(hasPhoneNumberColumnIndex)
                        if (hasNumbers == 1) {
                            numberList = getContactPhoneNumbersFromContentProvider(contactId)
                            if (numberList.isNotEmpty()) {
                                contact.phoneNumber = numberList[0]
                            }
                            if (numberList.size > 1) {
                                contact.extraPhoneNumber = numberList[1]
                            }
                        }

                        emailList = getContactEmailsFromContentProvider(contactId)
                        if (emailList.isNotEmpty()) {
                            contact.email = emailList[0]
                        }
                        if (emailList.size > 1) {
                            contact.extraEmail = emailList[1]
                        }

                        contactCursor.moveToNext()
                    }
                }
            }
            ref.get()?.onLoaded(contact)
        }
    }

    private fun getContactBirthdayFromContentProvider(contactId: Int?): Calendar? {
        var birthday: Calendar? = null
        val cursorBirthday = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Event.START_DATE),
            ContactsContract.Data.CONTACT_ID
                    + " = ? AND "
                    + ContactsContract.CommonDataKinds.Event.TYPE
                    + " = "
                    + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
            arrayOf(contactId.toString()),
            null
        )
        try {
            if (cursorBirthday != null && cursorBirthday.count > 0) {
                cursorBirthday.moveToFirst()
                val date = cursorBirthday.getString(
                    cursorBirthday.getColumnIndex(
                        ContactsContract.CommonDataKinds.Event.START_DATE
                    )
                )
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val calendar: Calendar = GregorianCalendar()
                try {
                    calendar.time = format.parse(date)
                } catch (e: ParseException) {
                    calendar.timeInMillis = 0
                }
                birthday = calendar
            }
        } finally {
            cursorBirthday?.close()
        }
        return birthday
    }

    private fun getContactEmailsFromContentProvider(contactId: Int?): List<String> {
        val emailList = mutableListOf<String>()
        val cursorEmail = contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
            arrayOf(contactId.toString()),
            null
        )
        try {
            if (cursorEmail != null) {
                val address: Int =
                    cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                cursorEmail.moveToFirst()
                while (!cursorEmail.isAfterLast) {
                    emailList.add(cursorEmail.getString(address))
                    cursorEmail.moveToNext()
                }
            }
        } finally {
            cursorEmail?.close()
        }
        return emailList
    }

    private fun getContactPhoneNumbersFromContentProvider(contactId: Int?): List<String> {
        val numberList = mutableListOf<String>()
        val phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId.toString()),
            null
        )
        try {
            if (phoneCursor != null) {
                val number =
                    phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                phoneCursor.moveToFirst()
                while (!phoneCursor.isAfterLast) {
                    numberList.add(phoneCursor.getString(number))
                    phoneCursor.moveToNext()
                }
            }
        } finally {
            phoneCursor?.close()
        }
        return numberList
    }
}
