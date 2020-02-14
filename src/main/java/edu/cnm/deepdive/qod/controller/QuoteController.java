package edu.cnm.deepdive.qod.controller;

import edu.cnm.deepdive.qod.model.entity.Quote;
import edu.cnm.deepdive.qod.model.entity.Source;
import edu.cnm.deepdive.qod.service.QuoteRepository;
import edu.cnm.deepdive.qod.service.SourceRepository;
import edu.cnm.deepdive.qod.view.QuoteCollectionRepresentation;
import edu.cnm.deepdive.qod.view.QuoteRepresentation;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quotes")
@ExposesResourceFor(Quote.class)
@Relation(itemRelation = "quote", collectionRelation = "quotes")
public class QuoteController {

  private final QuoteRepository quoteRepository;
  private final SourceRepository sourceRepository;

  @Autowired
  public QuoteController(QuoteRepository quoteRepository, SourceRepository sourceRepository) {
    this.quoteRepository = quoteRepository;
    this.sourceRepository = sourceRepository;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<QuoteRepresentation> post(@RequestBody Quote quote) {
    QuoteRepresentation representation =
        QuoteRepresentation.wrapSingle(quoteRepository.save(quote));
    return ResponseEntity.created(representation.getLink("self").get().toUri())
        .body(representation);
  }

  @Transactional
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public QuoteCollectionRepresentation get() {
    return new QuoteCollectionRepresentation(quoteRepository.getAllByOrderByCreatedDesc());
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public QuoteRepresentation get(@PathVariable UUID id) {
    return QuoteRepresentation.wrapSingle(quoteRepository.findOrFail(id));
  }

  @PutMapping(value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public QuoteRepresentation put(@PathVariable UUID id, @RequestBody Quote modifiedQuote) {
    Quote quote = quoteRepository.findOrFail(id);
    quote.setText(modifiedQuote.getText());
    return QuoteRepresentation.wrapSingle(quoteRepository.save(quote));
  }

  @PutMapping(value = "/{id}/text",
      consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
  public String put(@PathVariable UUID id, @RequestBody String modifiedQuote) {
    Quote quote = quoteRepository.findOrFail(id);
    quote.setText(modifiedQuote);
    quoteRepository.save(quote);
    return quote.getText();
  }

  @GetMapping(value = "/{id}/sources", produces = MediaType.APPLICATION_JSON_VALUE)
  public CollectionModel<Source> getSources(@PathVariable UUID id) {
    return new CollectionModel<>(quoteRepository.findOrFail(id).getSources());
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    quoteRepository.findById(id).ifPresent(quoteRepository::delete);
  }

  @PutMapping(value = "/{quoteId}/sources/{sourceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public QuoteRepresentation attach(@PathVariable UUID quoteId, @PathVariable UUID sourceId) {
    Quote quote = quoteRepository.findOrFail(quoteId);
    Source source = sourceRepository.findById(sourceId).get();
    if (quote.getSources().add(source)) {
      quoteRepository.save(quote);
    }
    return QuoteRepresentation.wrapSingle(quote);
  }

  @DeleteMapping(value = "/{quoteId}/sources/{sourceId}")
  public QuoteRepresentation detach(@PathVariable UUID quoteId, @PathVariable UUID sourceId) {
    Quote quote = quoteRepository.findOrFail(quoteId);
    Source source = sourceRepository.findById(sourceId).get();
    if (quote.getSources().remove(source)) {
      quoteRepository.save(quote);
    }
    return QuoteRepresentation.wrapSingle(quote);
  }

  @GetMapping(value = "/{quoteId}/sources/{sourceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean check(@PathVariable UUID quoteId, @PathVariable UUID sourceId) {
    Quote quote = quoteRepository.findOrFail(quoteId);
    Source source = sourceRepository.findById(sourceId).get();
    return quote.getSources().contains(source);
  }

}
