package e2e;

import Base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoveCardsBetweenListsTest1 extends BaseTest {

    private static String boardId;
    private static String firstListId;
    private static String secondListId;
    private static String cardId;

    @Test
    @Order(1)
    public void createBoard() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "My E2E board")
                .queryParam("defaultLists", false)
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
                .spec(reqSpec)
                .queryParam("name", "My FIRST list")
                .queryParam("idBoard", boardId)
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
                .spec(reqSpec)
                .queryParam("name", "My SECOND list")
                .queryParam("idBoard", boardId)
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

        Response response = given()
                .spec(reqSpec)
                .queryParam("idList", firstListId)
                .queryParam("name", "My CARD")
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
                .spec(reqSpec)
                .queryParam("idList", secondListId)
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
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + "/" + BOARDS + "/" + boardId)
                .then()
                .statusCode(200);
    }
}
