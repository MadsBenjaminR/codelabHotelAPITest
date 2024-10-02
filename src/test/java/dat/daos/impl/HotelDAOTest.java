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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HotelDAOTest {

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
    void read() {


        HotelDTO expectedHotel = new HotelDTO(1,"APA", "Shibuya", Hotel.HotelType.STANDARD);


        HotelDTO actualHotel = hotelDAO.read(h1.getId());


        assertEquals(expectedHotel, actualHotel );
    }

    @Test
    void createHotel() {

        HotelDTO newHotel = new HotelDTO("Tivoli", "Copenhagen", Hotel.HotelType.LUXURY);

        hotelDAO.create(newHotel);

        HotelDTO expectedHotel = hotelDAO.read(4);

        HotelDTO actualHotel = new HotelDTO(4, "Tivoli", "Copenhagen", Hotel.HotelType.LUXURY);

                assertEquals(expectedHotel, actualHotel);

    }



}