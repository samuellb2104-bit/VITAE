# VITAE

Desktop application for donation management between donors and foundations. Developed as a first-semester university final project.

## Description

VITAE is a platform that connects donors with foundations. Foundations can publish their needs and updates, and donors can browse that content and make donations directly through the application. It includes an internal messaging system so both types of users can communicate with each other.

## Features

**For donors**
- Registration and login
- Feed of publications from all foundations with filtering by title and category
- Make donations to foundations directly from a publication
- Donation history
- Messaging with foundations
- Browse active needs posted by foundations

**For foundations**
- Registration and login
- Create, view and delete publications with title, description, category and image
- Manage needs with a target amount
- Messaging with donors
- Publications summary in the side panel

## Technologies

- Java with Swing for the desktop interface
- SQL Server as the relational database
- JDBC for database connection and queries
- Layered architecture: models, DAO, services and interfaces

## Database

Main tables:

- Users: stores donors and foundations with their user type
- Publications: posts created by foundations
- Donations: record of each donation with donor, receiver and amount
- Needs: needs declared by foundations with a target amount
- Messages: messages between donors and foundations

## Requirements

- Java 17 or higher
- SQL Server with a local instance available
- SQL Server JDBC driver (mssql-jdbc)
- Create the VITAE_BD database and run the table creation script before launching the application

## Connection setup

In `src/dao/ConexionSQL.java` update the URL, username and password values to match your local SQL Server instance.

## Team

Project developed by a two-person team. In colaboration with Samuel Gonzáles Pérez. [Go to his page](https://github.com/SamuSgp). My role covered the development of the graphical interface with Swing, the backend logic, and the design and implementation of the SQL Server database.
