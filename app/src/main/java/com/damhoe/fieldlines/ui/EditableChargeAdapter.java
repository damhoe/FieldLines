package com.damhoe.fieldlines.ui;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damhoe.fieldlines.domain.Charge;
import com.damhoe.fieldlines.domain.ChargeList;
import com.example.fieldlines.R;

import java.util.Locale;

class EditableChargeAdapter extends RecyclerView.Adapter<EditableChargeAdapter.ChargeViewHolder> {

   NotifyItemClickListener listener;
   ChargeList charges;

   EditableChargeAdapter(NotifyItemClickListener listener) {
      this.listener = listener;
   }

   public void updateCharges(ChargeList charges) {
      this.charges = charges;
      notifyDataSetChanged();
   }

   @NonNull
   @Override
   public EditableChargeAdapter.ChargeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_editable_charge, parent, false);
      return new ChargeViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull EditableChargeAdapter.ChargeViewHolder holder, int position) {
      Charge currentCharge = charges.get(position);
      holder.textPosition.setText(String.format(Locale.US,
              "(%d,\t %d)", currentCharge.Position.x, currentCharge.Position.y)
      );
      holder.textCharge.setText(String.format(Locale.US, "%.2f", currentCharge.Amount));

      holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            listener.notifyChargeClicked(holder.getAdapterPosition(), currentCharge);
         }
      });
   }

   @Override
   public int getItemCount() {
      return charges.size();
   }

   public static class ChargeViewHolder extends RecyclerView.ViewHolder {
      TextView textPosition;
      TextView textCharge;

      public ChargeViewHolder(@NonNull View itemView) {
         super(itemView);
         textCharge = itemView.findViewById(R.id.text_charge);
         textPosition = itemView.findViewById(R.id.text_position);
      }
   }

   public static class ItemDecoration extends RecyclerView.ItemDecoration {
      @Override
      public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
         outRect.bottom = 16;
         outRect.top = 16;
      }
   }
}
