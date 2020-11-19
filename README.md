It's just a single page flashcard app. No sign-in, no persistence, all client side.


[play with it here!](https://larzeitlin.github.io/simple-flashcards/index.html)

## TODO

- [ ] keyboard input
- [ ] file upload
- [ ] export errors
- [ ] fix padding for long card values

### Development mode

```
lein figwheel
```

### Building for production

```
lein clean
lein package
```

Having built for production try out locally by viewing the index.html file in the root of the project in a browser. 
