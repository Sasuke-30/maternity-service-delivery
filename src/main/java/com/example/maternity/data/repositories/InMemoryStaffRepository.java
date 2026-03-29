package com.example.maternity.data.repositories;

import com.example.maternity.domain.Staff;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryStaffRepository implements StaffRepository {
    private final Map<Long, Staff> staffById = new HashMap<>();

    @Override
    public void addStaff(Staff staff) {
        staffById.put(staff.getId(), staff);
    }

    @Override
    public Staff findById(long id) {
        return staffById.get(id);
    }

    @Override
    public boolean existsById(long id) {
        return staffById.containsKey(id);
    }

    @Override
    public Collection<Staff> findAll() {
        return Collections.unmodifiableCollection(staffById.values());
    }
}

