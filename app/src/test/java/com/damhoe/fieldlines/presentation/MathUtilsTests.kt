package com.damhoe.fieldlines.presentation

import com.damhoe.fieldlines.app.utils.MathUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MathUtilsTests {

    @Test
    fun roundsUpToNearest_shouldReturnRoundedNumbers() {
        val value: Double = 1.452
        val increment: Double = 0.3
        val expectedResult: Double = 1.5

        val result = MathUtils.roundUpToNearest(value = value, increment = increment)

        Assertions.assertEquals(expectedResult, result)
    }
}