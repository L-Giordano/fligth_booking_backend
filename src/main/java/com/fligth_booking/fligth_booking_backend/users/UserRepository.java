package com.fligth_booking.fligth_booking_backend.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    public abstract List<UserModel> findAllByStatus(Boolean status);
    public abstract Optional<UserModel> findByIdAndStatus(Long id, Boolean status);
}
