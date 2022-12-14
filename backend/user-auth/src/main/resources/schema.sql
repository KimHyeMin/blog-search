CREATE TABLE users (
                       id INTEGER NOT NULL AUTO_INCREMENT,
                       firstName VARCHAR(255) NOT NULL,
                       lastName VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       registeredAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       PRIMARY KEY(id)
);
ALTER TABLE users ADD CONSTRAINT EMAIL_UNIQUE UNIQUE(email);
