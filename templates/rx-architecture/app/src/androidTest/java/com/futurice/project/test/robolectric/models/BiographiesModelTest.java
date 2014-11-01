package com.futurice.project.test.robolectric.models;

import com.futurice.project.models.BiographiesModel;
import com.futurice.project.test.robolectric.RobolectricGradleTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)
public class BiographiesModelTest {

    private BiographiesModel model;

    @Before
    public void setUp() throws Exception {
        model = BiographiesModel.getInstance();
    }

    @Test
    public void test_simulationAuthorMethodReturnsObjectOnNullParameter() throws Exception {
        assertNotNull(model.getAuthor(null));
    }

    @Test
    public void test_simulationBookPriceMethodReturnsObjectOnNullParameter() throws Exception {
        assertNotNull(model.getBookPrice(null));
    }

}
