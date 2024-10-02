package dat.daos.impl;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.dtos.HotelDTO;
import dat.entities.Hotel;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HotelAPITest {

    private static String BASE_URL = "http://localhost:7070/api/v1";
    private static Javalin app;
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static HotelDAO hotelDAO = new HotelDAO(emf);
    private static Populator populator = new Populator(hotelDAO, emf);

    private static HotelDTO h1, h2, h3;

    private static List<HotelDTO> hotels;

    @BeforeAll
    static void init() {
        app = ApplicationConfig.startServer(7070);
        HibernateConfig.setTest(true);
    }

    @BeforeEach
    void setUp() {
        hotels = populator.populate3Hotels();
        h1 = hotels.get(0);
        h2 = hotels.get(1);
        h3 = hotels.get(2);
    }

    @AfterEach
    void tearDown() {
        // Delete all data from database
        populator.clean();
    }

    @AfterAll
    void closeDown() {
        ApplicationConfig.stopServer(app);
    }


    @Test
    void testReadHotel() {
        HotelDTO hotel =
                given()
                        .when()
                        .get(BASE_URL + "/hotels/" + h1.getId())
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract()
                        .as(HotelDTO.class);

        assertThat(hotel, equalTo(h1));
    }

    @Test
    void testCreateHotelAPI() {

        HotelDTO newHotel = new HotelDTO ("Rundetorn", "Stroget", Hotel.HotelType.BUDGET);


        HotelDTO createdHotel =
                given()
                        .contentType("application/json")
                        .body(newHotel)
                        .when()
                        .post(BASE_URL + "/hotels/")
                        .then()
                        .log().all()
                        .statusCode(201)
                        .extract()
                        .as(HotelDTO.class);

        // Assert: Verify that the hotel ID is assigned (non-zero)
        assertNotNull(createdHotel.getId(), "Hotel ID should be assigned and not null");

        // Assert: Verify the hotel details
        assertEquals("Rundetorn", createdHotel.getHotelName());
        assertEquals("Stroget", createdHotel.getHotelAddress());
        assertEquals(Hotel.HotelType.BUDGET, createdHotel.getHotelType());


    }


}
