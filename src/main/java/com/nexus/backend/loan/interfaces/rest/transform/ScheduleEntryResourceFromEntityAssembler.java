package com.nexus.backend.loan.interfaces.rest.transform;

import com.nexus.backend.loan.domain.model.entities.ScheduleEntry;
import com.nexus.backend.loan.interfaces.rest.resources.ScheduleEntryResource;

public class ScheduleEntryResourceFromEntityAssembler {

    public static ScheduleEntryResource toResourceFromEntity(ScheduleEntry entity) {
        return new ScheduleEntryResource(
                entity.getId(),
                entity.getInstallmentNumber(),
                entity.getDueDate(),
                entity.getPrincipal().getAmount(),
                entity.getInterest().getAmount(),
                entity.getFee().getAmount(),
                entity.getTotalAmount().getAmount(),
                entity.isPaid()
        );
    }
}