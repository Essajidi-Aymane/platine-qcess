package univ.lille.dto.module;
        import lombok.AllArgsConstructor;
        import lombok.Data;
        import univ.lille.enums.ModuleKey;

        @Data
        @AllArgsConstructor
        public class ModuleStatusDTO {
        private ModuleKey moduleKey;
        private String key;
        private String description;
        private boolean isMandatory;
        private boolean isActive;
        private boolean canActivate;
        private boolean canDeactivate;

        public ModuleStatusDTO(ModuleKey moduleKey, boolean isActive,
        boolean canActivate, boolean canDeactivate) {
        this.moduleKey = moduleKey;
        this.key = moduleKey.name();
        this.isMandatory = moduleKey.isMandatory();
        this.isActive = isActive;
        this.canActivate = canActivate;
        this.canDeactivate = canDeactivate;
        }
        }