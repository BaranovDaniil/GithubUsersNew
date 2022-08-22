package com.example.githubusersnew

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class GithubUsersListFragment : Fragment() {

    interface Callbacks {
        fun onUserSelected(userId: Int)
    }

    private var callbacks: Callbacks? = null

    private lateinit var usersRecyclerView: RecyclerView
    private var adapter: UsersAdapter = UsersAdapter()

    private lateinit var backgroundDownloader: BackgroundDownloader<UserHolder>

    private val usersListViewModel: UsersListViewModel by lazy {
        ViewModelProvider(this)[UsersListViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val responseHandler = Handler(Looper.getMainLooper())
        backgroundDownloader = BackgroundDownloader(responseHandler)
        lifecycle.addObserver(backgroundDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_list, container, false)
        usersRecyclerView = view.findViewById(R.id.user_recycler_view)
        usersRecyclerView.layoutManager = LinearLayoutManager(context)
        usersRecyclerView.adapter = adapter
        usersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    backgroundDownloader.queueThumbnail(
                        UserHolder(recyclerView),
                        usersListViewModel.sinceParam
                    )
                }
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usersListViewModel.usersListLiveData.observe(viewLifecycleOwner) { users ->
            updateUI(users)
        }
    }


    private fun updateUI(users: List<User>) {
        adapter.submitList(users)
        if (users.isNotEmpty())
            usersListViewModel.sinceParam = users[users.size - 1].id
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(backgroundDownloader.fragmentLifecycleObserver)
    }


    companion object {
        fun newInstance(): GithubUsersListFragment {
            return GithubUsersListFragment()
        }
    }

    private inner class UserHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private lateinit var user: User
        private var viewPosition: Int = 0

        val loginTextView: TextView = itemView.findViewById(R.id.userLogin)
        val idTextView: TextView = itemView.findViewById(R.id.userId)
        val avatarImageView: ImageView = itemView.findViewById(R.id.userAvatar)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(user: User, position: Int) {
            this.user = user
            this.viewPosition = position
            loginTextView.text = this.user.login
            idTextView.text = this.user.id.toString()
            Picasso.get().load(user.avatarUrl).into(avatarImageView)
        }

        override fun onClick(p0: View?) {
            usersListViewModel.currentPosition = viewPosition
            callbacks?.onUserSelected(user.id)
        }
    }

    private inner class UsersAdapter : ListAdapter<User, UserHolder>(TaskDiffCallBack()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
            val view = layoutInflater.inflate(R.layout.user_list_item, parent, false)
            return UserHolder(view)
        }

        override fun onBindViewHolder(holder: UserHolder, position: Int) {
            val user = getItem(position)
            holder.bind(user, position)
        }
    }

    class TaskDiffCallBack : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            Log.d("Diffutil", Thread.currentThread().name)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            Log.d("Diffutil", Thread.currentThread().name)
            return oldItem == newItem
        }
    }
}