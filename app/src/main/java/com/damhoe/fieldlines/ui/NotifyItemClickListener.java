package com.damhoe.fieldlines.ui;

import com.damhoe.fieldlines.domain.Charge;

interface NotifyItemClickListener {
    void notifyChargeClicked(int position, Charge charge);
}
