package ep.com.lemans.exercise

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class SoftwareEngineeringExerciseApplication

fun main(args: Array<String>) {
    runApplication<SoftwareEngineeringExerciseApplication>(*args)
}

/**
 * Single controller class for this application.
 */
@RestController
class Controller(@Autowired val databaseService: DatabaseService) {

    /**
     * Refresh data on application start up.
     */
    init {
        databaseService.refresh()
    }

    /**
     * End point for fetching all data in the database.
     */
    @GetMapping("/")
    fun fetch(): Set<Product> {
        return databaseService.fetch()
    }

}
