package ar.edu.itba.pod;

import java.util.Optional;

public enum Province {
    JUNGLE("JUNGLE"), SAVANNAH("SAVANNAH"), TUNDRA("TUNDRA");

    private String description;

    private Province(String s){
        this.description = s;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Method to obtain an instance of the enum object given the string value
     * @param s value of the enum
     * @return Enum value
     * @throws RuntimeException if the value is invalid
     */
    public static Province fromValue(String s) throws RuntimeException {
        String value = Optional.ofNullable(s).orElseThrow(RuntimeException::new).toUpperCase();
        for (Province province : Province.values()){
            if (value.equals(province.description)){
                return province;
            }
        }
        throw new RuntimeException();
    }
}
