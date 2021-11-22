package com.shudss00.android_course

import java.util.Calendar
import java.util.GregorianCalendar

data class Contact(
        val name: String,
        val phoneNumber: String,
        val extraPhoneNumber: String,
        val email: String,
        val extraEmail: String,
        val img: String,
        val description: String,
        val dayOfBirth: Calendar
    ) {
    val id: Int = countOfContacts++

    companion object {
        var countOfContacts = 0
    }
}

val contactsList = listOf(
    Contact(
        name = "Elon Musk",
        phoneNumber = "+7-958-786-9849",
        extraPhoneNumber = "+7-922-773-6243",
        email = "musk0@mail.com",
        extraEmail = "musk1@mail.com",
        img = "https://goo.su/8dk1",
        description = "Genius",
        dayOfBirth = GregorianCalendar(1971, Calendar.JULY, 28)
    ),
    Contact(
        name = "Anonymous",
        phoneNumber = "+7-973-883-3184",
        extraPhoneNumber = "+7-800-151-8709",
        email = "nymus@mail.com",
        extraEmail = "anony@mail.com",
        img = "https://goo.su/8DJZ",
        description = "yoli pali",
        dayOfBirth = GregorianCalendar(1999, Calendar.NOVEMBER, 20)
    ),
    Contact(
        name = "Android",
        phoneNumber = "+7-998-503-6145",
        extraPhoneNumber = "+7-922-297-7902",
        email = "andrey7@mail.com",
        extraEmail = "droid@mail.com",
        img = "https://goo.su/8dk6",
        description = "Boss",
        dayOfBirth = GregorianCalendar(2008, Calendar.SEPTEMBER, 23)
    ),
    Contact(
        name = "Homer Simpson",
        phoneNumber = "+7-943-773-3456",
        extraPhoneNumber = "+7-922-369-3443",
        email = "homer@mail.com",
        extraEmail = "simpson@mail.com",
        img = "https://goo.su/8dKa",
        description = "I want to eat pelmeny",
        dayOfBirth = GregorianCalendar(1971, Calendar.JULY, 28)
    ),
    Contact(
        name = "God",
        phoneNumber = "+7-000-000-0001",
        extraPhoneNumber = "+7-922-030-7956",
        email = "iisus@paradise.com",
        extraEmail = "hristos@mail.com",
        img = "https://goo.su/8dkC",
        description = "Creator",
        dayOfBirth = GregorianCalendar(1956, Calendar.MAY, 12)
    ),
    Contact(
        name = "Papich",
        phoneNumber = "+7-740-040-8764",
        extraPhoneNumber = "+7-564-362-9846",
        email = "vitaly@mail.com",
        extraEmail = "cal@mail.com",
        img = "https://goo.su/8dSp",
        description = "Ya ne ponimay",
        dayOfBirth = GregorianCalendar(1990, Calendar.NOVEMBER, 19)
    )
)
