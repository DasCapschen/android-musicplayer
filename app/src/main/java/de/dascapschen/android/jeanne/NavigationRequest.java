package de.dascapschen.android.jeanne;

import android.os.Bundle;

public interface NavigationRequest
{
    void navigate(int actionID, Bundle arguments);
    void back();
}
