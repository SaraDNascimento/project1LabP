Project 1: The Video Club
This repository contains the implementation of a simplified video rental management system, developed as part of the Programming Labs course in the 1st year of the Bachelor's Degree in Computer Science at the Faculty of Sciences, University of Lisbon (2023/2024).

Project Overview

The goal of this project was to create a system that simulates the operations of a video rental store, managing the movie catalog, rentals, returns, and calculating daily revenue and profit. The management of inventory and transactions is handled through CSV files, which contain information about the movies and the operations performed during a day.


Features Implemented

Movie Class: Represents a movie, containing information such as title, release year, available quantity, active rentals, rental price, and an identifying code.
VideoClub Class: Manages the movie catalog, allowing for rental and return operations, generating reports on revenue, profit, and updating inventory at the end of the day.
CSV Manipulation: Reading and writing CSV files to manage the video club's state.
Filtering and Searching: Methods to filter movies by release year, price, and availability.


Code Structure

Movie.java: Implements the Movie class, responsible for representing a movie and its basic operations.
VideoClub.java: Implements the VideoClub class, responsible for managing the video club and its transactions.
Unit Tests: Includes a JUnit test class to verify the correctness of the system.
How to Run
The project can be executed directly from the RunProject1.java class, which includes examples of using the implemented features and comparing the results with the expected output.
