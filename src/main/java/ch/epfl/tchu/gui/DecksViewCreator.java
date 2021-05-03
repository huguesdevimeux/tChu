package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Creates Decks. Non instantiable
 *
 * @author Hugues Devimeux (327282)
 * @author Luca Mouchel (324748)
 */
class DecksViewCreator {

    public static final String CLASS_BACKGROUND = "background";
    public static final String CLASS_FOREGROUND = "foreground";
    public static final String CLASS_GAUGED = "gauged";
    public static final String CLASS_CARD = "card";
    public static final String CLASS_COLOR_NEUTRAL = "NEUTRAL";
    public static final String CLASS_COUNT = "count";
    public static final String CLASS_FILLED = "filled";
    public static final String CLASS_INSIDE = "inside";
    public static final String CLASS_OUTSIDE = "outside";
    public static final String CLASS_TRAIN_IMAGE = "train-image";

    public static final String ID_TICKETS = "tickets";
    public static final String ID_CARD_PANE = "card-pane";
    public static final String ID_HAND_PANE = "hand-pane";

    public static final String STYLE_COLORS = "colors.css";
    public static final String STYLE_DECKS = "decks.css";
    public static final List<String> CLASSES_COLOR =
            Color.ALL.stream().map(Objects::toString).collect(Collectors.toList());

    // Not instantiable.
    private DecksViewCreator() {}

    public static Node createCardsView(
            ObservableGameState observableGameState,
            ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandler,
            ObjectProperty<ActionHandlers.DrawCardHandler> drawCardHandler) {

        // Tickets pile.
        // Button group
		VBox cardsView = new VBox(itemPileWithGauge(observableGameState, "Billets"));
        cardsView.setId(ID_CARD_PANE);
        cardsView.getStylesheets().addAll(STYLE_DECKS, STYLE_COLORS);

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            StackPane displayedCard = individualCard();
			cardsView.getChildren().add(displayedCard);
			observableGameState
                    .faceUpCard(slot)
                    .addListener(
                            (observable, oldValue, newValue) -> {
                                String newColor =
                                        newValue.color() == null
                                                ? CLASS_COLOR_NEUTRAL
                                                : newValue.color().name();
                                // Remove any Color css attribute and replace by the new color.
                                displayedCard.getStyleClass().filtered(CLASSES_COLOR::contains);
                                displayedCard.getStyleClass().add(newColor);
                            });
        }
        cardsView.getChildren().add(itemPileWithGauge(observableGameState, "Cartes"));
        return cardsView;
    }

    private static Button itemPileWithGauge(
            ObservableGameState observableGameState, String itemName) {
        Button ticketPile = new Button(itemName);
        ticketPile.getStyleClass().add(CLASS_GAUGED);

        Rectangle backgroundButtonGraphic = new Rectangle(50, 5);
        backgroundButtonGraphic.getStyleClass().add(CLASS_BACKGROUND);
        Rectangle foregroundButtonGraphic = new Rectangle(50, 5);
        foregroundButtonGraphic.getStyleClass().add(CLASS_FOREGROUND);
        foregroundButtonGraphic
                .widthProperty()
                .bind(observableGameState.percentageTickets().divide(2));

        ticketPile.setGraphic(new Group(backgroundButtonGraphic, foregroundButtonGraphic));
        return ticketPile;
    }

    public static Node createHandView(ObservableGameState gameState) {

        // TICKETS HAND VIEW
        ListView<Ticket> ticketsListView = new ListView<>();
        ticketsListView.setItems(gameState.playersTicketsList());
        ticketsListView.setId(ID_TICKETS);

        //
        HBox cardsHandPanel = new HBox();
        cardsHandPanel.setId(ID_HAND_PANE);

        for (Card card : Card.ALL) {
            StackPane cardOfHand = individualCard();
            String color = card.color() == null ? CLASS_COLOR_NEUTRAL : card.color().name();
            cardOfHand.getStyleClass().addAll(color);
            // Count.
            Text count = new Text();
            count.textProperty().bind(gameState.playersNumberOfCards(card).asString());
            count.getStyleClass().add(CLASS_COUNT);
            cardOfHand.getChildren().add(count);

            cardsHandPanel.getChildren().add(cardOfHand);
        }

        HBox handView = new HBox(ticketsListView, cardsHandPanel);
        handView.getStylesheets().addAll(STYLE_DECKS, STYLE_COLORS);
        return handView;
    }

    private static StackPane individualCard() {
        // Inner icon of cards. Sorted in an exterior fashion.
        Rectangle inner1 = new Rectangle(60, 90);
        inner1.getStyleClass().add(CLASS_OUTSIDE);
        Rectangle inner2 = new Rectangle(40, 70);
        inner2.getStyleClass().addAll(CLASS_FILLED, CLASS_INSIDE);
        Rectangle inner3 = new Rectangle(40, 70);
        inner3.getStyleClass().add(CLASS_TRAIN_IMAGE);

        // Outer layout.
        StackPane cardOfHand = new StackPane();
        cardOfHand.getChildren().addAll(inner1, inner2, inner3);
        cardOfHand.getStyleClass(). add(CLASS_CARD);
        return cardOfHand;
    }
}
