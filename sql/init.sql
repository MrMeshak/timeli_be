
-- USER --
CREATE TABLE IF NOT EXISTS roles (
  id UUID PRIMARY KEY, 
  name VARCHAR(50) UNIQUE NOT NULL,
  mask TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  firstName VARCHAR(255) NOT NULL,
  lastName VARCHAR(255) NOT NULL,
  status VARCHAR(255) DEFAULT 'ACTIVE',
  roleId UUID,
  FOREIGN KEY (roleId) REFERENCES roles(id) ON DELETE RESTRICT
);


-- Locations --
create TABLE IF NOT EXISTS locations (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  street VARCHAR(255) NOT NULL,
  city VARCHAR(255) NOT NULL,
  state VARCHAR(255) NOT NULL,
  country VARCHAR(255) NOT NULL,
  postCode VARCHAR(255) NOT NULL
--   coordinates GEOGRPHY, --
);

create TABLE IF NOT EXISTS rooms (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  capacity INT,
  locationId UUID,
  CONSTRAINT fk_rooms_locations FOREIGN KEY (locationId) REFERENCES locations(id) ON DELETE CASCADE
);



-- Booking Slots --
create TABLE IF NOT EXISTS bookings (
  id UUID PRIMARY KEY,
  status VARCHAR(255) NOT NULL,
  userId UUID,
  CONSTRAINT fk_bookings_users FOREIGN KEY (userId) REFERENCES users(id) ON DELETE RESTRICT
);

create TABLE IF NOT EXISTS schedules(
  id UUID PRIMARY KEY,
  date Date NOT NULL,
  slotSize INT NOT NULL,
  scheduleMask TEXT NOT NULL,
  roomId UUID,
  CONSTRAINT fk_schedules_rooms FOREIGN KEY (roomID) REFERENCES rooms(id) ON DELETE CASCADE,
  CONSTRAINT unique_date_roomId UNIQUE (date, roomId)
);

create TABLE IF NOT EXISTS slots (
  id UUID PRIMARY KEY,
  status VARCHAR(255) NOT NULL,
  slotMask TEXT NOT NULL, 
  scheduleId UUID,
  bookingId UUID,
  CONSTRAINT fk_slots_schedules FOREIGN KEY (scheduleId) REFERENCES schedules(id) ON DELETE RESTRICT,
  CONSTRAINT fk_slots_bookings FOREIGN KEY (bookingId) REFERENCES bookings(id) ON DELETE CASCADE
);



-- Payments --
-- create TABLE IF NOT EXISTS pricingPolicies();
--
-- create TABLE IF NOT EXISTS cancelationPolicies();
--
-- create TABLE IF NOT EXISTS payments();
--
-- create TABLE IF NOT EXISTS  






