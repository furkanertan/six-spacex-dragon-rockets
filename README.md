# SpaceX Dragon Rockets Repository

This is a simple Java library for managing SpaceX Dragon Rockets and Missions. The library provides basic operations such as adding rockets, assigning rockets to missions, changing statuses, and retrieving mission summaries.

## Assumptions

- Rockets can only be assigned to one mission at a time.
- Missions can have multiple rockets assigned.
- The status of a mission is automatically updated based on the status of its assigned rockets.
- A mission cannot be ended if it still has rockets assigned.

## Changing Rocket Status
- ON_GROUND -> IN_SPACE: Rocket is not assigned to any mission, it would throw exception.
- ON_GROUND -> IN_REPAIR: It is possible, it should be updated after method call.

- IN_SPACE -> ON_GROUND: Rocket already assigned to a mission, fail. (Considered as we don't resign current mission, just update rocket/mission status)
- IN_SPACE -> IN_REPAIR: It is possible, mission is also updated to "Pending" status.

- IN_REPAIR -> IN_SPACE: -> If rocket is assigned to mission, it is possible and mission is updated to "In Progress", otherwise fails with no mission assignment.
- IN_REPAIR -> ON_GROUND: -> Rocket already assigned to a mission, fail. (Considered as we don't resign current mission, just update rocket/mission status)


## Usage

1. Create an instance of `SpaceXLibrary`.
2. Use the provided methods to add rockets, missions, and perform operations.

## Example

```java
SpaceXLibrary library = new SpaceXLibrary();

Rocket rocket1 = new Rocket("Dragon 1");
Mission mission1 = new Mission("Mars");

library.addRocket(rocket1);
library.addMission(mission1);

library.assignRocketToMission("Dragon 1", "Mars");

List<Mission> summary = library.getMissionSummary();