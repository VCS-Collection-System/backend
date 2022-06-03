@vaccinationCardNextStep

Feature: Vaccination Card Next Step Logic
    Scenario Outline: Check for invalid shot number
        When there is a Vaccine Document
        And there is a Document Review Outcome
        And the shot number is <shotNumber>
        Then valid shot number is <validShotNumber>
        Scenarios:
        | shotNumber | validShotNumber |
        | -1         | false           |
        |  0         | false           |
        |  1         | true            |
        |  2         | true            |
        |  3         | true            |

    Scenario Outline: Check for automatic approval qualification
        When there are Vaccine Documents
        And there is an Automatic Approval Inquiry
        And proof type is <proofType>
        Then qualified for automatic approval is <qualified>
        Scenarios:
        | proofType   | qualified |
        | CDC         | true      |
        | DIVOC       | true      |
        | SMARTHEALTH | false     |
        | GREEN_PASS  | false     |
        | OTHER       | false     |
        | EUGC        | true      |

    Scenario Outline: Check vaccination status for single shot brands
        When there is a List of Vaccine Documents
        And there is a Document Review Outcome
        And the vaccine brand is <brand>
        And the vaccine administration date was <adminDateDaysAgo> days ago
        And the shot number is <shotNumber>
        Then vaccination status is <fullyVaccinated>
        Scenarios:
          | brand             | adminDateDaysAgo | shotNumber | fullyVaccinated |
          | JANSSEN           | 1                | 1          | false           |
          | JANSSEN           | 13               | 1          | false           |
          | JANSSEN           | 14               | 1          | true            |
          | JANSSEN           | 15               | 1          | true            |
          | JOHNSON           | 1                | 1          | false           |
          | JOHNSON           | 13               | 1          | false           |
          | JOHNSON           | 14               | 1          | true            |
          | JOHNSON           | 15               | 1          | true            |
          | COMIRNATY         | 1                | 1          | false           |
          | COMIRNATY         | 13               | 1          | false           |
          | COMIRNATY         | 14               | 1          | false           |
          | COMIRNATY         | 15               | 1          | false           |
          | COVAXIN           | 1                | 1          | false           |
          | COVAXIN           | 13               | 1          | false           |
          | COVISHIELD        | 1                | 1          | false           |
          | COVISHIELD        | 13               | 1          | false           |
          | COVOVAX           | 1                | 1          | false           |
          | COVOVAX           | 13               | 1          | false           |
          | OXFORD            | 1                | 1          | false           |
          | OXFORD            | 13               | 1          | false           |
          | OXFORD            | 14               | 1          | false           |
          | OXFORD            | 15               | 1          | false           |
          | SINOPHARM         | 1                | 1          | false           |
          | SINOPHARM         | 13               | 1          | false           |
          | SINOPHARM         | 14               | 1          | false           |
          | SINOPHARM         | 15               | 1          | false           |
          | SPIKEVAX          | 1                | 1          | false           |
          | SPIKEVAX          | 13               | 1          | false           |
          | SPIKEVAX          | 14               | 1          | false           |
          | SPIKEVAX          | 15               | 1          | false           |
          | SPUTNIK           | 1                | 1          | false           |
          | SPUTNIK           | 13               | 1          | false           |
          | SPUTNIK           | 14               | 1          | false           |
          | SPUTNIK           | 15               | 1          | false           |
          | SINOVAC_CORONAVAC | 1                | 1          | false           |
          | SINOVAC_CORONAVAC | 13               | 1          | false           |
          | SINOVAC_CORONAVAC | 14               | 1          | false           |
          | SINOVAC_CORONAVAC | 15               | 1          | false           |
          | VAXZEVRIA         | 1                | 1          | false           |
          | VAXZEVRIA         | 13               | 1          | false           |
          | VAXZEVRIA         | 14               | 1          | false           |
          | VAXZEVRIA         | 15               | 1          | false           |
    
    

    Scenario Outline: Check vaccination status for double shot brands
        When there is a List of Vaccine Documents
        And there is a Document Review Outcome
        And the vaccine brand for two shot vaccine is <brand>
        And the vaccine administration date for second shot was <adminDateDaysAgo> days ago
        And the shot number for two shot vaccine is <shotNumber>
        Then vaccination status is <fullyVaccinated>
        Scenarios:
          | brand             | adminDateDaysAgo | shotNumber | fullyVaccinated |
          | PFIZER            | 1                | 1          | false           |
          | PFIZER            | 13               | 1          | false           |
          | PFIZER            | 14               | 1          | false           |
          | PFIZER            | 15               | 1          | false           |
          | PFIZER            | 1                | 2          | false           |
          | PFIZER            | 13               | 2          | false           |
          | PFIZER            | 14               | 2          | true            |
          | PFIZER            | 15               | 2          | true            |
          | ASTRAZENECA       | 1                | 1          | false           |
          | ASTRAZENECA       | 13               | 1          | false           |
          | ASTRAZENECA       | 14               | 1          | false           |
          | ASTRAZENECA       | 15               | 1          | false           |
          | ASTRAZENECA       | 1                | 2          | false           |
          | ASTRAZENECA       | 13               | 2          | false           |
          | ASTRAZENECA       | 14               | 2          | true            |
          | ASTRAZENECA       | 15               | 2          | true            |
          | MODERNA           | 1                | 1          | false           |
          | MODERNA           | 13               | 1          | false           |
          | MODERNA           | 14               | 1          | false           |
          | MODERNA           | 15               | 1          | false           |
          | MODERNA           | 1                | 2          | false           |
          | MODERNA           | 13               | 2          | false           |
          | MODERNA           | 14               | 2          | true            |
          | MODERNA           | 15               | 2          | true            |
          | NOVAVAX           | 1                | 1          | false           |
          | NOVAVAX           | 13               | 1          | false           |
          | NOVAVAX           | 14               | 1          | false           |
          | NOVAVAX           | 15               | 1          | false           |
          | NOVAVAX           | 1                | 2          | false           |
          | NOVAVAX           | 13               | 2          | false           |
          | NOVAVAX           | 14               | 2          | true            |
          | NOVAVAX           | 15               | 2          | true            |
          | SINOPHARM         | 1                | 2          | false           |
          | SINOPHARM         | 13               | 1          | false           |
          | SINOPHARM         | 15               | 2          | true            |
          | SPIKEVAX          | 1                | 1          | false           |
          | SPIKEVAX          | 13               | 1          | false           |
          | SPIKEVAX          | 15               | 1          | false           |
          | SPIKEVAX          | 15               | 2          | true            |
          | SPUTNIK           | 1                | 2          | false           |
          | SPUTNIK           | 14               | 1          | false           |
          | SPUTNIK           | 15               | 2          | true            |
          | SPUTNIK           | 13               | 1          | false           |
          | SINOVAC_CORONAVAC | 1                | 1          | false           |
          | SINOVAC_CORONAVAC | 13               | 1          | false           |
          | SINOVAC_CORONAVAC | 15               | 1          | false           |
          | SINOVAC_CORONAVAC | 15               | 2          | true            |
          | VAXZEVRIA         | 1                | 1          | false           |
          | VAXZEVRIA         | 13               | 1          | false           |
          | VAXZEVRIA         | 15               | 1          | false           |
          | VAXZEVRIA         | 15               | 2          | true            |

    Scenario Outline: Check for meets automatic approval threshold
         When there is a Vaccination Proof Automatic Approval Response
         And there is an Automatic Approval Threshold
         And there is a confidence score of <confidenceScore>
         Then threshold met is <thresholdMet>
         Scenarios:
                | confidenceScore | thresholdMet |
                | -1              | false        | 
                |  1              | false        | 
                |  44             | false        | 
                |  45             | true         | 
                |  46             | true         | 
                |  80             | true         |
                |  100            | true         |
