package edu.cnm.deepdive.qod.service;

import edu.cnm.deepdive.qod.model.entity.Quote;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {

  Stream<Quote> getAllByOrderByCreatedDesc();

  default Quote findOrFail(UUID id) {
    return findById(id).get();
  }

}
