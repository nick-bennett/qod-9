package edu.cnm.deepdive.qod.view;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import edu.cnm.deepdive.qod.controller.QuoteController;
import edu.cnm.deepdive.qod.model.entity.Quote;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

public class QuoteCollectionRepresentation extends CollectionModel<QuoteRepresentation> {

  public QuoteCollectionRepresentation(
      Iterable<QuoteRepresentation> content, Link... links) {
    super(content, links);
    add(linkTo(methodOn(QuoteController.class).get()).withSelfRel());
  }

  public QuoteCollectionRepresentation(
      Stream<Quote> content, Link... links) {
    this(content.map(QuoteRepresentation::wrapCollectionItem).collect(Collectors.toList()), links);
  }

}
