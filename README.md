# MTB-PID-TO-KAFKA

This project is a part of DNPM Pipeline for importing the genetic data from pathology gene sequencer (Archer) to the MTB (Onkostar).

## Usage
 -  Receive the pids using via. REST API
 -  Adjust the pids in the sql statement
 -  Receive oder-id for each pids from database
 -  Produce the kafka messages for each oder-id

## Deployment
 - Adjust the sample.env by providing values to the ENV variables
 - Adjust the sql_queries according to your database schema
 - Run the container
