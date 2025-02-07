package spacex.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ErrorMessages {
    public static final String ROCKET_OR_MISSION_NOT_FOUND = "Rocket or Mission not found";
    public static final String ROCKET_NOT_AVAILABLE = "Rocket is not available for assignment";
    public static final String MISSION_NOT_FOUND = "Mission not found";
    public static final String MISSION_CANNOT_END = "Cannot end mission with rockets assigned";
}