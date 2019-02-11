package com.upgrade.campsite.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation implements Serializable {

    private static final long serialVersionUID = -5021462203890304661L;

    @Id
    private String id;

    private String fullName;

    private String email;

    private LocalDate arrivalDate;

    private LocalDate departureDate;

    private Status status;

}
