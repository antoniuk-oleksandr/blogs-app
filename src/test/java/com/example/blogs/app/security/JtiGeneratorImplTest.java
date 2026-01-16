package com.example.blogs.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class JtiGeneratorImplTest {

    private JtiGenerator jtiGenerator;

    @BeforeEach
    void setUp() {
        jtiGenerator = new JtiGeneratorImpl();
    }


    @Test
    void generateJti_shouldReturnValidUUID() {
        String jti = jtiGenerator.generateJti();

        assertThat(jti).isNotNull();
        assertThatCode(() -> UUID.fromString(jti))
                .doesNotThrowAnyException();
    }

    @Test
    void generateJti_shouldReturnUniqueValues() {
        String jti1 = jtiGenerator.generateJti();
        String jti2 = jtiGenerator.generateJti();

        assertThat(jti1).isNotEqualTo(jti2);
    }
}
