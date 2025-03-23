package com.example.testjob

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val accountInfoTextView = view.findViewById<TextView>(R.id.accountInfoTextView)
        accountInfoTextView.text =
            "Профиль пользователя\n\nЗдесь будет отображаться информация о вашем аккаунте."
    }
}