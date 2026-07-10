CREATE TABLE proposals (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    lead_id         BIGINT NOT NULL,
    foundation_slug VARCHAR(50) NOT NULL,
    foundation_name VARCHAR(100) NOT NULL,
    match_score     INT NOT NULL,
    timeline_weeks  INT NOT NULL,
    complexity      ENUM('LOW','MEDIUM','HIGH') NOT NULL,
    file_path       VARCHAR(255),
    status          ENUM('DRAFT','SENT','VIEWED','ACCEPTED','REJECTED')
                    NOT NULL DEFAULT 'DRAFT',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);