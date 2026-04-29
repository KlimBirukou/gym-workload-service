# Integration Testing

## Execution
To run the integration tests, execute the following command in the project root:

```bash
./gradlew clean integrationTest
```

## Results
Execution results are available in two ways:

1. **Console**:  A brief status output for each scenario (passed/failed). 
2. **HTML Report** (Recommended): Detailed information for each Gherkin step and error logs (if it happens).

* **Report path:** `build/reports/cucumber/report.html`

## Technical Details
* **Stack**: JUnit 5, Cucumber, Testcontainers.
* **Infrastructure**: Tests automatically spin up isolated containers with MongoDB and Kafka.
* **Cleanup**:Database state is reset before each scenario via`DatabaseCleanupHook`.
