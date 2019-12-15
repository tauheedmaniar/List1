package com.example.Tauheed.todolist

// Here we are storing all the columns name in this filr

// Creating all the relevant id
const val DB_NAME = "ToDoList" // Name of the database
const val DB_VERSION = 1 // Version of the database.
const val TABLE_TODO = "ToDo"
const val COL_ID = "id" // for id
const val COL_CREATED_AT = "createdAt"
const val COL_NAME = "name"


const val TABLE_TODO_ITEM = "ToDoItem"
const val COL_TODO_ID = "toDoId"
const val COL_ITEM_NAME = "itemName"
const val COL_IS_COLPLETED = "isCompleted"



const val INTENT_TODO_ID = "TodoId"
const val INTENT_TODO_NAME = "TodoName"