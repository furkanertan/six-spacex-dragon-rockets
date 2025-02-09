package spacex.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spacex.domain.Rocket;

import static org.junit.jupiter.api.Assertions.*;

class RocketRepositoryTest {

    private RocketRepository rocketRepository;
    private Rocket rocket;

    @BeforeEach
    void setUp() {
        rocketRepository = new RocketRepository();
        rocket = new Rocket("Falcon 9");
    }

    @Test
    void should_AddAndRetrieveRocket() {
        // Given & When
        rocketRepository.addRocket(rocket);
        Rocket createdRocket = rocketRepository.getRocket("Falcon 9");

        // Then
        assertNotNull(createdRocket);
        assertEquals("Falcon 9", createdRocket.getName());
    }

    @Test
    void should_ReturnNull_IfRocketDoesNotExist() {
        // Given & When & Then
        assertNull(rocketRepository.getRocket("Starship"));
    }
}