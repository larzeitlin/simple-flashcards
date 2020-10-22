It's just a single page flashcard app. No magic, no DB, no sign-in, no persistence, all client side.


[play with it here!](https://larzeitlin.github.io/simple-flashcards/index.html)



### Development mode
To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```

Figwheel will automatically push cljs changes to the browser.
Once Figwheel starts up, you should be able to open the `public/index.html` page in the browser.

### REPL

The project is setup to start nREPL on port `7002` once Figwheel starts.
Once you connect to the nREPL, run `(cljs)` to switch to the ClojureScript REPL.

### Building for production

```
lein clean
lein package
```

Having built for production try out locally by viewing the index.html file in the root of the project in a browser. 
