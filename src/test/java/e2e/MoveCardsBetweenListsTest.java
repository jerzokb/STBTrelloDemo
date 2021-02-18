package e2e;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoveCardsBetweenListsTest {
    private static final String BASE_URL = "https://api.trello.com/1";
    private static final String BOARDS = "boards";
    private static final String LISTS = "lists";
    private static final String CARDS = "cards";

    // trello api key:
    private static final String KEY = "285f1f75c294e1191edbcc255efb6e88";
    // trello token:
    private static final String TOKEN = "44e6816ce1355a6fa40e5eb9e8da9770fd39623f21f188139a0df4f86a4c422b";

    private static String boardId;
    private static String firstListId;
    private static String secondListId;
    private static String cardId;

    @Test
    @Order(1)
    public void createBoard() {
        Response response = given()
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .queryParam("name", "My E2E board")
                .queryParam("defaultLists", false)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URL + "/" + BOARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("My E2E board");
        boardId = json.getString("id");
    }

    @Test
    @Order(2)
    public void createFirstList() {
        Response response = given()
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .queryParam("name", "My FIRST list")
                .queryParam("idBoard", boardId)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URL + "/" + LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("My FIRST list");
        firstListId = json.getString("id");
    }

    @Test
    @Order(3)
    public void createSecondList() {
        Response response = given()
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .queryParam("name", "My SECOND list")
                .queryParam("idBoard", boardId)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URL + "/" + LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("My SECOND list");
        secondListId = json.getString("id");
    }

    @Test
    @Order(4)
    public void addCardToFirstList() {
        // https://api.trello.com/1/cards idList
        Response response = given()
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .queryParam("idList", firstListId)
                .queryParam("name", "My CARD")
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URL + "/" + CARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("My CARD");
        cardId = json.getString("id");
    }

    @Test
    @Order(5)
    public void moveCardToSecondList() {
        Response response = given()
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .queryParam("idList", secondListId)
                .contentType(ContentType.JSON)
                .when()
                .put(BASE_URL + "/" + CARDS + "/" + cardId)
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("idList")).isEqualTo(secondListId);
    }

    @Test
    @Order(6)
    public void deleteBoard() {
        given()
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .delete(BASE_URL + "/" + BOARDS + "/" + boardId)
                .then()
                .statusCode(200);
    }

}
