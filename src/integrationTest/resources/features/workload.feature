@integration-test
Feature: Trainer workload complex lifecycle

  Background:
    Given Trainer "John.Doe" has no workload records

  Scenario: 1. Verify 404 when no document exists
    When Workload is requested for trainer "John.Doe"
    Then Response status should be 404

  Scenario: 2. Create document on ADD event
    When Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-01-10" and duration 60
    Then Trainer "John.Doe" workload should match JSON:
    """
    {
      "username": "John.Doe",
      "years": [
        {
          "year": 2026,
          "months": [{ "month": 1, "training_summary_duration": 60 }]
        }
      ]
    }
    """

  Scenario: 3. Increase workload for existing month and year
    When Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-01-10" and duration 60
    And Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-01-15" and duration 40
    Then Trainer "John.Doe" workload should match JSON:
    """
    {
      "username": "John.Doe",
      "years": [{
          "year": 2026,
          "months": [{ "month": 1, "training_summary_duration": 100 }]
      }]
    }
    """

  Scenario: 4. Create new month in existing year
    When Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-01-10" and duration 60
    And Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-02-20" and duration 30
    Then Trainer "John.Doe" workload should match JSON:
    """
    {
      "username": "John.Doe",
      "years": [{
          "year": 2026,
          "months": [
            { "month": 1, "training_summary_duration": 60 },
            { "month": 2, "training_summary_duration": 30 }
          ]
      }]
    }
    """

  Scenario: 5. Create new year in existing document
    When Kafka "ADD" event is sent for trainer "John.Doe" with date "2025-12-10" and duration 50
    And Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-01-10" and duration 70
    Then Trainer "John.Doe" workload should match JSON:
    """
    {
      "username": "John.Doe",
      "years": [
        { "year": 2025, "months": [{ "month": 12, "training_summary_duration": 50 }] },
        { "year": 2026, "months": [{ "month": 1, "training_summary_duration": 70 }] }
      ]
    }
    """

  Scenario: 6. Decrease workload
    When Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-05-10" and duration 100
    And Kafka "DELETE" event is sent for trainer "John.Doe" with date "2026-05-10" and duration 40
    Then Trainer "John.Doe" workload should match JSON:
    """
    {
      "username": "John.Doe",
      "years": [{
          "year": 2026,
          "months": [{ "month": 5, "training_summary_duration": 60 }]
      }]
    }
    """

  Scenario: 7. Month disappears when duration reaches zero (but year remains)
    When Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-01-10" and duration 60
    And Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-02-10" and duration 30
    And Kafka "DELETE" event is sent for trainer "John.Doe" with date "2026-01-10" and duration 60
    Then Trainer "John.Doe" workload should match JSON:
    """
    {
      "username": "John.Doe",
      "years": [{
          "year": 2026,
          "months": [{ "month": 2, "training_summary_duration": 30 }]
      }]
    }
    """

  Scenario: 8. Year disappears when last month is removed (but document remains)
    When Kafka "ADD" event is sent for trainer "John.Doe" with date "2025-12-10" and duration 60
    And Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-01-10" and duration 30
    And Kafka "DELETE" event is sent for trainer "John.Doe" with date "2025-12-10" and duration 60
    Then Trainer "John.Doe" workload should match JSON:
    """
    {
      "username": "John.Doe",
      "years": [{
          "year": 2026,
          "months": [{ "month": 1, "training_summary_duration": 30 }]
      }]
    }
    """

  Scenario: 9. Entire document is deleted when all workloads are zero
    When Kafka "ADD" event is sent for trainer "John.Doe" with date "2026-01-10" and duration 60
    And Kafka "DELETE" event is sent for trainer "John.Doe" with date "2026-01-10" and duration 60
    Then Workload for trainer "John.Doe" should eventually be not found
