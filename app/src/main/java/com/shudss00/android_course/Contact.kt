package com.shudss00.android_course

import android.net.Uri
import java.util.*

data class Contact(
    var id: Int = 0,
    var name: String = "",
    var phoneNumber: String = "",
    var extraPhoneNumber: String = "",
    var email: String = "",
    var extraEmail: String = "",
    var img: Uri = Uri.EMPTY,
    var description: String = "",
    var dayOfBirth: Calendar? = null
)
