package com.upgrade.campsite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDTO {

    private Date arrivalDate;

    private Date departureDate;

}
