package com.stepanov

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification

@DirtiesContext
@SpringBootTest
abstract class AbstractIntegrationTests extends Specification {
}
