package com.shudss00.android_course

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import com.shudss00.android_course.databinding.FragmentContactDetailsBinding
import java.text.SimpleDateFormat

class ContactDetailsFragment : Fragment() {

    private var contactId: Int? = null
    private var _binding: FragmentContactDetailsBinding? = null
    private val binding get() = _binding!!
    private var contactService: IContactService? = null
    private var buttonClickListener: OnBirthdayNotificationButtonClick? = null
    private var buttonStateListener: BirthdayNotificationButtonStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactId = it.getInt(CONTACT_ID)
        }
        buttonClickListener = context as OnBirthdayNotificationButtonClick
        buttonStateListener = context as BirthdayNotificationButtonStateListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactService = context as IContactService
        contactId?.let {
            contactService?.getContactById(it, requestResultListener)
        }
        requireActivity().apply {
            title = getString(R.string.contact_details_toolbar_title)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    interface ContactLoadListener {
        fun onLoaded(contact: Contact?)
    }

    private val requestResultListener = object : ContactLoadListener {
        override fun onLoaded(contact: Contact?) {
            Handler(Looper.getMainLooper()).apply {
                postAtFrontOfQueue {
                    bindContactDetails(contact)
                }
            }
        }
    }

    interface BirthdayNotificationButtonStateListener {
        fun getBirthdayNotificationButtonState(contact: Contact?): Boolean
    }

    interface OnBirthdayNotificationButtonClick {
        fun onBirthdayNotificationButtonClick(
            contact: Contact?,
            buttonStateListener: (buttonState: Boolean) -> Unit
        )
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun bindContactDetails(contact: Contact?) {
        with(binding) {
            contact?.let {
                textViewName.text = contact.name
                if (contact.phoneNumber != "") {
                    textViewPhoneNumber.text =
                        getString(R.string.phone_number_one_title) + " " + contact.phoneNumber
                }
                if (contact.extraPhoneNumber != "") {
                    textViewExtraPhoneNumber.text =
                        getString(R.string.phone_number_two_title) + " " + contact.extraPhoneNumber
                }
                if (contact.email != "") {
                    textViewEmail.text = getString(R.string.email_one_title) + " " + contact.email
                }
                if (contact.extraEmail != "") {
                    textViewExtraEmail.text =
                        getString(R.string.email_two_title) + " " + contact.extraEmail
                }
                if (contact.description != "") {
                    textViewDescription.text = contact.description
                }
                if (contact.dayOfBirth != null) {
                    textViewDayOfBirth.text = getString(R.string.date_of_birth_title) + " " +
                            SimpleDateFormat("dd MMMM").format(contact.dayOfBirth!!.time)
                } else {
                    buttonBirthdayNotify.isInvisible = true
                }
                if (contact.img != Uri.EMPTY) {
                    imageViewAvatar.setImageURI(contact.img)
                } else {
                    imageViewAvatar.setImageResource(
                        R.drawable.ic_baseline_image_24
                    )
                }
                buttonBirthdayNotify.setOnClickListener {
                    buttonClickListener?.onBirthdayNotificationButtonClick(contact) { buttonState ->
                        if (buttonState) {
                            buttonBirthdayNotify.setText(R.string.notify_button_turn_off)
                        } else {
                            buttonBirthdayNotify.setText(R.string.notify_button_turn_on)
                        }
                    }
                }
                if (buttonStateListener!!.getBirthdayNotificationButtonState(contact)) {
                    buttonBirthdayNotify.setText(R.string.notify_button_turn_off)
                } else {
                    buttonBirthdayNotify.setText(R.string.notify_button_turn_on)
                }
            }
        }
    }

    companion object {
        private const val CONTACT_ID = "CONTACT_ID"

        fun newInstance(contactId: Int) =
            ContactDetailsFragment().apply {
                arguments = bundleOf(CONTACT_ID to contactId)
            }
    }
}
