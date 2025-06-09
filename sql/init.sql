
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
  roleId UUID NOT NULL,
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

-- Rooms --
create TABLE IF NOT EXISTS roomTypes (
  id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  CONSTRAINT valid_roomTypes_id CHECK (id ~ '^[a-z0-9\-]+$') 
);

create TABLE IF NOT EXISTS rooms (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  displayName VARCHAR(6) NOT NULL,
  roomCode VARCHAR(255) UNIQUE NOT NULL,
  description TEXT NOT NULL,
  capacity INT NOT NULL,
  defaultPrice INT NOT NULL,
  slotSize INT NOT NULL CHECK (slotsize IN (5, 6, 10, 12, 15, 20, 30, 60)),
  roomTypeId VARCHAR(255) NOT NULL,
  locationId UUID NOT NULL,
  CONSTRAINT fk_rooms_roomTypes FOREIGN KEY (roomTypeId) REFERENCES roomTypes(id) ON DELETE RESTRICT,
  CONSTRAINT fk_rooms_locations FOREIGN KEY (locationId) REFERENCES locations(id) ON DELETE CASCADE
);

create TABLE IF NOT EXISTS availability (
  id UUID PRIMARY KEY,
  startDate DATE NOT NULL, 
  dayOfWeek INT NOT NULL CHECK (dayOfWeek BETWEEN 0 AND 6),
  mask TEXT NOT NULL,
  roomId UUID NOT NULL,
  CONSTRAINT unique_availability_startDate_dayOfWeek UNIQUE (startDate, dayOfWeek),
  CONSTRAINT fk_availability_rooms FOREIGN KEY (roomId) REFERENCES rooms(id) ON DELETE CASCADE

);

create TABLE IF NOT EXISTS pricePolicies (
  id UUID PRIMARY KEY,
  startDate DATE NOT NULL,
  dayOfWeek INT NOT NULL CHECK (dayOfWeek BETWEEN 0 AND 6), 
  price INT NOT NULL,
  mask TEXT NOT NULL, 
  roomId UUID NOT NULL,
  CONSTRAINT unique_pricePolicies_startDate_dayOfWeek UNIQUE (startDate, dayOfWeek),
  CONSTRAINT fk_pricePolicies_rooms FOREIGN KEY (roomId) REFERENCES rooms(id) ON DELETE CASCADE
);

-- Booking --
create TABLE IF NOT EXISTS bookings (
  id UUID PRIMARY KEY,
  status VARCHAR(255) NOT NULL,
  userId UUID NOT NULL,
  CONSTRAINT fk_bookings_users FOREIGN KEY (userId) REFERENCES users(id) ON DELETE RESTRICT
);

-- Slots --
create TABLE IF NOT EXISTS slots (
  id UUID PRIMARY KEY,
  slotDate DATE NOT NULL,
  slotIndex INT NOT NULL,
  status VARCHAR(255),
  roomId UUID NOT NULL,
  bookingId UUID NOT NULL,
  userId UUID NOT NULL,
  CONSTRAINT unique_slots_slotDate_slotIndex_roomId UNIQUE (slotDate, slotIndex, roomId)
  CONSTRAINT fk_slots_rooms FOREIGN KEY (roomId) REFERENCES rooms(id) ON DELETE RESTRICT,
  CONSTRAINT fk_slots_bookings FOREIGN KEY (bookingId) REFERENCES bookings(id) ON DELETE CASCADE,
  CONSTRAINT fk_slots_users FOREIGN KEY (userId) REFERENCES users(id) ON DELETE RESTRICT
);














-- Payments --
-- create TABLE IF NOT EXISTS pricingPolicies();
--
-- create TABLE IF NOT EXISTS cancelationPolicies();
--
-- create TABLE IF NOT EXISTS payments();
--
-- create TABLE IF NOT EXISTS  






