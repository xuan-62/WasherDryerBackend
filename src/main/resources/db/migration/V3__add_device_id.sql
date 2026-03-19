ALTER TABLE `item`
    ADD COLUMN device_id VARCHAR(255) DEFAULT NULL;

CREATE INDEX idx_item_device_id ON `item`(device_id);
