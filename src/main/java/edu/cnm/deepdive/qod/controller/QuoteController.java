package edu.cnm.deepdive.qod.controller;

import static edu.cnm.deepdive.qod.controller.QuoteController.BASE_PATH;

import edu.cnm.deepdive.qod.model.entity.Quote;
import edu.cnm.deepdive.qod.model.entity.Source;
import edu.cnm.deepdive.qod.service.QuoteRepository;
import edu.cnm.deepdive.qod.service.SourceRepository;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.ExposesResourceFor;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(BASE_PATH)
@ExposesResourceFor(Quote.class)
public class QuoteController {

  static final String ITEM_NAME = "quote";
  static final String COLLECTION_NAME = ITEM_NAME + "s";
  static final String BASE_PATH = "/" + COLLECTION_NAME;
  private final String ID_VARIABLE = "id";
  private final String ID_PATH = "/{" + ID_VARIABLE + "}";
  private final String TEXT_PATH = ID_PATH + "/text";
  private final String SOURCE_ATTACHMENT_PATH =
      ID_PATH + "/" + SourceController.COLLECTION_NAME + "/{sourceId}";

  private final QuoteRepository quoteRepository;
  private final SourceRepository sourceRepository;

  @Autowired
  public QuoteController(QuoteRepository quoteRepository, SourceRepository sourceRepository) {
    this.quoteRepository = quoteRepository;
    this.sourceRepository = sourceRepository;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Quote> post(@RequestBody Quote quote) {
    quoteRepository.save(quote);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .build(quote.getId());
    return ResponseEntity.created(location).body(quote);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Quote> get() {
    return quoteRepository.getAllByOrderByCreatedDesc();
  }

  @GetMapping(value = ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public Quote get(@PathVariable UUID id) {
    return quoteRepository.findOrFail(id);
  }

  @DeleteMapping(value = ID_PATH)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    quoteRepository.findById(id).ifPresent(quoteRepository::delete);
  }

  @PutMapping(value = ID_PATH,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Quote put(@PathVariable UUID id, @RequestBody Quote modifiedQuote) {
    Quote quote = quoteRepository.findOrFail(id);
    quote.setText(modifiedQuote.getText());
    return quoteRepository.save(quote);
  }

  @PutMapping(value = TEXT_PATH,
      consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
  public String put(@PathVariable UUID id, @RequestBody String modifiedQuote) {
    Quote quote = quoteRepository.findOrFail(id);
    quote.setText(modifiedQuote);
    quoteRepository.save(quote);
    return quote.getText();
  }

  @PutMapping(value = SOURCE_ATTACHMENT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public Quote attach(@PathVariable(ID_VARIABLE) UUID quoteId, @PathVariable UUID sourceId) {
    Quote quote = quoteRepository.findOrFail(quoteId);
    Source source = sourceRepository.findOrFail(sourceId);
    if (quote.getSources().add(source)) {
      quoteRepository.save(quote);
    }
    return quote;
  }

  @DeleteMapping(value = SOURCE_ATTACHMENT_PATH)
  public Quote detach(@PathVariable(ID_VARIABLE) UUID quoteId, @PathVariable UUID sourceId) {
    Quote quote = quoteRepository.findOrFail(quoteId);
    Source source = sourceRepository.findOrFail(sourceId);
    if (quote.getSources().remove(source)) {
      quoteRepository.save(quote);
    }
    return quote;
  }

}
