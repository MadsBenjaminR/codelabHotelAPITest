package dat.daos.impl;

import dat.dtos.HotelDTO;
import dat.entities.Hotel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;


public class Populator {
    private static HotelDAO hotelDAO;
    private static EntityManagerFactory emf;

    Populator(HotelDAO hotelDAO, EntityManagerFactory emf) {
        this.hotelDAO = hotelDAO;
        this.emf = emf;
    }

    public List<HotelDTO> populate3Hotels() {

        HotelDTO h1, h2, h3;

        // Populate data and test objects
        h1 = new HotelDTO("APA", "Shibuya", Hotel.HotelType.STANDARD);
        h2 = new HotelDTO("Capsuel", "Osaka", Hotel.HotelType.BUDGET);
        h3 = new HotelDTO("Appartments", "Tokyo", Hotel.HotelType.LUXURY);

        // Save the data
        h1 = hotelDAO.create(h1);
        h2 = hotelDAO.create(h2);
        h3 = hotelDAO.create(h3);

        return new ArrayList<>(List.of(h1, h2, h3));

    }

    public void clean() {
        try (
                EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Hotel").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE hotel_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
