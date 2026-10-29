package com.nexus.backend.iam.application.internal.eventhandlers;

import com.nexus.backend.iam.domain.model.commands.SeedAdministratorCommand;
import com.nexus.backend.iam.domain.model.commands.SeedRolesCommand;
import com.nexus.backend.iam.domain.services.RoleCommandService;
import com.nexus.backend.iam.domain.services.UserCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


/**
 * ApplicationReadyEventHandler class.
 * This class is used to handle the ApplicationReadyEvent
 */
@Service
public class ApplicationReadyEventHandler {
  private final RoleCommandService roleCommandService;
  private final UserCommandService userCommandService;
  private final String adminUsername;
  private final String adminPassword;
  private static final Logger LOGGER
      = LoggerFactory.getLogger(ApplicationReadyEventHandler.class);

  /**
   * Constructor.
   *
   * @param roleCommandService the RoleCommandService to use
   * @param userCommandService the UserCommandService that creates the first administrator
   * @param adminUsername the first administrator's username (NEXUS_ADMIN_USERNAME), may be blank
   * @param adminPassword the first administrator's password (NEXUS_ADMIN_PASSWORD), may be blank
   */
  public ApplicationReadyEventHandler(RoleCommandService roleCommandService,
      UserCommandService userCommandService,
      @Value("${nexus.admin.username:}") String adminUsername,
      @Value("${nexus.admin.password:}") String adminPassword) {
    this.roleCommandService = roleCommandService;
    this.userCommandService = userCommandService;
    this.adminUsername = adminUsername;
    this.adminPassword = adminPassword;
  }

  /**
   * Handle the ApplicationReadyEvent.
   * This method is used to seed the roles
   *
   * @param event the ApplicationReadyEvent the event to handle
   */
  @EventListener
  public void on(ApplicationReadyEvent event) {
    var applicationName = event.getApplicationContext().getId();
    LOGGER.info("Starting to verify if roles seeding is needed for {} at {}",
        applicationName, currentTimestamp());

    var seedRolesCommand = new SeedRolesCommand();
    roleCommandService.handle(seedRolesCommand);
    LOGGER.info("Roles seeding verification finished for {} at {}",
        applicationName, currentTimestamp());
    seedAdministrator();
  }

  /**
   * Creates the first administrator from the configuration, the only way one comes to be: the
   * public sign-up only creates clients.
   */
  private void seedAdministrator() {
    if (adminUsername.isBlank() || adminPassword.isBlank()) {
      LOGGER.info("No first administrator configured (NEXUS_ADMIN_USERNAME, NEXUS_ADMIN_PASSWORD)");
      return;
    }
    userCommandService.handle(new SeedAdministratorCommand(adminUsername, adminPassword))
        .ifPresentOrElse(
            admin -> LOGGER.info("First administrator {} created", admin.getUsername()),
            () -> LOGGER.info("User {} already exists: left as it is", adminUsername));
  }

  private Timestamp currentTimestamp() {
    return new Timestamp(System.currentTimeMillis());
  }
}
