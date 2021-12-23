package com.shudss00.android_course

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.shudss00.android_course.databinding.FragmentContactBinding

class ContactAdapter(
    private val values: List<Contact>,
    private var listener: OnContactClickListener?
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    interface OnContactClickListener {
        fun onContactClick(contactId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentContactBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(values[position], listener)
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder(
        private val binding: FragmentContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Contact, listener: OnContactClickListener?) {
            with(binding) {
                textViewName.text = item.name
                textViewNumber.text = item.phoneNumber
                if (item.img != Uri.EMPTY) {
                    imageViewAvatar.setImageURI(item.img)
                } else {
                    imageViewAvatar.setImageResource(
                        R.drawable.ic_baseline_image_24
                    )
                }
                constraintLayoutContactCard.setOnClickListener {
                    item.id.let { id ->
                        listener?.onContactClick(id)
                    }
                }
            }
        }
    }
}
