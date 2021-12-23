package com.shudss00.android_course

interface IContactService {
    fun getContactList(resultListener: ContactListFragment.ContactsLoadListener)
    fun getContactById(contactId: Int, resultListener: ContactDetailsFragment.ContactLoadListener)
}
