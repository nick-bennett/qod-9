package edu.cnm.deepdive.qod.service;

import edu.cnm.deepdive.qod.model.entity.Quote;
import edu.cnm.deepdive.qod.model.entity.Source;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {

  Iterable<Quote> getAllByOrderByCreatedDesc();

  default Quote findOrFail(UUID id) {
    return findById(id).get();
  }

  default Quote attach(Quote quote, Source source) {
    if (quote.getSources().add(source)) {
      save(quote);
    }
    return quote;
  }

  default Quote detach(Quote quote, Source source) {
    if (quote.getSources().remove(source)) {
      save(quote);
    }
    return quote;
  }

}
