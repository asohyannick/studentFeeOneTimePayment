package com.leedTech.studentFeeOneTimePayment.utils.studentFeeOneTimePayment;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IncentiveCalculator {

        private IncentiveCalculator() {}

		private static final BigDecimal TIER_1_MAX  = new BigDecimal("100000");
		private static final BigDecimal TIER_2_MAX  = new BigDecimal("500000");
		private static final BigDecimal RATE_TIER_1 = new BigDecimal("0.01");
		private static final BigDecimal RATE_TIER_2 = new BigDecimal("0.03");
		private static final BigDecimal RATE_TIER_3 = new BigDecimal("0.05");
		
		public static BigDecimal getIncentiveRate(BigDecimal amount) {
			if (amount.compareTo(TIER_1_MAX) < 0) {
				return RATE_TIER_1;
			}
			if (amount.compareTo(TIER_2_MAX) < 0) {
				return RATE_TIER_2;
			}
			return RATE_TIER_3;
		}
		
		public static BigDecimal calculateIncentiveAmount(BigDecimal paymentAmount) {
			BigDecimal rate = getIncentiveRate(paymentAmount);
			return paymentAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
		}
		
		public static BigDecimal calculateNewBalance(
				BigDecimal previousBalance,
				BigDecimal paymentAmount,
				BigDecimal incentiveAmount
		) {
			return previousBalance
					       .subtract(paymentAmount)
					       .subtract(incentiveAmount)
					       .setScale(2, RoundingMode.HALF_UP);
		}
}
