Feature added by Russell Waterhouse


Background:
    Dependency injection with Dagger is the android development industry standard
    It reports to create cleaner code that is easier to test
    When all developers working on a project are familiar with it, I suspect this to be the case,
    however with new developers it may serve to be more confusing than helpful

My reason for implementing this:
    I used the dagger library for dependency injection so that I would be able to create test cases otherwise not testable on android
    The idea with using dagger and dependency injection is simple: When testing classes like activities and fragments, dagger can give every class mock dependencies
    This allows for unit testing of UI components.
    When in production, dagger gives every class its real dependencies instead of mock ones, allowing production builds to be functional

What you, the next developer can do:
    If you are interested in dagger and dependency injection and professional android development, I recommend reading the documentation.
            https://dagger.dev/
            https://developer.android.com/training/dependency-injection/dagger-android

        The basic idea of dependency injection is that every object should be passed all of its external dependencies upon creation
        (without dagger, you can do dependency injection by just putting all your dependencies in the constructor). This allows what I wanted:
        to be able to pass mock dependencies to objects for unit testing, but pass real dependencies when in production builds. However, this would mean that there would be a
        god object that creates every single dependency in a tree, starting at the leaves and working up.
        Dagger, with the help of some annotations and helper classes, is instructed how to create dependencies and then injects them into classes for you.
        More information can be found in the documentation.

    However, if you find that this pattern is more of a hindrance than it is helpful, I encourage you to refactor it out.