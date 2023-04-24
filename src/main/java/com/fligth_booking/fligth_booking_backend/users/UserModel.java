package com.fligth_booking.fligth_booking_backend.users;

import com.fligth_booking.fligth_booking_backend.reservations.ReservationModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter@EqualsAndHashCode
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    @Email(message = "Email must have the correct format")
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationModel> reservation = new ArrayList<>();

    @Column
    private Boolean status;
}
