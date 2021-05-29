# Guide for contributors

Scala Native Bindgen follows Scala Native's [contributing guidelines].
Larger contributions should always begin by creating an issue to
ensure that it is properly scoped.

Important to note is that all contributors must have signed the [Scala CLA].

 [contributing guidelines]: http://www.scala-native.org/en/latest/contrib/contributing.html
 [Scala CLA]: https://www.lightbend.com/contribute/cla/scala

## Developer Workflow

Install the following for macOS:
```sh
brew tap nlohmann/json
brew install nlohmann-json
```

Build the `scalaBindgen` tool:

```sh
mkdir bindgen/target
cd bindgen/target
cmake ..
make

# Alternatively rerun on change
watchman-make -p '*.cpp' '*.h' --run 'make -C bindgen/target'
```

In another terminal, run the test suite:

```sh
cd tests
sbt ~test
```

To run the same tests as is run by Travis CI use the `test.sh` script, which will both build the executable and run `sbt verify`:

```sh
./scripts/test.sh
```

## Coding Guidelines

Code should be formatted with `./scripts/scalafmt` and `./scripts/clangfmt`.
Make sure that all of your contributions are properly formatted before
suggesting any changes. You can check the formatting using either:

```sh
scripts/scalafmt --test
scripts/clangfmt --test
```

or:

```sh
docker-compose run --rm ubuntu-18.04-llvm-6.0 scripts/scalafmt --test
docker-compose run --rm ubuntu-18.04-llvm-6.0 scripts/clangfmt --test
```

The C++ tool is built on Clang and Libtooling and should respect the conventions of
LLVM and Clang tools. The code itself should adhere to the [LLVM Coding Standards],
specifically:

 - For code generation and error reporting use `llvm::outs()` and `llvm::errs()`.
 - [Use `\n` instead of `std::endl`](https://llvm.org/docs/CodingStandards.html#avoid-std-endl)
   and remember to flush when reporting errors.

 [LLVM Coding Standards]: https://llvm.org/docs/CodingStandards.html
