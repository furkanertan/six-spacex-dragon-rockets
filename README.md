# SpaceX Dragon Rockets Repository

This is a simple Java library for managing SpaceX Dragon Rockets and Missions. The library provides basic operations such as adding rockets, assigning rockets to missions, changing statuses, and retrieving mission summaries.

## Setup

Prerequisites
- Java 17 or higher
- Maven (for building the project)

## Assumptions

- Rockets can only be assigned to one mission at a time. (It means that rockets can change their mission's unless they are assigned to 1 mission.)
- Missions can have multiple rockets assigned.
- The status of a mission is automatically updated based on the status of its assigned rockets.
- A mission cannot be ended if it still has rockets assigned.

## Project Structure

### Domain

#### `Mission`
The class contains data of Mission: mission name, mission status and list of assigned rocket to the mission

#### `Rocket`
The class contains data of Rocket: rocket name and rocket status

#### `MissionStatus`
#### `RocketStatus`

### Exception

#### `SpaceXException`
The SpaceXException is a custom exception that is thrown when specific issues or errors occur within the SpaceX domain, such as missing mission data or invalid rocket statuses.

### Repository
Repository classes help to keep related data in the related class within in-memory data structures.
In the future, it can be updated to database table if required.

#### `MissionRepository`
This class is responsible for storing and retrieving mission data. It uses an in-memory Map to keep the missions indexed by their name.

#### `RocketRepository`
This class is responsible for storing and retrieving rocket data. It uses an in-memory Map to keep the rockets indexed by their name.

### Service

#### `MissionRocketAssignmentService`

### Util

#### `MissionSummaryFormatter`
The MissionSummaryFormatter utility class is responsible for formatting the mission and rocket data into a readable string. It ensures that the summary is well-structured and easy to read.

### SpaceXLibrary
SpaceXLibrary is the entry point for the application. It initializes the repositories, adds data, changes statuses and gets summary of missions.

## Functionalities

### Adding new Rocket
New rockets can be created only with unique name. When it tries to create rockets with same name, it throws an exception that `Rocket already exist!`

### Adding new Mission
New missions can be created only with unique name. When it tries to create missions with same name, it throws an exception that `Mission already exist!`

### Assigning Rocket(s) to a mission
For assigning rocket to mission, the flow of actions like in below:

1. Given rocket and mission are checked, if they are not created before, an exception is thrown `Rocket is not found!` or `Mission is not found!`
2. If they are found, mission status is checked, if it is `ENDED`, an exception is thrown that Mission is ended and rockets cannot be assigned anymore!
3. If mission is `SCHEDULED` and rocket status is `ON_GROUND`, new rocket is assigned with `IN_SPACE` status and mission status is updated to `IN_PROGRESS`
4. If given rocket is assigned to another mission (which means `IN_SPACE` or `IN_REPAIR` status), old mission and new mission should updated according to logic below:

   ● Rocket status is `IN_SPACE` -> If Old mission has 0 rocket left, update it to `ENDED` otherwise keep the remaining status, New mission's status is not updated unless it wasn't `SCHEDULED`

   ● Rocket status is `IN_REPAIR` -> If Old mission has 0 rocket left, update it to `ENDED` otherwise check if any other `IN_REPAIR` left, if yes, remain the status, if not, update it to `IN_PROGRESS` status. New mission should be updated to `PENDING` status.

### Changing Rocket Status
Rocket status can be updated according to rules below:

OLD_STATUS -> NEW_STATUS 

- `ON_GROUND` -> `IN_SPACE`: When rocket is assigned to mission it automatically updates status like this, otherwise it shouldn't be possible to do it without any mission assignment.
- `ON_GROUND` -> `IN_REPAIR`: There is no mission assigned to rocket, it is not possible to update the status like this.

- `IN_SPACE` -> `ON_GROUND`: Rocket is back to initial state, If mission had only 1 rocket, it should be ENDED, if there are more rockets, mission should remain as "In Progress" according to left mission's status.
- `IN_SPACE` -> `IN_REPAIR`: It is possible, mission is also updated to "Pending" status.

- `IN_REPAIR` -> `IN_SPACE`: -> It is possible, we should check any other rockets in "IN REPAIR" status left in mission and if not, we can update mission status to "IN PROGRESS"
- `IN_REPAIR` -> `ON_GROUND`: ->  Rocket is back to initial state, If mission had only 1 rocket, it should be ENDED, if there are more rockets, mission should remain as "In Progress" or "Pending" according to left mission's status.

### Getting Summary of Missions

Get a summary of missions by number of rockets assigned. Missions with the same number of rockets are ordered in descending alphabetical order.

## Usage

1. Create an instance of `SpaceXLibrary`.
2. Use the provided methods to add rockets, missions, and perform operations.

## Example

```java
SpaceXLibrary library = new SpaceXLibrary();

// Add rockets
Rocket rocket1 = new Rocket("Dragon 1");
Rocket rocket2 = new Rocket("Dragon 2");
Rocket rocket3 = new Rocket("Dragon 3");
library.addRocket(rocket1);
library.addRocket(rocket2);
library.addRocket(rocket3);

// Add missions
Mission mission1 = new Mission("Mars");
Mission mission2 = new Mission("Luna");
library.addMission(mission1);
library.addMission(mission2);

// Assign rockets to missions
library.assignRocketToMission(rocket1.getName(), mission1.getName());
library.assignRocketsToMission(List.of(rocket2.getName(), rocket3.getName()), mission2.getName());

// Change statuses
library.changeRocketStatus(rocket1.getName(), RocketStatus.IN_REPAIR);

// Get mission summary
String missionSummary = library.getMissionSummary();
System.out.println(missionSummary);
```

## Questions

1. I implemented "Changing Mission Status" functionality as automatically, not manually due to example below:
For example, there is a mission data like in below:

   Mission_1 - IN_PROGRESS - Dragons: 2
   
      ● Red Dragon – In Space
      
      ● Dragon XL – In space
      
    If mission status updated to "PENDING", we need to have one of rockets in "IN_REPAIR" status, how to decide which one?

    So, in this case it doesn't make sense to have manual update for mission status, it should be only updated by rocket status changes.