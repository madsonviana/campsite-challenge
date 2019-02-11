package com.upgrade.campsite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatesDTO {

    private LocalDate arrivalDate;

    private LocalDate departureDate;

}
