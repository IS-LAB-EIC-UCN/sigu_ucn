-- Revertir multa.monto de DOUBLE PRECISION a DECIMAL(10,2)
-- V005 lo degradó a DOUBLE para coincidir con Java Double.
-- V006 restaura DECIMAL(10,2) para precision monetaria, alineado con BigDecimal en Java.

ALTER TABLE multa ALTER COLUMN monto TYPE DECIMAL(10,2);
