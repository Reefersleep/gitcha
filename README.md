# gitcha

I'm gunna gitcha!

A [kaocha](https://github.com/lambdaisland/kaocha) plugin that reports who touched a failing test `ns` last.

## Usage

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
## License

Copyright © 2021 Søren Knudsen. Special thanks to Arne Brasseur for invaluable guidance!

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
