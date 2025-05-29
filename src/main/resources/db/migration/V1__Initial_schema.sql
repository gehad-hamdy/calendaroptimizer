CREATE TABLE IF NOT EXISTS  users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS  properties (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL REFERENCES properties(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    CONSTRAINT valid_booking_dates CHECK (end_date >= start_date)
);

CREATE INDEX IF NOT EXISTS  idx_bookings_property_id ON bookings(property_id);
CREATE INDEX IF NOT EXISTS  idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_property_dates ON bookings(property_id, start_date, end_date);
