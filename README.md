# Kount

Perform actions based on a counter

## Why

I created this library because I didn't really like [Once](https://github.com/jonfinerty/Once)

## Setup

* Include Kount.kt in your project.


## Usage

* Create an instance of Kount
* If you use Koin, then register as follows:

```Kotlin
val koinModule: Module = module {
    single { Kount(WhateverSharedPreference()) as Kountable }
}
```

and lazily inject like:

```Kotlin
val counters: Kountable by inject()
```

Now check what available method you need. Here is a sample usage:

```Kotlin
val key = "PageVisits"
val currentCounter = counters.count(key) // returns null because this key doesn't exist

counters.matches(key, Komparison.Exactly(5)) // returns false AND sets the value of `key` to 1
counters.matches(key, Komparison.LessThan(5)) // returns true AND sets the value of `key` to 2
counters.matches(key, Komparison.Repeat(2, 2) // returns true AND sets the value of `key` to 3
```

The `Repeat` option is my favorite one. You can repeat a task for Y amount of times every X time you call `matches` on it!
Look at the code to see how it works.


## Contributing

Pull requests are not welcome

## TODO: 

* Add a value change listener
* Write tests (lol)

## License

Lol licenses
