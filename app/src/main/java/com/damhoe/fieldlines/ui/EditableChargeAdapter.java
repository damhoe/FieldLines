package com.damhoe.fieldlines.ui;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.damhoe.fieldlines.domain.Charge;
import com.damhoe.fieldlines.domain.ChargeList;
import com.example.fieldlines.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class EditableChargeAdapter extends RecyclerView.Adapter<EditableChargeAdapter.ChargeViewHolder> {

   NotifyItemClickListener listener;
   List<Charge> mCharges = new ArrayList<>();

   EditableChargeAdapter(NotifyItemClickListener listener) {
      this.listener = listener;
   }

   public void updateCharges(ChargeList newCharges) {
      if (this.mCharges == null) {
         setCharges(newCharges);
         return;
      }

      DiffUtil.Callback callback = new EditableChargeDiffCallback(this.mCharges, newCharges);
      DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
      setCharges(newCharges);
      result.dispatchUpdatesTo(this);
   }

   private void setCharges(ChargeList charges) {
      mCharges = charges.deepCopy();
   }

   public Charge removeItem(int position) {
      return mCharges.remove(position);
   }

   @Override
   public long getItemId(int position) {
      return (long) position;
   }

   @NonNull
   @Override
   public EditableChargeAdapter.ChargeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_editable_charge, parent, false);
      return new ChargeViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull EditableChargeAdapter.ChargeViewHolder holder, int position) {
      // General binding
      Charge currentCharge = mCharges.get(position);
      holder.bindCharge(currentCharge);
      holder.buttonMore.setOnClickListener(view ->
              listener.notifyChargeClicked(holder.getAdapterPosition(), currentCharge));
   }

   @Override
   public int getItemCount() {
      return mCharges.size();
   }

   protected static class ChargeViewHolder extends RecyclerView.ViewHolder {
      TextView textPosition;
      TextView textCharge;
      ShapeableImageView chargeImage;
      MaterialButton buttonMore;

      protected  ChargeViewHolder(@NonNull View itemView) {
         super(itemView);
         textCharge = itemView.findViewById(R.id.text_charge);
         textPosition = itemView.findViewById(R.id.text_position);
         buttonMore = itemView.findViewById(R.id.button_more);
         chargeImage = itemView.findViewById(R.id.charge_image);
      }

      protected void bindCharge(Charge charge) {
         bindAmount(charge.amount);
         bindPosition(charge.position);
      }

      protected void bindAmount(double amount) {
         Context context = itemView.getContext();
         String chargeTemplate = context.getString(R.string.charge_item_q_text);
         textCharge.setText(String.format(Locale.US, chargeTemplate, amount));
         chargeImage.setImageResource(getChargeImageResourceId(amount));
      }

      protected void bindPosition(Point position) {
         Context context = itemView.getContext();
         String positionTemplate = context.getString(R.string.charge_item_position_text);
         textPosition.setText(String.format(Locale.US, positionTemplate, position.x, position.y));
      }

      private int getChargeImageResourceId(double amount) {
         return amount >= 0 ? R.drawable.image_positive_charge : R.drawable.image_negative_charge;
      }
   }

   static class ItemDecoration extends RecyclerView.ItemDecoration {
      int mDividerHeight;

      ItemDecoration(int dividerHeight) {
         mDividerHeight = dividerHeight;
      }

      @Override
      public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                 @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
         outRect.bottom = mDividerHeight;
      }
   }

   public static class EditableChargeDiffCallback extends DiffUtil.Callback {
      private final List<Charge> oldCharges;
      private final List<Charge> newCharges;

      EditableChargeDiffCallback(List<Charge> oldCharges, List<Charge> newCharges) {
         this.oldCharges = oldCharges;
         this.newCharges = newCharges;
      }

      @Override
      public int getOldListSize() {
         return oldCharges.size();
      }

      @Override
      public int getNewListSize() {
         return newCharges.size();
      }

      @Override
      public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
         return oldCharges.get(oldItemPosition).getUuid() == newCharges.get(newItemPosition).getUuid();
      }

      @Override
      public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
         return oldCharges.get(oldItemPosition).amount == (newCharges.get(newItemPosition)).amount &&
                 oldCharges.get(oldItemPosition).position.x == (newCharges.get(newItemPosition)).position.x &&
                 oldCharges.get(oldItemPosition).position.y == (newCharges.get(newItemPosition)).position.y;
      }
   }
}
