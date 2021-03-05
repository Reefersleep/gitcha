# gitcha

I'm gunna gitcha!

A [kaocha](https://github.com/lambdaisland/kaocha) plugin that reports who touched a failing test `ns` last.

```
----
The last individuals to touch the file at
/Users/Jacob/dev/projects/my-project/test/literacy_test.clj
were:
* Arthur Conan Doyle (acd@canterbury.uk) at Thu Sep 26 10:48:46 2019 +0000
* William Shakespeare (billy@thespian.co.uk) at Tue Apr 2 11:06:13 2019 +0000
* Joanne Rowling (jkrowling@gryffindor.com) at Fri Jun 29 12:15:16 2018 +0000
Maybe they know what's wrong?
```

## Usage

[![Clojars Project](https://img.shields.io/clojars/v/gitcha.svg)](https://clojars.org/gitcha)

gitcha assumes that the [git client](https://git-scm.com/downloads) is installed.

In your `project.clj`, 
```
:dependencies [[gitcha "0.1.0-SNAPSHOT"]
               ...
```

In your `tests.edn`, 
```
#kaocha/v1
{...
 :plugins [:gitcha.core/plugin]
 ...
```
Then, when you run your tests with kaocha, and one ns fails, you should get something like this:
```
----
The last individuals to touch the file at
/Users/Jacob/dev/projects/my-project/test/literacy_test.clj
were:
* Arthur Conan Doyle (acd@canterbury.uk) at Thu Sep 26 10:48:46 2019 +0000
* William Shakespeare (billy@thespian.co.uk) at Tue Apr 2 11:06:13 2019 +0000
* Joanne Rowling (jkrowling@gryffindor.com) at Fri Jun 29 12:15:16 2018 +0000
Maybe they know what's wrong?
```

#### Warning

`gitcha` uses the version of [kaocha](https://github.com/lambdaisland/kaocha) that you require in your project.

`gitcha`will warn you if you are using a version of [kaocha](https://github.com/lambdaisland/kaocha) that it has not been tested with.

To suppress the warning, add `:gitcha.core/suppress-warning? true` to your `tests.edn`, like so:
```
#kaocha/v1
{...
 :plugins [:gitcha.core/plugin]
 :gitcha.core/suppress-warning? true}
```
or, if you are calling from the command line, add the option `--gitcha-suppress-warning`

## License

Copyright © 2021 Søren Knudsen. Special thanks to Arne Brasseur for invaluable guidance!

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
