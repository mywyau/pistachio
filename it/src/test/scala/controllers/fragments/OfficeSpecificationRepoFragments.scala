package controllers.fragments

import doobie.implicits._
import doobie.util.fragment

object OfficeSpecificationRepoFragments {

  val resetOfficeSpecsTable: fragment.Fragment = {
    sql"TRUNCATE TABLE office_specifications RESTART IDENTITY"
  }

  val createOfficeSpecsTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS office_specifications (
        id SERIAL PRIMARY KEY,
        business_id VARCHAR(255) NOT NULL,
        office_id VARCHAR(255) NOT NULL UNIQUE,
        office_name VARCHAR(255),
        description TEXT,
        office_type VARCHAR(100),
        number_of_floors INT,
        total_desks INT,
        capacity INT,
        amenities TEXT[],
        opening_hours JSONB,
        rules TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }

  val insertOfficeSpecificationsTable: fragment.Fragment = {
    sql"""
      INSERT INTO office_specifications (
        business_id,
        office_id,
        office_name,
        description,
        office_type,
        number_of_floors,
        total_desks,
        capacity,
        amenities,
        opening_hours,
        rules,
        created_at,
        updated_at
      ) VALUES
        (
          'BUS001',
          'OFF001',
          'Downtown Workspace',
          'A modern co-working space located in the heart of downtown.',
          'PrivateOffice',
          2,
          50,
          100,
          ARRAY['Wi-Fi', 'Coffee Machine', 'Meeting Rooms'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'No loud conversations. Keep the desks clean.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'BUS002',
          'OFF002',
          'Suburban Office',
          'A quiet office in the suburbs, perfect for focused work.',
          'PrivateOffice',
          1,
          20,
          40,
          ARRAY['Wi-Fi', 'Tea', 'Parking'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'No pets. Maintain silence.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        );
    """
  }

  val insertSameBusinessIdOfficeSpecificationsData: fragment.Fragment = {
    sql"""
      INSERT INTO office_specifications (
        business_id,
        office_id,
        office_name,
        description,
        office_type,
        number_of_floors,
        total_desks,
        capacity,
        amenities,
        opening_hours,
        rules,
        created_at,
        updated_at
      ) VALUES
        (
          'BUS123',
          'OFF001',
          'Downtown Workspace',
          'A modern co-working space located in the heart of downtown.',
          'PrivateOffice',
          2,
          50,
          100,
          ARRAY['Wi-Fi', 'Coffee Machine', 'Meeting Rooms'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'No loud conversations. Keep the desks clean.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'BUS123',
          'OFF002',
          'Suburban Office',
          'A quiet office in the suburbs, perfect for focused work.',
          'PrivateOffice',
          1,
          20,
          40,
          ARRAY['Wi-Fi', 'Tea', 'Parking'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'No pets. Maintain silence.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'BUS123',
          'OFF003',
          'Suburban Office',
          'A quiet office in the suburbs, perfect for focused work.',
          'PrivateOffice',
          1,
          20,
          40,
          ARRAY['Wi-Fi', 'Tea', 'Parking'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'No pets. Maintain silence.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'BUS123',
          'OFF004',
          'Suburban Office',
          'A quiet office in the suburbs, perfect for focused work.',
          'PrivateOffice',
          1,
          20,
          40,
          ARRAY['Wi-Fi', 'Tea', 'Parking'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'No pets. Maintain silence.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'BUS123',
          'OFF005',
          'Suburban Office',
          'A quiet office in the suburbs, perfect for focused work.',
          'PrivateOffice',
          1,
          20,
          40,
          ARRAY['Wi-Fi', 'Tea', 'Parking'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'No pets. Maintain silence.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        );
    """
  }
}
