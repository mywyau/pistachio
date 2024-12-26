package repository.fragments.business

import doobie.implicits.*
import doobie.util.fragment

object BusinessSpecificationsRepoFragments {

  val resetBusinessSpecsTable: fragment.Fragment = {
    sql"TRUNCATE TABLE business_specs RESTART IDENTITY"
  }

  val createBusinessSpecsTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS business_specs (
        id SERIAL PRIMARY KEY,
        user_id VARCHAR(255) NOT NULL UNIQUE,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        business_name VARCHAR(255) NOT NULL,
        description TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }


  val insertBusinessSpecificationsData: fragment.Fragment = {
    sql"""
      INSERT INTO business_specs (
        user_id,
        business_id,
        business_name,
        description,
        created_at,
        updated_at
      ) VALUES
      ('USER001', 'BUS001', 'business_name_1', 'some desc1', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER002', 'BUS002', 'business_name_2', 'some desc2', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER003', 'BUS003', 'business_name_3', 'some desc3', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER004', 'BUS004', 'business_name_4', 'some desc4', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER005', 'BUS005', 'business_name_5', 'some desc5', '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """
  }

}
