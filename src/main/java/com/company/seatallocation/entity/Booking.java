package com.company.seatallocation.entity;

import com.company.seatallocation.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "bookings")
@CompoundIndexes({
        @CompoundIndex(name = "seat_date_unique", def = "{'seatId': 1, 'bookingDate': 1}", unique = true),
        @CompoundIndex(name = "user_date_unique", def = "{'userId': 1, 'bookingDate': 1}", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    private String id;

    private String userId;

    private String seatId;

    private LocalDate bookingDate;

    private BookingStatus status;

    @CreatedDate
    private Instant createdAt;
}

