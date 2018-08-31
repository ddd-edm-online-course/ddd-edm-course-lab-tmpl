package com.mattstine.lab.setup;

import com.mattstine.lab.infrastructure.FirstSubmissionTest;
import com.mattstine.lab.setup.HelloWorldTest;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Suite.SuiteClasses({
        HelloWorldTest.class
})
@Categories.IncludeCategory({
        FirstSubmissionTest.class
})
public class FirstSubmissionSuite {
}
