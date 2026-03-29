package com.example.maternity.data.repositories;

import com.example.maternity.domain.Staff;

import java.util.Collection;

public interface StaffRepository {
    void addStaff(Staff staff);

    Staff findById(long id);

    boolean existsById(long id);

    Collection<Staff> findAll();
}

