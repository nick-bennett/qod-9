package edu.cnm.deepdive.qod.controller;

import static edu.cnm.deepdive.qod.controller.SourceController.BASE_PATH;

import edu.cnm.deepdive.qod.model.entity.Quote;
import edu.cnm.deepdive.qod.model.entity.Source;
import edu.cnm.deepdive.qod.service.QuoteRepository;
import edu.cnm.deepdive.qod.service.SourceRepository;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping(BASE_PATH)
@ExposesResourceFor(Source.class)
public class SourceController {

  static final String ITEM_NAME = "source";
  static final String COLLECTION_NAME = ITEM_NAME + "s";
  static final String BASE_PATH = "/" + COLLECTION_NAME;
  private final String ID_VARIABLE = "id";
  private final String ID_PATH = "/{" + ID_VARIABLE + "}";
  private final String NAME_PATH = ID_PATH + "/name";
  private final String QUOTE_ATTACHMENT_PATH =
      ID_PATH + "/" + QuoteController.COLLECTION_NAME + "/{quoteId}";

  private final SourceRepository sourceRepository;
  private final QuoteRepository quoteRepository;

  @Autowired
  public SourceController(SourceRepository sourceRepository,
      QuoteRepository quoteRepository) {
    this.sourceRepository = sourceRepository;
    this.quoteRepository = quoteRepository;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Source post(@RequestBody Source source) {
    return sourceRepository.save(source);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Source> get() {
    return sourceRepository.findAllByOrderByName();
  }

  @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Source get(@PathVariable UUID id) {
    return sourceRepository.findOrFail(id);
  }

  @DeleteMapping(value = "{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    sourceRepository.findById(id).ifPresent((source) -> {
      Set<Quote> quotes = source.getQuotes();
      quotes.forEach((quote) -> quote.getSources().remove(source));
      quotes.clear();
      sourceRepository.delete(source);
    });
  }

  @PutMapping(value = "{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Source put(@PathVariable UUID id, @RequestBody Source updated) {
    Source source = sourceRepository.findOrFail(id);
    source.setName(updated.getName());
    return sourceRepository.save(source);
  }

  @PutMapping(value = NAME_PATH,
      consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
  public String put(@PathVariable UUID id, @RequestBody String updated) {
    Source source = sourceRepository.findOrFail(id);
    source.setName(updated);
    sourceRepository.save(source);
    return source.getName();
  }

  @PutMapping(value = QUOTE_ATTACHMENT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public Source attach(@PathVariable(ID_VARIABLE) UUID sourceId, @PathVariable UUID quoteId) {
    Source source = sourceRepository.findOrFail(sourceId);
    Quote quote = quoteRepository.findOrFail(quoteId);
    if (quote.getSources().add(source)) {
      source.getQuotes().add(quote);
      quoteRepository.save(quote);
    }
    return source;
  }

  @DeleteMapping(value = QUOTE_ATTACHMENT_PATH)
  public Source detach(@PathVariable(ID_VARIABLE) UUID sourceId, @PathVariable UUID quoteId) {
    Source source = sourceRepository.findOrFail(sourceId);
    Quote quote = quoteRepository.findOrFail(quoteId);
    if (quote.getSources().remove(source)) {
      source.getQuotes().remove(quote);
      quoteRepository.save(quote);
    }
    return source;
  }

}
