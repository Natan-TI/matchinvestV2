-- Advisors
CREATE TABLE advisors (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    certifications TEXT,
    investment_focus VARCHAR(30) NOT NULL,
    years_experience INT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Investors
CREATE TABLE investors (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    goals TEXT,
    amount NUMERIC(19,2),
    currency VARCHAR(10),
    risk_profile VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Matches
CREATE TABLE matches (
    id UUID PRIMARY KEY,
    advisor_id UUID NOT NULL,
    investor_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_matches_advisor  FOREIGN KEY (advisor_id)  REFERENCES advisors(id),
    CONSTRAINT fk_matches_investor FOREIGN KEY (investor_id) REFERENCES investors(id)
);
