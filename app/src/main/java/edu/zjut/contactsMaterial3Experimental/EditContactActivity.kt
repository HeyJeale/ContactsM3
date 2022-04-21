package edu.zjut.contactsMaterial3Experimental

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.zjut.contactsMaterial3Experimental.beans.Contacts

class EditContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)
        val dataBundle = intent.getBundleExtra("Bundle")
    }
}