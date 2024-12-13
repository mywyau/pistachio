package controllers.fragments.business

import doobie.implicits.*
import doobie.util.fragment

object BusinessAddressRepoFragments {

  val resetBusinessAddressTable: fragment.Fragment = {
    sql"TRUNCATE TABLE business_address RESTART IDENTITY"
  }

  val createBusinessAddressTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS business_address (
        id BIGSERIAL PRIMARY KEY,
        user_id VARCHAR(255) NOT NULL UNIQUE,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        business_name VARCHAR(255),
        building_name VARCHAR(255),
        floor_number VARCHAR(255),
        street VARCHAR(255),
        city VARCHAR(255),
        country VARCHAR(255),
        county VARCHAR(255),
        postcode VARCHAR(255),
        latitude DECIMAL(9,6),
        longitude DECIMAL(9,6),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }


  val insertBusinessAddressTable: fragment.Fragment = {
    sql"""
        INSERT INTO business_address (
          business_id,
          business_id,
          building_name,
          floor_number,
          street,
          city,
          country,
          county,
          postcode,
          latitude,
          longitude,
          created_at,
          updated_at
        ) VALUES (
          'user123',                     
          'business456',                   
          '123 Main Street',             
          'New York',                    
          'USA',                         
          'Manhattan',                   
          '10001',                       
          'Empire State Building',       
          '5th Floor',                   
          40.748817,                     
          -73.985428,                    
          CURRENT_TIMESTAMP,             
          CURRENT_TIMESTAMP              
        );
    """
  }
}
