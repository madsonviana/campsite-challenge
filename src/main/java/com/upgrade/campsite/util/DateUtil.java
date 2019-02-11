package com.upgrade.campsite.util;

import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateUtil {

    public static List<LocalDate> getDaysFromRange(LocalDate initialDate, LocalDate finalDate) {
        long daysBetween = ChronoUnit.DAYS.between(initialDate, finalDate);
        return Stream.iterate(initialDate, date -> date.plusDays(1)).limit(daysBetween).collect(Collectors.toList());
    }

}
