package com.nexus.backend.client.domain.model.aggregates;

import com.nexus.backend.client.domain.model.commands.CreateClientCommand;
import com.nexus.backend.client.domain.model.valueobjects.Dni;
import com.nexus.backend.client.domain.model.valueobjects.EmailAddress;
import com.nexus.backend.client.domain.model.valueobjects.PersonName;
import com.nexus.backend.client.domain.model.valueobjects.StreetAddress;
import com.nexus.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Main Aggregate of the Clients Bounded Context.
 * Represents the financial entity (client) requesting a microloan.
 */
@Entity
@Getter
@NoArgsConstructor // Required by JPA
public class Client extends AuditableAbstractAggregateRoot<Client> {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "first_name")),
            @AttributeOverride(name = "lastName", column = @Column(name = "last_name"))
    })
    private PersonName name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "email_address", unique = true))
    })
    private EmailAddress email;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "number", column = @Column(name = "dni_number", unique = true))
    })
    private Dni dni;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "address_street")),
            @AttributeOverride(name = "city", column = @Column(name = "address_city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "address_zip_code")),
            @AttributeOverride(name = "country", column = @Column(name = "address_country"))
    })
    private StreetAddress address;

    /**
     * Constructor that handles the creation of the aggregate based on the command.
     * This is where the transformation from flat data (Command) to Value Objects (Domain) occurs.
     */
    public Client(CreateClientCommand command) {
        this.name = new PersonName(command.firstName(), command.lastName());
        this.email = new EmailAddress(command.email());
        this.dni = new Dni(command.dni());
        this.address = new StreetAddress(
                command.street(),
                command.city(),
                command.zipCode(),
                command.country()
        );
    }

    // Business methods would go here, e.g., updateAddress(), changeEmail(), etc.
}
