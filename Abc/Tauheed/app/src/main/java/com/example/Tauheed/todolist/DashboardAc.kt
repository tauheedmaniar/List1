package com.example.Tauheed.todolist

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.example.Tauheed.todolist.DTO.MainTask
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardAc : AppCompatActivity() {
     //Intialize the dbHandler
    lateinit var dbHandler: Database // Create DBHandler as variable

     // onCreated() created here we link to activity_dashboard xml file
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)// this activity will open
        setSupportActionBar(dashboard_toolbar)
        title = "Dashboard" // Title will on the dashboard tool bar
        dbHandler = Database(this)
        rv_dashboard.layoutManager = LinearLayoutManager(this) // TO manage the layout
        // Created a floating button to perform add operation
        fab_dashboard.setOnClickListener {
            val dialog = AlertDialog.Builder(this) // Created a alart box when user click on a floating button
            dialog.setTitle("Add List") // title of the alert box
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val taskname = view.findViewById<EditText>(R.id.todoList)  // Created a variable as taskname to get the value to apply condition statement
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                // condition is used to know whether to text in the editview is empty or not.
                if (taskname.text.isNotEmpty()) {
                    val items = MainTask()// this MainTask() from the Doto assigned to items
                    items.name = taskname.text.toString()
                    dbHandler.addToDo(items) // Add  text to database that we got from the  editview
                    refreshList() // after adding the text it refresh to make sure db is update
                }
            }
            // Created a dialog that appear on clicking on floating button
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show() // TO display the alert box.
        }

    }
    // This funtion is created to edit the task then this method is called in the drop down menu
     // MainTask has been called from the DTO.
    // It provide an conditional statement about the the text we got from the editview.
    fun updateResult(toDo: MainTask){
        val dialog = AlertDialog.Builder(this)  // To call the Alert box method
        dialog.setTitle("Update") // Set title
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.todoList)
        toDoName.setText(toDo.name)
        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            // Conditional statement about saved the task name into the db when it not empty.
            if (toDoName.text.isNotEmpty()) {
                toDo.name = toDoName.text.toString()
                dbHandler.updateResult(toDo)// Update into the db
                refreshList()//
            }
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int -> // TO press the cancel button.

        }
        dialog.show() // Dailog box appeared
    }
        // This method is called resume to get the list
    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList(){
        rv_dashboard.adapter = DashboardAdapter(this,dbHandler.getToDos())
    }

   // Here we created an adpater for the recycle view. And the recycle I have pass the view holder
    // Here we use Mutable<mainTask> adpater to the recycler view size
    class DashboardAdapter(val activity: DashboardAc, val list: MutableList<MainTask>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard, p0, false)) // retun the view holder using inflate lyout
        }
            // Here it return the size of the recycle view.
        override fun getItemCount(): Int {
            return list.size
        }
        // This method is created to get the view holder
        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.toDoName.text = list[p1].name
            // Holder to start the next activity
            holder.toDoName.setOnClickListener {
                val intent = Intent(activity,ItemActivity::class.java)
                intent.putExtra(INTENT_TODO_ID,list[p1].id)
                intent.putExtra(INTENT_TODO_NAME,list[p1].name)
                activity.startActivity(intent)
            }
            // To get the drop down menu
            holder.menu.setOnClickListener {
                val popup = PopupMenu(activity,holder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {

                    when(it.itemId){
                        // This will update the list
                        R.id.menu_edit->{
                            activity.updateResult(list[p1])
                        }
                        // For deleting the task from the list
                        // First we will receive alert message
                        R.id.menu_delete->{
                            val dialog = AlertDialog.Builder(activity)
                            dialog.setTitle("Are you sure")
                            dialog.setMessage("Do you want to delete task ?")
                            dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                                activity.dbHandler.deleteToDo(list[p1].id)
                                activity.refreshList() // update the list
                            }
                            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

                            }
                            dialog.show()
                        }
                        // To mark the status
                        R.id.menu_mark_as_completed->{
                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id,true)
                        }
                        // To reset the value
                        R.id.menu_reset->{
                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id,false)
                        }
                    }

                    true
                }
                popup.show()
            }
        }

        // ADding text view obj inside View Holder
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val toDoName: TextView = v.findViewById(R.id.tv_todo_name)
            val menu : ImageView = v.findViewById(R.id.iv_menu)
        }
    }
}
