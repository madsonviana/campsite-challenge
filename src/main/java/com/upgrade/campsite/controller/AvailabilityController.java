package com.upgrade.campsite.controller;

import com.upgrade.campsite.service.AvailabilityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@RestController
@RequestMapping("/availabilities")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @GetMapping("/")
    public Flux<LocalDate> checkAvailability(@RequestParam String initialDate, @RequestParam(required = false) String finalDate) {
        LocalDate initialParam = LocalDate.parse(initialDate);
        LocalDate finalParam;

        if (StringUtils.isBlank(finalDate)) {
            finalParam = initialParam.plusMonths(1);
        } else {
            finalParam = LocalDate.parse(finalDate);
        }

        return availabilityService.checkAvailability(initialParam, finalParam);
    }
}
