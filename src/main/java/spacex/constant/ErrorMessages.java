package spacex.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ErrorMessages {

    public static final String ROCKET_NOT_FOUND = "Rocket is not found!";
    public static final String ROCKET_NOT_AVAILABLE = "Rocket is not available for assignment!";
    public static final String ROCKET_ALREADY_EXISTS = "Rocket already exists!";
    public static final String ROCKET_CANNOT_BE_IN_SPACE_OR_IN_REPAIR_WITHOUT_MISSION = "Rocket cannot be set to In Space or In Repair without mission!";

    public static final String MISSION_NOT_FOUND = "Mission is not found!";
    public static final String MISSION_NOT_AVAILABLE = "Mission is not available for assignment!";
    public static final String MISSION_ALREADY_EXISTS = "Mission already ended!";
}