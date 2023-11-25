# MovieRama Application

## Overview

MovieRama is a social sharing platform that allows users to share their favorite movies. Each movie entry includes a title, description, the submitting user's name, the date it was added, and counts of likes and dislikes. Users can interact with the platform by liking or hating movies, and can sort the list of movies based on these interactions or the date added.

## System Requirements

- **Backend:** Java JDK 17, Apache Maven 3.6.3
- **Frontend:** Node v20.9.0, Angular CLI: 16.2.9

## Installation and Running the Application

The application is dockerized for ease of setup and consistency across different environments. To run the application:

1. Ensure Docker is installed on your system.
2. Clone the repository to your local machine and navigate to the `movierama/docker` folder.
3. Run the following command to build and start the application:
```bash
docker-compose up --build
```

## Usage

After the application services start:

1. Open a web browser.
2. Visit `http://localhost:4200` to access the MovieRama application.
3. From here, you can view movies, sign up for an account, or log in if you already have an account.


# API Documentation

This document outlines the usage of the API endpoints for the authentication, movie management, user management, and voting system.

## Authentication

### POST /authenticate
- **Purpose**: Authenticate a user and retrieve a JWT token.
- **Request Body**: Expects an `AuthenticationRequestDTO` with `username` and `password`.
- **Success Response**: `200 OK` with `AuthenticationResponseDTO` containing the JWT token.
- **Error Response**: `401 Unauthorized` if authentication fails.

## Movies

### POST /api/movies
- **Purpose**: Add a new movie.
- **Request Body**: Expects a `MovieDTO` with movie details.
- **Success Response**: `201 Created` with the added `MovieDTO`.
- **Error Response**: `400 Bad Request` if the request is invalid.

### GET /api/movies
- **Purpose**: List movies with pagination and sorting options.
- **Query Parameters**:
  - `page`: Page number (default is 0).
  - `size`: Number of items per page (default is 10).
  - `sort`: Sorting criteria (default is `addedDateDesc`).
- **Success Response**: `200 OK` with a `Page<MovieDTO>` containing the list of movies.
- **Error Response**: `400 Bad Request` if the parameters are invalid.

## Users

### GET /users/{id}
- **Purpose**: Retrieve user information by user ID.
- **Path Variables**:
  - `id`: The ID of the user.
- **Success Response**: `200 OK` with the `User` details.
- **Error Response**: `404 Not Found` if the user does not exist.

### POST /users/register
- **Purpose**: Register a new user.
- **Request Body**: Expects a `UserRegistrationDTO` with the user's registration details.
- **Success Response**: `200 OK` with the created `User` object.
- **Error Response**: `400 Bad Request` if the request is invalid.

## Votes

### POST /votes/vote
- **Purpose**: Submit a vote for a movie.
- **Query Parameters**:
  - `movieId`: The ID of the movie being voted on.
  - `voteType`: The type of vote (e.g., `LIKE` or `DISLIKE`).
- **Success Response**: `200 OK` with the `VoteDTO`.
- **Error Response**: `400 Bad Request` if the parameters are invalid or `404 Not Found` if the movie doesn't exist.

### DELETE /votes/vote
- **Purpose**: Remove a vote for a movie.
- **Query Parameters**:
  - `movieId`: The ID of the movie for which the vote should be removed.
- **Success Response**: `200 OK` with no content.
- **Error Response**: `400 Bad Request` if the parameters are invalid or `404 Not Found` if the vote or movie doesn't exist.


## Troubleshooting

If you encounter any issues with running the application using Docker, ensure the following:

- Docker daemon is running on your system.
- Ports required by the application are not being used by another service.
- You have the necessary permissions to run Docker commands on your system.
