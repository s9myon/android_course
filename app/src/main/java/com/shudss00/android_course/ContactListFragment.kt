package com.shudss00.android_course

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shudss00.android_course.databinding.FragmentContactListBinding

class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!
    private var listener: ContactAdapter.OnContactClickListener? = null
    private var contactService: IContactService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = context as ContactAdapter.OnContactClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactService = context as IContactService
        contactService?.getContactList(resultListener)
        requireActivity().apply {
            title = getString(R.string.contact_list_toolbar_title)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    interface ContactsLoadListener {
        fun onLoaded(contacts: List<Contact>)
    }

    private val resultListener = object : ContactsLoadListener {
        override fun onLoaded(contacts: List<Contact>) {
            Handler(Looper.getMainLooper()).apply {
                postAtFrontOfQueue {
                    with(binding.list) {
                        layoutManager = LinearLayoutManager(context)
                        adapter = ContactAdapter(contacts, listener)
                    }
                }
            }
        }
    }
}
