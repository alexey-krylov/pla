/*
 * Copyright (c) 3/10/15 8:33 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
public class TestUtil {

    public static <T> T fillDataIntoObjectGraph(Class<T> clazz) {
        PodamFactory factory = new PodamFactoryImpl();
        T objectGraph = factory.manufacturePojo(clazz);
        return objectGraph;
    }
}
