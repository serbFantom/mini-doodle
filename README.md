# Mini-Doodle: A Simple Scheduling Application

Mini-Doodle is a lightweight, REST-based scheduling application designed to help users manage their availability and schedule meetings. It provides a simple yet effective way to coordinate schedules by allowing users to define their available time slots and book meetings with other participants.

## Features

*   **Time Slot Management**: Define personal availability by creating, deleting, and modifying time slots.
*   **Meeting Scheduling**: Schedule meetings directly or create them from available time slots.
*   **Calendar View**: Get a consolidated view of your schedule, including both time slots and meetings.
*   **Rate Limiting**: Endpoints are protected against excessive requests to ensure application stability.
*   **Service Protection**: Resilience4j is used to protect the application from being overloaded.

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

The API is available at `/api/scheduler`. All endpoints are rate-limited.

### Time Slot Management

*   `POST /{userId}/slots`: Creates a new time slot for a user.
    *   **Path Variable**: `userId` (UUID)
    *   **Query Parameters**: `start` (Instant), `end` (Instant), `busy` (boolean, optional, default: false)

*   `DELETE /{userId}/slots/{slotId}`: Deletes a specific time slot.
    *   **Path Variables**: `userId` (UUID), `slotId` (UUID)

*   `PUT /{userId}/slots/{slotId}`: Modifies an existing time slot.
    *   **Path Variables**: `userId` (UUID), `slotId` (UUID)
    *   **Query Parameters**: `start` (Instant, optional), `end` (Instant, optional), `busy` (boolean, optional)

### Meeting Scheduling

*   `POST /{userId}/meetings`: Schedules a new meeting.
    *   **Path Variable**: `userId` (UUID of the organizer)
    *   **Request Body**:
        ```json
        {
          "title": "string",
          "description": "string",
          "participants": ["UUID"],
          "start": "Instant",
          "end": "Instant"
        }
        ```

*   `POST /{userId}/meetings/from-slots`: Creates meetings from a user's available (non-busy) time slots for a given set of participants.
    *   **Path Variable**: `userId` (UUID of the organizer)
    *   **Request Body**:
        ```json
        {
          "title": "string",
          "description": "string",
          "participantIds": ["UUID"]
        }
        ```

### Calendar View

*   `GET /{userId}/calendar`: Returns a consolidated view of a user's calendar, including time slots and meetings.
    *   **Path Variable**: `userId` (UUID)
    *   **Query Parameters**: `from` (Instant), `to` (Instant)
