package com.example.Tauheed.todolist.DTO

// This class is created to define the data structure.

class MainTask {

    var id: Long = -1 // Storing the id
    var name = "" // storing the task name
    var createdAt = "" // subtask storing
    var items: MutableList<Listitems> = ArrayList() // create array

}