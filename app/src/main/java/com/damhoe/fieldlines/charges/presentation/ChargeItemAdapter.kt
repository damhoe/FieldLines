package com.damhoe.fieldlines.charges.presentation

import android.graphics.PointF
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.damhoe.fieldlines.charges.domain.PointCharge
import com.damhoe.fieldlines.charges.presentation.ChargeItemAdapter.ChargeViewHolder
import com.example.fieldlines.R
import com.example.fieldlines.databinding.ItemChargeBinding
import java.util.Locale

class ChargeItemAdapter(private val onItemCLick: (position: Int, charge: PointCharge) -> Unit) :
    RecyclerView.Adapter<ChargeViewHolder>() {

        private val mCharges: MutableList<PointCharge> = mutableListOf()

    fun updateCharges(newCharges: List<PointCharge>) {
        val callback = ChargeItemDiffCallback(mCharges, newCharges)
        DiffUtil.calculateDiff(callback).run {
            // Update data
            mCharges.run {
                clear()
                addAll(newCharges.map { it.copy() })
            }

            // Display updated data
            dispatchUpdatesTo(this@ChargeItemAdapter)
        }
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemChargeBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_charge, parent, false)
        return ChargeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChargeViewHolder, position: Int) {
        // General binding
        val currentCharge = mCharges[position]
        holder.bindCharge(currentCharge)
        holder.binding.buttonMore.setOnClickListener {
            onItemCLick(position, currentCharge)
        }
    }

    override fun getItemCount() = mCharges.size

    class ChargeViewHolder(val binding: ItemChargeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindCharge(charge: PointCharge) {
            bindAmount(charge.charge)
            bindPosition(charge.position)
        }

        private fun bindAmount(amount: Double) = binding.run {
            val context = itemView.context
            val chargeTemplate = context.getString(R.string.charge_item_q_text)
            textCharge.text = String.format(Locale.US, chargeTemplate, amount)
            chargeImage.setImageResource(getChargeImageResourceId(amount))
        }

        private fun bindPosition(position: PointF) {
            val context = itemView.context
            val positionTemplate = context.getString(R.string.charge_item_position_text)
            binding.textPosition.text =
                String.format(Locale.US, positionTemplate, position.x, position.y)
        }

        private fun getChargeImageResourceId(amount: Double) =
            if (amount >= 0) {
                R.drawable.img_pos_charge
            } else {
                R.drawable.img_neg_charge
            }
    }

    class ItemDecoration(private val mDividerHeight: Int) :
        RecyclerView.ItemDecoration() {

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.bottom = mDividerHeight
            }
    }

    internal class ChargeItemDiffCallback(
        private val oldCharges: List<PointCharge>,
        private val newCharges: List<PointCharge>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldCharges.size

        override fun getNewListSize() = newCharges.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldCharges[oldItemPosition].id == newCharges[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            /**
             * PointCharges are data class objects, which allows simple comparison.
             */
            oldCharges[oldItemPosition] == newCharges[newItemPosition]
    }
}
