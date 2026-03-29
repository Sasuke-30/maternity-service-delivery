package com.example.maternity.data.repositories;

import com.example.maternity.domain.Ward;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryWardRepository implements WardRepository {
    private final Map<String, Ward> wardsByCode = new HashMap<>();

    @Override
    public void addWard(Ward ward) {
        wardsByCode.put(ward.getWardCode(), ward);
    }

    @Override
    public Ward findByCode(String wardCode) {
        return wardsByCode.get(wardCode);
    }

    @Override
    public Collection<Ward> findAll() {
        return Collections.unmodifiableCollection(wardsByCode.values());
    }
}

