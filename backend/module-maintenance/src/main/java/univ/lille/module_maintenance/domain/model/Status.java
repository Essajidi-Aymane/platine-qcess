package univ.lille.module_maintenance.domain.model;

public enum Status {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    REJECTED,
    CANCELLED;
	
    public boolean isTerminal() {
        return this == RESOLVED || this == REJECTED || this == CANCELLED;
    }
	
    public boolean canTransitionTo(Status newStatus) {
        if (newStatus == null) {
            return false;
        }
		
        if (isTerminal()) {
            return false;
        }
		
        if (newStatus == CANCELLED) {
            return this == OPEN || this == IN_PROGRESS;
        }
		
        return true;
    }
	
    public boolean isCancellable() {
        return this == OPEN || this == IN_PROGRESS;
    }
}