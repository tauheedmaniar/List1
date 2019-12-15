package com.example.Tauheed.todolist

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import com.example.Tauheed.todolist.DTO.Listitems
import kotlinx.android.synthetic.main.activity_item.*

// Here we  created this class to perform sub-task list

class ItemActivity : AppCompatActivity() {
    //  To intialize the db handler
    lateinit var dbHandler: Database
    var todoId: Long = -1 // To store the id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(item_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)// Back arrow button
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = intent.getStringExtra(INTENT_TODO_NAME) // setting the item
        todoId = intent.getLongExtra(INTENT_TODO_ID, -1)
        dbHandler = Database(this)

        dash_items.layoutManager = LinearLayoutManager(this)
      // To add the list in the sub we need the press
        Add_flt.setOnClickListener {
            val dialog = AlertDialog.Builder(this) // Alert box will open
            dialog.setTitle("Add Item")
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)// TO get the view
            val toDoName = view.findViewById<EditText>(R.id.todoList)
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                // Condition statement if the text is not empty then do followin operation
                if (toDoName.text.isNotEmpty()) {
                    val item = Listitems()
                    item.subtask = toDoName.text.toString()
                    item.id_main = todoId // to get the id
                    item.isCompleted = false // Status
                    dbHandler.addToDoItem(item)
                    refreshs() // Refresh into the db
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }

    }

    // Here I am passing the item as a parameter. I have created this method to update the item list.
    fun update_listitems(item :Listitems ){
        val alert_box = AlertDialog.Builder(this) // Alert box
        alert_box.setTitle("Update List") // To set the title name
        val dailog_view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val listNams = dailog_view.findViewById<EditText>(R.id.todoList)
        listNams.setText(item.subtask)
        alert_box.setView(dailog_view)
        alert_box.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            // // Condition statement if the text is not empty then do followin operation
            if (listNams.text.isNotEmpty()) {
                item.subtask = listNams.text.toString()
                item.id_main = todoId
                item.isCompleted = false
                dbHandler.updateToDoItem(item)
                refreshs()
            }
        }
        // Alert box to cancel the task.
        alert_box.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        alert_box.show()
    }

    override fun onResume() {
        refreshs() // to refresh the list
        super.onResume()
    }
    // it will refresh the database
    private fun refreshs() {
        dash_items.adapter = ItemAdapter(this,dbHandler.getToDoItems(todoId))
    }
    // TO refresh the list, and we can used the dbact for the db handler.
    class ItemAdapter(val dbact: ItemActivity, val list: MutableList<Listitems>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(dbact).inflate(R.layout.rv_child_item, p0, false))
        }

        // To get the size of the recycler view
        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.itemName.text = list[p1].subtask
            holder.itemName.isChecked = list[p1].isCompleted // status
            holder.itemName.setOnClickListener {
                list[p1].isCompleted = !list[p1].isCompleted
                dbact.dbHandler.updateToDoItem(list[p1])// here we are using  the activity to call the db handler to store the text valeue.
            }
            // When we want to remove item, so to show the alert i have passed on click
            holder.delete.setOnClickListener {
                val dialog = AlertDialog.Builder(dbact)
                dialog.setTitle("Are you sure")
                dialog.setMessage("Do you want to delete this item ?")
                dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                    dbact.dbHandler.deleteToDoItem(list[p1].id)
                    dbact.refreshs() // After  deleting it we need to call the refresh method
                }
                dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int -> // Dialog box for cancel.

                }
                dialog.show()
            }
            // to set the clickable button
            holder.edit.setOnClickListener {
                dbact.update_listitems(list[p1]) // it will call the updated() inside the adapter
            }
        }
        // This class is made to view the
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val itemName: CheckBox = v.findViewById(R.id.cb_item)
            val edit : ImageView = v.findViewById(R.id.iv_edit)
            val delete : ImageView = v.findViewById(R.id.iv_delete)
        }
    }
    // TO kill our current activity I created this method
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) { // toolbar button press
            finish() // killin the activity
            true
        } else
            super.onOptionsItemSelected(item)
    }

}
