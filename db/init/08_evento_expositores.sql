-- RF-11: un evento puede tener uno o más expositores.
BEGIN;
CREATE TABLE IF NOT EXISTS evento_expositores (
 evento_id BIGINT NOT NULL REFERENCES eventos(id) ON DELETE CASCADE,
 expositor_id BIGINT NOT NULL REFERENCES expositores(id),
 PRIMARY KEY(evento_id, expositor_id)
);
INSERT INTO evento_expositores(evento_id,expositor_id)
 SELECT id,expositor_id FROM eventos WHERE expositor_id IS NOT NULL ON CONFLICT DO NOTHING;
COMMIT;
