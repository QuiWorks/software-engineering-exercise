# LeMans Software Engineering Exercise

Required dependencies:
- Kotlin
- Gradle
- PostgreSQL

Getting Started:
1. Run the SQL script located at `resources/db/createDatabase.sql`.
   1. The script will create the `exercise` database and user required for this application.
   2. The script will also create the database tables and grant permissions to the `exercise` user.
2. Run the `main` function located at `ep/com/lemans/exercise/SoftwareEngineeringExerciseApplication.kt`.
   1. *Note:* The application will delete and re-insert the example data at start up. *See* `JdbcDataService#reload`
   2. Navigate to `http://localhost:8080/`.
