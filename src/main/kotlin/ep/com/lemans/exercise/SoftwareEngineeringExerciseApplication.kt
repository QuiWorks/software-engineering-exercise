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

@RestController
class Controller(@Autowired val dataService: DataService) {

	init {
		dataService.refresh()
	}

	@GetMapping("/")
	fun fetch(): List<Product>
	{
		return dataService.fetch()
	}

}
