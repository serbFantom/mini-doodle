# Mini-Doodle: A Simple Scheduling Application

Mini-Doodle is a lightweight, REST-based scheduling application designed to help users manage their availability and schedule meetings. It provides a simple yet effective way to coordinate schedules by allowing users to define their available time slots and book meetings with other participants.

## Features

*   **User Management**: Create and list users.
*   **Time Slot Management**: Define personal availability by creating, deleting, and modifying time slots.
*   **Meeting Scheduling**: Schedule meetings with multiple participants.
*   **Calendar View**: Get a consolidated view of your schedule, including both time slots and meetings.

## Getting Started

### Prerequisites

*   Java 24 or later
*   Maven 3.6 or later
*   Docker 

### Building and Running Locally

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/serbFantom/mini-doodle
    cd mini-doodle
    ```

2.  **Build the project using Maven:**
    ```bash
    ./mvnw clean install
    ```

3.  **Run the application:**
    ```bash
    java -jar target/mini-doodle-*.jar
    ```
    The application will start on port 8080.

### Building and Running with Docker

1.  **Build the Docker image:**
    ```bash
    docker build -t mini-doodle .
    ```

2.  **Run the application using Docker Compose:**
    ```bash
    docker-compose up
    ```
    This will start the application and make it accessible on port 8080.

## API Endpoints

The API is available at `/api`.

### Time Slot Management

*   `POST /api/users/{userId}/slots`: Creates a new time slot for a user.
    *   **Query Parameters**: `start` (Instant), `end` (Instant), `busy` (boolean, optional, default: false)
*   `DELETE /api/users/{userId}/slots/{slotId}`: Deletes a specific time slot.
*   `PUT /api/users/{userId}/slots/{slotId}`: Modifies an existing time slot.
    *   **Query Parameters**: `start` (Instant, optional), `end` (Instant, optional), `busy` (boolean, optional)

### Calendar View

*   `GET /api/users/{userId}/calendar`: Returns a consolidated view of a user's calendar, including time slots and meetings.
    *   **Query Parameters**: `from` (Instant), `to` (Instant)
