package ru.drmemex.classifieds.feature.region.exception;

import ru.drmemex.classifieds.common.exception.NotFoundException;

public class RegionNotFoundException extends NotFoundException {

    public RegionNotFoundException() {
        super("Region not found");
    }
}
