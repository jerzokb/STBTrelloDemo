package board;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class BoardTest {
    // trello api key:
    private final String key = "285f1f75c294e1191edbcc255efb6e88";
    // trello token:
    private final String token = "44e6816ce1355a6fa40e5eb9e8da9770fd39623f21f188139a0df4f86a4c422b";

    @Test
    public void createBoard() {
        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "My FIRST board")
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertEquals("My FIRST board", json.get("name"));

        String boardId = json.get("id");
        given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("https://api.trello.com/1/boards/" + boardId)
                .then()
                .statusCode(200);
    }

    @Test
    public void createBoardWithEmptyBoardName() {
        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "")
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(400)
                .extract()
                .response();
    }

    @Test
    public void creatBoardWithoutDefaultLists() {
        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "Board withput default lists")
                .queryParam("defaultLists", false)
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertEquals("Board withput default lists", json.get("name"));

        String boardId = json.get("id");
        Response responseGet = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .get("https://api.trello.com/1/boards/" + boardId + "/lists")
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath jsonGet = responseGet.jsonPath();
        List<String> idList = jsonGet.getList("id");
        Assertions.assertEquals(0, idList.size());

        given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("https://api.trello.com/1/boards/" + boardId)
                .then()
                .statusCode(200);
    }

    @Test
    public void createBoardWithDefualtLists() {
        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("name", "Board with default lists")
                .queryParam("defaultLists", true)
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertEquals("Board with default lists", json.get("name"));

        String boardId = json.get("id");
        Response responseGet = given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .get("https://api.trello.com/1/boards/" + boardId + "/lists")
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath jsonGet = responseGet.jsonPath();
        List<String> idList = jsonGet.getList("id");
        Assertions.assertEquals(3, idList.size());
        List<String> namesGet = jsonGet.getList("name");
        Assertions.assertEquals("Do zrobienia", namesGet.get(0));
        Assertions.assertEquals("W trakcie", namesGet.get(1));
        Assertions.assertEquals("Zrobione", namesGet.get(2));

        given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("https://api.trello.com/1/boards/" + boardId)
                .then()
                .statusCode(200);
    }
}
