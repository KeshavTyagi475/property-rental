package com.propertyrental.maintenance;

import org.springframework.data.jpa.domain.Specification;

public final class MaintenanceRequestSpecifications {

    private MaintenanceRequestSpecifications() {
    }

    public static Specification<MaintenanceRequest> hasSearch(
            String search) {

        return (root, query, criteriaBuilder) -> {

            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + search.toLowerCase().trim() + "%"
            );
        };
    }

    public static Specification<MaintenanceRequest> hasUnitId(
            Long unitId) {

        return (root, query, criteriaBuilder) -> {

            if (unitId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("unit").get("id"),
                    unitId
            );
        };
    }

    public static Specification<MaintenanceRequest> hasStatus(
            Status status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("status"),
                    status
            );
        };
    }

    public static Specification<MaintenanceRequest> hasPriority(
            Priority priority) {

        return (root, query, criteriaBuilder) -> {

            if (priority == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("priority"),
                    priority
            );
        };
    }

    public static Specification<MaintenanceRequest> hasContractorId(
            Long contractorId) {

        return (root, query, criteriaBuilder) -> {

            if (contractorId == null) {
                return criteriaBuilder.conjunction();
            }

            query.distinct(true);

            return criteriaBuilder.equal(
                    root.join("assignments")
                            .get("contractor")
                            .get("id"),
                    contractorId
            );
        };
    }
}