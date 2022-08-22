package com.example.githubusersnew

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso

private const val ARG_USER_ID = "user_id"

class UserFragment : Fragment() {
    private lateinit var user: User
    private lateinit var userLogin: TextView
    private lateinit var userId: TextView
    private lateinit var userAvatar: ImageView
    private lateinit var githubBtn: Button

    private val userDetailsViewModel: UserDetailsViewModel by lazy {
        ViewModelProvider(this)[UserDetailsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId: Int = arguments?.getSerializable(ARG_USER_ID) as Int
        userDetailsViewModel.loadUser(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_details, container, false)
        userLogin = view.findViewById(R.id.userLogin)
        userId = view.findViewById(R.id.userId)
        userAvatar = view.findViewById(R.id.userAvatar)
        githubBtn = view.findViewById(R.id.githubBtn)

        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        userDetailsViewModel.userLiveData.observe(
            viewLifecycleOwner
        ) { user ->
            user?.let {
                this.user = user
                updateUI()
            }
        }
    }

    private fun updateUI() {
        userLogin.text = user.login
        userId.text = user.id.toString()
        Picasso.get().load(user.avatarUrl).into(userAvatar)
        githubBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.userUrl))
            startActivity(intent)
        }
    }

    companion object {

        fun newInstance(userId: Int): UserFragment {
            val args = Bundle().apply {
                putSerializable(ARG_USER_ID, userId)
            }
            return UserFragment().apply {
                arguments = args
            }
        }
    }
}