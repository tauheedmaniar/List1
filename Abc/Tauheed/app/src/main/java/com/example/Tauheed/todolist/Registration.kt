package com.example.Tauheed.todolist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_registration.*

class Registration : AppCompatActivity() {
  /// To create an information
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarid)
        title="List Tracker"
        setContentView(R.layout.activity_registration)
            text.setOnClickListener{
                login()

            }

    }
             //Created an login to pass the if else statement.
                private fun login(){

                    if(userReg.text.toString().isEmpty()){
                        userReg.error = "Please enter the user name"
                        userReg.requestFocus()
                    }
                    if(!Patterns.EMAIL_ADDRESS.matcher(emailId.text.toString()).matches()){
                        emailId.error="Please enter the valid mail"
                        emailId.requestFocus()
                    }  else
                    {  //next activity
                        val intent = Intent(this,DashboardAc::class.java)
                        startActivity(intent)

                    }


                    }

        }






