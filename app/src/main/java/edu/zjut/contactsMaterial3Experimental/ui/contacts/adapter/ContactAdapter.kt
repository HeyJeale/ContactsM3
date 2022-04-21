package edu.zjut.contactsMaterial3Experimental.ui.contacts.adapter

import android.content.Context
import android.graphics.Typeface
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.zjut.contactsMaterial3Experimental.Helpers.FontHelper
import edu.zjut.contactsMaterial3Experimental.R
import edu.zjut.contactsMaterial3Experimental.beans.Contacts


class ContactAdapter(contactsList: List<Contacts>) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private val mList = ArrayList(contactsList)
    private val colorList = arrayListOf(
        R.color.dark_peach,
        R.color.hard_pink,
        R.color.immature,
        R.color.light_cyan,
        R.color.overcast,
        R.color.peach,
        R.color.soft_pink,
        R.color.summer_teal
    )
    private val iconTextTypeface: Typeface by lazy {
        FontHelper(mContext).getAliFontLight()
    }
    private lateinit var mContext: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactName: TextView = view.findViewById(R.id.contact_name)
        val holderView: ConstraintLayout = view.findViewById(R.id.holder_view)
        val iconView: ImageView = view.findViewById(R.id.iconView)
        val iconText: TextView = view.findViewById(R.id.iconText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.contact_holder, parent, false)
        mContext=view.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact:Contacts = mList[position]
        holder.contactName.text = contact.name
        holder.holderView.isClickable=true
        holder.holderView.setOnClickListener {
            Snackbar.make(it, holder.contactName.text.toString().plus("被点击"),Snackbar.LENGTH_SHORT).show()
            val vibratorManager = mContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        }
        holder.iconText.text=holder.contactName.text[0].toString()
        holder.iconText.typeface= iconTextTypeface
        val circle = AppCompatResources.getDrawable(mContext, R.drawable.circle)
        circle?.setTint(AppCompatResources.getColorStateList(mContext, colorList[(0 until colorList.size).random()]).defaultColor)
        holder.iconView.setImageDrawable(circle)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}