package com.shudss00.android_course

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shudss00.android_course.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),
    ContactAdapter.OnContactClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            openContactListFragment()
        }
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
}