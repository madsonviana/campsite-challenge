package com.upgrade.campsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Departure date in the past")
public class PastDepartureDateError extends Exception {
}
