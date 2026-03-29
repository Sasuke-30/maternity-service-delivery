package com.example.maternity.data.repositories;

import com.example.maternity.domain.Ward;

import java.util.Collection;

public interface WardRepository {
    void addWard(Ward ward);

    Ward findByCode(String wardCode);

    Collection<Ward> findAll();
}

