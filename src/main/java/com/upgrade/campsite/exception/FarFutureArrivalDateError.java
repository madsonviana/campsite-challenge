package com.upgrade.campsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Arrival date can't be before next month")
public class FarFutureArrivalDateError extends Exception {
}
