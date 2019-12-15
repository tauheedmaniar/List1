package com.example.Tauheed.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.Tauheed.todolist.DTO.MainTask
import com.example.Tauheed.todolist.DTO.Listitems

// This class is created to handle the data base . Here we pass the name of the database and the version.
class Database(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
   // creating an sqlite database.here we have created  2 tables
    override fun onCreate(db: SQLiteDatabase) {
       //First table, created id ,  datetime and the name
       // TO store table query. And here we have taken the name from the constant file.
        val createToDoTable = "  CREATE TABLE $TABLE_TODO (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_NAME varchar);"
       // Created the table where we can store the value of task. Similar we have created over here
       // Taken name from the constant files
       val createToDoItemTable =
            "CREATE TABLE $TABLE_TODO_ITEM (" +
                    "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                    "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                    "$COL_TODO_ID integer," +
                    "$COL_ITEM_NAME varchar," +
                    "$COL_IS_COLPLETED integer);"

        db.execSQL(createToDoTable) // Calling to pass the create query
        db.execSQL(createToDoItemTable) // Passing create query
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

      // This function is created to store the text
    fun addToDo(toDo: MainTask): Boolean {
        val db = writableDatabase // Create a db use during insertion of the values
        val cv = ContentValues() // Creating the varible to put the column inside it.
        cv.put(COL_NAME, toDo.name)
        val result = db.insert(TABLE_TODO, null, cv) // insert the text got from the user
        return result != (-1).toLong() // return when result is equal -1
    }
    // Here created to store the update result in db
    fun updateResult(toDo: MainTask) {
        val db = writableDatabase // Create db variable
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        // Update the old value with new
        db.update(TABLE_TODO,cv,"$COL_ID=?" , arrayOf(toDo.id
            .toString()))
    }
    // I have created this func to delete the items from the db when u will perform delete operation.
    fun deleteToDo(id_main: Long){
        val db = writableDatabase
        db.delete(TABLE_TODO_ITEM,"$COL_TODO_ID=?", arrayOf(id_main.toString())) // Query for deletet
        db.delete(TABLE_TODO,"$COL_ID=?", arrayOf(id_main.toString())) // Query for deletet
    }
     // To mark the task is completed.
    fun updateToDoItemCompletedStatus(todoId: Long,isCompleted: Boolean){
        val db = writableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$todoId", null)// Query

        if (queryResult.moveToFirst()) {
            do {
                val item = Listitems()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID)) // Gettin the id from the query
                item.id_main = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID)) // Gettin the from the query
                item.subtask = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME)) // Getting the name from the query
                item.isCompleted = isCompleted // Status
                updateToDoItem(item) // update
            } while (queryResult.moveToNext())
        }

        queryResult.close()
    }

      // Created this method for reading all the task from the db and return Mutablelist
    //  On refreshing this will get update into recycler view.
    fun getToDos(): MutableList<MainTask> {                    // Mutablelist task return values
        val result: MutableList<MainTask> = ArrayList()      // All the values will be store in result.
        val db = readableDatabase     //  varaible for readableDatabase
        val queryResult = db.rawQuery("SELECT * from $TABLE_TODO", null) // to read the data from the db
        // If it true so it will return some data
          if (queryResult.moveToFirst()) {
            // Use do while loop to read all the data
              do {
                val todo = MainTask() // Obj for storing data
                todo.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID)) //Gettin the id value from query  and for passing colum we r get col()
                todo.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME)) //Getting the name from query
                result.add(todo) // Mutuable list
            } while (queryResult.moveToNext())
        }
        queryResult.close() // close the query
        return result
    }
  // I have created this same function to add the item into list as well as into the database.
    fun addToDoItem(item: Listitems): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.subtask) // Storing the task name
        cv.put(COL_TODO_ID, item.id_main) // Created a value for stroing the primary key
        cv.put(COL_IS_COLPLETED, item.isCompleted)  // Storing the status.

        val result = db.insert(TABLE_TODO_ITEM, null, cv)
        return result != (-1).toLong()
    }
     //  TO update the databse
    fun updateToDoItem(item: Listitems) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.subtask)
        cv.put(COL_TODO_ID, item.id_main)
        cv.put(COL_IS_COLPLETED, item.isCompleted)

        db.update(TABLE_TODO_ITEM, cv, "$COL_ID=?", arrayOf(item.id.toString())) // Query to update db
    }

    fun deleteToDoItem(itemId : Long){
        val db = writableDatabase
        db.delete(TABLE_TODO_ITEM,"$COL_ID=?" , arrayOf(itemId.toString()))
    }
     // We have created this function to get the sub-task list in the mutable list
    fun getToDoItems(todoId: Long): MutableList<Listitems> {
        val result: MutableList<Listitems> = ArrayList() // Variable to store the data

        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$todoId", null) // Execute the query to get the data from the table.
       // Condition for getting the data
        if (queryResult.moveToFirst()) {
            do {
                val item = Listitems()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID)) //Gettin the id value from query
                item.id_main = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.subtask = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME)) //Gettin the item name from query
                item.isCompleted = queryResult.getInt(queryResult.getColumnIndex(COL_IS_COLPLETED)) == 1 ////Gettin the status value from query
                result.add(item)
            } while (queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }

}