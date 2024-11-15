package controllers.fragments

import doobie.implicits.*
import doobie.util.fragment

object WandererProfileSqlFragments {

  val resetWandererLoginDetailsTable = sql"TRUNCATE TABLE user_login_details RESTART IDENTITY"

  val createWandererLoginDetailsTable: fragment.Fragment = {
    sql"""
        CREATE TABLE IF NOT EXISTS user_login_details (
          id BIGSERIAL PRIMARY KEY,
          user_id VARCHAR(255) NOT NULL,
          username VARCHAR(255) NOT NULL,
          password_hash TEXT NOT NULL,
          email VARCHAR(255) NOT NULL,
          role VARCHAR(50) NOT NULL,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """
  }

  val insertWandererLoginDetailsTable: fragment.Fragment = {
    sql"""
      INSERT INTO user_login_details (user_id, username, password_hash, email, role, created_at, updated_at)
      VALUES('fake_user_id_1', 'fake_username', 'hashed_password', 'fake_user_1@example.com', 'Wanderer', '2023-01-01 12:00:00', '2023-01-01 12:00:00');
    """
  }

  val resetWandererAddressTable = sql"TRUNCATE TABLE wanderer_address RESTART IDENTITY"

  val createWandererAddressTable: fragment.Fragment = {
    sql"""
          CREATE TABLE IF NOT EXISTS wanderer_address (
             id BIGSERIAL PRIMARY KEY,
             user_id VARCHAR(255) NOT NULL,
             street VARCHAR(255) NOT NULL,
             city VARCHAR(255) NOT NULL,
             country VARCHAR(255) NOT NULL,
             county VARCHAR(255) NOT NULL,
             postcode VARCHAR(255) NOT NULL,
             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
          );
    """
  }

  val insertWandererAddressData: fragment.Fragment = {
    sql"""
          INSERT INTO wanderer_address (
            user_id,
            street,
            city,
            country,
            county,
            postcode,
            created_at,
            updated_at
          ) VALUES (
            'fake_user_id_1',
            '123 Example Street',
            'Sample City',
            'United Kingdom',
            'South Glamorgan',
            'CF5 3NJ',
            '2025-01-01 00:00:00',
            '2025-01-01 00:00:00'
          );
      """
  }

  val resetWandererContactDetailsTable = sql"TRUNCATE TABLE wanderer_contact_details RESTART IDENTITY"

  val createWandererContactDetailsTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS wanderer_contact_details (
          id BIGSERIAL PRIMARY KEY,
          user_id VARCHAR(255) NOT NULL,
          contact_number VARCHAR(100) NOT NULL,
          email VARCHAR(255) NOT NULL,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }

  val insertWandererContactDetailsData: fragment.Fragment = {
    sql"""
      INSERT INTO wanderer_contact_details (user_id, contact_number, email, created_at, updated_at)
      VALUES('fake_user_id_1', '0123456789', 'fake_user_1@example.com', '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """
  }
}