package com.shudss00.android_course

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
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
        contactService?.getContactById(contactId, requestResultListener)
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
        fun getBirthdayNotificationButtonState(contact: Contact?) : Boolean
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
                textViewPhoneNumber.text = contact.phoneNumber
                textViewExtraPhoneNumber.text = contact.extraPhoneNumber
                textViewEmail.text = contact.email
                textViewExtraEmail.text = contact.extraEmail
                textViewDescription.text = contact.description
                textViewDayOfBirth.text = getString(R.string.date_of_birth_title) +
                        SimpleDateFormat("dd MMMM").format(contact.dayOfBirth.time).toString()
                Glide.with(imageViewAvatar.context)
                    .load(contact.img)
                    .centerCrop()
                    .circleCrop()
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(imageViewAvatar)
                buttonBirthdayNotify.setOnClickListener{
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
