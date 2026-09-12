package com.naskoni.library.repository;

import com.naskoni.library.entity.Book;
import com.naskoni.library.entity.Client;
import com.naskoni.library.entity.Lend;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LendRepository extends JpaRepository<Lend, Long>, JpaSpecificationExecutor<Lend> {

  Optional<Lend> findByBook(Book book);

  Optional<Lend> findByClient(Client client);
}
