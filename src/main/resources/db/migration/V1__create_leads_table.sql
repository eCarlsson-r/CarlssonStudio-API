CREATE TABLE leads (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    company     VARCHAR(100),
    company_size VARCHAR(10),
    industry    VARCHAR(50) NOT NULL,
    build_type  VARCHAR(50) NOT NULL,
    problems    JSON NOT NULL,
    features    JSON NOT NULL,
    status      ENUM('NEW','CONTACTED','PROPOSAL_SENT','CLOSED') 
                NOT NULL DEFAULT 'NEW',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
                ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE lead_recommendations (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    lead_id         BIGINT NOT NULL,
    foundation_slug VARCHAR(50) NOT NULL,
    match_score     INT NOT NULL,
    match_reason    TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);