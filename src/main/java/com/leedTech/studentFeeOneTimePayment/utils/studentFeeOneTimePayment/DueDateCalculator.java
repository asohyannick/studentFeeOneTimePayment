package com.leedTech.studentFeeOneTimePayment.utils.studentFeeOneTimePayment;
import java.time.DayOfWeek;
import java.time.LocalDate;
public class DueDateCalculator {

	private DueDateCalculator() {}
	
	private static final int PAYMENT_PERIOD_DAYS = 90;
	
	public static LocalDate calculateNextDueDate(LocalDate paymentDate) {
		LocalDate dueDate = paymentDate.plusDays(PAYMENT_PERIOD_DAYS);
		return adjustForWeekend(dueDate);
	}
	
	private static LocalDate adjustForWeekend(LocalDate date) {
		if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
			return date.plusDays(2);
		}
		if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
			return date.plusDays(1);
		}
		return date;
	}
}