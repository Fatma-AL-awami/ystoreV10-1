package com.amroz.ystore.Chating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amroz.ystore.Models.Message
import com.amroz.ystore.Models.UserChat
import com.amroz.ystore.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    private var rootRef: FirebaseFirestore? = null
    private var fromUid: String? = ""
    private var adapter: MessageAdapter? = null
    lateinit var rec: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        rec = findViewById(R.id.recyclerView)
        var name: TextView = findViewById(R.id.name)
        rootRef = FirebaseFirestore.getInstance()

        //   supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        val fromUser = intent.extras?.get("fromUser") as UserChat
        fromUid = fromUser.uid
        var fromRooms = fromUser.rooms
        val toUser = intent.extras?.get("toUser") as UserChat
        val toUid = toUser.uid
        var toRooms = toUser.rooms

        var roomId = intent.extras?.get("roomId") as String
        //---------------------------------------------------------------------------------------------------//

        name.text = toUser.userName
        if (roomId == "noRoomId") {
            roomId = rootRef!!.collection("messages").document().id
            if (fromRooms != null) {
                for ((key, _) in fromRooms) {
                    if (toRooms != null) {
                        if (toRooms.contains(key)) {
                            roomId = key
                        }
                    }
                }
            }
        }

        send.setOnClickListener {
            if (fromRooms == null) {
                fromRooms = mutableMapOf()
            }
            fromRooms!![roomId] = true
            fromUser.rooms = fromRooms
            rootRef!!.collection("users").document(fromUid!!).set(fromUser, SetOptions.merge())
            rootRef!!.collection("contacts").document(toUid).collection("userContacts")
                .document(fromUid!!).set(fromUser, SetOptions.merge())
            rootRef!!.collection("rooms").document(toUid).collection("userRooms").document(roomId)
                .set(fromUser, SetOptions.merge())

            if (toRooms == null) {
                toRooms = mutableMapOf()
            }
            toRooms!![roomId] = true
            toUser.rooms = toRooms
            rootRef!!.collection("users").document(toUid).set(toUser, SetOptions.merge())
            rootRef!!.collection("contacts").document(fromUid!!).collection("userContacts")
                .document(toUid).set(toUser, SetOptions.merge())
            rootRef!!.collection("rooms").document(fromUid!!).collection("userRooms")
                .document(roomId).set(toUser, SetOptions.merge())

            val messageText = messagetext.text.toString()
            val message = Message(messageText, fromUid!!)
            rootRef!!.collection("messages").document(roomId).collection("roomMessages")
                .add(message)
            messagetext.text.clear()
        }

        val query = rootRef!!.collection("messages").document(roomId).collection("roomMessages")
            .orderBy("sentAt", Query.Direction.ASCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java).build()
//        adapter = MessageAdapter(options)
//        recycler_view.adapter = adapter

        adapter = MessageAdapter(options)
        rec.layoutManager = LinearLayoutManager(this)
        rec.adapter = adapter

        title = toUser.userName
    }

    inner class MessageViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {
        internal fun setMessage(message: Message) {
            val textView = view.findViewById<TextView>(R.id.text_view)
            val time = view.findViewById<TextView>(R.id.time)


            textView.text = message.messageText
            time.text = message.sentAt.toString()

        }
    }

    inner class MessageAdapter internal constructor(options: FirestoreRecyclerOptions<Message>) :
        FirestoreRecyclerAdapter<Message, MessageViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            return if (viewType == R.layout.item_message_to) {


                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_to, parent, false)
                MessageViewHolder(view)
            } else {


                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_from, parent, false)
                MessageViewHolder(view)
            }


        }


        override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
            holder.setMessage(model)
        }

        override fun getItemViewType(position: Int): Int {
            return if (fromUid != getItem(position).fromUid) {
                R.layout.item_message_to
            } else {
                R.layout.item_message_from
            }
        }

        override fun onDataChanged() {
            rec.layoutManager?.scrollToPosition(itemCount - 1)
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    override fun onStart() {
        super.onStart()

        if (adapter != null) {
            adapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

}
