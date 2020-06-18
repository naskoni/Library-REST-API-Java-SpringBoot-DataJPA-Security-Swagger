package com.naskoni.library.dao;

import com.naskoni.library.entity.Book;
import com.naskoni.library.entity.Client;
import com.naskoni.library.entity.Lend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LendDao extends JpaRepository<Lend, Long>, JpaSpecificationExecutor<Lend> {

  Optional<Lend> findByBook(Book book);

  Optional<Lend> findByClient(Client client);
}
