package com.futurice.project.test.device;

import com.futurice.project.MainActivity;
import com.futurice.project.R;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

public class BookFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public BookFragmentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void test_shouldShowBookTitle() throws Exception {
        TextView titleText = (TextView) solo.getView(R.id.title);
        assertEquals(View.VISIBLE, titleText.getVisibility());
        solo.sleep(1000);
        assertEquals(titleText.getText().toString().startsWith("Biography of"), true);
    }
}
