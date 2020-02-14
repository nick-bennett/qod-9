package edu.cnm.deepdive.qod.view;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import edu.cnm.deepdive.qod.controller.QuoteController;
import edu.cnm.deepdive.qod.model.entity.Quote;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

public class QuoteRepresentation extends EntityModel<Quote> {

  public QuoteRepresentation(Quote quote, Link... links) {
    super(quote, links);
    add(
        linkTo(methodOn(QuoteController.class).get(quote.getId())).withSelfRel(),
        linkTo(methodOn(QuoteController.class).getSources(quote.getId())).withRel(LinkRelation.of("sources"))
    );
  }

  public static QuoteRepresentation wrapCollectionItem(Quote quote) {
    return new QuoteRepresentation(quote);
  }

  public static QuoteRepresentation wrapSingle(Quote quote) {
    QuoteRepresentation representation = new QuoteRepresentation(quote);
    representation.add(
        linkTo(methodOn(QuoteController.class).get()).withRel("collection")
    );
    return representation;
  }

}
