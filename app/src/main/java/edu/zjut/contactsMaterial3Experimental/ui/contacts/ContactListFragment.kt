package edu.zjut.contactsMaterial3Experimental.ui.contacts


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.*
import edu.zjut.contactsMaterial3Experimental.Helpers.InitialPinyinHelper
import edu.zjut.contactsMaterial3Experimental.Helpers.NameUtils
import edu.zjut.contactsMaterial3Experimental.beans.Contacts
import edu.zjut.contactsMaterial3Experimental.databinding.FragmentContactsListBinding
import edu.zjut.contactsMaterial3Experimental.ui.contacts.adapter.ContactAdapter
import net.sourceforge.pinyin4j.PinyinHelper

class ContactListFragment : Fragment() {
    private var _binding: FragmentContactsListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsListBinding.inflate(inflater, container, false)

        val addNewContact:FloatingActionButton=binding.addNewContact

        val contactsRecyclerView=binding.contactsList
        val layoutManager=LinearLayoutManager(context)
        contactsRecyclerView.layoutManager=layoutManager
        contactsRecyclerView.adapter=ContactAdapter(initSampleContacts())
        addNewContact.setOnClickListener {
            view?.let { it1 -> Snackbar.make(it1,"Clicked", Snackbar.LENGTH_SHORT).show() }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initSampleContacts(): List<Contacts>{
        val contactsList=ArrayList<Contacts>()

        for (i in 1..200){
            val nameTemp: String = NameUtils.randomName(simple = true, len=(2..3).random())
            contactsList.add(Contacts(
                name=nameTemp,
                wiredPhoneNumber = null,
                mobilePhoneNumber = null,
                workEmail = null,
                groupBelonging = null,
                nameInPinyin = InitialPinyinHelper.chineseToSpell(nameTemp)))
        }

        contactsList.sortBy { Contacts -> Contacts.nameInPinyin }
        return contactsList
    }
}