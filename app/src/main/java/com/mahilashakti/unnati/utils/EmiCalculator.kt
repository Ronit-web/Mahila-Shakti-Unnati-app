package com.mahilashakti.unnati.utils

import kotlin.math.pow

object EmiCalculator {
    /**
     * Calculates the Equated Monthly Installment (EMI).
     * Formula: EMI = P × R × (1+R)^N / ((1+R)^N - 1)
     * 
     * @param principal The total loan amount (P)
     * @param annualInterestRate The annual interest rate in percentage
     * @param durationMonths The loan duration in months (N)
     * @return The monthly EMI amount
     */
    fun calculateEMI(principal: Double, annualInterestRate: Double, durationMonths: Int): Double {
        if (principal <= 0 || durationMonths <= 0) return 0.0
        if (annualInterestRate == 0.0) return principal / durationMonths

        val monthlyInterestRate = annualInterestRate / 12 / 100 // (R)
        val mathPower = (1 + monthlyInterestRate).pow(durationMonths.toDouble())
        
        val emi = principal * monthlyInterestRate * mathPower / (mathPower - 1)
        return emi
    }
}
