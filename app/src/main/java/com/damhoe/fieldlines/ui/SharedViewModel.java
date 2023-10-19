package com.damhoe.fieldlines.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.damhoe.fieldlines.domain.Charge;
import com.damhoe.fieldlines.domain.ChargeList;

public class SharedViewModel extends ViewModel {

   private final MutableLiveData<ChargeList> charges = new MutableLiveData<>();

   LiveData<ChargeList> getCharges() {
      return charges;
   }

   public SharedViewModel() {
      // Ignore.
      if (charges.getValue() == null) {
         charges.setValue(new ChargeList());
      }
   }

   protected void addCharge(Charge charge) {
      ChargeList mCharges = charges.getValue();
      if (mCharges != null) {
         mCharges.add(charge);
      }
      charges.postValue(mCharges);
   }

   protected void addCharge(int position, Charge charge) {
      ChargeList mCharges = charges.getValue();
      if (mCharges != null) {
         mCharges.add(position, charge);
      }
      charges.postValue(mCharges);
   }

   protected Charge removeCharge(int position) {
      ChargeList mCharges = charges.getValue();
      Charge removed = null;
      if (mCharges != null) {
         removed = mCharges.remove(position);
         charges.postValue(mCharges);
      }
      return removed;
   }

   protected void initializeMonopole() {
      charges.postValue(ChargeList.Factory.createMonopole());
   }

   protected void initializeDipole() {
      charges.postValue(ChargeList.Factory.createDipole());
   }

   protected void initializeQuadropole() {
      charges.postValue(ChargeList.Factory.createQuadropole());
   }

    protected void updateCharge(int position, int x, int y, double amount) {
       ChargeList mCharges = charges.getValue();
       if (mCharges != null) {
          Charge toEdit = mCharges.get(position);
          toEdit.position.x = x;
          toEdit.position.y = y;
          toEdit.amount = amount;
          mCharges.set(position, toEdit);
       }
       charges.postValue(mCharges);
    }
}
