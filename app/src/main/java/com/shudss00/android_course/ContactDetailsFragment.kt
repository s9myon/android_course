package com.shudss00.android_course

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

class ContactDetailsFragment : Fragment() {

    private var contactId: Int? = null
    private var _binding: FragmentContactDetailsBinding? = null
    private val binding get() = _binding!!
    private var contactService: IContactService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactId = it.getInt(CONTACT_ID)
        }
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
        contactService?.getContactById(contactId, resultListener)
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

    private val resultListener = object : ContactLoadListener {
        override fun onLoaded(contact: Contact?) {
            Handler(Looper.getMainLooper()).apply {
                postAtFrontOfQueue {
                    bindContactDetails(contact)
                }
            }
        }
    }

    private fun bindContactDetails(contact: Contact?) {
        with(binding) {
            textViewName.text = contact?.name
            textViewPhoneNumber.text = contact?.phoneNumber
            textViewExtraPhoneNumber.text = contact?.extraPhoneNumber
            textViewEmail.text = contact?.email
            textViewExtraEmail.text = contact?.extraEmail
            textViewDescription.text = contact?.description
            Glide.with(imageViewAvatar.context)
                .load(contact?.img)
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(imageViewAvatar)
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
