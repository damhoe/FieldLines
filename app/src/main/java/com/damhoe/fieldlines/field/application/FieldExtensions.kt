package com.damhoe.fieldlines.field.application

import com.damhoe.fieldlines.charges.domain.PointCharge
import com.damhoe.fieldlines.field.domain.Field
import kotlin.math.abs

fun Field.maxPointCharge(): PointCharge = pointCharges.maxBy { abs(it.charge) }
