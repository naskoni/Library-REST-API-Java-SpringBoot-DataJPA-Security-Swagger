package com.naskoni.library.dao;

import com.naskoni.library.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientDao extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {}
