package board;

import Base.BaseTest;
import com.github.javafaker.Faker;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrganizationsTest extends BaseTest {

    private String fakeName;
    private String fakeWebsite;
    private static String teamId1;
    private static String teamId2;
    private static String teamId3;
    private static String teamId4;
    private static String teamId5;
    private static String teamId6;


    @BeforeEach
    public void beforeEach() {

        fakeName = faker.name().name();
        fakeWebsite = faker.internet().url();
    }

    @Test
    @Order(1)
    public void createFirstTeam() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "My FIRST team")
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo("My FIRST team");

        teamId1 = json.getString("id");

        deleteTeam(teamId1);
    }

    @Test
    @Order(2)
    public void checkIfNameIsAtLeastThree() {

        JSONObject team = new JSONObject();
        team.put("name", fakeName);
        team.put("website", fakeWebsite);

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "My SECOND team")
                .body(team.toString())
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo("My SECOND team");
        Assertions.assertThat(json.getString("name").length()).isGreaterThanOrEqualTo(3);

        teamId2 = json.getString("id");

        deleteTeam(teamId2);
    }

     @Test
     @Order(3)
    public void checkIfNameIsUnique() {

        JSONObject team = new JSONObject();
        team.put("name", fakeName);
        team.put("website", fakeWebsite);

        Response response1 = given()
                .spec(reqSpec)
                .queryParam("displayName", "My THIRD team")
                .body(team.toString())
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json1 = response1.jsonPath();
        Assertions.assertThat(json1.getString("displayName")).isEqualTo("My THIRD team");

        Response response2 = given()
                .spec(reqSpec)
                .queryParam("displayName", "My FOURTH team")
                .body(team.toString())
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json2 = response2.jsonPath();
        Assertions.assertThat(json2.getString("displayName")).isEqualTo("My FOURTH team");
        Assertions.assertThat(json1.getString("name")).isNotEqualTo(json2.getString("name"));

        teamId3 = json1.getString("id");
        teamId4 = json2.getString("id");

         deleteTeam(teamId3);
         deleteTeam(teamId4);
    }

    @Test
    @Order(4)
    public void checkWebsite() {

        JSONObject team = new JSONObject();
        team.put("name", fakeName);
        team.put("website", fakeWebsite);

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "My FIFTH team")
                .body(team.toString())
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo("My FIFTH team");
        if (!json.getString("website").startsWith("http://")) {
            Assertions.assertThat(json.getString("website")).startsWith("https://");
        } else {
            Assertions.assertThat(json.getString("website")).startsWith("http://");
        }

        teamId5 = json.getString("id");

        deleteTeam(teamId5);
    }

    @Test
    @Order(5)
    public void checkNameIsLowerCase() {

        JSONObject team = new JSONObject();
        team.put("name", fakeName);
        team.put("website", fakeWebsite);

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "My SIXTH team")
                .body(team.toString())
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo("My SIXTH team");
        Assertions.assertThat(json.getString("name")).isLowerCase();
        teamId6 = json.getString("id");

        deleteTeam(teamId6);
    }

    public void deleteTeam(String teamID) {
        given()
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + "/" + ORGANIZATIONS + "/" + teamID)
                .then()
                .statusCode(200);
    }
}
