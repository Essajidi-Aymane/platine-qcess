package univ.lille.module_maintenance.domain.model;


public enum Priority {
    LOW,
    NORMAL,
    HIGH;

    public String getDisplayColor() {
        return switch (this) {
            case LOW -> "green";
            case NORMAL -> "blue";
            case HIGH -> "red";
        };
    }
}