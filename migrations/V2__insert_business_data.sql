INSERT INTO business_specs (business_id, business_name, description, business_type)
VALUES
('biz_001', 'Tech Innovators', 'A tech-focused co-working space.', 'Co-working'),
('biz_002', 'Green Startups Hub', 'A hub for eco-friendly startups.', 'Private Office'),
('biz_003', 'Corporate Solutions Inc.', 'Office solutions for corporate businesses.', 'Corporate'),
('biz_004', 'Freelancers Unite', 'A community-driven co-working space for freelancers.', 'Co-working'),
('biz_005', 'Design Studio Hub', 'A creative space for designers and artists.', 'Creative Studio');

INSERT INTO business_address (business_id, business_name, address_1, address_2, city, country, county, postcode)
VALUES
('biz_001', 'Tech Innovators', '123 Tech Drive', 'Suite 200', 'San Francisco', 'USA', 'California', '94107'),
('biz_002', 'Green Startups Hub', '456 Greenway Blvd', NULL, 'Austin', 'USA', 'Texas', '78701'),
('biz_003', 'Corporate Solutions Inc.', '789 Business Ave', 'Floor 5', 'New York', 'USA', 'New York', '10001'),
('biz_004', 'Freelancers Unite', '101 Creative Circle', NULL, 'Portland', 'USA', 'Oregon', '97209'),
('biz_005', 'Design Studio Hub', '202 Artistry Lane', NULL, 'Los Angeles', 'USA', 'California', '90001');

INSERT INTO business_contact_details (business_id, business_name, primary_contact, contact_email, contact_phone, website_url)
VALUES
('biz_001', 'Tech Innovators', 'John Doe', 'contact@techinnovators.com', '+1-800-555-001', 'https://techinnovators.com'),
('biz_002', 'Green Startups Hub', 'Jane Smith', 'info@greenstartups.com', '+1-800-555-002', 'https://greenstartups.com'),
('biz_003', 'Corporate Solutions Inc.', 'Michael Brown', 'support@corporatesolutions.com', '+1-800-555-003', 'https://corporatesolutions.com'),
('biz_004', 'Freelancers Unite', 'Sarah Connor', 'hello@freelancersunite.com', '+1-800-555-004', 'https://freelancersunite.com'),
('biz_005', 'Design Studio Hub', 'Emily Davis', 'contact@designstudiohub.com', '+1-800-555-005', 'https://designstudiohub.com');
