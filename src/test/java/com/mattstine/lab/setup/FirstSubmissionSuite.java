package com.mattstine.lab.setup;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({
        HelloWorldTest.class
})
@IncludeTags({
        "FirstSubmissionTest"
})
public class FirstSubmissionSuite {
}
